package com.auracosmetics.aura;

import com.auracosmetics.AuraCosmeticsPlugin;
import com.auracosmetics.data.PlayerAuraData;
import com.auracosmetics.data.PlayerAuraData.Selection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;

public final class AuraManager implements Listener {
    private final AuraCosmeticsPlugin plugin;
    private final PlayerAuraData data;
    private final Map<String, Aura> auras = new LinkedHashMap<>();
    private final Map<UUID, Selection> selections = new HashMap<>();
    private final SpriteRenderer sprites = new SpriteRenderer();
    private BukkitTask task;
    private int interval = 2;
    private boolean spectator, invisible, persist;
    private long tick;

    public AuraManager(AuraCosmeticsPlugin plugin, PlayerAuraData data) {
        this.plugin = plugin; this.data = data;
        for (AuraType type : AuraType.values()) register(new Aura(type));
        sprites.removeOrphans();
    }
    private void register(Aura aura) { auras.put(aura.id(), aura); }
    public Collection<Aura> all() { return Collections.unmodifiableCollection(auras.values()); }
    public Aura find(String id) { return id == null ? null : auras.get(id.toLowerCase(Locale.ROOT)); }
    public Selection selection(UUID uuid) { return selections.get(uuid); }
    public String requiredTag(Aura aura) {
        return plugin.getConfig().getString("access.tags." + aura.id(), aura.type().defaultTag);
    }
    public boolean allowed(Player player, Aura aura) {
        return aura != null && player.hasPermission("auracosmetics.use")
                && player.getScoreboardTags().contains(requiredTag(aura));
    }
    public void reload() {
        if (task != null) task.cancel();
        interval = Math.clamp(plugin.getConfig().getInt("animation.interval-ticks", 2), 1, 20);
        sprites.setInterval(interval);
        spectator = plugin.getConfig().getBoolean("settings.disable-in-spectator", true);
        invisible = plugin.getConfig().getBoolean("settings.disable-while-invisible", false);
        boolean wasPersist = persist;
        persist = plugin.getConfig().getBoolean("settings.persist-selection", true);
        if (persist && !wasPersist) selections.forEach(data::set);
        sprites.clear();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::render, 1L, interval);
    }
    public void load(Player player) {
        Selection saved = persist ? data.get(player.getUniqueId()) : null;
        if (saved != null && find(saved.aura()) != null) selections.put(player.getUniqueId(), saved);
        else if (saved != null) {
            plugin.getLogger().warning("Discarding unknown aura '" + saved.aura() + "' for " + player.getUniqueId());
            data.set(player.getUniqueId(), null);
        }
    }
    public void enable(Player player, Aura aura) {
        if (!allowed(player, aura)) { plugin.message(player, "no-permission"); return; }
        update(player.getUniqueId(), new Selection(aura.id(), true));
        plugin.message(player, "aura-enabled", aura.displayName());
    }
    public void disable(Player player) {
        Selection current = selection(player.getUniqueId());
        if (current != null) update(player.getUniqueId(), new Selection(current.aura(), false));
        sprites.remove(player.getUniqueId());
        plugin.message(player, "aura-disabled");
    }
    public void toggle(Player player) {
        Selection current = selection(player.getUniqueId());
        if (current == null || find(current.aura()) == null) { plugin.message(player, "no-selection"); return; }
        if (current.enabled()) disable(player); else enable(player, find(current.aura()));
    }
    private void update(UUID uuid, Selection next) {
        selections.put(uuid, next);
        if (persist) data.set(uuid, next);
    }
    private void render() {
        tick += interval;
        var iterator = selections.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                sprites.remove(entry.getKey()); iterator.remove(); continue;
            }
            if (!entry.getValue().enabled() || player.isDead() || (spectator && player.getGameMode() == GameMode.SPECTATOR)
                    || (invisible && (player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)))) {
                sprites.remove(entry.getKey()); continue;
            }
            Aura aura = find(entry.getValue().aura());
            if (aura == null || !allowed(player, aura)) { sprites.remove(entry.getKey()); continue; }
            sprites.render(player, aura.type(), tick);
        }
    }
    @EventHandler public void onJoin(PlayerJoinEvent event) { load(event.getPlayer()); }
    @EventHandler public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        sprites.remove(id);
        selections.remove(id);
    }
    public void stop() {
        if (task != null) { task.cancel(); task = null; }
        if (persist) selections.forEach(data::set);
        selections.clear(); sprites.clear();
    }
}
