package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StrictConfig implements JsonSerializable<StrictConfig> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.dataFolder(), "/strict.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public List<String> strict = new ArrayList<>() {{
        add("nigg");
        add("niger");
        add("nlgg");
        add("nlger");
        add("njgg");
        add("tranny");
        add("fag");
        add("beaner");
    }};
}
