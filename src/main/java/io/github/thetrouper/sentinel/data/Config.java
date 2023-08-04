/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config loader
 */
public abstract class Config {
    private static final FileConfiguration config = Sentinel.getInstance().getConfig();

    public static List<String> getPunishCommands() {
        return punishCommands;
    }

    /**
     * Config plugin section
     */
    public class Plugin {
        public static String getPrefix() {
            return config.getString("config.plugin.prefix");
        }
    }
    public static String webhook;
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

    // Chat Filter Setup & AntiSpam
    public static boolean antiUnicode;
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
    public static boolean logSwear;
    public static List<String> swearWhitelist;
    public static List<String> swearBlacklist;
    public static List<String> slurs;
    public static Map<String, String> leetPatterns;

    public static void loadConfiguration() {

        Sentinel.prefix = config.getString("config.plugin.prefix");
        Sentinel.key = config.getString("config.plugin.key");
        webhook = config.getString("config.plugin.webhook");
        trustedPlayers = config.getStringList("config.plugin.trusted");
        blockSpecific = config.getBoolean("config.plugin.block-specific");
        preventNBT = config.getBoolean("config.plugin.prevent-nbt");
        preventCmdBlockPlace = config.getBoolean("config.plugin.prevent-cmdblock-place");
        preventCmdBlockUse = config.getBoolean("config.plugin.prevent-cmdblock-use");
        preventCmdBlockChange = config.getBoolean("config.plugin.prevent-cmdblock-change");
        preventCmdCartPlace = config.getBoolean("config.plugin.prevent-cmdcart-place");
        preventCmdCartUse = config.getBoolean("config.plugin.prevent-cmdcart-use");
        cmdBlockOpCheck = config.getBoolean("config.plugin.cmdblock-op-check");
        dangerous = config.getStringList("config.plugin.dangerous");
        logDangerous = config.getBoolean("config.plugin.log-dangerous");
        logCmdBlocks = config.getBoolean("config.plugin.log-cmdblocks");
        logNBT = config.getBoolean("config.plugin.log-nbt");
        logSpecific = config.getBoolean("config.plugin.log-specific");
        logged = config.getStringList("config.plugin.logged");
        deop = config.getBoolean("config.plugin.deop");
        nbtPunish = config.getBoolean("config.plugin.nbt-punish");
        cmdBlockPunish = config.getBoolean("config.plugin.cmdblock-punish");
        commandPunish = config.getBoolean("config.plugin.command-punish");
        specificPunish = config.getBoolean("config.plugin.specific-punish");
        punishCommands = config.getStringList("config.plugin.punish-commands");
        reopCommand = config.getBoolean("config.plugin.reop-command");

        // Chat Filter Setup & AntiSpam
        antiUnicode = config.getBoolean("config.chat.anti-unicode");
        antiSpamEnabled = config.getBoolean("config.chat.anti-spam.enabled");
        defaultGain = config.getInt("config.chat.anti-spam.default-gain");
        lowGain = config.getInt("config.chat.anti-spam.low-gain");
        mediumGain = config.getInt("config.chat.anti-spam.medium-gain");
        highGain = config.getInt("config.chat.anti-spam.high-gain");
        heatDecay = config.getInt("config.chat.anti-spam.heat-decay");
        blockHeat = config.getInt("config.chat.anti-spam.block-heat");
        punishHeat = config.getInt("config.chat.anti-spam.punish-heat");
        clearChat = config.getBoolean("config.chat.anti-spam.clear-chat");
        chatClearCommand = config.getString("config.chat.anti-spam.chat-clear-command");
        spamPunishCommand = config.getString("config.chat.anti-spam.punish-command");
        logSpam = config.getBoolean("config.chat.anti-spam.log-spam");
        antiSwearEnabled = config.getBoolean("config.chat.anti-swear.enabled");
        lowScore = config.getInt("config.chat.anti-swear.low-score");
        mediumLowScore = config.getInt("config.chat.anti-swear.medium-low-score");
        mediumScore = config.getInt("config.chat.anti-swear.medium-score");
        mediumHighScore = config.getInt("config.chat.anti-swear.medium-high-score");
        highScore = config.getInt("config.chat.anti-swear.high-score");
        scoreDecay = config.getInt("config.chat.anti-swear.score-decay");
        punishScore = config.getInt("config.chat.anti-swear.punish-score");
        strictInstaPunish = config.getBoolean("config.chat.anti-swear.strict-insta-punish");
        swearPunishCommand = config.getString("config.chat.anti-swear.punish-command");
        strictPunishCommand = config.getString("config.chat.anti-swear.strict-command");
        logSwear = config.getBoolean("config.chat.anti-swear.log-swear");
        swearWhitelist = config.getStringList("config.chat.anti-swear.false-positives");
        swearBlacklist = config.getStringList("config.chat.anti-swear.blacklisted");
        slurs = config.getStringList("config.chat.anti-swear.strict");
        leetPatterns = loadLeetPatterns();
        logSwear = config.getBoolean("config.chat.anti-swear.log-swear");

    }
    private static Map<String, String> loadLeetPatterns() {
        Map<String, String> dictionary = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("config.chat.anti-swear.leet-patterns");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                dictionary.put(key, section.getString(key));
            }
        }

        return dictionary;
    }

}
