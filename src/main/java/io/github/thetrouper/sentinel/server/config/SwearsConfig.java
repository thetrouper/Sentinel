package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.List;

public class SwearsConfig implements JsonSerializable<SwearsConfig> {
    @Override
    public File getFile() {
        return new File("plugins/Sentinel/swears.json");
    }

    public static List<String> swears;
}
