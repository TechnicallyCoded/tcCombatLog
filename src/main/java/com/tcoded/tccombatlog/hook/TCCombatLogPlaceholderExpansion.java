package com.tcoded.tccombatlog.hook;

import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.types.CombatSession;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TCCombatLogPlaceholderExpansion extends PlaceholderExpansion {

    private final CombatManager combatManager;
    private final JavaPlugin plugin; // reference to your plugin main class

    public TCCombatLogPlaceholderExpansion(JavaPlugin plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    @Override
    public boolean canRegister() {
         return true;
    }

    @Override
    public String getIdentifier() {
         return "tccombatlog";
    }

    @Override
    public String getAuthor() {
         return "MatLW";
    }

    @Override
    public String getVersion() {
         return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
         if (player == null) {
             return "";
         }
         CombatSession session = combatManager.getSession(player);
         switch (identifier) {
             // Calculates the remaining combat seconds (rounded up)
             case "seconds":
                 if (session != null && session.isInCombat()) {
                     long seconds = (long) Math.ceil(session.getTimeRemaining() / 1000.0);
                     return String.valueOf(seconds);
                 }
                 return "0";
             
             // Returns true if the player is in combat, false otherwise.
             case "incombat":
                 if (session != null && session.isInCombat()) {
                     return "true";
                 }
                 return "false";
             default:
                 return null;
         }
    }
}