package me.luka.artifactplugin.managers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.models.Artifact;

import java.util.*;

public class ConfigManager {

    private final ArtifactPlugin plugin;
    private FileConfiguration config;
    
    private int updateInterval;
    private List<String> enabledWorlds;
    private Set<Integer> enabledSlots;
    private boolean enableArmorSlots;
    private boolean showParticles;
    private boolean showMessages;
    private boolean debug;
    private boolean pluginEnabled;
    
    private String msgArtifactActivated;
    private String msgArtifactDeactivated;
    private String msgBagFull;
    private String msgBagNotArtifact;
    private String msgBagCleaned;
    private String msgBagInfo;
    
    private boolean bagsEnabled;
    private boolean validateBagContents;
    private int bagValidationInterval;
    private Map<Integer, Integer> tierLimits;

    public ConfigManager(ArtifactPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        ConfigurationSection settings = config.getConfigurationSection("settings");
        if (settings != null) {
            pluginEnabled = settings.getBoolean("enabled", true);
            updateInterval = settings.getInt("update-interval", 5);
            enabledWorlds = settings.getStringList("enabled-worlds");
            enabledSlots = new HashSet<>();
            for (int slot : settings.getIntegerList("enabled-slots")) {
                enabledSlots.add(slot);
            }
            enableArmorSlots = settings.getBoolean("enable-armor-slots", true);
            showParticles = settings.getBoolean("show-particles", true);
            showMessages = settings.getBoolean("show-messages", false);
            debug = settings.getBoolean("debug", false);
        } else {
            pluginEnabled = true;
            updateInterval = 5;
            enabledWorlds = Arrays.asList("<all>");
            enabledSlots = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 40));
            enableArmorSlots = true;
            showParticles = true;
            showMessages = false;
            debug = false;
        }
        
        if (enableArmorSlots) {
            enabledSlots.add(36); // Boots
            enabledSlots.add(37); // Leggings
            enabledSlots.add(38); // Chestplate
            enabledSlots.add(39); // Helmet
        }
        
        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages != null) {
            msgArtifactActivated = colorize(messages.getString("artifact-activated", "&aАртефакт &e%artifact% &aактивирован!"));
            msgArtifactDeactivated = colorize(messages.getString("artifact-deactivated", "&cАртефакт &e%artifact% &cдеактивирован."));
            msgBagFull = colorize(messages.getString("bag-full", "&cМешок заполнен! Максимум &e%slots% &cартефактов."));
            msgBagNotArtifact = colorize(messages.getString("bag-not-artifact", "&cВ мешок можно класть только артефакты!"));
            msgBagCleaned = colorize(messages.getString("bag-cleaned", "&c⚠ Из аномального мешка удалены предметы, не являющиеся артефактами!"));
            msgBagInfo = colorize(messages.getString("bag-info", "&6Аномальный Мешок: &e%slots% слотов для артефактов!"));
        } else {
            msgArtifactActivated = "&aАртефакт &e%artifact% &aактивирован!";
            msgArtifactDeactivated = "&cАртефакт &e%artifact% &cдеактивирован.";
            msgBagFull = "&cМешок заполнен! Максимум &e%slots% &cартефактов.";
            msgBagNotArtifact = "&cВ мешок можно класть только артефакты!";
            msgBagCleaned = "&c⚠ Из аномального мешка удалены предметы, не являющиеся артефактами!";
            msgBagInfo = "&6Аномальный Мешок: &e%slots% слотов для артефактов!";
        }
        
        ConfigurationSection bags = config.getConfigurationSection("bags");
        if (bags != null) {
            bagsEnabled = bags.getBoolean("enabled", true);
            validateBagContents = bags.getBoolean("validate-contents", true);
            bagValidationInterval = bags.getInt("validation-interval", 60);
            
            tierLimits = new HashMap<>();
            ConfigurationSection tierSection = bags.getConfigurationSection("tier-limits");
            if (tierSection != null) {
                tierLimits.put(1, tierSection.getInt("tier-1", 2));
                tierLimits.put(2, tierSection.getInt("tier-2", 4));
                tierLimits.put(3, tierSection.getInt("tier-3", 6));
                tierLimits.put(4, tierSection.getInt("tier-4", 8));
            } else {
                tierLimits.put(1, 2);
                tierLimits.put(2, 4);
                tierLimits.put(3, 6);
                tierLimits.put(4, 8);
            }
        } else {
            bagsEnabled = true;
            validateBagContents = true;
            bagValidationInterval = 60;
            tierLimits = new HashMap<>();
            tierLimits.put(1, 2);
            tierLimits.put(2, 4);
            tierLimits.put(3, 6);
            tierLimits.put(4, 8);
        }
    }

    public void loadArtifacts() {
        Map<String, Artifact> artifacts = new HashMap<>();
        
        ConfigurationSection artifactsSection = config.getConfigurationSection("artifacts");
        if (artifactsSection != null) {
            for (String artifactId : artifactsSection.getKeys(false)) {
                ConfigurationSection section = artifactsSection.getConfigurationSection(artifactId);
                if (section == null) continue;
                
                if (!section.getBoolean("enabled", true)) continue;
                
                Artifact artifact = parseArtifact(artifactId, section);
                if (artifact != null && artifact.hasAnyEffect()) {
                    artifacts.put(artifactId, artifact);
                }
            }
        }
        
        plugin.getArtifactManager().setArtifacts(artifacts);
    }

    private Artifact parseArtifact(String id, ConfigurationSection section) {
        Artifact artifact = new Artifact(id);
        
        String materialStr = section.getString("material", "STONE");
        
        if (materialStr.contains(":")) {
            artifact.setNamespacedItem(materialStr);
            Material fallback = Material.matchMaterial(materialStr.split(":")[1]);
            artifact.setMaterial(fallback != null ? fallback : Material.PAPER);
        } else {
            Material material = Material.matchMaterial(materialStr);
            if (material == null) {
                plugin.getLogger().warning("Invalid material for artifact " + id + ": " + materialStr);
                return null;
            }
            artifact.setMaterial(material);
        }
        
        artifact.setName(colorize(section.getString("name", "&f" + id)));
        artifact.setLore(colorizeList(section.getStringList("lore")));
        artifact.setEnabled(section.getBoolean("enabled", true));
        artifact.setCustomModelData(section.getInt("custom-model-data", 0));
        
        ConfigurationSection effects = section.getConfigurationSection("effects");
        if (effects != null) {
            parsePotionEffects(artifact, effects);
            parseAttackEffects(artifact, effects);
            parseDefenseEffects(artifact, effects);
            parsePassiveAbilities(artifact, effects);
            parseTickEffects(artifact, effects);
            parseSpecialEffects(artifact, effects);
        }
        
        return artifact;
    }

    private void parsePotionEffects(Artifact artifact, ConfigurationSection effects) {
        List<?> list = effects.getList("potion-effects");
        if (list == null) return;
        
        for (Object obj : list) {
            if (!(obj instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            
            String effectName = (String) map.get("effect");
            PotionEffectType effectType = PotionEffectType.getByName(effectName);
            if (effectType == null) continue;
            
            int level = ((Number) map.getOrDefault("level", 1)).intValue() - 1;
            boolean ambient = (Boolean) map.getOrDefault("ambient", false);
            boolean particles = (Boolean) map.getOrDefault("particles", true);
            
            artifact.addPotionEffect(new Artifact.PotionEffectConfig(effectType, level, ambient, particles));
        }
    }

    private void parseAttackEffects(Artifact artifact, ConfigurationSection effects) {
        List<?> list = effects.getList("attack-effects");
        if (list == null) return;
        
        for (Object obj : list) {
            if (!(obj instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            
            String typeStr = (String) map.get("type");
            Artifact.AttackEffectType type = parseAttackEffectType(typeStr);
            if (type == null) continue;
            
            Artifact.AttackEffect effect = new Artifact.AttackEffect();
            effect.setType(type);
            effect.setChance(((Number) map.getOrDefault("chance", 1.0)).doubleValue());
            
            switch (type) {
                case POTION:
                    effect.setPotionType(PotionEffectType.getByName((String) map.get("effect")));
                    effect.setPotionLevel(((Number) map.getOrDefault("level", 1)).intValue() - 1);
                    effect.setPotionDuration(((Number) map.getOrDefault("duration", 60)).intValue());
                    break;
                case IGNITE:
                    effect.setIgniteDuration(((Number) map.getOrDefault("duration", 60)).intValue());
                    break;
                case KNOCKBACK:
                    effect.setKnockbackStrength(((Number) map.getOrDefault("strength", 1.0)).doubleValue());
                    break;
                case LIFESTEAL:
                    effect.setLifestealPercent(((Number) map.getOrDefault("percent", 10)).doubleValue());
                    break;
                case EXECUTE:
                    effect.setExecuteThreshold(((Number) map.getOrDefault("threshold", 0.3)).doubleValue());
                    effect.setExecuteMultiplier(((Number) map.getOrDefault("multiplier", 2.0)).doubleValue());
                    break;
                case POISON:
                    effect.setPoisonDamage(((Number) map.getOrDefault("damage", 1)).intValue());
                    effect.setPoisonDuration(((Number) map.getOrDefault("duration", 80)).intValue());
                    break;
                case CRITICAL:
                    effect.setCritChanceBonus(((Number) map.getOrDefault("chance-bonus", 0.15)).doubleValue());
                    effect.setCritDamageMultiplier(((Number) map.getOrDefault("damage-multiplier", 1.5)).doubleValue());
                    break;
                case PROJECTILE:
                    effect.setProjectileEntity((String) map.get("entity"));
                    effect.setProjectileVelocity(((Number) map.getOrDefault("velocity", 3.0)).doubleValue());
                    break;
                case SUMMON:
                    effect.setSummonEntity((String) map.get("entity"));
                    effect.setSummonCount(((Number) map.getOrDefault("count", 1)).intValue());
                    break;
                case EXPLOSION:
                    effect.setExplosionPower(((Number) map.getOrDefault("power", 1.0)).doubleValue());
                    effect.setExplosionFire((Boolean) map.getOrDefault("fire", false));
                    break;
                case LIGHTNING:
                    effect.setLightningDamage(((Number) map.getOrDefault("damage", 5)).intValue());
                    break;
                case PARTICLE_EFFECT:
                    effect.setParticleType((String) map.get("particle"));
                    effect.setParticleCount(((Number) map.getOrDefault("count", 10)).intValue());
                    break;
                case COMMAND:
                    effect.setCommand((String) map.get("command"));
                    break;
                case SATURATION:
                    effect.setSaturationAmount(((Number) map.getOrDefault("amount", 1)).doubleValue());
                    break;
                case SLOW:
                    effect.setSlowLevel(((Number) map.getOrDefault("level", 1)).intValue() - 1);
                    effect.setPotionDuration(((Number) map.getOrDefault("duration", 40)).intValue());
                    break;
                case THIEF:
                    effect.setThiefAmount(((Number) map.getOrDefault("amount", 1)).intValue());
                    break;
                case DAMAGE:
                    effect.setDamageAmplifier(((Number) map.getOrDefault("amplifier", 0.5)).doubleValue());
                    break;
                default:
                    break;
            }
            
            artifact.addAttackEffect(effect);
        }
    }

    private Artifact.AttackEffectType parseAttackEffectType(String type) {
        if (type == null) return null;
        try {
            return Artifact.AttackEffectType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void parseDefenseEffects(Artifact artifact, ConfigurationSection effects) {
        List<?> list = effects.getList("defense-effects");
        if (list == null) return;
        
        for (Object obj : list) {
            if (!(obj instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            
            String typeStr = (String) map.get("type");
            Artifact.DefenseEffectType type = parseDefenseEffectType(typeStr);
            if (type == null) continue;
            
            Artifact.DefenseEffect effect = new Artifact.DefenseEffect();
            effect.setType(type);
            effect.setChance(((Number) map.getOrDefault("chance", 1.0)).doubleValue());
            
            switch (type) {
                case REFLECT:
                    effect.setReflectPercent(((Number) map.getOrDefault("percent", 25)).doubleValue());
                    break;
                case ABSORB:
                    effect.setAbsorbPercent(((Number) map.getOrDefault("percent", 50)).doubleValue());
                    break;
                case BLOCK:
                    effect.setBlockChance(((Number) map.getOrDefault("chance", 0.2)).doubleValue());
                    break;
                case THORNS:
                    effect.setThornsDamage(((Number) map.getOrDefault("damage", 2)).intValue());
                    break;
                case KNOCKBACK:
                    effect.setKnockbackStrength(((Number) map.getOrDefault("strength", 2.0)).doubleValue());
                    break;
                case POTION_TO_ATTACKER:
                    effect.setPotionType(PotionEffectType.getByName((String) map.get("effect")));
                    effect.setPotionLevel(((Number) map.getOrDefault("level", 1)).intValue() - 1);
                    effect.setPotionDuration(((Number) map.getOrDefault("duration", 100)).intValue());
                    break;
                case ABSORPTION:
                    effect.setAbsorptionHearts(((Number) map.getOrDefault("hearts", 2)).doubleValue());
                    effect.setAbsorptionDuration(((Number) map.getOrDefault("duration", 200)).intValue());
                    break;
                case DODGE:
                    effect.setDodgeChance(((Number) map.getOrDefault("chance", 0.15)).doubleValue());
                    break;
                case COUNTER:
                    effect.setCounterDamage(((Number) map.getOrDefault("damage", 3)).doubleValue());
                    break;
                case INVULNERABILITY:
                    effect.setInvulnerabilityTicks(((Number) map.getOrDefault("ticks", 10)).intValue());
                    break;
                case EXPLOSION:
                    effect.setExplosionPower(((Number) map.getOrDefault("power", 2.0)).doubleValue());
                    effect.setExplosionFire((Boolean) map.getOrDefault("fire", true));
                    break;
                case TEMPORARY_RESISTANCE:
                    effect.setResistanceType(PotionEffectType.getByName((String) map.get("effect")));
                    effect.setResistanceLevel(((Number) map.getOrDefault("level", 5)).intValue() - 1);
                    effect.setResistanceDuration(((Number) map.getOrDefault("duration", 200)).intValue());
                    break;
                case REGENERATE_ON_HIT:
                    effect.setRegenAmount(((Number) map.getOrDefault("health", 1)).doubleValue());
                    break;
                case LEVITATE:
                    effect.setLevitateDuration(((Number) map.getOrDefault("duration", 40)).intValue());
                    effect.setLevitateStrength(((Number) map.getOrDefault("strength", 3)).intValue());
                    break;
                case BLEED:
                    effect.setBleedDamage(((Number) map.getOrDefault("damage", 1)).intValue());
                    effect.setBleedDuration(((Number) map.getOrDefault("duration", 60)).intValue());
                    break;
                case VAMPIRIC:
                    effect.setVampiricPercent(((Number) map.getOrDefault("percent", 20)).doubleValue());
                    break;
                case SHIELD:
                    effect.setShieldPoints(((Number) map.getOrDefault("points", 5)).doubleValue());
                    effect.setShieldRechargeTime(((Number) map.getOrDefault("recharge-time", 300)).intValue());
                    break;
                default:
                    break;
            }
            
            artifact.addDefenseEffect(effect);
        }
    }

    private Artifact.DefenseEffectType parseDefenseEffectType(String type) {
        if (type == null) return null;
        try {
            return Artifact.DefenseEffectType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void parsePassiveAbilities(Artifact artifact, ConfigurationSection effects) {
        List<?> list = effects.getList("passive-abilities");
        if (list == null) return;
        
        for (Object obj : list) {
            if (!(obj instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            
            String typeStr = (String) map.get("type");
            Artifact.PassiveAbilityType type = parsePassiveAbilityType(typeStr);
            if (type == null) continue;
            
            Artifact.PassiveAbility ability = new Artifact.PassiveAbility();
            ability.setType(type);
            
            switch (type) {
                case DOUBLE_JUMP:
                    ability.setDoubleJumpBoost(((Number) map.getOrDefault("boost", 1.5)).doubleValue());
                    ability.setDoubleJumpCooldown(((Number) map.getOrDefault("cooldown", 100)).intValue());
                    break;
                case SPRINT_JUMP:
                    ability.setSprintJumpSpeedBoost(((Number) map.getOrDefault("speed-boost", 0.5)).doubleValue());
                    ability.setSprintJumpDuration(((Number) map.getOrDefault("duration", 40)).intValue());
                    break;
                case REGENERATION_PASSIVE:
                    ability.setRegenAmount(((Number) map.getOrDefault("amount", 1)).doubleValue());
                    ability.setRegenInterval(((Number) map.getOrDefault("interval", 40)).intValue());
                    break;
                case RAGE:
                    ability.setRageThreshold(((Number) map.getOrDefault("threshold", 0.3)).doubleValue());
                    ability.setRageStrengthLevel(((Number) map.getOrDefault("strength-level", 2)).intValue() - 1);
                    break;
                case ADRENALINE:
                    ability.setAdrenalineThreshold(((Number) map.getOrDefault("threshold", 0.25)).doubleValue());
                    ability.setAdrenalineSpeedLevel(((Number) map.getOrDefault("speed-level", 2)).intValue() - 1);
                    break;
                case XP_BOOST:
                    ability.setXpMultiplier(((Number) map.getOrDefault("multiplier", 1.5)).doubleValue());
                    break;
                case LUCK_BOOST:
                    ability.setLuckLevel(((Number) map.getOrDefault("level", 5)).intValue());
                    break;
                case FLIGHT:
                    ability.setFlightSpeed(((Number) map.getOrDefault("speed", 0.5)).doubleValue());
                    break;
                case HASTE:
                    ability.setHasteLevel(((Number) map.getOrDefault("level", 2)).intValue());
                    break;
                case ENDER_PEARL_CD:
                    ability.setEnderPearlCDReduction(((Number) map.getOrDefault("reduction-percent", 50)).intValue());
                    break;
                case JUMP_BOOST:
                    ability.setJumpBoostLevel(((Number) map.getOrDefault("level", 1)).intValue());
                    break;
                case SOUL_SPEED:
                    ability.setSoulSpeedLevel(((Number) map.getOrDefault("level", 1)).intValue());
                    break;
                case DEPTH_STRIDER:
                    ability.setDepthStriderLevel(((Number) map.getOrDefault("level", 3)).intValue());
                    break;
                case FROST_WALKER:
                    ability.setFrostWalkerLevel(((Number) map.getOrDefault("level", 1)).intValue());
                    break;
                case STEP_ASSIST:
                    ability.setStepAssistHeight(((Number) map.getOrDefault("height", 1.0)).doubleValue());
                    break;
                case AUTO_REPAIR:
                    ability.setAutoRepairAmount(((Number) map.getOrDefault("amount", 1)).intValue());
                    ability.setAutoRepairInterval(((Number) map.getOrDefault("interval", 100)).intValue());
                    break;
                case UNBREAKING_BONUS:
                    ability.setUnbreakingLevel(((Number) map.getOrDefault("level", 3)).intValue());
                    break;
                case PROTECTION:
                    ability.setProtectionLevel(((Number) map.getOrDefault("level", 4)).intValue());
                    break;
                case FIRE_PROTECTION:
                    ability.setFireProtectionLevel(((Number) map.getOrDefault("level", 4)).intValue());
                    break;
                case BLAST_PROTECTION:
                    ability.setBlastProtectionLevel(((Number) map.getOrDefault("level", 4)).intValue());
                    break;
                case PROJECTILE_PROTECTION:
                    ability.setProjectileProtectionLevel(((Number) map.getOrDefault("level", 4)).intValue());
                    break;
                case FEATHER_FALLING:
                    ability.setFeatherFallingLevel(((Number) map.getOrDefault("level", 4)).intValue());
                    break;
                case THORNS_PASSIVE:
                    ability.setThornsLevel(((Number) map.getOrDefault("level", 3)).intValue());
                    break;
                case KNOCKBACK_RESISTANCE:
                    ability.setKnockbackResistanceLevel(((Number) map.getOrDefault("level", 1)).intValue());
                    break;
                case LOOT_BONUS:
                    ability.setLootBonusChance(((Number) map.getOrDefault("chance", 0.1)).doubleValue());
                    break;
                case FISHING_LUCK:
                    ability.setFishingLuckLevel(((Number) map.getOrDefault("level", 5)).intValue());
                    break;
                case DAMAGE_VS:
                    @SuppressWarnings("unchecked")
                    List<String> entities = (List<String>) map.get("entities");
                    ability.setDamageVsEntities(entities != null ? entities : new ArrayList<>());
                    ability.setDamageVsBonus(((Number) map.getOrDefault("bonus", 2)).doubleValue());
                    break;
                case FAST_PLACE:
                    ability.setFastPlaceSpeed(((Number) map.getOrDefault("speed", 2)).intValue());
                    break;
                case ARROW_EFFECTS:
                    ability.setArrowEffectType(PotionEffectType.getByName((String) map.get("effect")));
                    ability.setArrowEffectLevel(((Number) map.getOrDefault("level", 1)).intValue() - 1);
                    ability.setArrowEffectDuration(((Number) map.getOrDefault("duration", 60)).intValue());
                    break;
                case MAGNETIC:
                    ability.setMagneticRange(((Number) map.getOrDefault("range", 5)).doubleValue());
                    break;
                case TRACKING_COMPASS:
                    ability.setTrackingEntity((String) map.get("entity"));
                    break;
                case WITHER_REDUCTION:
                    ability.setWitherReduction(((Number) map.getOrDefault("percent", 50)).doubleValue());
                    break;
                case MAGIC_REDUCTION:
                    ability.setMagicReduction(((Number) map.getOrDefault("percent", 25)).doubleValue());
                    break;
                default:
                    break;
            }
            
            artifact.addPassiveAbility(ability);
        }
    }

    private Artifact.PassiveAbilityType parsePassiveAbilityType(String type) {
        if (type == null) return null;
        try {
            return Artifact.PassiveAbilityType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void parseTickEffects(Artifact artifact, ConfigurationSection effects) {
        List<?> list = effects.getList("tick-effects");
        if (list == null) return;
        
        for (Object obj : list) {
            if (!(obj instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            
            String typeStr = (String) map.get("type");
            Artifact.TickEffectType type = parseTickEffectType(typeStr);
            if (type == null) continue;
            
            Artifact.TickEffect effect = new Artifact.TickEffect();
            effect.setType(type);
            effect.setInterval(((Number) map.getOrDefault("interval", 100)).intValue());
            
            switch (type) {
                case LIGHT:
                    effect.setLightRadius(((Number) map.getOrDefault("radius", 5)).intValue());
                    break;
                case PARTICLE_CLOUD:
                    effect.setParticleType((String) map.get("particle"));
                    effect.setParticleRadius(((Number) map.getOrDefault("radius", 2)).doubleValue());
                    break;
                case HEAL_OTHERS:
                    effect.setHealAmount(((Number) map.getOrDefault("amount", 1)).doubleValue());
                    effect.setHealRadius(((Number) map.getOrDefault("radius", 10)).intValue());
                    break;
                case HUNGER_LESS:
                    effect.setHungerlessAmount(((Number) map.getOrDefault("amount", 1)).doubleValue());
                    break;
                case PROTECT_NEARBY:
                    @SuppressWarnings("unchecked")
                    List<String> entities = (List<String>) map.get("entities");
                    effect.setProtectEntities(entities != null ? entities : new ArrayList<>());
                    effect.setProtectRadius(((Number) map.getOrDefault("radius", 10)).intValue());
                    break;
                case LIGHTNING_STRIKE:
                    effect.setLightningChance(((Number) map.getOrDefault("chance", 0.3)).doubleValue());
                    break;
                case SUMMON_MOB:
                    effect.setSummonEntity((String) map.get("entity"));
                    effect.setSummonCount(((Number) map.getOrDefault("count", 1)).intValue());
                    effect.setSummonRadius(((Number) map.getOrDefault("radius", 3)).intValue());
                    break;
                case FEED_PLAYER:
                    effect.setFeedAmount(((Number) map.getOrDefault("amount", 1)).doubleValue());
                    break;
                default:
                    break;
            }
            
            artifact.addTickEffect(effect);
        }
    }

    private Artifact.TickEffectType parseTickEffectType(String type) {
        if (type == null) return null;
        try {
            return Artifact.TickEffectType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void parseSpecialEffects(Artifact artifact, ConfigurationSection effects) {
        List<?> list = effects.getList("special-effects");
        if (list == null) return;
        
        for (Object obj : list) {
            if (!(obj instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            
            String typeStr = (String) map.get("type");
            Artifact.SpecialEffectType type = parseSpecialEffectType(typeStr);
            if (type == null) continue;
            
            Artifact.SpecialEffect effect = new Artifact.SpecialEffect();
            effect.setType(type);
            
            switch (type) {
                case RESPAWN_EFFECTS:
                    @SuppressWarnings("unchecked")
                    List<String> respawnEffects = (List<String>) map.get("effects");
                    effect.setRespawnEffects(respawnEffects != null ? respawnEffects : new ArrayList<>());
                    break;
                case KILL_EFFECTS:
                    @SuppressWarnings("unchecked")
                    List<String> killEffects = (List<String>) map.get("effects");
                    effect.setKillEffects(killEffects != null ? killEffects : new ArrayList<>());
                    break;
                case DEATH_PREVENT:
                    effect.setConsumeOnDeathPrevent((Boolean) map.getOrDefault("consume-item", true));
                    break;
                case DEATH_MESSAGE:
                    effect.setDeathMessage((String) map.get("message"));
                    break;
                case KILL_STREAK:
                    effect.setKillStreakKills(((Number) map.getOrDefault("kills", 5)).intValue());
                    @SuppressWarnings("unchecked")
                    List<String> streakEffects = (List<String>) map.get("effects");
                    effect.setKillStreakEffects(streakEffects != null ? streakEffects : new ArrayList<>());
                    break;
                case EQUIP_PARTICLES:
                    effect.setEquipParticle((String) map.get("particle"));
                    break;
                case ACTIVATE_SOUND:
                    effect.setActivateSound((String) map.get("sound"));
                    break;
                case PLAYER_ONLY:
                    @SuppressWarnings("unchecked")
                    List<String> players = (List<String>) map.get("players");
                    effect.setAllowedPlayers(players != null ? players : new ArrayList<>());
                    break;
                case WORLD_ONLY:
                    @SuppressWarnings("unchecked")
                    List<String> worlds = (List<String>) map.get("worlds");
                    effect.setAllowedWorlds(worlds != null ? worlds : new ArrayList<>());
                    break;
                default:
                    break;
            }
            
            artifact.addSpecialEffect(effect);
        }
    }

    private Artifact.SpecialEffectType parseSpecialEffectType(String type) {
        if (type == null) return null;
        try {
            return Artifact.SpecialEffectType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String colorize(String text) {
        if (text == null) return "";
        return text.replace("&", "§");
    }

    private List<String> colorizeList(List<String> lines) {
        List<String> result = new ArrayList<>();
        if (lines != null) {
            for (String line : lines) {
                result.add(colorize(line));
            }
        }
        return result;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public List<String> getEnabledWorlds() {
        return enabledWorlds;
    }

    public Set<Integer> getEnabledSlots() {
        return enabledSlots;
    }

    public boolean isShowParticles() {
        return showParticles;
    }

    public boolean isShowMessages() {
        return showMessages;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isWorldEnabled(String worldName) {
        if (enabledWorlds.contains("<all>")) return true;
        return enabledWorlds.contains(worldName);
    }
    
    public boolean isPluginEnabled() {
        return pluginEnabled;
    }
    
    public String getMsgArtifactActivated(String artifactName) {
        return msgArtifactActivated.replace("%artifact%", artifactName);
    }
    
    public String getMsgArtifactDeactivated(String artifactName) {
        return msgArtifactDeactivated.replace("%artifact%", artifactName);
    }
    
    public String getMsgBagFull(int slots) {
        return msgBagFull.replace("%slots%", String.valueOf(slots));
    }
    
    public String getMsgBagNotArtifact() {
        return msgBagNotArtifact;
    }
    
    public String getMsgBagCleaned() {
        return msgBagCleaned;
    }
    
    public String getMsgBagInfo(int slots) {
        return msgBagInfo.replace("%slots%", String.valueOf(slots));
    }
    
    public boolean isBagsEnabled() {
        return bagsEnabled;
    }
    
    public boolean isValidateBagContents() {
        return validateBagContents;
    }
    
    public int getBagValidationInterval() {
        return bagValidationInterval;
    }
    
    public int getTierLimit(int tier) {
        return tierLimits.getOrDefault(tier, 2);
    }
}
