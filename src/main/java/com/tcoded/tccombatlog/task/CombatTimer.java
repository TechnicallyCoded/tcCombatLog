package com.tcoded.tccombatlog.task;

import com.tcoded.tccombatlog.manager.FlyManager;
import com.tcoded.tccombatlog.types.CombatSession;
import com.tcoded.tccombatlog.manager.CombatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class CombatTimer extends BukkitRunnable {

    private final CombatManager combatManager;
    private final FlyManager flyManager;
    private final JavaPlugin plugin;

    // Tracks which players are currently in combat to detect when they leave combat
    private final HashSet<UUID> playersInCombat = new HashSet<>();

    public CombatTimer(JavaPlugin plugin, CombatManager combatManager, FlyManager flyManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.flyManager = flyManager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CombatSession session = combatManager.getSession(player);
            if (session != null && session.isInCombat()) {
                // Add the player to the set of players in combat.
                playersInCombat.add(player.getUniqueId());

                long seconds = (long) Math.ceil(session.getTimeRemaining() / 1000.0);
                player.sendActionBar(Component.text("PVP Timer: " + seconds + " seconds", NamedTextColor.RED));
            } else if (playersInCombat.contains(player.getUniqueId())) {
                // If the player is no longer in combat, remove them from the set.
                playersInCombat.remove(player.getUniqueId());

                player.sendActionBar(Component.text("You are no longer in combat.", NamedTextColor.GREEN));

                // Re-enable fly ability if they had it before combat
                flyManager.enableFly(player);
            }
        }
    }

    public void start() {
        runTaskTimer(plugin, 0L, 20L);
    }

}