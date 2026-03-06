package com.tcoded.tccombatlog.listeners;

import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.manager.FlyManager;
import com.tcoded.tccombatlog.types.CombatSession;
import com.tcoded.tccombatlog.util.MsgUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class QuitListener implements Listener {

    private final CombatManager combatManager;
    private final FlyManager flyManager;

    public QuitListener(CombatManager combatManager, FlyManager flyManager) {
        this.combatManager = combatManager;
        this.flyManager = flyManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // If the player is currently in combat, "kill" them.
        CombatSession session = combatManager.getSession(player);

        if (session != null && session.isInCombat() && event.getReason() == PlayerQuitEvent.QuitReason.DISCONNECTED) {
            player.damage(0, session.getAttacker());
            player.setHealth(0.0);
            MsgUtil.broadcast(ChatColor.RED + player.getName() + " combat logged and was killed!");
        }

        // Remove the combat session.
        combatManager.removeSession(player);

        // Clean up fly data to prevent memory leaks
        flyManager.cleanupPlayer(player);
    }

}