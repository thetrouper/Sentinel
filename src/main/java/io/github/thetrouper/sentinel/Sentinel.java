package io.github.thetrouper.sentinel;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.itzispyder.pdk.PDK;
import io.github.itzispyder.pdk.utils.misc.JsonSerializable;
import io.github.thetrouper.sentinel.auth.Auth;
import io.github.thetrouper.sentinel.cmds.*;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistStorage;
import io.github.thetrouper.sentinel.data.config.*;
import io.github.thetrouper.sentinel.events.*;
import io.github.thetrouper.sentinel.server.functions.*;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class Sentinel extends JavaPlugin {

    private static Sentinel instance;
    private static final File cfgfile = new File("plugins/Sentinel/main-config.json");
    private static final File nbtcfg = new File("plugins/Sentinel/nbt-config.json");
    private static final File strctcfg = new File("plugins/Sentinel/strict.json");
    private static final File swrcfg = new File("plugins/Sentinel/swears.json");
    private static final File fpcfg = new File("plugins/Sentinel/false-positives.json");
    private static final File advcfg = new File("plugins/Sentinel/advanced-config.json");
    private static final File cmdWhitelist = new File("plugins/Sentinel/storage/whitelist.json");
    public static WhitelistStorage whitelist = JsonSerializable.load(cmdWhitelist, WhitelistStorage.class, new WhitelistStorage());

    public static MainConfig mainConfig = JsonSerializable.load(cfgfile, MainConfig.class, new MainConfig());
    public static FPConfig fpConfig = JsonSerializable.load(fpcfg, FPConfig.class, new FPConfig());
    public static SwearsConfig swearConfig = JsonSerializable.load(swrcfg, SwearsConfig.class, new SwearsConfig());
    public static StrictConfig strictConfig = JsonSerializable.load(strctcfg, StrictConfig.class, new StrictConfig());
    public static NBTConfig nbtConfig = JsonSerializable.load(nbtcfg, NBTConfig.class, new NBTConfig());
    public static AdvancedConfig advConfig = JsonSerializable.load(advcfg, AdvancedConfig.class, new AdvancedConfig());
    public static LanguageFile lang;
    public static ProtocolManager protocolManager;
    public static final PluginManager manager = Bukkit.getPluginManager();

    public static final Logger log = Bukkit.getLogger();
    public static boolean usesDynamicIP;
    public static String serverID;
    public static String license;
    public static String IP;
    public static boolean doNoPlugins = false;

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

        log.info("Loading ProtocolLib");

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib") && mainConfig.plugin.pluginHider) {
            doNoPlugins = true;
            protocolManager = ProtocolLibrary.getProtocolManager();
        } else {
            doNoPlugins = false;
            log.warning("Sentinel: ProtocolLib not found. Sentinel will not attempt to hide your plugins.");
        }

        log.info("Language Status: (%s)".formatted(lang.brokenLang));

        log.info("Initializing Server ID...");
        serverID = Authenticator.getServerID();

        license = mainConfig.plugin.license;

        log.info("Pre-load finished!\n]====---- Requesting Authentication ----====[ \n- License Key: %s\n- Server ID: %s".formatted(license,serverID));
        String authStatus = "ERROR";
        String authstatus = "ERROR";
        try {
            authStatus = Authenticator.authorize(license, serverID);
            authstatus = Auth.authorize(license, serverID);
            IP = Authenticator.getPublicIPAddress();
            log.info("Auth Requested...");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("WTFFFF ARE YOU DOING MAN??????");
            manager.disablePlugin(this);
        }
        switch (authStatus) {
            case "AUTHORIZED" -> {
                log.info("\n]======----- Auth Success! -----======[");
                startup();
            }
            case "MINEHUT" -> {
                usesDynamicIP = true;
                Telemetry.initTelemetryHook();
                boolean minehutStatus = Telemetry.sendStartupLog();
                if (minehutStatus) {
                    authstatus = authstatus.replaceAll("ur a skid lmao", "get out of here kiddo");
                    ServerUtils.sendDebugMessage(authstatus);
                    log.info("Dynamic IP auth Success! ");
                    startup();
                } else {
                    log.info("Dynamic IP Failure. Webhook Error possible? Please contact obvWolf to fix this.");
                    manager.disablePlugin(this);
                }
            }
            case "INVALID-ID" -> {
                log.info("Authentication Failure, You have not whitelisted this server ID yet.");
                manager.disablePlugin(this);
            }
            case "UNREGISTERED" -> {
                log.warning("Authentication Failure, YOU SHALL NOT PASS! License: %s Server ID: %s".formatted(license,serverID));
                manager.disablePlugin(this);
            }
            case "ERROR" -> {
                log.warning("Hmmmmmm thats not right... License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,serverID));
                manager.disablePlugin(this);
            }
            default -> {
                log.warning("Achievement unlocked: How did we get here? License: %s Server ID: %s\nPlease report the above stacktrace.".formatted(license,serverID));
                manager.disablePlugin(this);
            }
        }
    }

    public void startup() {
        log.info("\n]======----- Loading Sentinel! -----======[");

        // Plugin startup logic
        log.info("Starting Up! (%s)...".formatted(getDescription().getVersion()));

        // Commands
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
        new MiscEvents().register();
        if (doNoPlugins) {
            new TrapCommand().register();
            new PluginHiderEvents().register();
            TabCompleteEvent.registerEvent(this);
        }

        // Scheduled timers
        Bukkit.getScheduler().runTaskTimer(this, AntiSpam::decayHeat,0, 20);
        Bukkit.getScheduler().runTaskTimer(this, ProfanityFilter::decayScore,0,1200);
        log.info("""
                Finished!
                 ____                   __                        ___     \s
                /\\  _`\\                /\\ \\__  __                /\\_ \\    \s
                \\ \\,\\L\\_\\     __    ___\\ \\ ,_\\/\\_\\    ___      __\\//\\ \\   \s
                 \\/_\\__ \\   /'__`\\/' _ `\\ \\ \\/\\/\\ \\ /' _ `\\  /'__`\\\\ \\ \\  \s
                   /\\ \\L\\ \\/\\  __//\\ \\/\\ \\ \\ \\_\\ \\ \\/\\ \\/\\ \\/\\  __/ \\_\\ \\_\s
                   \\ `\\____\\ \\____\\ \\_\\ \\_\\ \\__\\\\ \\_\\ \\_\\ \\_\\ \\____\\/\\____\\
                    \\/_____/\\/____/\\/_/\\/_/\\/__/ \\/_/\\/_/\\/_/\\/____/\\/____/
                     ]====---- Advanced Anti-Grief & Chat Filter ----====[""");
    }

    public void loadConfig() {

        // Init
        mainConfig = JsonSerializable.load(cfgfile,MainConfig.class,new MainConfig());
        advConfig = JsonSerializable.load(advcfg,AdvancedConfig.class,new AdvancedConfig());
        fpConfig = JsonSerializable.load(fpcfg,FPConfig.class,new FPConfig());
        strictConfig = JsonSerializable.load(strctcfg,StrictConfig.class,new StrictConfig());
        swearConfig = JsonSerializable.load(swrcfg,SwearsConfig.class,new SwearsConfig());
        nbtConfig = JsonSerializable.load(nbtcfg,NBTConfig.class,new NBTConfig());


        // Save
        mainConfig.save();
        advConfig.save();
        fpConfig.save();
        strictConfig.save();
        swearConfig.save();
        nbtConfig.save();

        whitelist = JsonSerializable.load(cmdWhitelist, WhitelistStorage.class, new WhitelistStorage());
        whitelist.save();

        log.info("Loading Dictionary (%s)...".formatted(Sentinel.mainConfig.plugin.lang));

        lang = JsonSerializable.load(LanguageFile.PATH,LanguageFile.class,new LanguageFile());
        lang.save();
    }


    /**
     * Plugin shutdown logic
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Sentinel has disabled! (%s) Your server is now no longer protected!".formatted(getDescription().getVersion()));
        if (usesDynamicIP) {
            Telemetry.sendShutdownLog();
        }
    }

    /**
     * Checks if a player is trusted.
     * @param player the player to check
     * @return true if the player is trusted, false otherwise
     */
    public static boolean isTrusted(Player player) {
        return Sentinel.mainConfig.plugin.trustedPlayers.contains(player.getUniqueId().toString());
    }

    public static boolean isTrusted(String uuid) {
        return Sentinel.mainConfig.plugin.trustedPlayers.contains(uuid);
    }

    /**
     * Checks if a command is a logged command.
     * @param command the command to check
     * @return true if the command is logged, false otherwise
     */
    public static boolean isLoggedCommand(String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        for (String logged : Sentinel.mainConfig.plugin.logged) {
            if (command.contains(logged)) return true;
        }
        return false;
    }

    /**
     * Checks if a command is dangerous.
     * @param command the command to check
     * @return true if the command is dangerous, false otherwise
     */
    public static boolean isDangerousCommand(String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        for (String blocked : Sentinel.mainConfig.plugin.dangerous) {
            if (command.startsWith(blocked)) return true;
        }
        return false;
    }
    /**
     * Returns an instance of this plugin
     * @return an instance of this plugin
     */
    public static Sentinel getInstance() {
        return instance;
    }

}
