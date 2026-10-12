package com.auracosmetics.gui;

import com.auracosmetics.AuraCosmeticsPlugin;
import com.auracosmetics.aura.AuraManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class AuraMenuListener implements Listener {
    private final AuraCosmeticsPlugin plugin;
    private final AuraManager manager;
    public AuraMenuListener(AuraCosmeticsPlugin plugin, AuraManager manager) { this.plugin = plugin; this.manager = manager; }
    @EventHandler public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof AuraMenu menu)) return;
        // Cancel every action in the entire view, including bottom shift-clicks, number keys and double-clicks.
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || !menu.owns(player)) return;
        if (event.getRawSlot() < 0 || event.getRawSlot() >= menu.getInventory().getSize()) return;
        if (!player.hasPermission("auracosmetics.use")) { plugin.message(player, "no-permission"); return; }
        boolean close = false;
        if (event.getRawSlot() == 22) { manager.disable(player); close = true; }
        else {
            var aura = menu.auraAt(event.getRawSlot());
            if (aura != null) {
                close = manager.allowed(player, aura);
                manager.enable(player, aura);
            }
        }
        if (close) Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && player.getOpenInventory().getTopInventory() == menu.getInventory()) player.closeInventory();
        });
    }
    @EventHandler public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof AuraMenu) event.setCancelled(true);
    }
}
