package me.trouper.sentinel.data;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.*;
import me.trouper.sentinel.data.config.lang.LanguageFile;
import me.trouper.sentinel.data.config.lists.FalsePositiveList;
import me.trouper.sentinel.data.config.lists.StrictList;
import me.trouper.sentinel.data.config.lists.SwearList;
import me.trouper.sentinel.data.storage.CommandBlockStorage;
import me.trouper.sentinel.data.storage.ExtraStorage;
import me.trouper.sentinel.data.storage.NBTStorage;

import java.io.File;

public class IO {
    private final File dataFolder;
    private final File violationFile;
    private final File mainFile;
    private final File nbtConfigFile;
    private final File strictFile;
    private final File swearFile;
    private final File falsePositiveFile;
    private final File advancedConfigFile;
    private final File whitelistStorageFile;
    private final File nbtStorageFile;
    private final File extraStorageFile;

    public LanguageFile lang;
    
    public MainConfig mainConfig;
    public ViolationConfig violationConfig;
    public NBTConfig nbtConfig;
    public AdvancedConfig advConfig;

    public FalsePositiveList falsePositiveList;
    public SwearList swearList;
    public StrictList strictList;

    public CommandBlockStorage whitelistStorage;
    public ExtraStorage extraStorage;
    public NBTStorage nbtStorage;

    public IO() {
        dataFolder = new File("plugins/SentinelAntiNuke");
        violationFile = new File(dataFolder,"/violation-config.json");
        mainFile = new File(dataFolder,"/main-config.json");
        nbtConfigFile = new File(dataFolder, "/nbt-config.json");
        strictFile = new File(dataFolder, "/strict.json");
        swearFile = new File(dataFolder, "/swears.json");
        falsePositiveFile = new File(dataFolder, "/false-positives.json");
        advancedConfigFile = new File(dataFolder, "/advanced-config.json");
        whitelistStorageFile = new File(dataFolder, "/storage/whitelist.json");
        nbtStorageFile = new File(dataFolder,"/storage/nbt.json");
        extraStorageFile = new File(dataFolder, "/storage/extra.json");

        violationConfig = JsonSerializable.load(violationFile, ViolationConfig.class, new ViolationConfig());
        whitelistStorage = JsonSerializable.load(whitelistStorageFile, CommandBlockStorage.class, new CommandBlockStorage());
        extraStorage = JsonSerializable.load(whitelistStorageFile, ExtraStorage.class, new ExtraStorage());
        mainConfig = JsonSerializable.load(mainFile, MainConfig.class, new MainConfig());
        falsePositiveList = JsonSerializable.load(falsePositiveFile, FalsePositiveList.class, new FalsePositiveList());
        swearList = JsonSerializable.load(swearFile, SwearList.class, new SwearList());
        strictList = JsonSerializable.load(strictFile, StrictList.class, new StrictList());
        nbtConfig = JsonSerializable.load(nbtConfigFile, NBTConfig.class, new NBTConfig());
        advConfig = JsonSerializable.load(advancedConfigFile, AdvancedConfig.class, new AdvancedConfig());
        nbtStorage = JsonSerializable.load(nbtStorageFile, NBTStorage.class, new NBTStorage());
    }

    public void loadConfig() {
        // Init
        mainConfig = JsonSerializable.load(mainFile,MainConfig.class,new MainConfig());
        advConfig = JsonSerializable.load(advancedConfigFile,AdvancedConfig.class,new AdvancedConfig());
        falsePositiveList = JsonSerializable.load(falsePositiveFile, FalsePositiveList.class,new FalsePositiveList());
        strictList = JsonSerializable.load(strictFile, StrictList.class,new StrictList());
        swearList = JsonSerializable.load(swearFile, SwearList.class,new SwearList());
        nbtConfig = JsonSerializable.load(nbtConfigFile,NBTConfig.class,new NBTConfig());
        violationConfig = JsonSerializable.load(violationFile,ViolationConfig.class,new ViolationConfig());
        

        // Save
        mainConfig.save();
        advConfig.save();
        falsePositiveList.save();
        strictList.save();
        swearList.save();
        nbtConfig.save();
        violationConfig.save();
        
        // Storage

        whitelistStorage = JsonSerializable.load(whitelistStorageFile, CommandBlockStorage.class, new CommandBlockStorage());
        extraStorage = JsonSerializable.load(extraStorageFile, ExtraStorage.class, new ExtraStorage());
        nbtStorage = JsonSerializable.load(nbtStorageFile,NBTStorage.class,new NBTStorage());
        
        whitelistStorage.save();
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
