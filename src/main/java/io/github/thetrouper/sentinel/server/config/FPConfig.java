package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.List;

public class FPConfig implements JsonSerializable<FPConfig> {

    @Override
    public File getFile() {
        return new File("plugins/Sentinel/false-positives.json");
    }

    public static List<String> swearWhitelist;
}
