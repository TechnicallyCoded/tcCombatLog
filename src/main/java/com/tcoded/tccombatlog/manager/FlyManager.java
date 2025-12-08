package com.tcoded.tccombatlog.manager;

import com.tcoded.tccombatlog.config.Config;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Manages fly states for players during combat.
 * Stores previous fly abilities and handles enabling/disabling flight with configurable messages and sounds.
 */
public class FlyManager {

    // Stores whether players had fly ability before combat
    private final Map<UUID, Boolean> previousFlyAbility = new ConcurrentHashMap<>();
    private final Config config;

    public FlyManager(Config config) {
        this.config = config;
    }

    /*
     * Disables flight for a player during combat.
     * Stores their previous fly ability to restore later.
     * Respects bypass permission.
     */
    public void disableFly(Player player, boolean sendMessage) {
        // Admins with bypass permission are never affected
        if (player.hasPermission("tccombatlog.bypass.fly")) {
            return;
        }

        // Only disable if player currently has fly ability
        if (player.getAllowFlight()) {
            // Store that they had fly so we can restore it later
            previousFlyAbility.put(player.getUniqueId(), true);
            player.setAllowFlight(false);
            player.setFlying(false);

            // Send configured message if enabled
            if (sendMessage && !config.disableFlyMessage().isEmpty()) {
                player.sendMessage(config.disableFlyMessage());
            }

            // Play configured sound if enabled
            if (!config.disableFlySound().isEmpty()) {
                try {
                    Sound sound = Sound.valueOf(config.disableFlySound().toUpperCase());
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                } catch (IllegalArgumentException ignored) {
                    // Invalid sound name in config, silently ignore
                }
            }
        }
    }

    /*
     * Re-enables flight for a player after combat ends.
     * Only restores fly ability if they had it before combat.
     * Does not set flying state, only the ability.
     * Respects bypass permission.
     */
    public void enableFly(Player player) {
        // Admins with bypass permission are never affected
        if (player.hasPermission("tccombatlog.bypass.fly")) {
            return;
        }

        // Check if player had fly ability before combat
        Boolean hadFly = previousFlyAbility.remove(player.getUniqueId());
        if (hadFly != null && hadFly) {
            // Restore fly ability (not flying state)
            player.setAllowFlight(true);

            // Send configured message if enabled
            if (!config.enableFlyMessage().isEmpty()) {
                player.sendMessage(config.enableFlyMessage());
            }

            // Play configured sound if enabled
            if (!config.enableFlySound().isEmpty()) {
                try {
                    Sound sound = Sound.valueOf(config.enableFlySound().toUpperCase());
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                } catch (IllegalArgumentException ignored) {
                    // Invalid sound name in config, silently ignore
                }
            }
        }
    }

    /*
     * Cleans up stored fly data for a player.
     * Called when player quits to prevent memory leaks.
     */
    public void cleanupPlayer(Player player) {
        previousFlyAbility.remove(player.getUniqueId());
    }

}