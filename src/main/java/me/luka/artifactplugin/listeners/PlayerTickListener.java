package me.luka.artifactplugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTickListener implements Listener {

    private final ArtifactPlugin plugin;
    private final Map<UUID, Long> doubleJumpCooldowns = new HashMap<>();
    private final Map<UUID, Double> lastYPositions = new HashMap<>();
    private final Map<UUID, Boolean> wasOnGround = new HashMap<>();

    public PlayerTickListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.PassiveAbility ability : artifact.getPassiveAbilities()) {
                if (ability.getType() == Artifact.PassiveAbilityType.FROST_WALKER) {
                    handleFrostWalker(player, ability.getFrostWalkerLevel());
                }
                
                if (ability.getType() == Artifact.PassiveAbilityType.DOUBLE_JUMP) {
                    boolean isOnGround = player.isOnGround();
                    Boolean wasGrounded = wasOnGround.getOrDefault(uuid, false);
                    
                    if (!isOnGround && wasGrounded && player.getVelocity().getY() > 0) {
                        handleDoubleJump(player, ability);
                    }
                    
                    wasOnGround.put(uuid, isOnGround);
                }
            }
        }
        
        if (manager.hasMagneticArtifact(uuid)) {
            handleMagnetic(player, manager.getMagneticRange(uuid));
        }
        
        lastYPositions.put(uuid, event.getFrom().getY());
    }

    private void handleDoubleJump(Player player, Artifact.PassiveAbility ability) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastJump = doubleJumpCooldowns.get(uuid);
        
        if (lastJump != null && currentTime - lastJump < 500) {
            return;
        }
        
        double boost = ability.getDoubleJumpBoost();
        Vector velocity = player.getVelocity();
        velocity.setY(velocity.getY() * boost);
        player.setVelocity(velocity);
        doubleJumpCooldowns.put(uuid, currentTime);
        
        player.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, player.getLocation(), 10, 0.3, 0.3, 0.3, 0.01);
    }

    private void handleFrostWalker(Player player, int level) {
        if (player.getLocation().getBlock().getTemperature() <= 0.15) {
            int radius = 2 + level;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        var block = player.getLocation().add(x, 1, z).getBlock();
                        if (block.getType().name().contains("WATER")) {
                            block.setType(org.bukkit.Material.FROSTED_ICE);
                        }
                    }
                }
            }
        }
    }

    private void handleMagnetic(Player player, double range) {
        player.getLocation().getWorld().getNearbyEntities(player.getLocation(), range, range, range).stream()
            .filter(entity -> entity instanceof Item)
            .filter(entity -> entity.getVelocity().length() > 0.01)
            .forEach(item -> {
                Vector direction = player.getLocation().toVector().subtract(item.getLocation().toVector()).normalize();
                direction.multiply(0.3);
                direction.setY(0.1);
                item.setVelocity(direction);
            });
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        int originalExp = event.getAmount();
        plugin.getArtifactManager().processXPBoost(player, originalExp);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        doubleJumpCooldowns.remove(uuid);
        lastYPositions.remove(uuid);
        wasOnGround.remove(uuid);
    }
}
