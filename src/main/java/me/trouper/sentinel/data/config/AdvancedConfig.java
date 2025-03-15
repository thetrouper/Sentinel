package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedConfig implements JsonSerializable<AdvancedConfig> {
    public transient String nonce = "%%__NONCE__%%";

    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/advanced-config.json");
        file.getParentFile().mkdirs();
        return file;
    }
    public List<String> fakePlugins = Arrays.asList(
            "Nocheatplus",
            "Negativity",
            "Warden",
            "Horizon",
            "Illegalstack",
            "CoreProtect",
            "ExploitsX",
            "Vulcan",
            "ABC",
            "Spartan",
            "Kauri",
            "AnticheatReloaded",
            "WitherAC",
            "GodsEye",
            "Matrix",
            "Wraith",
            "AntiXrayHeuristics",
            "GrimAC"
    );

    public List<String> commandsWithPluginAccess = Arrays.asList(
            "version",
            "bukkit:version",
            "ver",
            "bukkit:ver",
            "about",
            "bukkit:about",
            "?",
            "bukkit:?",
            "pl",
            "bukkit:pl",
            "plugins",
            "bukkit:plugins",
            "help",
            "bukkit:help"
    );

    public List<String> pluginTabCompletions = Arrays.asList(
            "version",
            "bukkit:version",
            "ver",
            "bukkit:ver",
            "about",
            "bukkit:about",
            "?",
            "bukkit:?",
            "help",
            "bukkit:help"
    );

    public Map<String, String> leetPatterns = new HashMap<>() {{
        put("0", "o");
        put("1", "i");
        put("3", "e");
        put("4", "a");
        put("5", "s");
        put("6", "g");
        put("7", "l");
        put("8","ate");
        put("$", "s");
        put("!", "i");
        put("|_|","u");
        put("|", "i");
        put("+", "t");
        put("#", "h");
        put("@", "a");
        put("<", "c");
        put("v", "u");
    }};
}
