package com.tcoded.tccombatlog.listeners;

import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.manager.FlyManager;
import com.tcoded.tccombatlog.types.CombatSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/*
 * Listens for players attempting to toggle flight.
 * Prevents players from enabling flight during combat unless they have bypass permission.
 */
public class FlyListener implements Listener {

    private final CombatManager combatManager;
    private final FlyManager flyManager;

    public FlyListener(CombatManager combatManager, FlyManager flyManager) {
        this.combatManager = combatManager;
        this.flyManager = flyManager;
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // Allow admins with bypass permission to fly during combat
        if (player.hasPermission("tccombatlog.bypass.fly")) {
            return;
        }

        CombatSession session = combatManager.getSession(player);
        if (session != null && session.isInCombat()) {
            // If player tries to enable flight during combat, cancel and disable
            if (event.isFlying()) {
                event.setCancelled(true);
                flyManager.disableFly(player, true);
            }
        }
    }

}