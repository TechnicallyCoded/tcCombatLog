package com.tcoded.tccombatlog.manager;

import com.tcoded.tccombatlog.types.CombatSession;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    // Stores combat sessions per player UUID.
    private final Map<UUID, CombatSession> combatSessions = new ConcurrentHashMap<>();
    // Stores the last safe location for each player.
    private final Map<UUID, Location> setbackLocations = new ConcurrentHashMap<>();

    public CombatSession getSession(Player player) {
        return combatSessions.get(player.getUniqueId());
    }

    public CombatSession getOrCreateSession(Player player) {
        return combatSessions.computeIfAbsent(player.getUniqueId(), k -> new CombatSession());
    }

    public void removeSession(Player player) {
        combatSessions.remove(player.getUniqueId());
    }

    public void setSetbackLocation(Player player, Location location) {
        setbackLocations.put(player.getUniqueId(), location);
    }

    public Location getSetbackLocation(Player player) {
        return setbackLocations.get(player.getUniqueId());
    }

    public void tagPlayer(Player victim, Player attacker) {
        tagPlayers(victim, attacker, true, true);
    }

    // Tags players into combat with per-player control to support bypass permissions.
    public void tagPlayers(Player victim, Player attacker, boolean tagVictim, boolean tagAttacker) {
        if (tagVictim) {
            CombatSession victimSession = getOrCreateSession(victim);
            victimSession.updateCombat(attacker);
        }

        if (tagAttacker) {
            CombatSession attackerSession = getOrCreateSession(attacker);
            attackerSession.updateCombat(victim);
        }
    }

    public void resetMaxTimer(Player player) {
        CombatSession session = getSession(player);
        if (session != null) {
            session.resetMaxTimer(); // Reset the combat session without an attacker.
        }
    }

}