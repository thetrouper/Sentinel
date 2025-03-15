package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SwearsConfig implements JsonSerializable<SwearsConfig> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/swears.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public String regexSwears = "";
    public boolean useRegex = false;
    public List<String> swears = Arrays.asList(
            "anal",
                    "anus",
                    "arse",
                    "ass",
                    "ballsack",
                    "balls",
                    "bastard",
                    "bitch",
                    "btch",
                    "biatch",
                    "blowjob",
                    "bollock",
                    "bollok",
                    "boner",
                    "boob",
                    "bugger",
                    "butt",
                    "choad",
                    "clitoris",
                    "cock",
                    "coon",
                    "crap",
                    "cum",
                    "cunt",
                    "dick",
                    "dildo",
                    "douchebag",
                    "dyke",
                    "feck",
                    "fellate",
                    "fellatio",
                    "felching",
                    "fuck",
                    "fudgepacker",
                    "flange",
                    "gtfo",
                    "hoe",
                    "horny",
                    "incest",
                    "jerk",
                    "jizz",
                    "labia",
                    "masturb",
                    "muff",
                    "nazi",
                    "nipple",
                    "nips",
                    "nude",
                    "pedophile",
                    "penis",
                    "piss",
                    "poop",
                    "porn",
                    "prick",
                    "prostit",
                    "pube",
                    "pussie",
                    "pussy",
                    "queer",
                    "rape",
                    "rapist",
                    "rimjob",
                    "scrotum",
                    "sex",
                    "shit",
                    "slut",
                    "spunk",
                    "stfu",
                    "suckmy",
                    "tits",
                    "tittie",
                    "titty",
                    "turd",
                    "twat",
                    "vagina",
                    "wank",
                    "whore"
    );
}
