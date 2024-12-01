package me.trouper.sentinel;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.itzispyder.pdk.PDK;
import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.data.WhitelistStorage;
import me.trouper.sentinel.data.config.*;
import me.trouper.sentinel.data.config.lang.LanguageFile;
import me.trouper.sentinel.startup.Auth;
import me.trouper.sentinel.startup.Load;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class Sentinel extends JavaPlugin {

    private static final File dataFolder = new File("plugins/SentinelAntiNuke");
    public static final Logger log = Bukkit.getLogger();
    private static Sentinel instance;
    private static final File violationcfg = new File(Sentinel .dataFolder(),"/violation-config.json");
    private static final File cfgfile = new File(Sentinel.dataFolder(),"/main-config.json");
    private static final File nbtcfg = new File(Sentinel.dataFolder(), "/nbt-config.json");
    private static final File strctcfg = new File(Sentinel.dataFolder(), "/strict.json");
    private static final File swrcfg = new File(Sentinel.dataFolder(), "/swears.json");
    private static final File fpcfg = new File(Sentinel.dataFolder(), "/false-positives.json");
    private static final File advcfg = new File(Sentinel.dataFolder(), "/advanced-config.json");
    private static final File cmdWhitelist = new File(Sentinel.dataFolder(), "/storage/whitelist.json");

    public static ViolationConfig violationConfig = JsonSerializable.load(violationcfg, ViolationConfig.class, new ViolationConfig());
    public static WhitelistStorage whitelist = JsonSerializable.load(cmdWhitelist, WhitelistStorage.class, new WhitelistStorage());
    public static MainConfig mainConfig = JsonSerializable.load(cfgfile, MainConfig.class, new MainConfig());
    public static FPConfig fpConfig = JsonSerializable.load(fpcfg, FPConfig.class, new FPConfig());
    public static SwearsConfig swearConfig = JsonSerializable.load(swrcfg, SwearsConfig.class, new SwearsConfig());
    public static StrictConfig strictConfig = JsonSerializable.load(strctcfg, StrictConfig.class, new StrictConfig());
    public static NBTConfig nbtConfig = JsonSerializable.load(nbtcfg, NBTConfig.class, new NBTConfig());
    public static AdvancedConfig advConfig = JsonSerializable.load(advcfg, AdvancedConfig.class, new AdvancedConfig());
    public static LanguageFile lang;
    public static File us;

    public static ProtocolManager protocolManager;
    public static boolean doNoPlugins = false;


    @Override
    public void onEnable() {
        log.info("\n]======------ Pre-load started! ------======[");

        PDK.init(this);
        instance = this;
        us = getFile();

        log.info("Loading Config...");

        loadConfig();

        log.info("Loading ProtocolLib");

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib") && mainConfig.plugin.pluginHider) {
            doNoPlugins = true;
            protocolManager = ProtocolLibrary.getProtocolManager();
        } else {
            doNoPlugins = false;
            log.warning("Sentinel: ProtocolLib not found. Sentinel will attempt to hide your plugins through Bukkit's systems, although it may not catch everything.");
        }

        log.info("Language Status: (%s)".formatted(lang.brokenLang));

        log.info("Initializing Server ID...");

        String serverID = Auth.getServerID();
        String license = Sentinel.mainConfig.plugin.license;
        String nonce = Auth.getNonce();
        int port = Auth.getPort();

        log.info("Pre-load finished!\n]====---- Requesting Authentication ----====[ \n- License Key: %s\n- Server ID: %s\n- Nonce: %s\n".formatted(license,serverID,nonce));

        Load.load(license,serverID);
    }

    public static void loadConfig() {

        // Init
        mainConfig = JsonSerializable.load(cfgfile,MainConfig.class,new MainConfig());
        advConfig = JsonSerializable.load(advcfg,AdvancedConfig.class,new AdvancedConfig());
        fpConfig = JsonSerializable.load(fpcfg,FPConfig.class,new FPConfig());
        strictConfig = JsonSerializable.load(strctcfg,StrictConfig.class,new StrictConfig());
        swearConfig = JsonSerializable.load(swrcfg,SwearsConfig.class,new SwearsConfig());
        nbtConfig = JsonSerializable.load(nbtcfg,NBTConfig.class,new NBTConfig());
        violationConfig = JsonSerializable.load(violationcfg,ViolationConfig.class,new ViolationConfig());


        // Save
        mainConfig.save();
        advConfig.save();
        fpConfig.save();
        strictConfig.save();
        swearConfig.save();
        nbtConfig.save();
        violationConfig.save();

        whitelist = JsonSerializable.load(cmdWhitelist, WhitelistStorage.class, new WhitelistStorage());
        whitelist.save();

        log.info("Loading Dictionary (%s)...".formatted(Sentinel.mainConfig.plugin.lang));

        lang = JsonSerializable.load(LanguageFile.PATH,LanguageFile.class,new LanguageFile());
        lang.save();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Sentinel has disabled! (%s) Your server is now no longer protected!".formatted(getDescription().getVersion()));
    }

    public static Sentinel getInstance() {
        return instance;
    }

    public static File dataFolder() {
        return dataFolder;
    }

}
