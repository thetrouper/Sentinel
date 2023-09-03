/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel;

import io.github.thetrouper.sentinel.commands.*;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.events.*;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.Authenticator;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.Telemetry;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.Randomizer;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * This is your main class, you register everything important here.
 *
 * To build the jar, go to terminal and run "./gradlew build"
 */
public final class Sentinel extends JavaPlugin {
    private static Sentinel instance;

    public static final PluginManager manager = Bukkit.getPluginManager();
    public static String prefix = "";
    public static String key = "";
    public static final Logger log = Bukkit.getLogger();
    public static String identifier = "";
    public static boolean usesDynamicIP;

    /**
     * Plugin startup logic
     */
    @Override
    public void onEnable() {
        log.info("\n]======------ Pre-load started! ------======[");
        String hook = Telemetry.loadTelemetryHook();
        instance = this;
        Config.loadConfiguration();
        String serverID = Authenticator.getServerID();
        identifier = serverID;
        log.info("\n]====---- Requesting Authentication ----====[ \n- license Key: " + key + " \n- Server ID: " + serverID);
        String authStatus = "ERROR";
        try {
            authStatus = Authenticator.authorize(key, serverID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (authStatus) {
            case "AUTHORIZED" -> {
                startup();
            }
            case "MINEHUT" -> {
                usesDynamicIP = true;
                String minehutStatus = Telemetry.loadTelemetryHook(serverID, key);
                switch (minehutStatus) {
                    case "SUCCESS" -> {
                        log.info("Dynamic IP auth Success!");
                        startup();
                    }
                    case "FAILURE" -> {
                        log.info("Dynamic IP Failure. Webhook Error possible? Please contact obvwolf to fix this.");
                        getServer().getPluginManager().disablePlugin(this);
                    }
                }
            }
            case "INVALID-ID" -> {
                log.info("Authentication Failure, You have not whitelisted this server ID yet.");
                getServer().getPluginManager().disablePlugin(this);
            }
            case "UNREGISTERED" -> {
                log.warning("Authentication Failure, YOU SHALL NOT PASS! License: " + key + " Server ID: " + serverID);
                getServer().getPluginManager().disablePlugin(this);
            }
            case "ERROR" -> {
                log.warning("Hmmmmmm thats not right... License: " + key + " Server ID: " + serverID + "\nPlease report the above stacktrace.");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    private void startup() {
        log.info("\n]======----- Auth Success! -----======[");
        // Init
        getConfig().options().copyDefaults();
        saveDefaultConfig();


        // Plugin startup logic
        log.info("Sentinel has loaded! (" + getDescription().getVersion() + ")");

        // Enable Functions
        AntiSpam.enableAntiSpam();
        ProfanityFilter.enableAntiSwear();

        prefix = Config.Plugin.getPrefix();

        // Commands -> BE SURE TO REGISTER ANY NEW COMMANDS IN PLUGIN.YML (src/main/java/resources/plugin.yml)!
        new SentinelCommand().register();
        new MessageCommand().register();
        new ReplyCommand().register();
        new ReopCommand().register();
        new SocialSpyCommand().register();

        // Events
        manager.registerEvents(new CommandEvent(),this);
        manager.registerEvents(new CMDBlockExecute(), this);
        manager.registerEvents(new CMDBlockPlace(), this);
        manager.registerEvents(new CMDBlockUse(), this);
        manager.registerEvents(new CMDMinecartPlace(), this);
        manager.registerEvents(new CMDMinecartUse(), this);
        manager.registerEvents(new NBTEvents(), this);
        manager.registerEvents(new ChatEvent(),this);

        // Scheduled timers
        Bukkit.getScheduler().runTaskTimer(this, AntiSpam::decayHeat,0, 20);
        Bukkit.getScheduler().runTaskTimer(this, ProfanityFilter::decayScore,0,1200);
        log.info("\n" +
                " ____                   __                        ___      \n" +
                "/\\  _`\\                /\\ \\__  __                /\\_ \\     \n" +
                "\\ \\,\\L\\_\\     __    ___\\ \\ ,_\\/\\_\\    ___      __\\//\\ \\    \n" +
                " \\/_\\__ \\   /'__`\\/' _ `\\ \\ \\/\\/\\ \\ /' _ `\\  /'__`\\\\ \\ \\   \n" +
                "   /\\ \\L\\ \\/\\  __//\\ \\/\\ \\ \\ \\_\\ \\ \\/\\ \\/\\ \\/\\  __/ \\_\\ \\_ \n" +
                "   \\ `\\____\\ \\____\\ \\_\\ \\_\\ \\__\\\\ \\_\\ \\_\\ \\_\\ \\____\\/\\____\\\n" +
                "    \\/_____/\\/____/\\/_/\\/_/\\/__/ \\/_/\\/_/\\/_/\\/____/\\/____/\n" +
                "     ]====---- Advanced Anti-Grief & Chat Filter ----====[");
    }

    /**
     * Plugin shutdown logic
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Sentinel has disabled! (" + getDescription().getVersion() + ") Your server is now no longer protected!");
        Telemetry.sendShutdownLog(identifier,key);
    }

    /**
     * Checks if a player is trusted.
     * @param player the player to check
     * @return true if the player is trusted, false otherwise
     */
    public static boolean isTrusted(Player player) {
        return Config.trustedPlayers.contains(player.getUniqueId().toString());
    }

    /**
     * Checks if a command is a logged command.
     * @param command the command to check
     * @return true if the command is logged, false otherwise
     */
    public static boolean isLoggedCommand(String command) {
        return Config.logged.contains(command);
    }

    /**
     * Checks if a command is dangerous.
     * @param command the command to check
     * @return true if the command is dangerous, false otherwise
     */
    public static boolean isDangerousCommand(String command) {
        return Config.dangerous.contains(command);
    }
    /**
     * Returns an instance of this plugin
     * @return an instance of this plugin
     */
    public static Sentinel getInstance() {
        return instance;
    }

}
