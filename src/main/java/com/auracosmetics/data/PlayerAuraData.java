package com.auracosmetics.data;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;

/** YAML access is on the main thread; a single writer persists immutable snapshots off-thread. */
public final class PlayerAuraData implements AutoCloseable {
    public record Selection(String aura, boolean enabled) { }
    private final JavaPlugin plugin;
    private final Path file;
    private final YamlConfiguration yaml = new YamlConfiguration();
    private final ExecutorService writer = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "AuraCosmetics-data-writer");
        thread.setDaemon(true); return thread;
    });
    private final java.util.concurrent.atomic.AtomicReference<String> pending = new java.util.concurrent.atomic.AtomicReference<>();
    private final java.util.concurrent.atomic.AtomicBoolean writing = new java.util.concurrent.atomic.AtomicBoolean();
    private boolean closing;

    public PlayerAuraData(JavaPlugin plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        file = plugin.getDataFolder().toPath().resolve("playerdata.yml");
        Files.createDirectories(file.getParent());
        if (Files.exists(file)) yaml.load(file.toFile());
        else Files.writeString(file, "players: {}\n", StandardCharsets.UTF_8);
    }
    public Selection get(UUID uuid) {
        String path = "players." + uuid;
        String aura = yaml.getString(path + ".aura");
        return aura == null ? null : new Selection(aura, yaml.getBoolean(path + ".enabled", true));
    }
    public void set(UUID uuid, Selection selection) {
        String path = "players." + uuid;
        if (selection == null) yaml.set(path, null);
        else { yaml.set(path + ".aura", selection.aura()); yaml.set(path + ".enabled", selection.enabled()); }
        save();
    }
    public void save() {
        pending.set(yaml.saveToString());
        scheduleWriter();
    }
    private synchronized void scheduleWriter() {
        if (!closing && writing.compareAndSet(false, true)) writer.execute(this::drain);
    }
    private void drain() {
        try {
            String snapshot;
            while ((snapshot = pending.getAndSet(null)) != null) {
                Path temporary = file.resolveSibling("playerdata.yml.tmp");
                try {
                    Files.writeString(temporary, snapshot, StandardCharsets.UTF_8);
                    try { Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
                    catch (AtomicMoveNotSupportedException ignored) { Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING); }
                } catch (IOException ex) { plugin.getLogger().log(Level.SEVERE, "Could not persist player selections", ex); }
            }
        } finally {
            writing.set(false);
            if (pending.get() != null && !writer.isShutdown()) scheduleWriter();
        }
    }
    @Override public void close() {
        save();
        // Guarantee a final drain even if save races with the previous drain finishing.
        synchronized (this) {
            writer.execute(this::drain);
            closing = true;
            writer.shutdown();
        }
        try {
            if (!writer.awaitTermination(10, TimeUnit.SECONDS)) {
                writer.shutdownNow();
                plugin.getLogger().severe("Player data writer did not finish within 10 seconds.");
            }
        } catch (InterruptedException ex) { writer.shutdownNow(); Thread.currentThread().interrupt(); }
    }
}
