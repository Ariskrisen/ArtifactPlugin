package me.luka.artifactplugin.models;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Artifact {

    private final String id;
    private Material material;
    private String namespacedItem;
    private String name;
    private List<String> lore;
    private boolean enabled;
    private int customModelData;
    
    private List<PotionEffectConfig> potionEffects;
    private List<AttackEffect> attackEffects;
    private List<DefenseEffect> defenseEffects;
    private List<PassiveAbility> passiveAbilities;
    private List<TickEffect> tickEffects;
    private List<SpecialEffect> specialEffects;

    public Artifact(String id) {
        this.id = id;
        this.lore = new ArrayList<>();
        this.enabled = true;
        this.customModelData = 0;
        this.potionEffects = new ArrayList<>();
        this.attackEffects = new ArrayList<>();
        this.defenseEffects = new ArrayList<>();
        this.passiveAbilities = new ArrayList<>();
        this.tickEffects = new ArrayList<>();
        this.specialEffects = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getNamespacedItem() {
        return namespacedItem;
    }

    public void setNamespacedItem(String namespacedItem) {
        this.namespacedItem = namespacedItem;
    }

    public boolean hasNamespacedItem() {
        return namespacedItem != null && !namespacedItem.isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public List<PotionEffectConfig> getPotionEffects() {
        return potionEffects;
    }

    public void addPotionEffect(PotionEffectConfig effect) {
        this.potionEffects.add(effect);
    }

    public List<AttackEffect> getAttackEffects() {
        return attackEffects;
    }

    public void addAttackEffect(AttackEffect effect) {
        this.attackEffects.add(effect);
    }

    public List<DefenseEffect> getDefenseEffects() {
        return defenseEffects;
    }

    public void addDefenseEffect(DefenseEffect effect) {
        this.defenseEffects.add(effect);
    }

    public List<PassiveAbility> getPassiveAbilities() {
        return passiveAbilities;
    }

    public void addPassiveAbility(PassiveAbility ability) {
        this.passiveAbilities.add(ability);
    }

    public List<TickEffect> getTickEffects() {
        return tickEffects;
    }

    public void addTickEffect(TickEffect effect) {
        this.tickEffects.add(effect);
    }

    public List<SpecialEffect> getSpecialEffects() {
        return specialEffects;
    }

    public void addSpecialEffect(SpecialEffect effect) {
        this.specialEffects.add(effect);
    }

    public boolean hasAnyEffect() {
        return !potionEffects.isEmpty() || 
               !attackEffects.isEmpty() || 
               !defenseEffects.isEmpty() || 
               !passiveAbilities.isEmpty() || 
               !tickEffects.isEmpty() || 
               !specialEffects.isEmpty();
    }

    public static class PotionEffectConfig {
        private PotionEffectType effectType;
        private int level;
        private boolean ambient;
        private boolean particles;

        public PotionEffectConfig(PotionEffectType effectType, int level, boolean ambient, boolean particles) {
            this.effectType = effectType;
            this.level = level;
            this.ambient = ambient;
            this.particles = particles;
        }

        public PotionEffectType getEffectType() {
            return effectType;
        }

        public int getLevel() {
            return level;
        }

        public boolean isAmbient() {
            return ambient;
        }

        public boolean isParticles() {
            return particles;
        }

        public PotionEffect toPotionEffect() {
            return new PotionEffect(effectType, Integer.MAX_VALUE, level, ambient, particles);
        }
    }

    public static class AttackEffect {
        private AttackEffectType type;
        private double chance;
        
        // Common
        private PotionEffectType potionType;
        private int potionLevel;
        private int potionDuration;
        
        // Damage
        private double damageAmplifier;
        
        // Ignite
        private int igniteDuration;
        
        // Knockback
        private double knockbackStrength;
        
        // Lifesteal
        private double lifestealPercent;
        
        // Execute
        private double executeThreshold;
        private double executeMultiplier;
        
        // Poison
        private int poisonDamage;
        private int poisonDuration;
        
        // Critical
        private double critChanceBonus;
        private double critDamageMultiplier;
        
        // Projectile
        private String projectileEntity;
        private double projectileVelocity;
        
        // Summon
        private String summonEntity;
        private int summonCount;
        
        // Explosion
        private double explosionPower;
        private boolean explosionFire;
        private boolean explosionBreakBlocks;
        
        // Lightning
        private int lightningDamage;
        
        // Particle
        private String particleType;
        private int particleCount;
        
        // Command
        private String command;
        
        // Saturation
        private double saturationAmount;
        
        // Slow
        private int slowLevel;
        
        // Thief
        private int thiefAmount;

        public AttackEffectType getType() {
            return type;
        }

        public void setType(AttackEffectType type) {
            this.type = type;
        }

        public double getChance() {
            return chance;
        }

        public void setChance(double chance) {
            this.chance = chance;
        }

        public PotionEffectType getPotionType() {
            return potionType;
        }

        public void setPotionType(PotionEffectType potionType) {
            this.potionType = potionType;
        }

        public int getPotionLevel() {
            return potionLevel;
        }

        public void setPotionLevel(int potionLevel) {
            this.potionLevel = potionLevel;
        }

        public int getPotionDuration() {
            return potionDuration;
        }

        public void setPotionDuration(int potionDuration) {
            this.potionDuration = potionDuration;
        }

        public double getDamageAmplifier() {
            return damageAmplifier;
        }

        public void setDamageAmplifier(double damageAmplifier) {
            this.damageAmplifier = damageAmplifier;
        }

        public int getIgniteDuration() {
            return igniteDuration;
        }

        public void setIgniteDuration(int igniteDuration) {
            this.igniteDuration = igniteDuration;
        }

        public double getKnockbackStrength() {
            return knockbackStrength;
        }

        public void setKnockbackStrength(double knockockbackStrength) {
            this.knockbackStrength = knockockbackStrength;
        }

        public double getLifestealPercent() {
            return lifestealPercent;
        }

        public void setLifestealPercent(double lifestealPercent) {
            this.lifestealPercent = lifestealPercent;
        }

        public double getExecuteThreshold() {
            return executeThreshold;
        }

        public void setExecuteThreshold(double executeThreshold) {
            this.executeThreshold = executeThreshold;
        }

        public double getExecuteMultiplier() {
            return executeMultiplier;
        }

        public void setExecuteMultiplier(double executeMultiplier) {
            this.executeMultiplier = executeMultiplier;
        }

        public int getPoisonDamage() {
            return poisonDamage;
        }

        public void setPoisonDamage(int poisonDamage) {
            this.poisonDamage = poisonDamage;
        }

        public int getPoisonDuration() {
            return poisonDuration;
        }

        public void setPoisonDuration(int poisonDuration) {
            this.poisonDuration = poisonDuration;
        }

        public double getCritChanceBonus() {
            return critChanceBonus;
        }

        public void setCritChanceBonus(double critChanceBonus) {
            this.critChanceBonus = critChanceBonus;
        }

        public double getCritDamageMultiplier() {
            return critDamageMultiplier;
        }

        public void setCritDamageMultiplier(double critDamageMultiplier) {
            this.critDamageMultiplier = critDamageMultiplier;
        }

        public String getProjectileEntity() {
            return projectileEntity;
        }

        public void setProjectileEntity(String projectileEntity) {
            this.projectileEntity = projectileEntity;
        }

        public double getProjectileVelocity() {
            return projectileVelocity;
        }

        public void setProjectileVelocity(double projectileVelocity) {
            this.projectileVelocity = projectileVelocity;
        }

        public String getSummonEntity() {
            return summonEntity;
        }

        public void setSummonEntity(String summonEntity) {
            this.summonEntity = summonEntity;
        }

        public int getSummonCount() {
            return summonCount;
        }

        public void setSummonCount(int summonCount) {
            this.summonCount = summonCount;
        }

        public double getExplosionPower() {
            return explosionPower;
        }

        public void setExplosionPower(double explosionPower) {
            this.explosionPower = explosionPower;
        }

        public boolean isExplosionFire() {
            return explosionFire;
        }

        public void setExplosionFire(boolean explosionFire) {
            this.explosionFire = explosionFire;
        }

        public boolean isExplosionBreakBlocks() {
            return explosionBreakBlocks;
        }

        public void setExplosionBreakBlocks(boolean explosionBreakBlocks) {
            this.explosionBreakBlocks = explosionBreakBlocks;
        }

        public int getLightningDamage() {
            return lightningDamage;
        }

        public void setLightningDamage(int lightningDamage) {
            this.lightningDamage = lightningDamage;
        }

        public String getParticleType() {
            return particleType;
        }

        public void setParticleType(String particleType) {
            this.particleType = particleType;
        }

        public int getParticleCount() {
            return particleCount;
        }

        public void setParticleCount(int particleCount) {
            this.particleCount = particleCount;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public double getSaturationAmount() {
            return saturationAmount;
        }

        public void setSaturationAmount(double saturationAmount) {
            this.saturationAmount = saturationAmount;
        }

        public int getSlowLevel() {
            return slowLevel;
        }

        public void setSlowLevel(int slowLevel) {
            this.slowLevel = slowLevel;
        }

        public int getThiefAmount() {
            return thiefAmount;
        }

        public void setThiefAmount(int thiefAmount) {
            this.thiefAmount = thiefAmount;
        }
    }

    public enum AttackEffectType {
        POTION, DAMAGE, IGNITE, KNOCKBACK, LIFESTEAL, EXECUTE, POISON,
        CRITICAL, BLIND, SLOW, WEAKNESS, HUNGER, PROJECTILE, SUMMON,
        EXPLOSION, LIGHTNING, PARTICLE_EFFECT, DISPEL, THIEF, COMMAND, SATURATION, REGENERATION_BURST
    }

    public static class DefenseEffect {
        private DefenseEffectType type;
        private double chance;
        
        // Reflect
        private double reflectPercent;
        
        // Absorb
        private double absorbPercent;
        
        // Block
        private double blockChance;
        
        // Thorns
        private int thornsDamage;
        
        // Knockback
        private double knockbackStrength;
        
        // Potion to attacker
        private PotionEffectType potionType;
        private int potionLevel;
        private int potionDuration;
        
        // Absorption
        private double absorptionHearts;
        private int absorptionDuration;
        
        // Dodge
        private double dodgeChance;
        
        // Counter
        private double counterDamage;
        
        // Invulnerability
        private int invulnerabilityTicks;
        
        // Explosion
        private double explosionPower;
        private boolean explosionFire;
        
        // Resistance
        private PotionEffectType resistanceType;
        private int resistanceLevel;
        private int resistanceDuration;
        
        // Regen on hit
        private double regenAmount;
        
        // Levitate
        private int levitateDuration;
        private int levitateStrength;
        
        // Bleed
        private int bleedDamage;
        private int bleedDuration;
        
        // Vampiric
        private double vampiricPercent;
        
        // Shield
        private double shieldPoints;
        private int shieldRechargeTime;

        public DefenseEffectType getType() {
            return type;
        }

        public void setType(DefenseEffectType type) {
            this.type = type;
        }

        public double getChance() {
            return chance;
        }

        public void setChance(double chance) {
            this.chance = chance;
        }

        public double getReflectPercent() {
            return reflectPercent;
        }

        public void setReflectPercent(double reflectPercent) {
            this.reflectPercent = reflectPercent;
        }

        public double getAbsorbPercent() {
            return absorbPercent;
        }

        public void setAbsorbPercent(double absorbPercent) {
            this.absorbPercent = absorbPercent;
        }

        public double getBlockChance() {
            return blockChance;
        }

        public void setBlockChance(double blockChance) {
            this.blockChance = blockChance;
        }

        public int getThornsDamage() {
            return thornsDamage;
        }

        public void setThornsDamage(int thornsDamage) {
            this.thornsDamage = thornsDamage;
        }

        public double getKnockbackStrength() {
            return knockbackStrength;
        }

        public void setKnockbackStrength(double knockbackStrength) {
            this.knockbackStrength = knockbackStrength;
        }

        public PotionEffectType getPotionType() {
            return potionType;
        }

        public void setPotionType(PotionEffectType potionType) {
            this.potionType = potionType;
        }

        public int getPotionLevel() {
            return potionLevel;
        }

        public void setPotionLevel(int potionLevel) {
            this.potionLevel = potionLevel;
        }

        public int getPotionDuration() {
            return potionDuration;
        }

        public void setPotionDuration(int potionDuration) {
            this.potionDuration = potionDuration;
        }

        public double getAbsorptionHearts() {
            return absorptionHearts;
        }

        public void setAbsorptionHearts(double absorptionHearts) {
            this.absorptionHearts = absorptionHearts;
        }

        public int getAbsorptionDuration() {
            return absorptionDuration;
        }

        public void setAbsorptionDuration(int absorptionDuration) {
            this.absorptionDuration = absorptionDuration;
        }

        public double getDodgeChance() {
            return dodgeChance;
        }

        public void setDodgeChance(double dodgeChance) {
            this.dodgeChance = dodgeChance;
        }

        public double getCounterDamage() {
            return counterDamage;
        }

        public void setCounterDamage(double counterDamage) {
            this.counterDamage = counterDamage;
        }

        public int getInvulnerabilityTicks() {
            return invulnerabilityTicks;
        }

        public void setInvulnerabilityTicks(int invulnerabilityTicks) {
            this.invulnerabilityTicks = invulnerabilityTicks;
        }

        public double getExplosionPower() {
            return explosionPower;
        }

        public void setExplosionPower(double explosionPower) {
            this.explosionPower = explosionPower;
        }

        public boolean isExplosionFire() {
            return explosionFire;
        }

        public void setExplosionFire(boolean explosionFire) {
            this.explosionFire = explosionFire;
        }

        public PotionEffectType getResistanceType() {
            return resistanceType;
        }

        public void setResistanceType(PotionEffectType resistanceType) {
            this.resistanceType = resistanceType;
        }

        public int getResistanceLevel() {
            return resistanceLevel;
        }

        public void setResistanceLevel(int resistanceLevel) {
            this.resistanceLevel = resistanceLevel;
        }

        public int getResistanceDuration() {
            return resistanceDuration;
        }

        public void setResistanceDuration(int resistanceDuration) {
            this.resistanceDuration = resistanceDuration;
        }

        public double getRegenAmount() {
            return regenAmount;
        }

        public void setRegenAmount(double regenAmount) {
            this.regenAmount = regenAmount;
        }

        public int getLevitateDuration() {
            return levitateDuration;
        }

        public void setLevitateDuration(int levitateDuration) {
            this.levitateDuration = levitateDuration;
        }

        public int getLevitateStrength() {
            return levitateStrength;
        }

        public void setLevitateStrength(int levitateStrength) {
            this.levitateStrength = levitateStrength;
        }

        public int getBleedDamage() {
            return bleedDamage;
        }

        public void setBleedDamage(int bleedDamage) {
            this.bleedDamage = bleedDamage;
        }

        public int getBleedDuration() {
            return bleedDuration;
        }

        public void setBleedDuration(int bleedDuration) {
            this.bleedDuration = bleedDuration;
        }

        public double getVampiricPercent() {
            return vampiricPercent;
        }

        public void setVampiricPercent(double vampiricPercent) {
            this.vampiricPercent = vampiricPercent;
        }

        public double getShieldPoints() {
            return shieldPoints;
        }

        public void setShieldPoints(double shieldPoints) {
            this.shieldPoints = shieldPoints;
        }

        public int getShieldRechargeTime() {
            return shieldRechargeTime;
        }

        public void setShieldRechargeTime(int shieldRechargeTime) {
            this.shieldRechargeTime = shieldRechargeTime;
        }
    }

    public enum DefenseEffectType {
        REFLECT, ABSORB, BLOCK, THORNS, KNOCKBACK, POTION_TO_ATTACKER, ABSORPTION,
        DODGE, COUNTER, INVULNERABILITY, EXPLOSION, TEMPORARY_RESISTANCE, REGENERATE_ON_HIT,
        PARTICLE_EFFECT, LEVITATE, BLEED, VAMPIRIC, SHIELD
    }

    public static class PassiveAbility {
        private PassiveAbilityType type;
        
        // Double jump
        private double doubleJumpBoost;
        private int doubleJumpCooldown;
        
        // Sprint jump
        private double sprintJumpSpeedBoost;
        private int sprintJumpDuration;
        
        // Regen passive
        private double regenAmount;
        private int regenInterval;
        
        // Rage
        private double rageThreshold;
        private int rageStrengthLevel;
        
        // Adrenaline
        private double adrenalineThreshold;
        private int adrenalineSpeedLevel;
        
        // XP boost
        private double xpMultiplier;
        
        // Luck boost
        private int luckLevel;
        
        // Flight
        private double flightSpeed;
        
        // Haste
        private int hasteLevel;
        
        // Ender pearl CD
        private int enderPearlCDReduction;
        
        // Jump boost
        private int jumpBoostLevel;
        
        // Soul speed
        private int soulSpeedLevel;
        
        // Depth strider
        private int depthStriderLevel;
        
        // Frost walker
        private int frostWalkerLevel;
        
        // Step assist
        private double stepAssistHeight;
        
        // Auto repair
        private int autoRepairAmount;
        private int autoRepairInterval;
        
        // Unbreaking bonus
        private int unbreakingLevel;
        
        // Protection types
        private int protectionLevel;
        private int fireProtectionLevel;
        private int blastProtectionLevel;
        private int projectileProtectionLevel;
        private int featherFallingLevel;
        private int thornsLevel;
        private int knockbackResistanceLevel;
        
        // Loot bonus
        private double lootBonusChance;
        
        // Fishing luck
        private int fishingLuckLevel;
        
        // Damage vs entities
        private List<String> damageVsEntities;
        private double damageVsBonus;
        
        // Fast place
        private int fastPlaceSpeed;
        
        // Arrow effects
        private PotionEffectType arrowEffectType;
        private int arrowEffectLevel;
        private int arrowEffectDuration;
        
        // Magnetic
        private double magneticRange;
        
        // Tracking compass
        private String trackingEntity;
        
        // Reduce damage types
        private double witherReduction;
        private double magicReduction;

        public PassiveAbilityType getType() {
            return type;
        }

        public void setType(PassiveAbilityType type) {
            this.type = type;
        }

        public double getDoubleJumpBoost() {
            return doubleJumpBoost;
        }

        public void setDoubleJumpBoost(double doubleJumpBoost) {
            this.doubleJumpBoost = doubleJumpBoost;
        }

        public int getDoubleJumpCooldown() {
            return doubleJumpCooldown;
        }

        public void setDoubleJumpCooldown(int doubleJumpCooldown) {
            this.doubleJumpCooldown = doubleJumpCooldown;
        }

        public double getSprintJumpSpeedBoost() {
            return sprintJumpSpeedBoost;
        }

        public void setSprintJumpSpeedBoost(double sprintJumpSpeedBoost) {
            this.sprintJumpSpeedBoost = sprintJumpSpeedBoost;
        }

        public int getSprintJumpDuration() {
            return sprintJumpDuration;
        }

        public void setSprintJumpDuration(int sprintJumpDuration) {
            this.sprintJumpDuration = sprintJumpDuration;
        }

        public double getRegenAmount() {
            return regenAmount;
        }

        public void setRegenAmount(double regenAmount) {
            this.regenAmount = regenAmount;
        }

        public int getRegenInterval() {
            return regenInterval;
        }

        public void setRegenInterval(int regenInterval) {
            this.regenInterval = regenInterval;
        }

        public double getRageThreshold() {
            return rageThreshold;
        }

        public void setRageThreshold(double rageThreshold) {
            this.rageThreshold = rageThreshold;
        }

        public int getRageStrengthLevel() {
            return rageStrengthLevel;
        }

        public void setRageStrengthLevel(int rageStrengthLevel) {
            this.rageStrengthLevel = rageStrengthLevel;
        }

        public double getAdrenalineThreshold() {
            return adrenalineThreshold;
        }

        public void setAdrenalineThreshold(double adrenalineThreshold) {
            this.adrenalineThreshold = adrenalineThreshold;
        }

        public int getAdrenalineSpeedLevel() {
            return adrenalineSpeedLevel;
        }

        public void setAdrenalineSpeedLevel(int adrenalineSpeedLevel) {
            this.adrenalineSpeedLevel = adrenalineSpeedLevel;
        }

        public double getXpMultiplier() {
            return xpMultiplier;
        }

        public void setXpMultiplier(double xpMultiplier) {
            this.xpMultiplier = xpMultiplier;
        }

        public int getLuckLevel() {
            return luckLevel;
        }

        public void setLuckLevel(int luckLevel) {
            this.luckLevel = luckLevel;
        }

        public double getFlightSpeed() {
            return flightSpeed;
        }

        public void setFlightSpeed(double flightSpeed) {
            this.flightSpeed = flightSpeed;
        }

        public int getHasteLevel() {
            return hasteLevel;
        }

        public void setHasteLevel(int hasteLevel) {
            this.hasteLevel = hasteLevel;
        }

        public int getEnderPearlCDReduction() {
            return enderPearlCDReduction;
        }

        public void setEnderPearlCDReduction(int enderPearlCDReduction) {
            this.enderPearlCDReduction = enderPearlCDReduction;
        }

        public int getJumpBoostLevel() {
            return jumpBoostLevel;
        }

        public void setJumpBoostLevel(int jumpBoostLevel) {
            this.jumpBoostLevel = jumpBoostLevel;
        }

        public int getSoulSpeedLevel() {
            return soulSpeedLevel;
        }

        public void setSoulSpeedLevel(int soulSpeedLevel) {
            this.soulSpeedLevel = soulSpeedLevel;
        }

        public int getDepthStriderLevel() {
            return depthStriderLevel;
        }

        public void setDepthStriderLevel(int depthStriderLevel) {
            this.depthStriderLevel = depthStriderLevel;
        }

        public int getFrostWalkerLevel() {
            return frostWalkerLevel;
        }

        public void setFrostWalkerLevel(int frostWalkerLevel) {
            this.frostWalkerLevel = frostWalkerLevel;
        }

        public double getStepAssistHeight() {
            return stepAssistHeight;
        }

        public void setStepAssistHeight(double stepAssistHeight) {
            this.stepAssistHeight = stepAssistHeight;
        }

        public int getAutoRepairAmount() {
            return autoRepairAmount;
        }

        public void setAutoRepairAmount(int autoRepairAmount) {
            this.autoRepairAmount = autoRepairAmount;
        }

        public int getAutoRepairInterval() {
            return autoRepairInterval;
        }

        public void setAutoRepairInterval(int autoRepairInterval) {
            this.autoRepairInterval = autoRepairInterval;
        }

        public int getUnbreakingLevel() {
            return unbreakingLevel;
        }

        public void setUnbreakingLevel(int unbreakingLevel) {
            this.unbreakingLevel = unbreakingLevel;
        }

        public int getProtectionLevel() {
            return protectionLevel;
        }

        public void setProtectionLevel(int protectionLevel) {
            this.protectionLevel = protectionLevel;
        }

        public int getFireProtectionLevel() {
            return fireProtectionLevel;
        }

        public void setFireProtectionLevel(int fireProtectionLevel) {
            this.fireProtectionLevel = fireProtectionLevel;
        }

        public int getBlastProtectionLevel() {
            return blastProtectionLevel;
        }

        public void setBlastProtectionLevel(int blastProtectionLevel) {
            this.blastProtectionLevel = blastProtectionLevel;
        }

        public int getProjectileProtectionLevel() {
            return projectileProtectionLevel;
        }

        public void setProjectileProtectionLevel(int projectileProtectionLevel) {
            this.projectileProtectionLevel = projectileProtectionLevel;
        }

        public int getFeatherFallingLevel() {
            return featherFallingLevel;
        }

        public void setFeatherFallingLevel(int featherFallingLevel) {
            this.featherFallingLevel = featherFallingLevel;
        }

        public int getThornsLevel() {
            return thornsLevel;
        }

        public void setThornsLevel(int thornsLevel) {
            this.thornsLevel = thornsLevel;
        }

        public int getKnockbackResistanceLevel() {
            return knockbackResistanceLevel;
        }

        public void setKnockbackResistanceLevel(int knockbackResistanceLevel) {
            this.knockbackResistanceLevel = knockbackResistanceLevel;
        }

        public double getLootBonusChance() {
            return lootBonusChance;
        }

        public void setLootBonusChance(double lootBonusChance) {
            this.lootBonusChance = lootBonusChance;
        }

        public int getFishingLuckLevel() {
            return fishingLuckLevel;
        }

        public void setFishingLuckLevel(int fishingLuckLevel) {
            this.fishingLuckLevel = fishingLuckLevel;
        }

        public List<String> getDamageVsEntities() {
            return damageVsEntities;
        }

        public void setDamageVsEntities(List<String> damageVsEntities) {
            this.damageVsEntities = damageVsEntities;
        }

        public double getDamageVsBonus() {
            return damageVsBonus;
        }

        public void setDamageVsBonus(double damageVsBonus) {
            this.damageVsBonus = damageVsBonus;
        }

        public int getFastPlaceSpeed() {
            return fastPlaceSpeed;
        }

        public void setFastPlaceSpeed(int fastPlaceSpeed) {
            this.fastPlaceSpeed = fastPlaceSpeed;
        }

        public PotionEffectType getArrowEffectType() {
            return arrowEffectType;
        }

        public void setArrowEffectType(PotionEffectType arrowEffectType) {
            this.arrowEffectType = arrowEffectType;
        }

        public int getArrowEffectLevel() {
            return arrowEffectLevel;
        }

        public void setArrowEffectLevel(int arrowEffectLevel) {
            this.arrowEffectLevel = arrowEffectLevel;
        }

        public int getArrowEffectDuration() {
            return arrowEffectDuration;
        }

        public void setArrowEffectDuration(int arrowEffectDuration) {
            this.arrowEffectDuration = arrowEffectDuration;
        }

        public double getMagneticRange() {
            return magneticRange;
        }

        public void setMagneticRange(double magneticRange) {
            this.magneticRange = magneticRange;
        }

        public String getTrackingEntity() {
            return trackingEntity;
        }

        public void setTrackingEntity(String trackingEntity) {
            this.trackingEntity = trackingEntity;
        }

        public double getWitherReduction() {
            return witherReduction;
        }

        public void setWitherReduction(double witherReduction) {
            this.witherReduction = witherReduction;
        }

        public double getMagicReduction() {
            return magicReduction;
        }

        public void setMagicReduction(double magicReduction) {
            this.magicReduction = magicReduction;
        }
    }

    public enum PassiveAbilityType {
        ANTI_FALL, WATER_BREATHING, NIGHT_VISION, AUTO_SMELT, DOUBLE_JUMP, SPRINT_JUMP,
        REGENERATION_PASSIVE, RAGE, ADRENALINE, NO_HUNGER, XP_BOOST, LUCK_BOOST, FLIGHT,
        INVISIBILITY_PASSIVE, HASTE, ENDER_PEARL_CD, FIRE_RESISTANCE_PASSIVE, JUMP_BOOST,
        SLOW_FALLING, CONDUIT_POWER, DOLPHIN_GRACE, SOUL_SPEED, DEPTH_STRIDER, FROST_WALKER,
        STEP_ASSIST, AUTO_REPAIR, UNBREAKING_BONUS, PROJECTILE_PROTECTION, BLAST_PROTECTION,
        FIRE_PROTECTION, FEATHER_FALLING, PROTECTION, THORNS_PASSIVE, KNOCKBACK_RESISTANCE,
        LOOT_BONUS, FISHING_LUCK, AQUA_AFFINITY, RESPIRATION, DAMAGE_VS, FAST_PLACE,
        ARROW_EFFECTS, TNT_IGNITE, PICK_BLOCK, TRACKING_COMPASS, MAGNETIC, SNOWBALL_IMMUNE,
        ARROW_DEFLECT, CREEPER_IMMUNE, WITHER_REDUCTION, MAGIC_REDUCTION, GLOWING,
        LEVITATION, HERO_OF_THE_VILLAGE
    }

    public static class TickEffect {
        private TickEffectType type;
        private int interval;
        
        // Light
        private int lightRadius;
        
        // Particle cloud
        private String particleType;
        private double particleRadius;
        
        // Heal others
        private double healAmount;
        private int healRadius;
        
        // Hungerless
        private double hungerlessAmount;
        
        // Protect nearby
        private List<String> protectEntities;
        private int protectRadius;
        
        // Lightning
        private double lightningChance;
        
        // Summon
        private String summonEntity;
        private int summonCount;
        private int summonRadius;
        
        // Feed
        private double feedAmount;

        public TickEffectType getType() {
            return type;
        }

        public void setType(TickEffectType type) {
            this.type = type;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public int getLightRadius() {
            return lightRadius;
        }

        public void setLightRadius(int lightRadius) {
            this.lightRadius = lightRadius;
        }

        public String getParticleType() {
            return particleType;
        }

        public void setParticleType(String particleType) {
            this.particleType = particleType;
        }

        public double getParticleRadius() {
            return particleRadius;
        }

        public void setParticleRadius(double particleRadius) {
            this.particleRadius = particleRadius;
        }

        public double getHealAmount() {
            return healAmount;
        }

        public void setHealAmount(double healAmount) {
            this.healAmount = healAmount;
        }

        public int getHealRadius() {
            return healRadius;
        }

        public void setHealRadius(int healRadius) {
            this.healRadius = healRadius;
        }

        public double getHungerlessAmount() {
            return hungerlessAmount;
        }

        public void setHungerlessAmount(double hungerlessAmount) {
            this.hungerlessAmount = hungerlessAmount;
        }

        public List<String> getProtectEntities() {
            return protectEntities;
        }

        public void setProtectEntities(List<String> protectEntities) {
            this.protectEntities = protectEntities;
        }

        public int getProtectRadius() {
            return protectRadius;
        }

        public void setProtectRadius(int protectRadius) {
            this.protectRadius = protectRadius;
        }

        public double getLightningChance() {
            return lightningChance;
        }

        public void setLightningChance(double lightningChance) {
            this.lightningChance = lightningChance;
        }

        public String getSummonEntity() {
            return summonEntity;
        }

        public void setSummonEntity(String summonEntity) {
            this.summonEntity = summonEntity;
        }

        public int getSummonCount() {
            return summonCount;
        }

        public void setSummonCount(int summonCount) {
            this.summonCount = summonCount;
        }

        public int getSummonRadius() {
            return summonRadius;
        }

        public void setSummonRadius(int summonRadius) {
            this.summonRadius = summonRadius;
        }

        public double getFeedAmount() {
            return feedAmount;
        }

        public void setFeedAmount(double feedAmount) {
            this.feedAmount = feedAmount;
        }
    }

    public enum TickEffectType {
        LIGHT, PARTICLE_CLOUD, HEAL_OTHERS, HUNGER_LESS, PROTECT_NEARBY,
        LIGHTNING_STRIKE, SUMMON_MOB, FEED_PLAYER, INVISIBLE_HORSE, HORN_OF_CONQUEST, TOTEM_OF_UNDYING
    }

    public static class SpecialEffect {
        private SpecialEffectType type;
        
        // Respawn effects
        private List<String> respawnEffects;
        
        // Kill effects
        private List<String> killEffects;
        
        // Death prevent
        private boolean consumeOnDeathPrevent;
        
        // Death message
        private String deathMessage;
        
        // Kill streak
        private int killStreakKills;
        private List<String> killStreakEffects;
        
        // Equip particles
        private String equipParticle;
        
        // Sound
        private String activateSound;
        
        // Player only
        private List<String> allowedPlayers;
        
        // World only
        private List<String> allowedWorlds;

        public SpecialEffectType getType() {
            return type;
        }

        public void setType(SpecialEffectType type) {
            this.type = type;
        }

        public List<String> getRespawnEffects() {
            return respawnEffects;
        }

        public void setRespawnEffects(List<String> respawnEffects) {
            this.respawnEffects = respawnEffects;
        }

        public List<String> getKillEffects() {
            return killEffects;
        }

        public void setKillEffects(List<String> killEffects) {
            this.killEffects = killEffects;
        }

        public boolean isConsumeOnDeathPrevent() {
            return consumeOnDeathPrevent;
        }

        public void setConsumeOnDeathPrevent(boolean consumeOnDeathPrevent) {
            this.consumeOnDeathPrevent = consumeOnDeathPrevent;
        }

        public String getDeathMessage() {
            return deathMessage;
        }

        public void setDeathMessage(String deathMessage) {
            this.deathMessage = deathMessage;
        }

        public int getKillStreakKills() {
            return killStreakKills;
        }

        public void setKillStreakKills(int killStreakKills) {
            this.killStreakKills = killStreakKills;
        }

        public List<String> getKillStreakEffects() {
            return killStreakEffects;
        }

        public void setKillStreakEffects(List<String> killStreakEffects) {
            this.killStreakEffects = killStreakEffects;
        }

        public String getEquipParticle() {
            return equipParticle;
        }

        public void setEquipParticle(String equipParticle) {
            this.equipParticle = equipParticle;
        }

        public String getActivateSound() {
            return activateSound;
        }

        public void setActivateSound(String activateSound) {
            this.activateSound = activateSound;
        }

        public List<String> getAllowedPlayers() {
            return allowedPlayers;
        }

        public void setAllowedPlayers(List<String> allowedPlayers) {
            this.allowedPlayers = allowedPlayers;
        }

        public List<String> getAllowedWorlds() {
            return allowedWorlds;
        }

        public void setAllowedWorlds(List<String> allowedWorlds) {
            this.allowedWorlds = allowedWorlds;
        }
    }

    public enum SpecialEffectType {
        RESPAWN_EFFECTS, KILL_EFFECTS, DEATH_PREVENT, DEATH_MESSAGE, PVP_DISABLE,
        EQUIP_PARTICLES, ACTIVATE_SOUND, KILL_STREAK, PLAYER_ONLY, WORLD_ONLY, TOTEM_OF_UNDYING
    }
}
