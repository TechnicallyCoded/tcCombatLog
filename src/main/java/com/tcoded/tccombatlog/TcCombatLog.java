package com.tcoded.tccombatlog;

import com.tcoded.tccombatlog.hook.TCCombatLogPlaceholderExpansion;
import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.task.CombatTimer;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import com.tcoded.tccombatlog.listeners.*;

public class TcCombatLog extends JavaPlugin {

    private CombatManager combatManager;
    private TCCombatLogPlaceholderExpansion papiHook;

    @Override
    public void onEnable() {
        combatManager = new CombatManager();

        // Register listeners
        getServer().getPluginManager().registerEvents(new CombatListener(combatManager), this);
        getServer().getPluginManager().registerEvents(new SafeBlockListener(combatManager), this);
        getServer().getPluginManager().registerEvents(new MoveListener(combatManager), this);
        getServer().getPluginManager().registerEvents(new JoinListener(combatManager), this);
        getServer().getPluginManager().registerEvents(new QuitListener(combatManager), this);

        papiHook = new TCCombatLogPlaceholderExpansion(this, combatManager);
        papiHook.register();

        new CombatTimer(this, combatManager).start();

        getLogger().info("tcCombatLog enabled!");
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

    public CombatManager getCombatManager() {
        return combatManager;
    }
}
