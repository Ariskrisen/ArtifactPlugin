package me.luka.artifactplugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.models.Artifact;
import me.luka.artifactplugin.models.AnomalousBag;

import java.util.*;
import java.util.stream.Collectors;

public class ArtifactManager {

    private final ArtifactPlugin plugin;
    private Map<String, Artifact> artifacts;
    private final NamespacedKey ARTIFACT_KEY;
    private final Set<UUID> hasMagnetic;
    private final Map<UUID, Double> magneticRanges;

    public ArtifactManager(ArtifactPlugin plugin) {
        this.plugin = plugin;
        this.ARTIFACT_KEY = new NamespacedKey(plugin, "artifact_id");
        this.artifacts = new HashMap<>();
        this.hasMagnetic = new HashSet<>();
        this.magneticRanges = new HashMap<>();
    }

    public void start() {
        startTask();
        if (plugin.getConfigManager().isBagsEnabled() && plugin.getConfigManager().isValidateBagContents()) {
            startBundleValidationTask();
        }
    }
    
    private void startBundleValidationTask() {
        int interval = plugin.getConfigManager().getBagValidationInterval();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfigManager().isBagsEnabled() || !plugin.getConfigManager().isValidateBagContents()) {
                    return;
                }
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    validatePlayerBags(player);
                }
            }
        }.runTaskTimer(plugin, 20L, interval);
    }
    
    public void validatePlayerBags(Player player) {
        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (AnomalousBag.isAnomalousBag(item)) {
                validateBagContents(player, slot, item);
            } else {
                cleanupCorruptedItem(player, slot, item);
            }
        }
    }
    
    private void cleanupCorruptedItem(Player player, int slot, ItemStack item) {
        if (item == null) return;
        
        if (item.getType() == Material.BUNDLE) {
            try {
                BundleContents contents = item.getData(DataComponentTypes.BUNDLE_CONTENTS);
                if (contents != null && !contents.contents().isEmpty()) {
                    for (ItemStack inner : contents.contents()) {
                        if (inner != null && inner.getType() != Material.AIR) {
                            Artifact artifact = getArtifact(inner);
                            if (artifact != null) {
                                player.getWorld().dropItemNaturally(player.getLocation(), inner);
                            }
                        }
                    }
                    item.unsetData(DataComponentTypes.BUNDLE_CONTENTS);
                    player.getInventory().setItem(slot, item);
                    plugin.getLogger().info("Cleaned up corrupted bundle item for " + player.getName());
                }
            } catch (Exception ignored) {}
        }
    }
    
    private void validateBagContents(Player player, int slot, ItemStack bag) {
        List<ItemStack> bundleItems = getAllItemsFromBundle(bag);
        List<ItemStack> validArtifacts = new ArrayList<>();
        List<ItemStack> invalidItems = new ArrayList<>();
        
        for (ItemStack item : bundleItems) {
            if (item != null && item.getType() != Material.AIR) {
                Artifact artifact = getArtifact(item);
                if (artifact != null) {
                    validArtifacts.add(item);
                } else {
                    invalidItems.add(item);
                }
            }
        }
        
        if (!invalidItems.isEmpty()) {
            plugin.getLogger().info("Found " + invalidItems.size() + " non-artifact items in bag for " + player.getName() + ", cleaning...");
            
            if (!validArtifacts.isEmpty()) {
                setBundleContents(player, slot, bag, validArtifacts);
            } else {
                clearBundleContents(player, slot, bag);
            }
            
            for (ItemStack invalid : invalidItems) {
                player.getWorld().dropItemNaturally(player.getLocation(), invalid);
            }
            
            player.sendMessage(plugin.getConfigManager().getMsgBagCleaned());
        }
    }
    
    public List<ItemStack> getAllItemsFromBundle(ItemStack bundle) {
        List<ItemStack> items = new ArrayList<>();
        
        try {
            BundleContents contents = bundle.getData(DataComponentTypes.BUNDLE_CONTENTS);
            if (contents != null) {
                items.addAll(contents.contents());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error reading bundle contents: " + e.getMessage());
        }
        
        return items;
    }
    
    private void setBundleContents(Player player, int slot, ItemStack bag, List<ItemStack> items) {
        try {
            BundleContents newContents = BundleContents.bundleContents(items);
            bag.setData(DataComponentTypes.BUNDLE_CONTENTS, newContents);
            player.getInventory().setItem(slot, bag);
        } catch (Exception e) {
            plugin.getLogger().warning("Error setting bundle contents: " + e.getMessage());
        }
    }
    
    private void clearBundleContents(Player player, int slot, ItemStack bag) {
        try {
            bag.unsetData(DataComponentTypes.BUNDLE_CONTENTS);
            player.getInventory().setItem(slot, bag);
        } catch (Exception e) {
            plugin.getLogger().warning("Error clearing bundle contents: " + e.getMessage());
        }
    }

    public void setArtifacts(Map<String, Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public int getArtifactCount() {
        return artifacts.size();
    }

    public Artifact getArtifactById(String id) {
        return artifacts.get(id.toLowerCase());
    }

    public Map<String, Artifact> getArtifacts() {
        return artifacts;
    }

    public boolean hasMagneticArtifact(UUID uuid) {
        return hasMagnetic.contains(uuid);
    }

    public double getMagneticRange(UUID uuid) {
        return magneticRanges.getOrDefault(uuid, 0.0);
    }

    public Artifact getArtifact(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        
        String artifactId = meta.getPersistentDataContainer().get(ARTIFACT_KEY, org.bukkit.persistence.PersistentDataType.STRING);
        if (artifactId == null) return null;
        
        return artifacts.get(artifactId.toLowerCase());
    }

    public List<Artifact> getActiveArtifacts(Player player) {
        List<Artifact> result = new ArrayList<>();
        if (player == null) return result;
        
        Set<String> addedArtifacts = new HashSet<>();
        
        for (int slot : plugin.getConfigManager().getEnabledSlots()) {
            ItemStack item = getItemInSlot(player, slot);
            
            if (item == null) continue;
            
            Artifact directArtifact = getArtifact(item);
            if (directArtifact != null && directArtifact.isEnabled() && !addedArtifacts.contains(directArtifact.getId())) {
                result.add(directArtifact);
                addedArtifacts.add(directArtifact.getId());
            }
            
            if (AnomalousBag.isAnomalousBag(item)) {
                List<ItemStack> bagArtifacts = getArtifactsFromBundle(item);
                for (ItemStack bagItem : bagArtifacts) {
                    Artifact artifact = getArtifact(bagItem);
                    if (artifact != null && artifact.isEnabled() && !addedArtifacts.contains(artifact.getId())) {
                        result.add(artifact);
                        addedArtifacts.add(artifact.getId());
                    }
                }
            }
        }
        
        return result;
    }
    
    private List<ItemStack> getArtifactsFromBundle(ItemStack bundle) {
        List<ItemStack> artifactItems = new ArrayList<>();
        
        try {
            BundleContents contents = bundle.getData(DataComponentTypes.BUNDLE_CONTENTS);
            if (contents != null) {
                for (ItemStack item : contents.contents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        Artifact artifact = getArtifact(item);
                        if (artifact != null) {
                            artifactItems.add(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error reading bundle contents: " + e.getMessage());
        }
        
        return artifactItems;
    }
    
    private ItemStack getItemInSlot(Player player, int slot) {
        switch (slot) {
            case 0: case 1: case 2: case 3: case 4:
            case 5: case 6: case 7: case 8:
                return player.getInventory().getItem(slot);
            case 40:
                return player.getInventory().getItemInOffHand();
            case 36:
                return player.getInventory().getBoots();
            case 37:
                return player.getInventory().getLeggings();
            case 38:
                return player.getInventory().getChestplate();
            case 39:
                return player.getInventory().getHelmet();
            default:
                return null;
        }
    }

    public void updatePlayerEffects(Player player) {
        List<Artifact> activeArtifacts = getActiveArtifacts(player);
        
        if (plugin.getConfigManager().isDebug()) {
            if (!activeArtifacts.isEmpty()) {
                plugin.getLogger().info("Активные артефакты для " + player.getName() + ": " + activeArtifacts.size());
            }
        }
        
        Set<PotionEffectType> shouldHaveEffects = new HashSet<>();
        
        boolean hasMagneticNow = false;
        double maxMagneticRange = 0;
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.PotionEffectConfig config : artifact.getPotionEffects()) {
                shouldHaveEffects.add(config.getEffectType());
                player.addPotionEffect(new PotionEffect(
                    config.getEffectType(),
                    6000,
                    config.getLevel(),
                    true,
                    false
                ));
            }
            
            for (Artifact.PassiveAbility ability : artifact.getPassiveAbilities()) {
                switch (ability.getType()) {
                    case MAGNETIC:
                        hasMagneticNow = true;
                        maxMagneticRange = Math.max(maxMagneticRange, ability.getMagneticRange());
                        break;
                        
                    case RAGE:
                        double healthPercent = player.getHealth() / player.getMaxHealth();
                        if (healthPercent <= ability.getRageThreshold()) {
                            shouldHaveEffects.add(PotionEffectType.STRENGTH);
                            player.addPotionEffect(new PotionEffect(
                                PotionEffectType.STRENGTH,
                                6000,
                                ability.getRageStrengthLevel(),
                                true,
                                false
                            ));
                        }
                        break;
                        
                    case ADRENALINE:
                        double healthPercent2 = player.getHealth() / player.getMaxHealth();
                        if (healthPercent2 <= ability.getAdrenalineThreshold()) {
                            shouldHaveEffects.add(PotionEffectType.SPEED);
                            player.addPotionEffect(new PotionEffect(
                                PotionEffectType.SPEED,
                                6000,
                                ability.getAdrenalineSpeedLevel(),
                                true,
                                false
                            ));
                        }
                        break;
                        
                    case FIRE_RESISTANCE_PASSIVE:
                        shouldHaveEffects.add(PotionEffectType.FIRE_RESISTANCE);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0, true, false));
                        break;
                        
                    case WATER_BREATHING:
                        shouldHaveEffects.add(PotionEffectType.WATER_BREATHING);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 6000, 0, true, false));
                        break;
                        
                    case NIGHT_VISION:
                        shouldHaveEffects.add(PotionEffectType.NIGHT_VISION);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0, true, false));
                        break;
                        
                    case SLOW_FALLING:
                        shouldHaveEffects.add(PotionEffectType.SLOW_FALLING);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6000, 0, true, false));
                        break;
                        
                    case REGENERATION_PASSIVE:
                        shouldHaveEffects.add(PotionEffectType.REGENERATION);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6000, (int) ability.getRegenAmount() - 1, true, false));
                        break;
                        
                    case INVISIBILITY_PASSIVE:
                        shouldHaveEffects.add(PotionEffectType.INVISIBILITY);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 0, true, false));
                        break;
                        
                    case HASTE:
                        shouldHaveEffects.add(PotionEffectType.HASTE);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 6000, ability.getHasteLevel() - 1, true, false));
                        break;
                        
                    case JUMP_BOOST:
                        shouldHaveEffects.add(PotionEffectType.JUMP_BOOST);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 6000, ability.getJumpBoostLevel() - 1, true, false));
                        break;
                        
                    case LUCK_BOOST:
                        shouldHaveEffects.add(PotionEffectType.LUCK);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 6000, ability.getLuckLevel() - 1, true, false));
                        break;
                        
                    case CONDUIT_POWER:
                        shouldHaveEffects.add(PotionEffectType.CONDUIT_POWER);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 6000, 0, true, false));
                        break;
                        
                    case DOLPHIN_GRACE:
                        shouldHaveEffects.add(PotionEffectType.DOLPHINS_GRACE);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 6000, 0, true, false));
                        break;
                        
                    case SOUL_SPEED:
                        PotionEffectType soulSpeed = PotionEffectType.getByName("SOUL_SPEED");
                        if (soulSpeed != null) {
                            shouldHaveEffects.add(soulSpeed);
                            player.addPotionEffect(new PotionEffect(soulSpeed, 6000, ability.getSoulSpeedLevel() - 1, true, false));
                        }
                        break;
                        
                    case THORNS_PASSIVE:
                        PotionEffectType thorns = PotionEffectType.getByName("THORNS");
                        if (thorns != null) {
                            shouldHaveEffects.add(thorns);
                            player.addPotionEffect(new PotionEffect(thorns, 6000, ability.getThornsLevel() - 1, true, false));
                        }
                        break;
                        
                    case KNOCKBACK_RESISTANCE:
                        PotionEffectType kbRes = PotionEffectType.getByName("KNOCKBACK_RESISTANCE");
                        if (kbRes != null) {
                            shouldHaveEffects.add(kbRes);
                            player.addPotionEffect(new PotionEffect(kbRes, 6000, ability.getKnockbackResistanceLevel() - 1, true, false));
                        }
                        break;
                        
                    case PROTECTION:
                        PotionEffectType resistance = PotionEffectType.getByName("DAMAGE_RESISTANCE");
                        if (resistance != null) {
                            shouldHaveEffects.add(resistance);
                            player.addPotionEffect(new PotionEffect(resistance, 6000, ability.getProtectionLevel() - 1, true, false));
                        }
                        break;
                        
                    case FROST_WALKER:
                        PotionEffectType frostWalker = PotionEffectType.getByName("FROST_WALKER");
                        if (frostWalker != null) {
                            shouldHaveEffects.add(frostWalker);
                            player.addPotionEffect(new PotionEffect(frostWalker, 6000, ability.getFrostWalkerLevel() - 1, true, false));
                        }
                        break;
                        
                    case GLOWING:
                        PotionEffectType glowing = PotionEffectType.getByName("GLOWING");
                        if (glowing != null) {
                            shouldHaveEffects.add(glowing);
                            player.addPotionEffect(new PotionEffect(glowing, 6000, 0, true, false));
                        }
                        break;
                        
                    case DEPTH_STRIDER:
                        PotionEffectType depthStrider = PotionEffectType.getByName("DEPTH_STRIDER");
                        if (depthStrider != null) {
                            shouldHaveEffects.add(depthStrider);
                            player.addPotionEffect(new PotionEffect(depthStrider, 6000, ability.getDepthStriderLevel() - 1, true, false));
                        }
                        break;
                        
                    case ANTI_FALL:
                        shouldHaveEffects.add(PotionEffectType.SLOW_FALLING);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6000, 0, true, false));
                        break;
                        
                    case NO_HUNGER:
                        shouldHaveEffects.add(PotionEffectType.SATURATION);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 6000, 10, true, false));
                        break;
                        
                    case LEVITATION:
                        PotionEffectType levitation = PotionEffectType.getByName("LEVITATION");
                        if (levitation != null) {
                            shouldHaveEffects.add(levitation);
                            player.addPotionEffect(new PotionEffect(levitation, 6000, 0, true, false));
                        }
                        break;
                        
                    case HERO_OF_THE_VILLAGE:
                        PotionEffectType hero = PotionEffectType.getByName("HERO_OF_THE_VILLAGE");
                        if (hero != null) {
                            shouldHaveEffects.add(hero);
                            player.addPotionEffect(new PotionEffect(hero, 6000, 0, true, false));
                        }
                        break;
                        
                    default:
                        break;
                }
            }
        }
        
        Set<PotionEffectType> toRemove = new HashSet<>();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            PotionEffectType type = effect.getType();
            if (!shouldHaveEffects.contains(type)) {
                toRemove.add(type);
            }
        }
        for (PotionEffectType type : toRemove) {
            player.removePotionEffect(type);
        }
        
        UUID uuid = player.getUniqueId();
        if (hasMagneticNow) {
            hasMagnetic.add(uuid);
            magneticRanges.put(uuid, maxMagneticRange);
        } else {
            hasMagnetic.remove(uuid);
            magneticRanges.remove(uuid);
        }
    }

    public void processAttackEffects(Player player, Entity target, EntityDamageByEntityEvent event) {
        List<Artifact> activeArtifacts = getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.AttackEffect attackEffect : artifact.getAttackEffects()) {
                if (Math.random() > attackEffect.getChance()) continue;
                
                switch (attackEffect.getType()) {
                    case POTION:
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).addPotionEffect(
                                new PotionEffect(
                                    attackEffect.getPotionType(),
                                    attackEffect.getPotionDuration(),
                                    attackEffect.getPotionLevel()
                                )
                            );
                        }
                        break;
                        
                    case IGNITE:
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).setFireTicks(attackEffect.getIgniteDuration());
                        }
                        break;
                        
                    case KNOCKBACK:
                        Vector direction = player.getLocation().getDirection().normalize();
                        direction.setY(0.3);
                        target.setVelocity(direction.multiply(attackEffect.getKnockbackStrength()));
                        break;
                        
                    case LIFESTEAL:
                        double healAmount = event.getFinalDamage() * (attackEffect.getLifestealPercent() / 100.0);
                        double newHealth = Math.min(player.getHealth() + healAmount, player.getMaxHealth());
                        player.setHealth(newHealth);
                        break;
                        
                    case EXECUTE:
                        if (target instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) target;
                            double healthPercent = living.getHealth() / living.getMaxHealth();
                            if (healthPercent <= attackEffect.getExecuteThreshold()) {
                                event.setDamage(event.getDamage() * attackEffect.getExecuteMultiplier());
                            }
                        }
                        break;
                        
                    case POISON:
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).addPotionEffect(
                                new PotionEffect(PotionEffectType.POISON, attackEffect.getPoisonDuration(), attackEffect.getPoisonDamage())
                            );
                        }
                        break;
                        
                    case CRITICAL:
                        if (Math.random() < attackEffect.getCritChanceBonus()) {
                            event.setDamage(event.getDamage() * attackEffect.getCritDamageMultiplier());
                        }
                        break;
                        
                    case LIGHTNING:
                        target.getWorld().strikeLightning(target.getLocation());
                        break;
                        
                    case SUMMON:
                        try {
                            String entityType = attackEffect.getSummonEntity();
                            EntityType type = EntityType.valueOf(entityType);
                            for (int i = 0; i < attackEffect.getSummonCount(); i++) {
                                target.getWorld().spawnEntity(target.getLocation(), type);
                            }
                        } catch (Exception ignored) {}
                        break;
                        
                    case EXPLOSION:
                        target.getWorld().createExplosion(
                            target.getLocation(),
                            (float) attackEffect.getExplosionPower(),
                            attackEffect.isExplosionFire(),
                            false,
                            player
                        );
                        break;
                        
                    case PARTICLE_EFFECT:
                        try {
                            Particle particle = Particle.valueOf(attackEffect.getParticleType().toUpperCase());
                            target.getWorld().spawnParticle(particle, target.getLocation(), attackEffect.getParticleCount());
                        } catch (Exception ignored) {}
                        break;
                        
                    case SATURATION:
                        player.setSaturation((float) (player.getSaturation() + attackEffect.getSaturationAmount()));
                        break;
                        
                    case SLOW:
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).addPotionEffect(
                                new PotionEffect(PotionEffectType.SLOWNESS, attackEffect.getPotionDuration(), attackEffect.getSlowLevel())
                            );
                        }
                        break;
                        
                    case WEAKNESS:
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).addPotionEffect(
                                new PotionEffect(PotionEffectType.WEAKNESS, attackEffect.getPotionDuration(), attackEffect.getPotionLevel())
                            );
                        }
                        break;
                        
                    case BLIND:
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).addPotionEffect(
                                new PotionEffect(PotionEffectType.BLINDNESS, attackEffect.getPotionDuration(), 0)
                            );
                        }
                        break;
                        
                    case THIEF:
                        if (target instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) target;
                            if (living.getEquipment() != null) {
                                ItemStack hand = living.getEquipment().getItemInMainHand();
                                if (hand != null && hand.getType() != Material.AIR) {
                                    player.getInventory().addItem(hand.clone());
                                    living.getEquipment().setItemInMainHand(null);
                                }
                            }
                        }
                        break;
                        
                    default:
                        break;
                }
            }
        }
    }

    public void processDefenseEffects(Player player, Entity attacker, double damage, EntityDamageEvent event) {
        List<Artifact> activeArtifacts = getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.DefenseEffect defenseEffect : artifact.getDefenseEffects()) {
                if (Math.random() > defenseEffect.getChance()) continue;
                
                switch (defenseEffect.getType()) {
                    case REFLECT:
                        if (attacker instanceof LivingEntity) {
                            double reflectDamage = damage * (defenseEffect.getReflectPercent() / 100.0);
                            ((LivingEntity) attacker).damage(reflectDamage);
                        }
                        break;
                        
                    case ABSORB:
                        double absorbedDamage = damage * (defenseEffect.getAbsorbPercent() / 100.0);
                        event.setDamage(event.getDamage() - absorbedDamage);
                        break;
                        
                    case DODGE:
                        if (Math.random() < defenseEffect.getDodgeChance()) {
                            event.setCancelled(true);
                            return;
                        }
                        break;
                        
                    case THORNS:
                        if (attacker instanceof LivingEntity) {
                            ((LivingEntity) attacker).damage(defenseEffect.getThornsDamage());
                        }
                        break;
                        
                    case KNOCKBACK:
                        if (attacker != null) {
                            Vector direction = attacker.getLocation().toVector()
                                .subtract(player.getLocation().toVector()).normalize();
                            direction.setY(0.5);
                            attacker.setVelocity(direction.multiply(defenseEffect.getKnockbackStrength()));
                        }
                        break;
                        
                    case POTION_TO_ATTACKER:
                        if (attacker instanceof LivingEntity) {
                            ((LivingEntity) attacker).addPotionEffect(
                                new PotionEffect(
                                    defenseEffect.getPotionType(),
                                    defenseEffect.getPotionDuration(),
                                    defenseEffect.getPotionLevel()
                                )
                            );
                        }
                        break;
                        
                    case ABSORPTION:
                        player.addPotionEffect(
                            new PotionEffect(
                                PotionEffectType.ABSORPTION,
                                defenseEffect.getAbsorptionDuration(),
                                (int) defenseEffect.getAbsorptionHearts() - 1
                            )
                        );
                        break;
                        
                    case COUNTER:
                        if (attacker instanceof LivingEntity) {
                            ((LivingEntity) attacker).damage(defenseEffect.getCounterDamage());
                        }
                        break;
                        
                    case INVULNERABILITY:
                        player.setNoDamageTicks(defenseEffect.getInvulnerabilityTicks());
                        break;
                        
                    case EXPLOSION:
                        player.getWorld().createExplosion(
                            player.getLocation(),
                            (float) defenseEffect.getExplosionPower(),
                            defenseEffect.isExplosionFire(),
                            false,
                            player
                        );
                        break;
                        
                    case LEVITATE:
                        if (attacker instanceof LivingEntity) {
                            ((LivingEntity) attacker).addPotionEffect(
                                new PotionEffect(PotionEffectType.LEVITATION, defenseEffect.getLevitateDuration(), defenseEffect.getLevitateStrength())
                            );
                        }
                        break;
                        
                    case VAMPIRIC:
                        double heal = damage * (defenseEffect.getVampiricPercent() / 100.0);
                        player.setHealth(Math.min(player.getHealth() + heal, player.getMaxHealth()));
                        break;
                        
                    case REGENERATE_ON_HIT:
                        player.setHealth(Math.min(player.getHealth() + defenseEffect.getRegenAmount(), player.getMaxHealth()));
                        break;
                        
                    default:
                        break;
                }
            }
        }
    }

    public boolean isFallDamageDisabled(Player player) {
        List<Artifact> activeArtifacts = getActiveArtifacts(player);
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.PassiveAbility ability : artifact.getPassiveAbilities()) {
                if (ability.getType() == Artifact.PassiveAbilityType.ANTI_FALL) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean processXPBoost(Player player, int originalXP) {
        List<Artifact> activeArtifacts = getActiveArtifacts(player);
        double maxMultiplier = 1.0;
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.PassiveAbility ability : artifact.getPassiveAbilities()) {
                if (ability.getType() == Artifact.PassiveAbilityType.XP_BOOST) {
                    maxMultiplier = Math.max(maxMultiplier, ability.getXpMultiplier());
                }
            }
        }
        
        if (maxMultiplier > 1.0) {
            int bonusXP = (int) (originalXP * (maxMultiplier - 1.0));
            player.giveExp(bonusXP);
            return true;
        }
        return false;
    }

    public void processKillEffects(Player player, Entity entity) {
        UUID uuid = player.getUniqueId();
        
        long currentTime = System.currentTimeMillis();
        long lastKill = System.currentTimeMillis();
        
        if (currentTime - lastKill <= 5000) {
            // streak logic
        }
        
        List<Artifact> activeArtifacts = getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.SpecialEffect effect : artifact.getSpecialEffects()) {
                if (effect.getType() == Artifact.SpecialEffectType.KILL_EFFECTS) {
                    applyEffectList(player, effect.getKillEffects());
                }
            }
        }
    }

    private void applyEffectList(Player player, List<String> effects) {
        if (effects == null) return;
        
        for (String effectStr : effects) {
            String[] parts = effectStr.split(":");
            if (parts.length >= 2) {
                PotionEffectType type = PotionEffectType.getByName(parts[0]);
                int level = Integer.parseInt(parts[1]) - 1;
                int duration = parts.length > 2 ? Integer.parseInt(parts[2]) : 60;
                
                if (type != null) {
                    player.addPotionEffect(new PotionEffect(type, duration, level));
                }
            }
        }
    }

    public void cleanup() {
        hasMagnetic.clear();
        magneticRanges.clear();
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    updatePlayerEffects(player);
                }
            }
        }.runTaskTimer(plugin, 5L, 5L);
    }
}
