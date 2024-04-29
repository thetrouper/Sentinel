package io.github.thetrouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.List;

public class SwearsConfig implements JsonSerializable<SwearsConfig> {
    @Override
    public File getFile() {
        File file = new File("plugins/Sentinel/swears.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public List<String> swears = List.of(
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
                    "retard",
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
