package com.auracosmetics.aura;

import org.bukkit.Material;

public record Aura(AuraType type) {
    public String id() { return type.id(); }
    public String displayName() { return type.displayName; }
    public Material material() { return type.material; }
    public String description() { return type.description; }
}
