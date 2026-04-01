package me.luka.artifactplugin.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.List;

public class EntityDeathListener implements Listener {

    private final ArtifactPlugin plugin;

    public EntityDeathListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        
        if (killer == null) {
            return;
        }
        
        Player player = killer;
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        plugin.getArtifactManager().processKillEffects(player, entity);
        
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.PassiveAbility ability : artifact.getPassiveAbilities()) {
                if (ability.getType() == Artifact.PassiveAbilityType.LOOT_BONUS) {
                    double bonusChance = ability.getLootBonusChance();
                    if (Math.random() < bonusChance && !event.getDrops().isEmpty()) {
                        event.getDrops().add(event.getDrops().get(0).clone());
                    }
                }
            }
        }
    }
}
