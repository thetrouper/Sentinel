package me.trouper.sentinel;

import com.github.retrooper.packetevents.PacketEvents;
import de.tr7zw.changeme.nbtapi.NBT;
import io.github.itzispyder.pdk.PDK;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.trouper.sentinel.server.Director;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Sentinel extends JavaPlugin {
    
    private static Sentinel instance;
    private Director director;
    
    public String identifier;
    public String license;
    public String nonce;
    public String ip;
    public int port;
    public String version;
    public String build;

    /* ]=- Sentinel Startup Flow -=[
    Make sure everything is done in sequence to avoid NullPointerException!
        1. onLoad
            - PacketEvents Loading & Registration
        2. onEnable
            - Init PacketEvents
            - Init NBT-API
            - Init PDK
            - Instantiate Sentinel
            - Instantiate Director
        3. Launch
            - Init DRM
            - Read Config
        4. Load
            - Run DRM Checks
            - Register Commands
            - Register Events
            - Register Timers
     */

    @Override
    public void onLoad() {
        version = getPluginMeta().getVersion().split("\\-")[0];
        build = getPluginMeta().getVersion().split("\\-")[1];
        getLogger().info("Build ID: %s".formatted(build));
        getLogger().info("\n]======------ Pre-load started ------======[");

        getLogger().info("Setting PacketEvents API");
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));

        getLogger().info("Loading PacketEvents");
        PacketEvents.getAPI().load();
    }
    
    @Override
    public void onEnable() {    
        getLogger().info("\n]======------ Loading Sentinel ------======[");

        getLogger().info("Initializing PacketEvents");

        PacketEvents.getAPI().init();

        getLogger().info("Pre-loading NBT-API");
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly. Sentinel may error out.");
        }

        getLogger().info("Initializing PDK");
        PDK.init(this);

        getLogger().info("Instantiating Sentinel");
        instance = this;

        Sentinel.getInstance().getLogger().info("Instantiating Director");
        director = new Director();

        director.launch();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
        getLogger().info("Sentinel has disabled! (%s) Your server is now no longer protected!".formatted(version));
    }

    public static Sentinel getInstance() {
        return instance;
    }
    
    public Director getDirector() {
        return director;
    }

    public NamespacedKey getNamespace(String key) {
        return new NamespacedKey(Sentinel.getInstance(), key);
    }
}
