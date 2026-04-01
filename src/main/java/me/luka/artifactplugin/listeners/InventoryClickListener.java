package me.luka.artifactplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.List;

public class InventoryClickListener implements Listener {

    private final ArtifactPlugin plugin;

    public InventoryClickListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        if (event.isCancelled()) {
            return;
        }
        
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType().isAir()) {
            return;
        }
        
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.SpecialEffect effect : artifact.getSpecialEffects()) {
                if (effect.getType() == Artifact.SpecialEffectType.EQUIP_PARTICLES) {
                    try {
                        player.getWorld().spawnParticle(
                            org.bukkit.Particle.valueOf(effect.getEquipParticle().toUpperCase()),
                            player.getLocation(),
                            10
                        );
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}
