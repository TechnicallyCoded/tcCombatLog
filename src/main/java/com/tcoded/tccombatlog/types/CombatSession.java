package com.tcoded.tccombatlog.types;

import org.bukkit.entity.Player;

public class CombatSession {

    private static final long COMBAT_DURATION_MS = 10000; // 10 seconds combat tag duration

    private long lastCombatTime;
    private Player attacker;

    public CombatSession() {
        this.lastCombatTime = 0;
        this.attacker = null;
    }

    // Called whenever the player takes combat damage. Updates the timer and attacker.
    public void updateCombat(Player attacker) {
        this.attacker = attacker;
        this.lastCombatTime = System.currentTimeMillis();
    }

    // Returns true if the combat session is still active.
    public boolean isInCombat() {
        long timeSinceCombat = System.currentTimeMillis() - lastCombatTime;
        return timeSinceCombat < COMBAT_DURATION_MS;
    }

    // Provides the remaining combat time in milliseconds.
    public long getTimeRemaining() {
        long elapsed = System.currentTimeMillis() - lastCombatTime;
        long remaining = COMBAT_DURATION_MS - elapsed;
        return remaining > 0 ? remaining : 0;
    }

    // Returns the attacker if the player is still in combat; otherwise, returns null.
    public Player getAttacker() {
        return isInCombat() ? attacker : null;
    }

    // Reset the combat timer to the max
    public void resetMaxTimer() {
        this.lastCombatTime = System.currentTimeMillis();
    }

    // Exit combat
    public void cancelCombat() {
        this.lastCombatTime = 0L;
    }

}
