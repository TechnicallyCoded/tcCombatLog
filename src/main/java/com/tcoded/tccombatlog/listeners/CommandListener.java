package com.tcoded.tccombatlog.listeners;

import com.tcoded.tccombatlog.config.Config;
import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.types.CombatSession;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public class CommandListener implements Listener {

    private final Supplier<Config> configSupplier;
    private final CombatManager combatManager;

    public CommandListener(Supplier<Config> configSupplier, CombatManager combatManager) {
        this.configSupplier = configSupplier;
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        CombatSession session = combatManager.getSession(player);
        if (session == null) return;

        if (!session.isInCombat()) return;

        String message = event.getMessage();
        Config config = configSupplier.get();

        // Set our default action
        boolean allowResult = config.blockByDefault();

        // Only check allowlist if our default action is to block
        if (!allowResult) {
            for (Pattern allowed : config.allowedCommands()) {
                if (!allowed.matcher(message).matches()) continue;
                allowResult = true;
                break;
            }
        }

        // Only check blocklist if our resulting action is to allow
        if (allowResult) {
            for (Pattern blocked : config.blockedCommands()) {
                if (!blocked.matcher(message).matches()) continue;
                allowResult = false;
                break;
            }
        }

        if (!allowResult) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't run this command when in combat!");
        }
    }

}
