package com.auracosmetics.command;

import com.auracosmetics.aura.*;
import org.bukkit.command.*;
import java.util.*;

public final class AuraTabCompleter implements TabCompleter {
    private final AuraManager manager;
    public AuraTabCompleter(AuraManager manager) { this.manager = manager; }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("auracosmetics.use")) return List.of();
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.addAll(List.of("help", "list", "set", "off", "toggle"));
            if (sender.hasPermission("auracosmetics.admin")) options.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            manager.all().stream().filter(a -> !(sender instanceof org.bukkit.entity.Player player) || manager.allowed(player, a))
                    .map(Aura::id).forEach(options::add);
        } else return List.of();
        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.startsWith(prefix)).sorted().toList();
    }
}
