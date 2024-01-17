package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainConfig implements JsonSerializable<MainConfig> {

    @Override
    public File getFile() {
        return new File("plugins/Sentinel/main-config.json");
    }

    public static class Plugin {
        public static String prefix = "§d§lSentinel §8» §7";
        public static String webhook = "https://discord.com/api/webhooks/id/token";
        public static String lang = "en-us.json";
        public static List<String> trustedPlayers = new ArrayList<>() {{
            add("049460f7-21cb-42f5-8059-d42752bf406f");
        }};
        public static boolean blockSpecific = true;
        public static boolean preventNBT = true;
        public static boolean preventCmdBlockPlace = true;
        public static boolean preventCmdBlockUse = true;
        public static boolean preventCmdBlockChange = true;
        public static boolean preventCmdCartPlace = true;
        public static boolean preventCmdCartUse = true;
        public static boolean cmdBlockOpCheck = true;
        public static List<String> dangerous = new ArrayList<>() {{
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
        public static boolean logDangerous = true;
        public static boolean logCmdBlocks = true;
        public static boolean logNBT = true;
        public static boolean logSpecific = false;
        public static List<String> logged = new ArrayList<>() {{
            add("give");
            add("item");
        }};
        public static boolean deop = true;
        public static boolean nbtPunish = false;
        public static boolean cmdBlockPunish = false;
        public static boolean commandPunish = false;
        public static boolean specificPunish = false;
        public static List<String> punishCommands = new ArrayList<>() {{
            add("smite %player%");
            add("ban %player% ]=- Sentinel -=[ You have been banned for attempting a dangerous action. If you believe this to be a mistake, please contact the server owner.");
        }};
        public static boolean reopCommand = false;
    }

    public static class Chat {
        public static boolean antiUnicode = true;

        public static class AntiSpam {
            public static boolean antiSpamEnabled = true;
            public static int defaultGain = 1;
            public static int lowGain = 2;
            public static int mediumGain = 4;
            public static int highGain = 6;
            public static int heatDecay = 1;
            public static int blockHeat = 10;
            public static int punishHeat = 25;
            public static boolean clearChat = true;
            public static String chatClearCommand = "cc";
            public static String spamPunishCommand = "mute %player% 1m Please refrain from spamming!";
            public static boolean logSpam = true;
        }
        public static class AntiSwear {
            public static boolean antiSwearEnabled = true;
            public static int lowScore = 0;
            public static int mediumLowScore = 1;
            public static int mediumScore = 3;
            public static int mediumHighScore = 5;
            public static int highScore = 7;
            public static int scoreDecay = 3;
            public static int punishScore = 20;
            public static boolean strictInstaPunish = true;
            public static String swearPunishCommand = "mute %player% 15m Do not attempt to bypass the Profanity Filter";
            public static String strictPunishCommand = "mute %player% 1h Discriminatory speech is not tolerated on this server!";
            public static boolean logSwears = true;
        }

    }
}
