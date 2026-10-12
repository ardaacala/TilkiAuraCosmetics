package com.auracosmetics.aura;

import org.bukkit.Material;
import java.util.Locale;

public enum AuraType {
    VIP("VIP", "&6", Material.GOLD_INGOT, "Altın ışık halkaları ve parıltılar.", "VIP"),
    MEGAVIP("MegaVIP", "&d", Material.AMETHYST_SHARD, "Mor enerji sarmalı ve kristaller.", "MegaVIP"),
    TILKI("Tilki", "&6", Material.FIRE_CHARGE, "Ateş tilkisi, taç ve alev çemberi.", "Tilki");

    public final String displayName;
    public final String color;
    public final Material material;
    public final String description;
    public final String defaultTag;

    AuraType(String name, String color, Material material, String description, String defaultTag) {
        this.displayName = name;
        this.color = color;
        this.material = material;
        this.description = description;
        this.defaultTag = defaultTag;
    }

    public String id() { return name().toLowerCase(Locale.ROOT); }
}
