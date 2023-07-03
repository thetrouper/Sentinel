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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
    public static String webhook;
    public static List<String> trustedPlayers;
    public static boolean blockSpecificCommands;
    public static boolean preventNBT;
    public static boolean logNBT;
    public static boolean preventCmdBlocks;
    public static boolean logCmdBlocks;
    public static List<String> dangerousCommands;
    public static boolean logDangerousCommands;
    public static List<String> loggedCommands;
    public static boolean deop;
    public static boolean ban;
    public static boolean reopCommand;
    /**
     * Plugin startup logic
     */
    @Override
    public void onEnable() {

        // Files
        getConfig().options().copyDefaults();
        saveDefaultConfig(); 

        // Plugin startup logic
        loadConfiguration();
        log.info("Sentinel has loaded! (" + getDescription().getVersion() + ")");
        // Enable Functions
        AntiSpam.enableAntiSpam();

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
    private void loadConfiguration() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Load prefix
        prefix = config.getString("config.plugin.prefix");

        // Load webhook
        webhook = config.getString("config.plugin.webhook");

        // Load trusted players
        trustedPlayers = config.getStringList("config.plugin.trusted");

        // Load block-specific commands
        blockSpecificCommands = config.getBoolean("config.plugin.block-specific");

        // Load prevent NBT
        preventNBT = config.getBoolean("config.plugin.prevent-nbt");

        // Load log NBT
        logNBT = config.getBoolean("config.plugin.log-nbt");

        // Load prevent command blocks
        preventCmdBlocks = config.getBoolean("config.plugin.prevent-cmdblocks");

        // Load log command blocks
        logCmdBlocks = config.getBoolean("config.plugin.log-cmdblocks");

        // Load dangerous commands
        dangerousCommands = config.getStringList("config.plugin.dangerous");

        // Load log protected commands
        logDangerousCommands = config.getBoolean("config.plugin.log-dangerous");

        // Load logged commands
        loggedCommands = config.getStringList("config.plugin.logged");

        deop = config.getBoolean("config.plugin.deop");
        ban = config.getBoolean("config.plugin.ban");
        reopCommand = config.getBoolean("config.plugin.reop-command");
    }
    /**
     * Checks if a player is trusted.
     * @param player the player to check
     * @return true if the player is trusted, false otherwise
     */
    public static boolean isTrusted(Player player) {
        return trustedPlayers.contains(player.getUniqueId().toString());
    }

    /**
     * Checks if a command is a logged command.
     * @param command the command to check
     * @return true if the command is logged, false otherwise
     */
    public static boolean isLoggedCommand(String command) {
        return loggedCommands.contains(command);
    }

    /**
     * Checks if a command is dangerous.
     * @param command the command to check
     * @return true if the command is dangerous, false otherwise
     */
    public static boolean isDangerousCommand(String command) {
        return dangerousCommands.contains(command);
    }
    /**
     * Returns an instance of this plugin
     * @return an instance of this plugin
     */
    public static Plugin getInstance() {
        return manager.getPlugin("Sentinel");
    }

}
