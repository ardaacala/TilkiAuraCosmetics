package com.auracosmetics;

import com.auracosmetics.aura.AuraManager;
import com.auracosmetics.command.*;
import com.auracosmetics.data.PlayerAuraData;
import com.auracosmetics.gui.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.util.logging.Level;

public final class AuraCosmeticsPlugin extends JavaPlugin {
    private PlayerAuraData data;
    private AuraManager manager;
    @Override public void onEnable() {
        saveDefaultConfig();
        try { data = new PlayerAuraData(this); }
        catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Cannot load playerdata.yml; disabling to preserve existing data", ex);
            Bukkit.getPluginManager().disablePlugin(this); return;
        }
        manager = new AuraManager(this, data);
        manager.reload();
        Bukkit.getOnlinePlayers().forEach(manager::load);
        AuraMenu menu = new AuraMenu(manager);
        Bukkit.getPluginManager().registerEvents(manager, this);
        Bukkit.getPluginManager().registerEvents(new AuraMenuListener(this, manager), this);
        var command = Objects.requireNonNull(getCommand("aura"), "Missing aura command in plugin.yml");
        command.setExecutor(new AuraCommand(this, manager, menu));
        command.setTabCompleter(new AuraTabCompleter(manager));
    }
    @Override public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof AuraMenu) player.closeInventory();
        });
        if (manager != null) manager.stop();
        if (data != null) data.close();
    }
    public void reloadSettings() { reloadConfig(); manager.reload(); }
    public void message(CommandSender sender, String key) { message(sender, key, ""); }
    public void message(CommandSender sender, String key, String aura) {
        sender.sendMessage(color(getConfig().getString("messages.prefix", "&8[&bAura&8] ")
                + getConfig().getString("messages." + key, key).replace("%aura%", aura)));
    }
    public static String color(String text) { return ChatColor.translateAlternateColorCodes('&', text); }
}
