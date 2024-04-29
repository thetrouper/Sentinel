package io.github.thetrouper.sentinel;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.itzispyder.pdk.PDK;
import io.github.itzispyder.pdk.utils.misc.JsonSerializable;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistStorage;
import io.github.thetrouper.sentinel.data.config.*;
import io.github.thetrouper.sentinel.server.functions.Authenticator;
import io.github.thetrouper.sentinel.server.functions.Load;
import io.github.thetrouper.sentinel.server.functions.Telemetry;
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

    Load load = new Load();

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

        load.load(license,serverID);
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

    public static boolean isTrusted(Player player) {
        return Sentinel.mainConfig.plugin.trustedPlayers.contains(player.getUniqueId().toString());
    }

    public static boolean isTrusted(String uuid) {
        return Sentinel.mainConfig.plugin.trustedPlayers.contains(uuid);
    }

    public static Sentinel getInstance() {
        return instance;
    }

}
