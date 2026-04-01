package me.luka.artifactplugin.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.List;

public class PlayerInteractListener implements Listener {

    private final ArtifactPlugin plugin;

    public PlayerInteractListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            
            if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
                return;
            }
            
            ItemStack item = event.getItem();
            if (item == null) return;
            
            ArtifactManager manager = plugin.getArtifactManager();
            List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
            
            for (Artifact artifact : activeArtifacts) {
                for (Artifact.SpecialEffect effect : artifact.getSpecialEffects()) {
                    if (effect.getType() == Artifact.SpecialEffectType.ACTIVATE_SOUND) {
                        try {
                            player.playSound(player.getLocation(), 
                                effect.getActivateSound(), 1.0f, 1.0f);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.SpecialEffect effect : artifact.getSpecialEffects()) {
                if (effect.getType() == Artifact.SpecialEffectType.TOTEM_OF_UNDYING) {
                    for (ItemStack drop : event.getDrops()) {
                        if (drop.getType() == artifact.getMaterial()) {
                            event.getDrops().remove(drop);
                            
                            player.getWorld().playSound(player.getLocation(), 
                                org.bukkit.Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
                            
                            player.setHealth(1.0);
                            player.addPotionEffect(new PotionEffect(
                                PotionEffectType.ABSORPTION, 200, 1));
                            player.addPotionEffect(new PotionEffect(
                                PotionEffectType.REGENERATION, 200, 1));
                            player.addPotionEffect(new PotionEffect(
                                PotionEffectType.FIRE_RESISTANCE, 200, 0));
                            
                            return;
                        }
                    }
                }
            }
        }
    }
}
