package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FPConfig implements JsonSerializable<FPConfig> {

    @Override
    public File getFile() {
        File file = new File(Sentinel.dataFolder(), "/false-positives.json");
        file.getParentFile().mkdirs();
        return file;
    }


    public String regexWhitelist = "";
    public boolean useRegex = false;
    public List<String> swearWhitelist = Arrays.asList(
            "but then",
            "was scamming",
            "an alt",
            "can also",
            "analysis",
            "analytics",
            "arsenal",
            "assassin",
            "as saying",
            "assert",
            "assign",
            "assimil",
            "assist",
            "associat",
            "assum",
            "assur",
            "basement",
            "bass",
            "cass",
            "butter",
            "canvass",
            "cocktail",
            "cumber",
            "document",
            "evaluate",
            "exclusive",
            "expensive",
            "explain",
            "expression",
            "grape",
            "grass",
            "harass",
            "hotwater",
            "identit",
            "kassa",
            "kassi",
            "lass",
            "leafage",
            "libshitz",
            "magnacumlaude",
            "mass",
            "mocha",
            "pass",
            "phoebe",
            "phoenix",
            "push it",
            "sassy",
            "saturday",
            "scrap",
            "serfage",
            "sexist",
            "shoe",
            "stitch",
            "therapist",
            "but its",
            "whoever",
            " again"
    );
}
