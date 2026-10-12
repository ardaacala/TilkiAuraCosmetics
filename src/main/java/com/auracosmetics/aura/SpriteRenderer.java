package com.auracosmetics.aura;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Animates private-use bitmap glyphs from the companion resource pack. */
public final class SpriteRenderer {
    private static final String MARKER = "auracosmetics_sprite";
    private static final char GOLD_RING = '\uE900';
    private static final char VIOLET_RING = '\uE901';
    private static final char GOLD_SPARK = '\uE902';
    private static final char VIOLET_SPARK = '\uE903';
    private static final char CRYSTAL = '\uE904';
    private static final char RIBBON = '\uE905';
    private static final char FOX = '\uE906';
    private static final char CROWN = '\uE907';
    private static final char FLAME = '\uE908';

    private final Map<UUID, EffectSet> active = new HashMap<>();
    private int interval = 2;

    public void setInterval(int interval) { this.interval = interval; }

    public void removeOrphans() {
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                if (display.getScoreboardTags().contains(MARKER)) display.remove();
            }
        }
    }

    public void render(Player player, AuraType type, long tick) {
        UUID id = player.getUniqueId();
        EffectSet set = active.get(id);
        if (set == null || set.type != type || set.world != player.getWorld() || !set.valid()) {
            remove(id);
            set = create(player, type);
            active.put(id, set);
        }
        switch (type) {
            case VIP -> vip(player, set, tick);
            case MEGAVIP -> megaVip(player, set, tick);
            case TILKI -> tilki(player, set, tick);
        }
    }

    public void remove(UUID id) {
        EffectSet set = active.remove(id);
        if (set != null) set.displays.forEach(TextDisplay::remove);
    }

    public void clear() {
        for (UUID id : List.copyOf(active.keySet())) remove(id);
    }

    private EffectSet create(Player player, AuraType type) {
        EffectSet set = new EffectSet(type, player.getWorld());
        switch (type) {
            case VIP -> {
                add(set, player, GOLD_RING, true, 1.65f);
                add(set, player, GOLD_RING, true, 1.35f);
                for (int i = 0; i < 6; i++) add(set, player, GOLD_SPARK, false, 0.45f);
            }
            case MEGAVIP -> {
                add(set, player, VIOLET_RING, true, 1.8f);
                for (int i = 0; i < 4; i++) add(set, player, CRYSTAL, false, 0.75f);
                for (int i = 0; i < 12; i++) add(set, player, RIBBON, false, 0.52f);
                for (int i = 0; i < 4; i++) add(set, player, VIOLET_SPARK, false, 0.42f);
            }
            case TILKI -> {
                add(set, player, GOLD_RING, true, 1.8f);
                add(set, player, FOX, false, 2.15f);
                add(set, player, CROWN, false, 0.8f);
                for (int i = 0; i < 8; i++) add(set, player, FLAME, false, 0.7f);
                for (int i = 0; i < 5; i++) add(set, player, GOLD_SPARK, false, 0.5f);
            }
        }
        return set;
    }

    private void add(EffectSet set, Player player, char glyph, boolean horizontal, float scale) {
        TextDisplay display = player.getWorld().spawn(player.getLocation(), TextDisplay.class, entity -> {
            entity.setText(String.valueOf(glyph));
            entity.setDefaultBackground(false);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            entity.setShadowed(false);
            entity.setSeeThrough(false);
            entity.setBillboard(horizontal ? Display.Billboard.FIXED : Display.Billboard.CENTER);
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setViewRange(2.0f);
            entity.setShadowRadius(0);
            entity.setPersistent(false);
            entity.addScoreboardTag(MARKER);
            entity.setTeleportDuration(interval);
            entity.setInterpolationDuration(interval);
            entity.setTransformation(new Transformation(new Vector3f(),
                    horizontal ? new Quaternionf().rotateX((float) (Math.PI / 2)) : new Quaternionf(),
                    new Vector3f(scale), new Quaternionf()));
        });
        set.displays.add(display);
    }

    private static void place(Player player, TextDisplay display, double x, double y, double z) {
        Location at = player.getLocation();
        at.add(x, y, z);
        display.teleport(at);
    }

    private static void ring(TextDisplay display, float scale, double angle) {
        display.setTransformation(new Transformation(new Vector3f(),
                new Quaternionf().rotateY((float) angle).rotateX((float) (Math.PI / 2)),
                new Vector3f(scale), new Quaternionf()));
    }

    private static void vip(Player player, EffectSet set, long tick) {
        double phase = tick * 0.018;
        place(player, set.get(0), 0, 1.12 + Math.sin(tick * 0.05) * 0.04, 0);
        ring(set.get(0), 1.65f, phase);
        place(player, set.get(1), 0, 0.14, 0);
        ring(set.get(1), 1.35f, -phase * 0.7);
        for (int i = 0; i < 6; i++) {
            double a = phase * (i % 2 == 0 ? 1 : -1) + i * Math.PI / 3;
            double y = 0.24 + ((tick * 0.012 + i * 0.19) % 1.0) * 1.65;
            place(player, set.get(i + 2), Math.cos(a) * 0.9, y, Math.sin(a) * 0.9);
        }
    }

    private static void megaVip(Player player, EffectSet set, long tick) {
        double phase = tick * 0.024;
        place(player, set.get(0), 0, 0.11, 0);
        ring(set.get(0), 1.8f, -phase * 0.5);
        for (int i = 0; i < 4; i++) {
            double a = phase * 0.55 + i * Math.PI / 2;
            place(player, set.get(i + 1), Math.cos(a) * 1.05,
                    0.75 + (i % 2) * 0.95 + Math.sin(phase * 2 + i) * 0.1, Math.sin(a) * 1.05);
        }
        for (int i = 0; i < 12; i++) {
            double fraction = i / 12.0;
            double a = phase + fraction * Math.PI * 3.6 + (i % 2) * Math.PI;
            place(player, set.get(i + 5), Math.cos(a) * 0.72,
                    0.25 + fraction * 1.9, Math.sin(a) * 0.72);
        }
        for (int i = 0; i < 4; i++) {
            double a = phase + i * Math.PI / 2;
            place(player, set.get(i + 17), Math.cos(a) * 1.22,
                    0.45 + ((tick * 0.01 + i * 0.25) % 1.0) * 1.6, Math.sin(a) * 1.22);
        }
    }

    private static void tilki(Player player, EffectSet set, long tick) {
        double phase = tick * 0.017;
        place(player, set.get(0), 0, 0.1, 0);
        ring(set.get(0), 1.8f, phase);
        double yaw = Math.toRadians(player.getLocation().getYaw());
        place(player, set.get(1), Math.sin(yaw) * 0.82, 1.2 + Math.sin(tick * 0.04) * 0.05,
                -Math.cos(yaw) * 0.82);
        place(player, set.get(2), 0, 2.55 + Math.sin(tick * 0.055) * 0.05, 0);
        for (int i = 0; i < 8; i++) {
            double a = phase + i * Math.PI / 4;
            place(player, set.get(i + 3), Math.cos(a) * (1.03 + 0.12 * Math.sin(phase * 2 + i)),
                    0.35 + (i % 4) * 0.35 + Math.sin(phase * 2 + i) * 0.13,
                    Math.sin(a) * 1.03);
        }
        for (int i = 0; i < 5; i++) {
            double a = -phase + i * Math.PI * 2 / 5;
            place(player, set.get(i + 11), Math.cos(a) * 1.4,
                    0.35 + ((tick * 0.011 + i * 0.2) % 1.0) * 2.0, Math.sin(a) * 1.4);
        }
    }

    private static final class EffectSet {
        final AuraType type;
        final World world;
        final List<TextDisplay> displays = new ArrayList<>();

        EffectSet(AuraType type, World world) { this.type = type; this.world = world; }
        TextDisplay get(int index) { return displays.get(index); }
        boolean valid() { return displays.stream().allMatch(TextDisplay::isValid); }
    }
}
