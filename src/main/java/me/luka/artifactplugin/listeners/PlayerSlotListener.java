package me.luka.artifactplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import me.luka.artifactplugin.ArtifactPlugin;

public class PlayerSlotListener implements Listener {

    private final ArtifactPlugin plugin;

    public PlayerSlotListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getArtifactManager().updatePlayerEffects(event.getPlayer());
        }, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getArtifactManager().cleanup();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getArtifactManager().updatePlayerEffects(event.getPlayer());
        }, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getArtifactManager().updatePlayerEffects(player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getArtifactManager().updatePlayerEffects(player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getArtifactManager().updatePlayerEffects(event.getPlayer());
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemBreak(PlayerItemBreakEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getArtifactManager().updatePlayerEffects(event.getPlayer());
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDropItem(PlayerDropItemEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getArtifactManager().updatePlayerEffects(event.getPlayer());
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickupItem(PlayerPickupItemEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getArtifactManager().updatePlayerEffects(event.getPlayer());
        }, 1L);
    }
}
