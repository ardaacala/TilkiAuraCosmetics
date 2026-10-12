package com.auracosmetics.util;

import com.auracosmetics.AuraCosmeticsPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public final class ItemUtil {
    private ItemUtil() { }
    public static ItemStack item(Material material, String name, List<String> lore, boolean selected) {
        ItemStack item = new ItemStack(material);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(AuraCosmeticsPlugin.color(name));
            meta.setLore(lore.stream().map(AuraCosmeticsPlugin::color).toList());
            meta.setEnchantmentGlintOverride(selected);
            item.setItemMeta(meta);
        }
        return item;
    }
}
