package me.luka.artifactplugin.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.List;

public class PlayerDamageListener implements Listener {

    private final ArtifactPlugin plugin;

    public PlayerDamageListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        Entity attacker = null;
        double damage = event.getFinalDamage();
        
        if (event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent) event).getDamager();
        }
        
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.PassiveAbility ability : artifact.getPassiveAbilities()) {
                if (ability.getType() == Artifact.PassiveAbilityType.ANTI_FALL) {
                    if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        event.setCancelled(true);
                        return;
                    }
                }
                
                if (ability.getType() == Artifact.PassiveAbilityType.WITHER_REDUCTION) {
                    if (event.getCause() == EntityDamageEvent.DamageCause.WITHER ||
                        event.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
                        double reduction = ability.getWitherReduction() / 100.0;
                        event.setDamage(event.getDamage() * (1.0 - reduction));
                    }
                }
                
                if (ability.getType() == Artifact.PassiveAbilityType.MAGIC_REDUCTION) {
                    if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
                        double reduction = ability.getMagicReduction() / 100.0;
                        event.setDamage(event.getDamage() * (1.0 - reduction));
                    }
                }
            }
        }
        
        manager.processDefenseEffects(player, attacker, damage, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.DefenseEffect defense : artifact.getDefenseEffects()) {
                if (defense.getType() == Artifact.DefenseEffectType.DODGE) {
                    if (Math.random() < defense.getDodgeChance()) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
