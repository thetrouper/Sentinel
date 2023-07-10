/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel;

import io.github.thetrouper.sentinel.commands.InfoCommand;
import io.github.thetrouper.sentinel.commands.ReopCommand;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.events.ChatEvent;
import io.github.thetrouper.sentinel.events.CmdBlockEvents;
import io.github.thetrouper.sentinel.events.CommandEvent;
import io.github.thetrouper.sentinel.events.NBTEvents;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.Authenticator;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is your main class, you register everything important here.
 *
 * To build the jar, go to terminal and run "./gradlew build"
 */
public final class Sentinel extends JavaPlugin {

    public static final PluginManager manager = Bukkit.getPluginManager();
    public static String prefix = "";
    public static final Logger log = Bukkit.getLogger();

    /**
     * Plugin startup logic
     */
    @Override
    public void onEnable() {
        log.info("Your server ID is: " + Authenticator.getServerID());
        switch (Authenticator.authorize(Config.license, Authenticator.getServerID())) {
            case "AUTHORIZED" -> {
                log.info("Authentication Success!");
            }
            case "INVALID-ID" -> {
                log.info("Authentication Failure, You have not whitelisted this server ID yet.");
            }
            case "UNREGISTERED" -> {
                log.info("YOU SHALL NOT PASS! License: " + Config.license + " Server ID: " + Authenticator.getServerID());
                throw new IllegalStateException("YOU SHALL NOT PASS! License: " + Config.license + " Server ID: " + Authenticator.getServerID());
            }
        }
        // Files
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Plugin startup logic
        Config.loadConfiguration();
        log.info("Sentinel has loaded! (" + getDescription().getVersion() + ")");

        // Enable Functions
        AntiSpam.enableAntiSpam();
        ProfanityFilter.enableAntiSwear();

        prefix = Config.Plugin.getPrefix();

        // Commands -> BE SURE TO REGISTER ANY NEW COMMANDS IN PLUGIN.YML (src/main/java/resources/plugin.yml)!
        getCommand("sentinel").setExecutor(new InfoCommand());
        getCommand("sentinel").setTabCompleter(new InfoCommand());
        getCommand("reop").setExecutor(new ReopCommand());

        // Events
        manager.registerEvents(new CommandEvent(),this);
        manager.registerEvents(new CmdBlockEvents(), this);
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
    }
    public static File getDF() {
        return getInstance().getDataFolder();
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
        return Config.loggedCommands.contains(command);
    }

    /**
     * Checks if a command is dangerous.
     * @param command the command to check
     * @return true if the command is dangerous, false otherwise
     */
    public static boolean isDangerousCommand(String command) {
        return Config.dangerousCommands.contains(command);
    }
    /**
     * Returns an instance of this plugin
     * @return an instance of this plugin
     */
    public static Plugin getInstance() {
        return manager.getPlugin("Sentinel");
    }

}
