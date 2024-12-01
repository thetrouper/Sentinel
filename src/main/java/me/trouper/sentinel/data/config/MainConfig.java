package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.List;

public class MainConfig implements JsonSerializable<MainConfig> {

    public static String user = "%%__USER__%%";
    public static String username = "%%__USERNAME__%%";

    @Override
    public File getFile() {
        File file = new File(Sentinel.dataFolder(), "/main-config.json");
        file.getParentFile().mkdirs();
        return file;
    }
    public Plugin plugin = new Plugin();
    public Chat chat = new Chat();
    public boolean debugMode = false;

    public class Plugin {
        public String license = "null";
        public String prefix = "§d§lSentinel §8» §7";
        public String webhook = "https://discord.com/api/webhooks/id/token";
        public String lang = "en-us.json";
        public List<String> trustedPlayers = List.of(
                "049460f7-21cb-42f5-8059-d42752bf406f"
        );

        public boolean reopCommand = false;
        public boolean pluginHider = true;
        public String identifier = "My Server (Edit in main-config.json)";
    }

    public class Chat {
        public AntiSwear swearFilter = new AntiSwear();
        public AntiSpam spamFilter = new AntiSpam();
        public boolean useAntiURL = true;
        public boolean useSwearRegex = false;
        public boolean useStrictRegex = false;
        public boolean useAntiUnicode = true;


        public class AntiSpam {
            public boolean enabled = true;
            public int defaultGain = 1;
            public int lowGain = 2;
            public int mediumGain = 4;
            public int highGain = 6;
            public int heatDecay = 1;
            public int blockSimilarity = 99;
            public int blockHeat = 10;
            public int punishHeat = 25;
            public List<String> punishCommands = List.of(
                    "clearchat",
                    "mute %player% 1m Please refrain from spamming!"
            );
        }

        public class AntiSwear {
            public boolean enabled = true;
            public int lowScore = 0;
            public int mediumLowScore = 1;
            public int mediumScore = 3;
            public int mediumHighScore = 5;
            public int highScore = 7;
            public int regexScore = 4;
            public int scoreDecay = 3;
            public int punishScore = 20;
            public List<String> swearPunishCommands = List.of(
                    "mute %player% 15m Do not attempt to bypass the Profanity Filter"
            );
            public List<String> strictPunishCommands = List.of(
                    "mute %player% 1h Discriminatory speech is not tolerated on this server!"
            );
        }

    }
}
