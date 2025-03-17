package me.trouper.sentinel.data;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.*;
import me.trouper.sentinel.data.config.lang.LanguageFile;
import me.trouper.sentinel.data.storage.ExtraStorage;
import me.trouper.sentinel.data.storage.CommandBlockStorage;
import me.trouper.sentinel.data.storage.NBTStorage;

import java.io.File;

public class IO {
    private final File dataFolder = new File("plugins/SentinelAntiNuke");
    private final File violationcfg = new File(dataFolder,"/violation-config.json");
    private final File cfgfile = new File(dataFolder,"/main-config.json");
    private final File nbtcfg = new File(dataFolder, "/nbt-config.json");
    private final File strctcfg = new File(dataFolder, "/strict.json");
    private final File swrcfg = new File(dataFolder, "/swears.json");
    private final File fpcfg = new File(dataFolder, "/false-positives.json");
    private final File advcfg = new File(dataFolder, "/advanced-config.json");
    private final File cmdWhitelist = new File(dataFolder, "/storage/whitelist.json");
    private final File extraFile = new File(dataFolder, "/storage/extra.json");
    private final File nbtFile =  new File(dataFolder,"/storage/nbt.json");

    public LanguageFile lang;
    public ViolationConfig violationConfig = JsonSerializable.load(violationcfg, ViolationConfig.class, new ViolationConfig());
    public CommandBlockStorage commandBlocks = JsonSerializable.load(cmdWhitelist, CommandBlockStorage.class, new CommandBlockStorage());
    public ExtraStorage extraStorage = JsonSerializable.load(cmdWhitelist, ExtraStorage.class, new ExtraStorage());
    public MainConfig mainConfig = JsonSerializable.load(cfgfile, MainConfig.class, new MainConfig());
    public FPConfig fpConfig = JsonSerializable.load(fpcfg, FPConfig.class, new FPConfig());
    public SwearsConfig swearConfig = JsonSerializable.load(swrcfg, SwearsConfig.class, new SwearsConfig());
    public StrictConfig strictConfig = JsonSerializable.load(strctcfg, StrictConfig.class, new StrictConfig());
    public NBTConfig nbtConfig = JsonSerializable.load(nbtcfg, NBTConfig.class, new NBTConfig());
    public AdvancedConfig advConfig = JsonSerializable.load(advcfg, AdvancedConfig.class, new AdvancedConfig());
    public NBTStorage nbtStorage = JsonSerializable.load(nbtFile, NBTStorage.class, new NBTStorage());

    public void loadConfig() {
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
        
        // Storage

        commandBlocks = JsonSerializable.load(cmdWhitelist, CommandBlockStorage.class, new CommandBlockStorage());
        extraStorage = JsonSerializable.load(extraFile, ExtraStorage.class, new ExtraStorage());
        nbtStorage = JsonSerializable.load(nbtFile,NBTStorage.class,new NBTStorage());
        
        commandBlocks.save();
        extraStorage.save();
        nbtStorage.save();
        

        Sentinel.getInstance().getLogger().info("Loading Dictionary (%s)...".formatted(mainConfig.plugin.lang));

        lang = JsonSerializable.load(LanguageFile.PATH,LanguageFile.class,new LanguageFile());
        lang.save();
    }

    public File getDataFolder() {
        return dataFolder;
    }
}
