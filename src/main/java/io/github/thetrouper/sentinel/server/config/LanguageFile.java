package io.github.thetrouper.sentinel.server.config;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.JsonSerializable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageFile implements JsonSerializable<LanguageFile> {
    public static final File PATH = new File(Sentinel.getInstance().getDataFolder(), "/lang/" + Config.lang);
    private final Map<String,String> dictionary = new HashMap<>();
    public LanguageFile() {}

    @Override
    public File getFile() {
        return PATH;
    }
    public String get(String key) {
        return dictionary.getOrDefault(key,key);
    }
    public Map<String, String> getDictionary() {
        return dictionary;
    }
    public String format(String input) {
        return input;
    }
}
