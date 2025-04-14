package com.tcoded.tccombatlog.listeners;

import com.tcoded.tccombatlog.manager.CombatManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

// WorldGuard API imports – adjust as needed for your WorldGuard version.


public class SafeBlockListener implements Listener {

    private final CombatManager combatManager;

    public SafeBlockListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        // Only update on block change.
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ())
        {
            return;
        }

        Player player = event.getPlayer();
        Location to = event.getTo();

        // Check if the new location is safe. (In this case, safe means not within any region that actively denies PvP.)
        if (isValidSetbackLocation(player, to)) {
            Location safeLoc = to.toBlockLocation().add(0.5, 0.2, 0.5);
            combatManager.setSetbackLocation(player, safeLoc);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Location to = event.getTo();

        if (isValidSetbackLocation(player, to)) {
            Location safeLoc = to.toBlockLocation().add(0.5, 0.2, 0.5);
            combatManager.setSetbackLocation(player, safeLoc);
        }
    }

    private boolean isValidSetbackLocation(Player player, Location to) {
        Block block = to.getBlock();
        Block under = block.getRelative(0, -1, 0);

        // Check if the block under the player is a safe block (e.g., grass, dirt, etc.)
        if (!under.getType().isSolid()) {
            return false;
        }

        return MoveListener.isPvp(player, to);
    }

}
