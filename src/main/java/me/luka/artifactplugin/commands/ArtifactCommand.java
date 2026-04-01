package me.luka.artifactplugin.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.luka.artifactplugin.ArtifactPlugin;
import me.luka.artifactplugin.models.Artifact;
import me.luka.artifactplugin.models.AnomalousBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArtifactCommand implements CommandExecutor, TabCompleter {

    private final ArtifactPlugin plugin;

    public ArtifactCommand(ArtifactPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":
            case "выдать":
                handleGive(sender, args);
                break;
            case "list":
            case "список":
                handleList(sender);
                break;
            case "info":
            case "инфо":
                handleInfo(sender, args);
                break;
            case "reload":
            case "перезагрузка":
                handleReload(sender);
                break;
            case "bag":
            case "мешок":
                handleBag(sender, args);
                break;
            case "help":
            case "помощь":
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && args.length < 3) {
            sender.sendMessage("§cИспользование: /artifact give <игрок> <id_артефакта> [кол-во]");
            return;
        }

        Player target;
        String artifactId;
        int amount = 1;

        if (args.length >= 3) {
            target = plugin.getServer().getPlayer(args[1]);
            artifactId = args[2].toLowerCase();
        } else {
            target = (Player) sender;
            artifactId = args[1].toLowerCase();
        }

        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[args.length - 1]);
            } catch (NumberFormatException e) {
                amount = 1;
            }
        }

        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }

        Artifact artifact = plugin.getArtifactManager().getArtifactById(artifactId);
        if (artifact == null) {
            sender.sendMessage("§cАртефакт '" + artifactId + "' не найден!");
            sender.sendMessage("§7Используйте §e/artifact list §7для списка.");
            return;
        }

        ItemStack item = createArtifactItem(artifact);
        item.setAmount(amount);
        
        target.getInventory().addItem(item);
        
        sender.sendMessage("§a✓ Выдан §e" + artifact.getName() + " §a(×" + amount + ") игроку §f" + target.getName());
        target.sendMessage("§aВы получили §e" + artifact.getName() + "§a!");
    }

    private void handleList(CommandSender sender) {
        var artifacts = plugin.getArtifactManager().getArtifacts();
        
        sender.sendMessage("§6§l═══ Список Артефактов §l═══");
        sender.sendMessage("");
        
        if (artifacts.isEmpty()) {
            sender.sendMessage("§7Артефакты не настроены.");
            return;
        }
        
        int count = 0;
        for (Artifact artifact : artifacts.values()) {
            if (artifact.isEnabled()) {
                sender.sendMessage("§e• " + artifact.getId() + " §7- " + artifact.getName());
                count++;
            }
        }
        
        sender.sendMessage("");
        sender.sendMessage("§7Всего: §f" + count + " артефактов");
        sender.sendMessage("§7Использование: §e/artifact give <id> [игрок] [кол-во]");
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /artifact info <id_артефакта>");
            return;
        }

        String artifactId = args[1].toLowerCase();
        Artifact artifact = plugin.getArtifactManager().getArtifactById(artifactId);
        
        if (artifact == null) {
            sender.sendMessage("§cАртефакт '" + artifactId + "' не найден!");
            return;
        }

        sender.sendMessage("§6§l═══ " + artifact.getName() + " §l═══");
        sender.sendMessage("§7ID: §f" + artifact.getId());
        sender.sendMessage("§7Материал: §f" + artifact.getMaterial().name());
        
        if (!artifact.getPotionEffects().isEmpty()) {
            sender.sendMessage("§aЭффекты зелий: §f" + artifact.getPotionEffects().size());
        }
        if (!artifact.getAttackEffects().isEmpty()) {
            sender.sendMessage("§cЭффекты атаки: §f" + artifact.getAttackEffects().size());
        }
        if (!artifact.getDefenseEffects().isEmpty()) {
            sender.sendMessage("§bЭффекты защиты: §f" + artifact.getDefenseEffects().size());
        }
        if (!artifact.getPassiveAbilities().isEmpty()) {
            sender.sendMessage("§dПассивные способности: §f" + artifact.getPassiveAbilities().size());
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("artifact.reload")) {
            sender.sendMessage("§cНет прав!");
            return;
        }
        
        plugin.reloadConfig();
        plugin.getConfigManager().loadArtifacts();
        sender.sendMessage("§a✓ Конфигурация перезагружена!");
        sender.sendMessage("§7Загружено §f" + plugin.getArtifactManager().getArtifactCount() + " §7артефактов");
    }

    private void handleBag(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /artifact bag <тир> [игрок]");
            sender.sendMessage("§7Тиры: §e1 §7(2 слота), §e2 §7(4 слота), §e3 §7(6 слотов), §e4 §7(8 слотов)");
            return;
        }

        int tier;
        try {
            tier = Integer.parseInt(args[1]);
            if (tier < 1 || tier > 4) {
                sender.sendMessage("§cТип должен быть от 1 до 4!");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cНеверный тир!");
            return;
        }

        Player target;
        if (args.length >= 3) {
            target = plugin.getServer().getPlayer(args[2]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cУкажите игрока!");
            return;
        }

        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }

        AnomalousBag bag = new AnomalousBag(plugin, tier);
        ItemStack bagItem = bag.createItem();
        
        target.getInventory().addItem(bagItem);
        
        sender.sendMessage("§a✓ Выдан §eАномальный Мешок " + toRoman(tier) + " §aигроку §f" + target.getName());
        target.sendMessage("§aВы получили §eАномальный Мешок " + toRoman(tier) + "§a!");
    }

    private String toRoman(int num) {
        switch (num) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            default: return String.valueOf(num);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l═══ Команды Артефактов §l═══");
        sender.sendMessage("§e/artifact give <id> [игрок] [кол-во] §7- Выдать артефакт");
        sender.sendMessage("§e/artifact bag <тир> [игрок] §7- Выдать аномальный мешок (1-4)");
        sender.sendMessage("§e/artifact list §7- Список всех артефактов");
        sender.sendMessage("§e/artifact info <id> §7- Информация об артефакте");
        sender.sendMessage("§e/artifact reload §7- Перезагрузить конфиг");
    }

    private ItemStack createArtifactItem(Artifact artifact) {
        ItemStack item;
        
        if (artifact.hasNamespacedItem()) {
            item = getNamespacedItem(artifact.getNamespacedItem());
            if (item == null) {
                item = new ItemStack(artifact.getMaterial());
            }
        } else {
            item = new ItemStack(artifact.getMaterial());
        }
        
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(artifact.getName());
            
            List<String> lore = new ArrayList<>();
            if (artifact.getLore() != null && !artifact.getLore().isEmpty()) {
                lore.addAll(artifact.getLore());
            }
            lore.add("");
            lore.add("§8§o[Артефакт: " + artifact.getId() + "]");
            
            meta.setLore(lore);
            
            if (artifact.getCustomModelData() > 0) {
                meta.setCustomModelData(artifact.getCustomModelData());
            }
            
            NamespacedKey key = new NamespacedKey(plugin, "artifact_id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, artifact.getId());
            
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private ItemStack getNamespacedItem(String namespacedKey) {
        if (namespacedKey.contains(":")) {
            String[] parts = namespacedKey.split(":", 2);
            String namespace = parts[0];
            String itemId = parts[1];
            
            switch (namespace.toLowerCase()) {
                case "itemsadder":
                    return getItemsAdderItem(itemId);
                default:
                    return null;
            }
        }
        return null;
    }
    
    private ItemStack getItemsAdderItem(String itemId) {
        try {
            Class<?> iaPluginClass = Class.forName("dev.lone.itemsadder.api.ItemsAdder");
            Object api = iaPluginClass.getMethod("getInstance").invoke(null);
            Object items = api.getClass().getMethod("getItems").invoke(api);
            Object itemStack = items.getClass().getMethod("getById", String.class).invoke(items, "itemsadder:" + itemId);
            if (itemStack != null) {
                return (ItemStack) itemStack.getClass().getMethod("getItemStack").invoke(itemStack);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("ItemsAdder not found or item not available: " + itemId);
        }
        return null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("give", "bag", "list", "info", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(
                plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList())
            );
        } else if (args.length == 2 && args[0].equalsIgnoreCase("bag")) {
            completions.addAll(Arrays.asList("1", "2", "3", "4"));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("bag")) {
            completions.addAll(
                plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList())
            );
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(plugin.getArtifactManager().getArtifacts().keySet());
        }
        
        String last = args[args.length - 1].toLowerCase();
        return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(last))
            .collect(Collectors.toList());
    }
}
