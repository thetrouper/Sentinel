package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.List;

public class StrictConfig implements JsonSerializable<StrictConfig> {
    @Override
    public File getFile() {
        return new File("plugins/Sentinel/strict.json");
    }

    public static List<String> strict;
}
