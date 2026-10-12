package com.auracosmetics.command;

import com.auracosmetics.AuraCosmeticsPlugin;
import com.auracosmetics.aura.*;
import com.auracosmetics.gui.AuraMenu;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.Locale;
import java.util.stream.Collectors;

public final class AuraCommand implements CommandExecutor {
    private final AuraCosmeticsPlugin plugin;
    private final AuraManager manager;
    private final AuraMenu menu;
    public AuraCommand(AuraCosmeticsPlugin plugin, AuraManager manager, AuraMenu menu) {
        this.plugin = plugin; this.manager = manager; this.menu = menu;
    }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("auracosmetics.use")) { plugin.message(sender, "no-permission"); return true; }
        if (args.length == 0) {
            if (sender instanceof Player player) menu.open(player); else plugin.message(sender, "player-only");
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("help") && args.length == 1) { help(sender); return true; }
        if (sub.equals("list") && args.length == 1) {
            sender.sendMessage(AuraCosmeticsPlugin.color("&bAuras: &f" + manager.all().stream().map(Aura::id).collect(Collectors.joining(", "))));
            return true;
        }
        if (sub.equals("reload") && args.length == 1) {
            if (!sender.hasPermission("auracosmetics.admin")) plugin.message(sender, "no-permission");
            else { plugin.reloadSettings(); plugin.message(sender, "reloaded"); }
            return true;
        }
        if (!(sender instanceof Player player)) { plugin.message(sender, "player-only"); return true; }
        switch (sub) {
            case "set" -> {
                if (args.length != 2) { help(sender); break; }
                Aura aura = manager.find(args[1]);
                if (aura == null) plugin.message(sender, "unknown-aura"); else manager.enable(player, aura);
            }
            case "off" -> { if (args.length == 1) manager.disable(player); else help(sender); }
            case "toggle" -> { if (args.length == 1) manager.toggle(player); else help(sender); }
            default -> help(sender);
        }
        return true;
    }
    private void help(CommandSender sender) {
        sender.sendMessage(AuraCosmeticsPlugin.color("&6/aura &7- Aura menüsünü aç"));
        sender.sendMessage(AuraCosmeticsPlugin.color("&6/aura help|list|set <aura>|off|toggle"));
        if (sender.hasPermission("auracosmetics.admin")) sender.sendMessage(AuraCosmeticsPlugin.color("&6/aura reload &7- Ayarları yeniden yükle"));
    }
}
