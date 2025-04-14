package com.tcoded.tccombatlog.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.tcoded.tccombatlog.manager.CombatManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MoveListener implements Listener {

    private final CombatManager combatManager;

    public MoveListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        this.onMove(event, false);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;

        this.onMove(event, true);
        if (event.isCancelled()) return;

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Player player = event.getPlayer();
            if (combatManager.getOrCreateSession(player).isInCombat()) {
                combatManager.resetMaxTimer(player);
            }
        }
    }

    public void onMove(PlayerMoveEvent event, boolean teleport) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        // For demonstration, calculate a simulated "pvp setting" from regions.
        // Here, a positive setting indicates PvP allowed; a negative indicates PvP denied.
        boolean fromPvp = isPvp(player, from);
        boolean toPvp = isPvp(player, to);

        // If moving from a region that allows PvP into one that denies PvP while in combat:
        if (fromPvp && !toPvp) {
            if (combatManager.getOrCreateSession(player).isInCombat()) {
                Location safe = combatManager.getSetbackLocation(player);
                if (safe != null) {
                    if (teleport) event.setCancelled(true);
                    else event.setTo(safe);
                    player.sendMessage(ChatColor.RED + "You cannot leave a PVP zone while in combat!");
                }
            }
        }
    }

    // Determines if a location is considered "safe"
    public static boolean isPvp(Player player, Location bukkitLoc) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(bukkitLoc);

        WorldGuard wg = WorldGuard.getInstance();
        WorldGuardPlatform platform = wg.getPlatform();
        RegionContainer container = platform.getRegionContainer();

        RegionQuery query = container.createQuery();
        return query.testState(loc, localPlayer, Flags.PVP);
    }

}
