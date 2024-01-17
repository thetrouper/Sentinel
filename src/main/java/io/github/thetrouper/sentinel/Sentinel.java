package io.github.thetrouper.sentinel;

import io.github.itzispyder.pdk.PDK;
import io.github.thetrouper.sentinel.auth.Auth;
import io.github.thetrouper.sentinel.cmds.*;
import io.github.thetrouper.sentinel.events.*;
import io.github.thetrouper.sentinel.server.config.Config;
import io.github.thetrouper.sentinel.server.config.LanguageFile;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.Authenticator;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.Telemetry;
import io.github.thetrouper.sentinel.server.util.JsonSerializable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class Sentinel extends JavaPlugin {
    private static Sentinel instance;
    public static LanguageFile dict;
    private static File cfgfile = new File("plugins/Sentinel/main-config.json");
    public static MainConfig mainConfig = JsonSerializable.load(cfgfile, MainConfig.class, new MainConfig());
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
        PDK.init(this);
        instance = this;
        log.info("Loading Config...");
        loadConfig();
        log.info("Initializing Server ID...");
        String serverID = Authenticator.getServerID();
        identifier = serverID;
        log.info("Pre-load finished!\n]====---- Requesting Authentication (" + dict.get("example-message") + ") ----====[ \n- License Key: " + key + " \n- Server ID: " + serverID);
        String authStatus = "ERROR";
        String authstatus = "ERROR";
        try {
            authStatus = Authenticator.authorize(key, serverID);
            authstatus = Auth.authorize(key,serverID);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("WTFFFF ARE YOU DOING MAN??????");
            manager.disablePlugin(this);
        }
        switch (authStatus) {
            case "AUTHORIZED" -> {
                log.info("\n]======----- Auth Success! -----======[");
                startup();
                authstatus = authstatus.replaceAll("ur a skid lmao","get out of here kiddo");
            }
            case "MINEHUT" -> {
                usesDynamicIP = true;
                String minehutStatus = Telemetry.loadTelemetryHook(serverID, key);
                switch (minehutStatus) {
                    case "SUCCESS" -> {
                        log.info("Dynamic IP auth Success! " + authstatus);
                        startup();
                    }
                    case "FAILURE" -> {
                        log.info("Dynamic IP Failure. Webhook Error possible? Please contact obvWolf to fix this.");
                        manager.disablePlugin(this);
                    }
                }
            }
            case "INVALID-ID" -> {
                log.info("Authentication Failure, You have not whitelisted this server ID yet.");
                manager.disablePlugin(this);
            }
            case "UNREGISTERED" -> {
                log.warning("Authentication Failure, YOU SHALL NOT PASS! License: " + key + " Server ID: " + serverID);
                manager.disablePlugin(this);
            }
            case "ERROR" -> {
                log.warning("Hmmmmmm thats not right... License: " + key + " Server ID: " + serverID + "\nPlease report the above stacktrace.");
                manager.disablePlugin(this);
            }
        }
    }

    public void startup() {
        log.info("\n]======----- Loading Sentinel! -----======[");
        loadConfig();
        // Plugin startup logic
        log.info("Starting Up! (" + getDescription().getVersion() + ")...");

        // Enable Functions
        AntiSpam.enableAntiSpam();
        ProfanityFilter.enableAntiSwear();

        prefix = MainConfig.Plugin.prefix;

        // Commands -> BE SURE TO REGISTER ANY NEW COMMANDS IN PLUGIN.YML (src/main/java/resources/plugin.yml)!
        new SentinelCommand().register();
        new MessageCommand().register();
        new ReplyCommand().register();
        new ReopCommand().register();
        new SocialSpyCommand().register();
        new ChatClickCallback().register();

        // Events
        new ChatEvent().register();
        new CommandEvent().register();
        new CMDBlockExecute().register();
        new CMDBlockPlace().register();
        new CMDBlockUse().register();
        new CMDMinecartPlace().register();
        new CMDMinecartUse().register();
        new NBTEvents().register();


        // Scheduled timers
        Bukkit.getScheduler().runTaskTimer(this, AntiSpam::decayHeat,0, 20);
        Bukkit.getScheduler().runTaskTimer(this, ProfanityFilter::decayScore,0,1200);
        log.info("Finished!\n" +
                " ____                   __                        ___      \n" +
                "/\\  _`\\                /\\ \\__  __                /\\_ \\     \n" +
                "\\ \\,\\L\\_\\     __    ___\\ \\ ,_\\/\\_\\    ___      __\\//\\ \\    \n" +
                " \\/_\\__ \\   /'__`\\/' _ `\\ \\ \\/\\/\\ \\ /' _ `\\  /'__`\\\\ \\ \\   \n" +
                "   /\\ \\L\\ \\/\\  __//\\ \\/\\ \\ \\ \\_\\ \\ \\/\\ \\/\\ \\/\\  __/ \\_\\ \\_ \n" +
                "   \\ `\\____\\ \\____\\ \\_\\ \\_\\ \\__\\\\ \\_\\ \\_\\ \\_\\ \\____\\/\\____\\\n" +
                "    \\/_____/\\/____/\\/_/\\/_/\\/__/ \\/_/\\/_/\\/_/\\/____/\\/____/\n" +
                "     ]====---- Advanced Anti-Grief & Chat Filter ----====[");
    }

    public void loadConfig() {
        // Init

        log.info("Loading Dictionary (" + MainConfig.Plugin.lang + ")...");
        dict = JsonSerializable.load(LanguageFile.PATH,LanguageFile.class,new LanguageFile());

        log.info("Verifying Config...");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    /**
     * Plugin shutdown logic
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Sentinel has disabled! (" + getDescription().getVersion() + ") Your server is now no longer protected!");
        if (usesDynamicIP) {
            Telemetry.sendShutdownLog(identifier,key);
        }
    }

    /**
     * Checks if a player is trusted.
     * @param player the player to check
     * @return true if the player is trusted, false otherwise
     */
    public static boolean isTrusted(Player player) {
        return MainConfig.Plugin.trustedPlayers.contains(player.getUniqueId().toString());
    }

    /**
     * Checks if a command is a logged command.
     * @param command the command to check
     * @return true if the command is logged, false otherwise
     */
    public static boolean isLoggedCommand(String command) {
        return MainConfig.Plugin.logged.contains(command);
    }

    /**
     * Checks if a command is dangerous.
     * @param command the command to check
     * @return true if the command is dangerous, false otherwise
     */
    public static boolean isDangerousCommand(String command) {
        return MainConfig.Plugin.dangerous.contains(command);
    }
    /**
     * Returns an instance of this plugin
     * @return an instance of this plugin
     */
    public static Sentinel getInstance() {
        return instance;
    }

}
