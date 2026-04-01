package me.luka.artifactplugin.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.List;

public class PlayerAttackListener implements Listener {

    private final ArtifactPlugin plugin;

    public PlayerAttackListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        plugin.getArtifactManager().processAttackEffects(player, event.getEntity(), event);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
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
            for (Artifact.PassiveAbility ability : artifact.getPassiveAbilities()) {
                if (ability.getType() == Artifact.PassiveAbilityType.ARROW_EFFECTS) {
                    if (event.getProjectile() instanceof Arrow) {
                        Arrow arrow = (Arrow) event.getProjectile();
                        ProjectileSource shooter = arrow.getShooter();
                        
                        if (shooter instanceof LivingEntity) {
                            ((LivingEntity) shooter).addPotionEffect(
                                new PotionEffect(
                                    ability.getArrowEffectType(),
                                    ability.getArrowEffectDuration(),
                                    ability.getArrowEffectLevel()
                                )
                            );
                        }
                    }
                }
            }
        }
    }
}
