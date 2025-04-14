package com.tcoded.tccombatlog.listeners;

import com.tcoded.tccombatlog.manager.CombatManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class JoinListener implements Listener {

    private final CombatManager combatManager;

    public JoinListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Create a CombatSession if one does not already exist for the player.
        if (combatManager.getSession(player) == null) {
            combatManager.getOrCreateSession(player);
        }
    }
}
