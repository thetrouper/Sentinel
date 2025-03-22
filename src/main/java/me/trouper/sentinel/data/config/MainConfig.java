package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainConfig implements JsonSerializable<MainConfig> {

    public transient String user = "%%__USER__%%";
    public transient String username = "%%__USERNAME__%%";


    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/main-config.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public boolean debugMode = false;
    public boolean telemetry = true;

    public Plugin plugin = new Plugin();
    public BackdoorDetection backdoorDetection = new BackdoorDetection();
    public Chat chat = new Chat();

    public class Plugin {
        public String license = "null";
        public String prefix = "§d§lSentinel §8» §7";
        public String webhook = "https://discord.com/api/webhooks/id/token";
        public String lang = "en-us.json";
        public List<String> trustedPlayers = Arrays.asList(
                "049460f7-21cb-42f5-8059-d42752bf406f"
        );
        public boolean antiBan = true;
        public boolean reopCommand = false;
        public boolean pluginHider = true;
        public String identifier = "My Server (Edit in main-config.json)";
    }

    public class BackdoorDetection {
        public boolean enabled = false;
        public boolean setupMode = true;
        public boolean keepSetupMode = true;
    }

    public class Chat {
        public ProfanityFilter profanityFilter = new ProfanityFilter();
        public SpamFilter spamFilter = new SpamFilter();
        public UnicodeFilter unicodeFilter = new UnicodeFilter();
        public UrlFilter urlFilter = new UrlFilter();

        public class SpamFilter {
            public boolean enabled = true;
            public boolean silent = false;
            public int defaultGain = 1;
            public int lowGain = 2;
            public int mediumGain = 4;
            public int highGain = 6;
            public int heatDecay = 1;
            public int blockSimilarity = 99;
            public int blockHeat = 10;
            public int punishHeat = 25;
            public List<String> whitelist = Arrays.asList(
                "welcome",
                "wl"
            );
            public List<String> punishCommands = Arrays.asList(
                    "clearchat",
                    "mute %player% 1m Please refrain from spamming!"
            );
        }

        public class ProfanityFilter {
            public boolean enabled = true;
            public boolean silent = false;
            public int lowScore = 0;
            public int mediumLowScore = 1;
            public int mediumScore = 3;
            public int mediumHighScore = 5;
            public int highScore = 7;
            public int regexScore = 4;
            public int scoreDecay = 3;
            public int punishScore = 20;
            public List<String> profanityPunishCommands = Arrays.asList(
                    "mute %player% 15m Do not attempt to bypass the Profanity Filter"
            );
            public List<String> strictPunishCommands = Arrays.asList(
                    "mute %player% 1h Discriminatory speech is not tolerated on this server!"
            );
        }

        public class UnicodeFilter {
            public boolean enabled = true;
            public boolean silent = false;
            public boolean punished = false;
            public String regex = "[^A-Za-z0-9\\[,./?><|\\]§()*&^%$#@!~`{}:;'\"-_ ]";
            public List<String> punishCommands = Arrays.asList(
                    "clearchat",
                    "mute %player% 1m Please refrain from spamming!"
            );
        }

        public class UrlFilter {
            public boolean enabled = true;
            public boolean silent = false;
            public boolean punished = true;
            public String regex = "\\b(?:(?:https?|ftp):\\/\\/)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:com|org|net|int|edu|gov|mil|arpa|biz|info|mobi|app|name|aero|jobs|museum|travel|a[c-gil-oq-uwxz]|b[abd-jmnoq-tvwyz]|c[acdf-ik-orsu-z]|d[dejkmoz]|e[ceghr-u]|f[ijkmor]|g[abd-ilmnp-uwy]|h[kmnrtu]|i[delmnoq-t]|j[emop]|k[eghimnprwyz]|l[abcikr-vy]|m[acdeghk-z]|n[acefgilopruz]|om|p[ae-hk-nrstwy]|qa|r[eosuw]|s[a-eg-or-vxyz]|t[cdfghj-prtvwz]|u[agksyz]|v[aceginu]|w[fs]|y[etu]|z[amrw])))(?::\\d{2,5})?(?:\\/\\S*)?\\b";
            public List<String> whitelist = Arrays.asList(
                    "play.example.com/this-could-be-your-server-ip",
                    "store.example.com/for-sharing-your-store",
                    "example.com/these-can-even-be-regex"
            );
            public List<String> punishCommands = Arrays.asList(
                    "clearchat",
                    "mute %player% 1m Please refrain from spamming!"
            );
        }
    }
}
