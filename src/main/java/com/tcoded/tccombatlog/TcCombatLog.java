package com.tcoded.tccombatlog;

import com.tcoded.tccombatlog.command.TcCombatLogCmd;
import com.tcoded.tccombatlog.config.Config;
import com.tcoded.tccombatlog.hook.TCCombatLogPlaceholderExpansion;
import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.task.CombatTimer;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.tcoded.tccombatlog.listeners.*;

import java.io.File;

public class TcCombatLog extends JavaPlugin {

    private Config config;
    private CombatManager combatManager;
    private TCCombatLogPlaceholderExpansion papiHook;

    @Override
    public void onEnable() {
        // Config
        this.reloadConfig();

        // Manager
        combatManager = new CombatManager();

        // Commands
        this.getCommand("tccombatlog").setExecutor(new TcCombatLogCmd(this));

        // Register listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new CombatListener(combatManager), this);
        pluginManager.registerEvents(new SafeBlockListener(combatManager), this);
        pluginManager.registerEvents(new MoveListener(combatManager), this);
        pluginManager.registerEvents(new JoinListener(combatManager), this);
        pluginManager.registerEvents(new DeathListener(combatManager), this);
        pluginManager.registerEvents(new QuitListener(combatManager), this);
        pluginManager.registerEvents(new CommandListener(this::getConfiguration, combatManager), this);

        papiHook = new TCCombatLogPlaceholderExpansion(this, combatManager);
        papiHook.register();

        new CombatTimer(this, combatManager).start();

        getLogger().info("tcCombatLog enabled!");
    }

    @Override
    public void reloadConfig() {
        File dataFolder = this.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) this.saveDefaultConfig();

        super.reloadConfig();

        this.config = Config.deserialize(this.getConfig());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        if (papiHook != null) {
            papiHook.unregister();
        }

        this.getServer().getScheduler().cancelTasks(this);

        getLogger().info("tcCombatLog disabled!");
    }

    public Config getConfiguration() {
        return this.config;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

}
