package me.luka.artifactplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.models.Artifact;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeathListener implements Listener {

    private final ArtifactPlugin plugin;

    public PlayerDeathListener(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            return;
        }
        
        ArtifactManager manager = plugin.getArtifactManager();
        List<Artifact> activeArtifacts = manager.getActiveArtifacts(player);
        
        boolean hasTotem = false;
        List<ItemStack> itemsToRemove = new ArrayList<>();
        
        for (Artifact artifact : activeArtifacts) {
            for (Artifact.SpecialEffect effect : artifact.getSpecialEffects()) {
                if (effect.getType() == Artifact.SpecialEffectType.TOTEM_OF_UNDYING) {
                    hasTotem = true;
                    for (int slot : plugin.getConfigManager().getEnabledSlots()) {
                        ItemStack item = getItemInSlot(player, slot);
                        if (item != null && manager.getArtifact(item) != null && 
                            manager.getArtifact(item).getId().equals(artifact.getId())) {
                            itemsToRemove.add(item);
                        }
                    }
                }
                
                if (effect.getType() == Artifact.SpecialEffectType.DEATH_PREVENT) {
                    if (effect.isConsumeOnDeathPrevent()) {
                        for (int slot : plugin.getConfigManager().getEnabledSlots()) {
                            ItemStack item = getItemInSlot(player, slot);
                            if (item != null && item.getType() == artifact.getMaterial()) {
                                itemsToRemove.add(item);
                            }
                        }
                    }
                }
                
                if (effect.getType() == Artifact.SpecialEffectType.DEATH_MESSAGE) {
                    String message = effect.getDeathMessage();
                    if (message != null) {
                        message = message.replace("%player%", player.getName());
                        event.setDeathMessage(message);
                    }
                }
            }
        }
        
        if (hasTotem) {
            event.setCancelled(true);
            event.setDroppedExp(0);
            event.getDrops().clear();
            
            for (ItemStack item : itemsToRemove) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().remove(item);
                }
            }
            
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.setFallDistance(0);
            
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.INVISIBILITY, 1200, 0, false, false));
            
            player.sendMessage("§a§lВаш тотем спас вас от смерти!");
        } else {
            for (ItemStack item : itemsToRemove) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().remove(item);
                }
            }
        }
    }

    private ItemStack getItemInSlot(Player player, int slot) {
        switch (slot) {
            case 0: case 1: case 2: case 3: case 4:
            case 5: case 6: case 7: case 8:
                return player.getInventory().getItem(slot);
            case 40:
                return player.getInventory().getItemInOffHand();
            case 36:
                return player.getInventory().getBoots();
            case 37:
                return player.getInventory().getLeggings();
            case 38:
                return player.getInventory().getChestplate();
            case 39:
                return player.getInventory().getHelmet();
            default:
                return null;
        }
    }
}
