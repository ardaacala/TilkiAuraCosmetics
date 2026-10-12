package com.auracosmetics.aura;

import org.bukkit.Material;
import java.util.List;
import java.util.Locale;

public enum AuraType {
    // Default TilkiTags ranks (LuckPerms group ids) that unlock each aura; higher ranks include lower auras.
    VIP("VIP", "&6", Material.GOLD_INGOT, "Altın ışık halkaları ve parıltılar.", "VIP",
            List.of("vip", "vipplus", "megavip", "megavipplus", "tilki", "kurucu")),
    MEGAVIP("MegaVIP", "&d", Material.AMETHYST_SHARD, "Mor enerji sarmalı ve kristaller.", "MegaVIP",
            List.of("megavip", "megavipplus", "tilki", "kurucu")),
    TILKI("Tilki", "&6", Material.FIRE_CHARGE, "Ateş tilkisi, taç ve alev çemberi.", "Tilki",
            List.of("tilki", "kurucu"));

    public final String displayName;
    public final String color;
    public final Material material;
    public final String description;
    public final String defaultTag;
    public final List<String> defaultRanks;

    AuraType(String name, String color, Material material, String description, String defaultTag, List<String> defaultRanks) {
        this.displayName = name;
        this.color = color;
        this.material = material;
        this.description = description;
        this.defaultTag = defaultTag;
        this.defaultRanks = defaultRanks;
    }

    public String id() { return name().toLowerCase(Locale.ROOT); }
}
