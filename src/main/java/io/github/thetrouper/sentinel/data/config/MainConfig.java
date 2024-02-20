package io.github.thetrouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainConfig implements JsonSerializable<MainConfig> {

    @Override
    public File getFile() {
        File file = new File("plugins/Sentinel/main-config.json");
        file.getParentFile().mkdirs();
        return file;
    }
    public Plugin plugin = new Plugin();
    public Chat chat = new Chat();

    public class Plugin {
        public String license = "null";
        public String prefix = "§d§lSentinel §8» §7";
        public String webhook = "https://discord.com/api/webhooks/id/token";
        public String lang = "en-us.json";
        public List<String> trustedPlayers = new ArrayList<>() {{
            add("049460f7-21cb-42f5-8059-d42752bf406f");
        }};
        public boolean blockSpecific = true;
        public boolean preventNBT = true;
        public boolean preventCmdBlockPlace = true;
        public boolean preventCmdBlockUse = true;
        public boolean preventCmdBlockChange = true;
        public boolean cmdBlockWhitelist = false;
        public boolean deleteUnauthorizedCmdBlocks = false;
        public boolean logUnauthorizedCmdBlocks = false;
        public boolean preventCmdCartPlace = true;
        public boolean preventCmdCartUse = true;
        public boolean cmdBlockOpCheck = true;
        public List<String> dangerous = new ArrayList<>() {{
            add("op");
            add("deop");
            add("stop");
            add("restart");
            add("execute");
            add("sudo");
            add("esudo");
            add("fill");
            add("setblock");
            add("data");
            add("whitelist");
        }};
        public boolean logDangerous = true;
        public boolean logCmdBlocks = true;
        public boolean logNBT = true;
        public boolean logSpecific = false;
        public List<String> logged = new ArrayList<>() {{
            add("give");
            add("item");
        }};
        public boolean deop = true;
        public boolean nbtPunish = false;
        public boolean cmdBlockPunish = false;
        public boolean commandPunish = false;
        public boolean specificPunish = false;
        public List<String> punishCommands = new ArrayList<>() {{
            add("smite %player%");
            add("ban %player% ]=- Sentinel -=[ You have been banned for attempting a dangerous action. If you believe this to be a mistake, please contact the server owner.");
        }};
        public boolean reopCommand = false;
        public boolean pluginHider = true;
    }

    public class Chat {
        public AntiSwear antiSwear = new AntiSwear();
        public AntiSpam antiSpam = new AntiSpam();
        public boolean useAntiURL = false;
        public boolean useSwearRegex = false;
        public boolean useStrictRegex = false;
        public boolean useAntiUnicode = true;

        public class AntiSpam {
            public boolean antiSpamEnabled = true;
            public int defaultGain = 1;
            public int lowGain = 2;
            public int mediumGain = 4;
            public int highGain = 6;
            public int heatDecay = 1;
            public int blockHeat = 10;
            public int punishHeat = 25;
            public boolean clearChat = true;
            public String chatClearCommand = "cc";
            public String spamPunishCommand = "mute %player% 1m Please refrain from spamming!";
            public boolean logSpam = true;
        }

        public class AntiSwear {
            public boolean antiSwearEnabled = true;
            public int lowScore = 0;
            public int mediumLowScore = 1;
            public int mediumScore = 3;
            public int mediumHighScore = 5;
            public int highScore = 7;
            public int scoreDecay = 3;
            public int punishScore = 20;
            public String swearPunishCommand = "mute %player% 15m Do not attempt to bypass the Profanity Filter";
            public String strictPunishCommand = "mute %player% 1h Discriminatory speech is not tolerated on this server!";
            public boolean logSwears = true;
        }

    }
}
