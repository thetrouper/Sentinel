package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedConfig implements JsonSerializable<AdvancedConfig> {
    public static String nonce = "%%__NONCE__%%";

    @Override
    public File getFile() {
        File file = new File(Sentinel.dataFolder(), "/advanced-config.json");
        file.getParentFile().mkdirs();
        return file;
    }
    public List<String> fakePlugins = Arrays.asList(
            "nocheatplus",
            "negativity",
            "warden",
            "horizon",
            "illegalstack",
            "coreprotect",
            "exploitsx",
            "vulcan",
            "abc",
            "spartan",
            "kauri",
            "anticheatreloaded",
            "witherac",
            "godseye",
            "matrix",
            "wraith",
            "antixrayheuristics",
            "grimac"
    );

    public List<String> versionAliases = List.of(
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
    public String allowedCharRegex = "[A-Za-z0-9\\[,./?><|\\]§()*&^%$#@!~`{}:;'\"-_]";
    public String falsePosRegex = "";
    public String swearRegex = "";
    public String strictRegex = "";
    public String urlRegex = "\\b(?:(?:https?|ftp):\\/\\/)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:com|org|net|int|edu|gov|mil|arpa|biz|info|mobi|app|name|aero|jobs|museum|travel|a[c-gil-oq-uwxz]|b[abd-jmnoq-tvwyz]|c[acdf-ik-orsu-z]|d[dejkmoz]|e[ceghr-u]|f[ijkmor]|g[abd-ilmnp-uwy]|h[kmnrtu]|i[delmnoq-t]|j[emop]|k[eghimnprwyz]|l[abcikr-vy]|m[acdeghk-z]|n[acefgilopruz]|om|p[ae-hk-nrstwy]|qa|r[eosuw]|s[a-eg-or-vxyz]|t[cdfghj-prtvwz]|u[agksyz]|v[aceginu]|w[fs]|y[etu]|z[amrw])))(?::\\d{2,5})?(?:\\/\\S*)?\\b";
}
