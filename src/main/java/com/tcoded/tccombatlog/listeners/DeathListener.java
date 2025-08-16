package com.tcoded.tccombatlog.listeners;

import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.types.CombatSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final CombatManager combatManager;

    public DeathListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        CombatSession session = this.combatManager.getSession(player);
        if (session == null) return;

        Player attacker = session.getAttacker();
        session.cancelCombat();

        if (attacker == null) return;

        CombatSession attackerSession = this.combatManager.getSession(attacker);
        if (attackerSession == null) return;

        attackerSession.cancelCombat();
    }

}
