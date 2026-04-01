package me.luka.artifactplugin;

import org.bukkit.plugin.java.JavaPlugin;
import me.luka.artifactplugin.commands.ArtifactCommand;
import me.luka.artifactplugin.listeners.*;
import me.luka.artifactplugin.managers.ArtifactManager;
import me.luka.artifactplugin.managers.ConfigManager;

public class ArtifactPlugin extends JavaPlugin {

    private static ArtifactPlugin instance;
    private ConfigManager configManager;
    private ArtifactManager artifactManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        
        artifactManager = new ArtifactManager(this);
        configManager = new ConfigManager(this);
        configManager.loadArtifacts();
        artifactManager.start();

        registerListeners();
        registerCommands();

        getLogger().info("ArtifactPlugin enabled!");
        getLogger().info("Loaded " + artifactManager.getArtifactCount() + " artifacts");
    }

    @Override
    public void onDisable() {
        if (artifactManager != null) {
            artifactManager.cleanup();
        }
        getLogger().info("ArtifactPlugin disabled!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerSlotListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerTickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerAttackListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BagValidationListener(this), this);
    }

    public static ArtifactPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ArtifactManager getArtifactManager() {
        return artifactManager;
    }

    private void registerCommands() {
        ArtifactCommand command = new ArtifactCommand(this);
        getCommand("artifact").setExecutor(command);
        getCommand("artifact").setTabCompleter(command);
    }
}
