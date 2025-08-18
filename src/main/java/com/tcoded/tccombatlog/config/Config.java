package com.tcoded.tccombatlog.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.regex.Pattern;

public record Config(boolean blockByDefault, List<Pattern> allowedCommands, List<Pattern> blockedCommands) {

    public static Config deserialize(ConfigurationSection section) {
        boolean blockByDefault = section.getBoolean("block-commands-by-default");

        List<String> allowed = section.getStringList("allowed-commands");
        List<String> blocked = section.getStringList("blocked-commands");

        List<Pattern> allowedPatterns = allowed.stream().map(Pattern::compile).toList();
        List<Pattern> blockedPatterns = blocked.stream().map(Pattern::compile).toList();

        return new Config(blockByDefault, allowedPatterns, blockedPatterns);
    }

}
