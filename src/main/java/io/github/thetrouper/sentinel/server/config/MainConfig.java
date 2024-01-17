package io.github.thetrouper.sentinel.server.config;

import io.github.thetrouper.sentinel.server.util.JsonSerializable;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MainConfig implements JsonSerializable<MainConfig> {

    @Override
    public File getFile() {
        return new File("plugins/Sentinel/main-config.json");
    }

    public static class Plugin {
        public static String prefix = "§d§lSentinel §8» §7";
        public static String webhook;
        public static String lang;
        public static List<String> trustedPlayers;
        public static boolean blockSpecific;
        public static boolean preventNBT;
        public static boolean preventCmdBlockPlace;
        public static boolean preventCmdBlockUse;
        public static boolean preventCmdBlockChange;
        public static boolean preventCmdCartPlace;
        public static boolean preventCmdCartUse;
        public static boolean cmdBlockOpCheck;
        public static List<String> dangerous;
        public static boolean logDangerous;
        public static boolean logCmdBlocks;
        public static boolean logNBT;
        public static boolean logSpecific;
        public static List<String> logged;
        public static boolean deop;
        public static boolean nbtPunish;
        public static boolean cmdBlockPunish;
        public static boolean commandPunish;
        public static boolean specificPunish;
        public static List<String> punishCommands;
        public static boolean reopCommand;
    }

    public static class Chat {
        public static boolean antiUnicode;

        public static class antiSpam {
            public static boolean antiSpamEnabled;
            public static int defaultGain;
            public static int lowGain;
            public static int mediumGain;
            public static int highGain;
            public static int heatDecay;
            public static int blockHeat;
            public static int punishHeat;
            public static boolean clearChat;
            public static String chatClearCommand;
            public static String spamPunishCommand;
            public static boolean logSpam;
        }
        public static class antiSwear {
            public static boolean antiSwearEnabled;
            public static int lowScore;
            public static int mediumLowScore;
            public static int mediumScore;
            public static int mediumHighScore;
            public static int highScore;
            public static int scoreDecay;
            public static int punishScore;
            public static boolean strictInstaPunish;
            public static String swearPunishCommand;
            public static String strictPunishCommand;
            public static boolean logSwears;
        }

    }
}
