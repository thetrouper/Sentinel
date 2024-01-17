package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdvancedConfig implements JsonSerializable<AdvancedConfig> {

    @Override
    public File getFile() {
        return new File("plugins/Sentinel/advanced-config.json");
    }

    public static Map<String, String> leetPatterns = new HashMap<>() {{
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
    public static String falsePosRegex = "";
    public static String swearRegex;
    public static String strictRegex;
    public static String urlRegex = "^(https?://)?([a-zA-Z0-9-]+\\.)*[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/\\S*)?$\n";
}
