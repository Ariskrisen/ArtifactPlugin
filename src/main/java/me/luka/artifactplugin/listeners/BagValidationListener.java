package me.luka.artifactplugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.AnomalousBag;
import me.luka.artifactplugin.models.Artifact;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BagValidationListener implements Listener {

    private final ArtifactPlugin plugin;
    private final Map<UUID, Integer> openBagSlots = new HashMap<>();

    public BagValidationListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.getConfigManager().isBagsEnabled()) return;
        
        ItemStack item = event.getItem();
        if (item == null) return;
        
        if (AnomalousBag.isAnomalousBag(item)) {
            Player player = event.getPlayer();
            int maxSlots = AnomalousBag.getBagMaxSlots(item);
            player.sendMessage(plugin.getConfigManager().getMsgBagInfo(maxSlots));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int rawSlot = event.getRawSlot();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        
        if (currentItem != null && AnomalousBag.isAnomalousBag(currentItem)) {
            openBagSlots.put(player.getUniqueId(), rawSlot);
        }
        
        if (cursorItem != null && AnomalousBag.isAnomalousBag(cursorItem)) {
            openBagSlots.put(player.getUniqueId(), rawSlot);
            
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                return;
            }
            
            Artifact targetArtifact = plugin.getArtifactManager().getArtifact(currentItem);
            if (targetArtifact == null) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMsgBagNotArtifact());
                return;
            }
            
            int maxSlots = AnomalousBag.getBagMaxSlots(cursorItem);
            int currentCount = plugin.getArtifactManager().getAllItemsFromBundle(cursorItem).size();
            if (currentCount >= maxSlots) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMsgBagFull(maxSlots));
                return;
            }
            
            return;
        }
        
        if (cursorItem == null || cursorItem.getType() == Material.AIR) {
            return;
        }
        
        Integer bagSlot = openBagSlots.get(player.getUniqueId());
        if (bagSlot == null || bagSlot != rawSlot) {
            return;
        }
        
        if (currentItem == null || !AnomalousBag.isAnomalousBag(currentItem)) {
            return;
        }
        
        Artifact artifact = plugin.getArtifactManager().getArtifact(cursorItem);
        if (artifact == null) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMsgBagNotArtifact());
            return;
        }
        
        int maxSlots = AnomalousBag.getBagMaxSlots(currentItem);
        int currentCount = plugin.getArtifactManager().getAllItemsFromBundle(currentItem).size();
        if (currentCount >= maxSlots) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMsgBagFull(maxSlots));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        ItemStack draggedItem = event.getOldCursor();
        if (draggedItem == null) return;
        
        Integer bagSlot = openBagSlots.get(player.getUniqueId());
        if (bagSlot == null) return;
        
        boolean draggingIntoBag = false;
        for (int slot : event.getRawSlots()) {
            if (slot == bagSlot) {
                draggingIntoBag = true;
                break;
            }
        }
        
        if (!draggingIntoBag) return;
        
        ItemStack bag = player.getOpenInventory().getItem(bagSlot);
        if (bag == null || !AnomalousBag.isAnomalousBag(bag)) return;
        
        if (AnomalousBag.isAnomalousBag(draggedItem)) {
            return;
        }
        
        Artifact artifact = plugin.getArtifactManager().getArtifact(draggedItem);
        if (artifact == null) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMsgBagNotArtifact());
            return;
        }
        
        int maxSlots = AnomalousBag.getBagMaxSlots(bag);
        int currentCount = plugin.getArtifactManager().getAllItemsFromBundle(bag).size();
        if (currentCount >= maxSlots) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMsgBagFull(maxSlots));
        }
    }
}
