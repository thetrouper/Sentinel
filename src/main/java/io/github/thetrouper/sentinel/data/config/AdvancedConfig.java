package io.github.thetrouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdvancedConfig implements JsonSerializable<AdvancedConfig> {

    @Override
    public File getFile() {
        File file = new File("plugins/Sentinel/advanced-config.json");
        file.getParentFile().mkdirs();
        return file;
    }

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
    public String falsePosRegex = "";
    public String swearRegex = "";
    public String strictRegex = "";
    public String urlRegex = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
}
