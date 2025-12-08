package com.tcoded.tccombatlog.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.regex.Pattern;

public record Config(
        boolean blockByDefault,
        List<Pattern> allowedCommands,
        List<Pattern> blockedCommands,
        String disableFlyMessage,
        String disableFlySound,
        String enableFlyMessage,
        String enableFlySound
) {

    public static Config deserialize(ConfigurationSection section) {
        boolean blockByDefault = section.getBoolean("block-commands-by-default");

        List<String> allowed = section.getStringList("allowed-commands");
        List<String> blocked = section.getStringList("blocked-commands");

        List<Pattern> allowedPatterns = allowed.stream().map(Pattern::compile).toList();
        List<Pattern> blockedPatterns = blocked.stream().map(Pattern::compile).toList();

        // Load fly configuration with defaults
        String disableFlyMessage = ChatColor.translateAlternateColorCodes('&',
                section.getString("fly.disable-message", "&cFlight has been disabled during combat!"));
        String disableFlySound = section.getString("fly.disable-sound", "ENTITY_ENDERMAN_TELEPORT");

        String enableFlyMessage = ChatColor.translateAlternateColorCodes('&',
                section.getString("fly.enable-message", ""));
        String enableFlySound = section.getString("fly.enable-sound", "");

        return new Config(
                blockByDefault,
                allowedPatterns,
                blockedPatterns,
                disableFlyMessage,
                disableFlySound,
                enableFlyMessage,
                enableFlySound
        );
    }

}