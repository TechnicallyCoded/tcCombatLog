package com.tcoded.tccombatlog.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MsgUtil {

    public static void broadcast(String msg) {
        // Broadcast the message to all players in the server.
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }

}
