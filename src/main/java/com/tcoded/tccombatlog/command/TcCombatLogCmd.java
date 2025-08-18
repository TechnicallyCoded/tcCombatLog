package com.tcoded.tccombatlog.command;

import com.tcoded.tccombatlog.TcCombatLog;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TcCombatLogCmd implements TabExecutor {

    private final TcCombatLog plugin;

    public TcCombatLogCmd(TcCombatLog plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        String arg0Lower = args[0].toLowerCase();
        switch (arg0Lower) {
            case "reload" -> this.handleReload(sender, args);
            default -> sendHelp(sender);
        }

        return true;
    }

    private static void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "/tcl <reload>");
    }

    private void handleReload(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        this.plugin.reloadConfig();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> options = new ArrayList<>();

        if (args.length < 1) {
            return options;
        }

        String arg0Lower = args[0].toLowerCase();

        if (args.length <= 1) {
            options.addAll(List.of("reload"));
        }

        String lastArgLower = args[args.length - 1].toLowerCase();
        return options.stream().filter(s -> s.startsWith(lastArgLower)).toList();
    }

}
