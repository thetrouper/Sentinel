package io.github.thetrouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.*;

public class AdvancedConfig implements JsonSerializable<AdvancedConfig> {

    @Override
    public File getFile() {
        File file = new File("plugins/Sentinel/advanced-config.json");
        file.getParentFile().mkdirs();
        return file;
    }
    public List<String> fakePlugins = Arrays.asList(
            "This server wishes to keep their plugins confidential. Anyways, Enjoy your meteor client! If the owner is incompetent, then .server plugins MassScan should still work ;)",
            "NoCheatPlus",
            "Negativity",
            "Warden",
            "Horizon",
            "Illegalstack",
            "CoreProtect",
            "ExploitsX",
            "Vulcan (Outdated version frfr)",
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

    public String[] versionAliases = {"version", "ver", "about", "bukkit:version", "bukkit:ver", "bukkit:about", "?", "bukkit:?","pl","bukkit:pl","plugins","bukkit:plugins","bukkit:help"};

    public Map<String, String> leetPatterns = new HashMap<>() {{
        put("0", "o");
        put("1", "i");
        put("3", "e");
        put("4", "a");
        put("5", "s");
        put("6", "g");
        put("7", "l");
        put("$", "s");
        put("!", "i");
        put("|", "i");
        put("+", "t");
        put("#", "h");
        put("@", "a");
        put("<", "c");
        put("V", "u");
        put("v", "u");
    }};
    public String allowedCharRegex = "[A-Za-z0-9\\[,./?><|\\]§()*&^%$#@!~`{}:;'\"-_]";
    public String falsePosRegex = "";
    public String swearRegex = "";
    public String strictRegex = "";
    public String urlRegex = "\\b(?:(?:https?|ftp):\\/\\/)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:com|org|net|int|edu|gov|mil|arpa|biz|info|mobi|app|name|aero|jobs|museum|travel|a[c-gil-oq-uwxz]|b[abd-jmnoq-tvwyz]|c[acdf-ik-orsu-z]|d[dejkmoz]|e[ceghr-u]|f[ijkmor]|g[abd-ilmnp-uwy]|h[kmnrtu]|i[delmnoq-t]|j[emop]|k[eghimnprwyz]|l[abcikr-vy]|m[acdeghk-z]|n[acefgilopruz]|om|p[ae-hk-nrstwy]|qa|r[eosuw]|s[a-eg-or-vxyz]|t[cdfghj-prtvwz]|u[agksyz]|v[aceginu]|w[fs]|y[etu]|z[amrw])))(?::\\d{2,5})?(?:\\/\\S*)?\\b";
}
