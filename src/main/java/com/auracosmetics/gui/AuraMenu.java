package com.auracosmetics.gui;

import com.auracosmetics.aura.*;
import com.auracosmetics.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import java.util.*;

public final class AuraMenu implements InventoryHolder {
    private static final Map<Integer, String> SLOTS = Map.of(11, "vip", 13, "megavip", 15, "tilki");
    private final AuraManager manager;
    private final Inventory inventory;
    private UUID owner;
    public AuraMenu(AuraManager manager) {
        this.manager = manager;
        inventory = Bukkit.createInventory(this, 27, "Aura Cosmetics");
    }
    public void open(Player player) {
        AuraMenu view = new AuraMenu(manager);
        view.owner = player.getUniqueId();
        var filler = ItemUtil.item(Material.GRAY_STAINED_GLASS_PANE, " ", List.of(), false);
        for (int i = 0; i < 27; i++) view.inventory.setItem(i, filler);
        var selection = manager.selection(player.getUniqueId());
        SLOTS.forEach((slot, id) -> {
            Aura aura = manager.find(id);
            boolean allowed = manager.allowed(player, aura);
            boolean selected = selection != null && selection.enabled() && selection.aura().equals(id);
            List<String> lore = new ArrayList<>();
            lore.add("&7" + aura.description()); lore.add("");
            lore.add("&8Gerekli tag: " + manager.requiredTag(aura));
            lore.add(!allowed ? "&cBu aura için tag gerekli." : selected ? "&aŞu anda etkin" : "&eEtkinleştirmek için tıkla");
            view.inventory.setItem(slot, ItemUtil.item(aura.material(), aura.type().color + aura.displayName(), lore, selected));
        });
        view.inventory.setItem(22, ItemUtil.item(Material.BARRIER, "&cAurayı Kapat", List.of("&7Etkin aurayı kapatmak için tıkla."), false));
        player.openInventory(view.inventory);
    }
    public boolean owns(Player player) { return player.getUniqueId().equals(owner); }
    public Aura auraAt(int slot) { return manager.find(SLOTS.get(slot)); }
    @Override public Inventory getInventory() { return inventory; }
}
