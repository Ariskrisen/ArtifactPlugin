package me.luka.artifactplugin.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import me.luka.artifactplugin.ArtifactPlugin;

public class AnomalousBag {

    private final ArtifactPlugin plugin;
    private final int tier;
    private final int customModelData;
    private final String displayName;

    private static final NamespacedKey BAG_TIER_KEY = new NamespacedKey("artifactplugin", "bag_tier");

    public AnomalousBag(ArtifactPlugin plugin, int tier) {
        this.plugin = plugin;
        this.tier = tier;
        
        String[] romanNumerals = {"", "I", "II", "III", "IV"};
        this.displayName = "§a§lАномальный Мешок " + romanNumerals[tier];
        this.customModelData = 9000 + tier;
    }

    public int getTier() {
        return tier;
    }

    public int getMaxArtifacts() {
        return plugin.getConfigManager().getTierLimit(tier);
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getLore() {
        int slots = getMaxArtifacts();
        return new String[]{
            "§7Аномальный мешок способный",
            "§7хранить силу артефактов.",
            "",
            "§e§lТИР: §f" + (tier == 1 ? "I" : tier == 2 ? "II" : tier == 3 ? "III" : "IV"),
            "§e§lСлотов: §f" + slots,
            "",
            "§7Положите артефакты внутрь",
            "§7чтобы получить их эффекты."
        };
    }

    public ItemStack createItem() {
        Material bagMaterial = Material.getMaterial("LEATHER_BAG");
        if (bagMaterial == null) {
            bagMaterial = Material.BUNDLE;
        }
        if (bagMaterial == null) {
            bagMaterial = Material.SHULKER_BOX;
        }
        
        ItemStack item = new ItemStack(bagMaterial);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(java.util.Arrays.asList(getLore()));
            if (bagMaterial != Material.SHULKER_BOX) {
                meta.setCustomModelData(customModelData);
            }
            meta.setUnbreakable(true);
            meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
            
            meta.getPersistentDataContainer().set(BAG_TIER_KEY, PersistentDataType.INTEGER, tier);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    public static boolean isAnomalousBag(ItemStack item) {
        if (item == null) return false;
        
        Material type = item.getType();
        Material leatherBag = Material.getMaterial("LEATHER_BAG");
        Material bundle = Material.getMaterial("BUNDLE");
        
        boolean isValidType = type == leatherBag || type == bundle || type == Material.SHULKER_BOX;
        
        if (!isValidType) return false;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        
        return meta.getPersistentDataContainer().has(BAG_TIER_KEY, PersistentDataType.INTEGER);
    }

    public static int getBagTier(ItemStack item) {
        if (!isAnomalousBag(item)) {
            return -1;
        }
        
        ItemMeta meta = item.getItemMeta();
        Integer tierValue = meta.getPersistentDataContainer().get(BAG_TIER_KEY, PersistentDataType.INTEGER);
        return tierValue != null ? tierValue : -1;
    }

    public static int getBagMaxSlots(ItemStack item) {
        if (!isAnomalousBag(item)) {
            return 0;
        }
        
        int tier = getBagTier(item);
        if (tier < 1 || tier > 4) return 0;
        
        ArtifactPlugin plugin = ArtifactPlugin.getInstance();
        if (plugin == null || plugin.getConfigManager() == null) {
            return 2;
        }
        
        return plugin.getConfigManager().getTierLimit(tier);
    }
}
