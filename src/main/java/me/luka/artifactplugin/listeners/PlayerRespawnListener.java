package me.luka.artifactplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.List;

public class PlayerRespawnListener implements Listener {

    private final ArtifactPlugin plugin;

    public PlayerRespawnListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getConfigManager().isWorldEnabled(event.getRespawnLocation().getWorld().getName())) {
            return;
        }
        
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.SpecialEffect effect : artifact.getSpecialEffects()) {
                if (effect.getType() == Artifact.SpecialEffectType.RESPAWN_EFFECTS) {
                    if (effect.getRespawnEffects() != null) {
                        for (String effectStr : effect.getRespawnEffects()) {
                            String[] parts = effectStr.split(":");
                            if (parts.length >= 2) {
                                PotionEffectType type = PotionEffectType.getByName(parts[0]);
                                int level = Integer.parseInt(parts[1]) - 1;
                                int duration = parts.length > 2 ? Integer.parseInt(parts[2]) : 300;
                                
                                if (type != null) {
                                    player.addPotionEffect(new PotionEffect(type, duration, level));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
