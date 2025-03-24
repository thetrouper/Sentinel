package me.trouper.sentinel.data.config.lists;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class StrictList implements JsonSerializable<StrictList> {
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
