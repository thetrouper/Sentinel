package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StrictConfig implements JsonSerializable<StrictConfig> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/strict.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public String regexStrict = "";
    public boolean useRegex = false;
    public List<String> strict = Arrays.asList(
            "nigg",
            "niger",
            "nlg",
            "tranny",
            "fag",
            "beaner",
            "retard"
    );
}
