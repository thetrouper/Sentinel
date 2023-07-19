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
    public static boolean blockSpecificCommands;
    public static boolean preventNBT;
    public static boolean logNBT;
    public static boolean preventCmdBlocks;
    public static boolean logCmdBlocks;
    public static boolean cmdBlockOpCheck;
    public static List<String> dangerousCommands;
    public static boolean logDangerousCommands;
    public static List<String> loggedCommands;
    public static boolean deop;
    public static boolean nbtPunish;
    public static boolean commandPunish;
    public static boolean cmdblockPunish;
    public static boolean specificPunish;
    public static List<String> punishCommands;
    public static boolean reopCommand;

    public static boolean blockUnicode;
    public static boolean antiSpamEnabled;
    public static int defaultGain;
    public static int lowGain;
    public static int mediumGain;
    public static int highGain;
    public static int heatDecay;
    public static int blockHeat;
    public static int punishHeat;
    public static String punishSpamCommand;
    public static String clearChatCommand;
    public static boolean clearChat;
    public static boolean logSpam;
    public static boolean antiSwearEnabled;
    public static int lowScore;
    public static int mediumLowScore;
    public static int mediumScore;
    public static int mediumHighScore;
    public static int highScore;
    public static int punishScore;
    public static String swearPunishCommand;
    public static boolean slurInstaPunish;
    public static String slurPunishCommand;
    public static Integer scoreDecay;
    public static List<String> swearWhitelist;
    public static List<String> swearBlacklist;
    public static List<String> slurs;
    public static Map<String, String> leetPatterns;
    public static boolean logSwear;

    public static void loadConfiguration() {

        Sentinel.prefix = config.getString("config.plugin.prefix");
        Sentinel.key = config.getString("config.plugin.key");
        // antiNuke
        webhook = config.getString("config.plugin.webhook");
        trustedPlayers = config.getStringList("config.plugin.trusted");
        blockSpecificCommands = config.getBoolean("config.plugin.block-specific");
        preventNBT = config.getBoolean("config.plugin.prevent-nbt");
        logNBT = config.getBoolean("config.plugin.log-nbt");
        preventCmdBlocks = config.getBoolean("config.plugin.prevent-cmdblocks");
        logCmdBlocks = config.getBoolean("config.plugin.log-cmdblocks");
        cmdBlockOpCheck = config.getBoolean("config.plugin.cmdblock-op-check");
        dangerousCommands = config.getStringList("config.plugin.dangerous");
        logDangerousCommands = config.getBoolean("config.plugin.log-dangerous");
        loggedCommands = config.getStringList("config.plugin.logged");
        deop = config.getBoolean("config.plugin.deop");
        nbtPunish = config.getBoolean("config.plugin.nbt-punish");
        commandPunish = config.getBoolean("config.plugin.command-punish");
        cmdblockPunish = config.getBoolean("config.plugin.cmdblock-punish");
        specificPunish = config.getBoolean("config.plugin.punish-specific");
        punishCommands = config.getStringList("config.plugin.punish-commands");
        reopCommand = config.getBoolean("config.plugin.reop-command");
        // Chat
        blockUnicode = config.getBoolean("config.chat.anti-unicode");
        // antiSpam
        antiSpamEnabled = config.getBoolean("config.chat.anti-spam.enabled");
        defaultGain = config.getInt("config.chat.anti-spam.default-gain");
        lowGain = config.getInt("config.chat.anti-spam.low-gain");
        mediumGain = config.getInt("config.chat.anti-spam.medium-gain");
        highGain = config.getInt("config.chat.anti-spam.high-gain");
        heatDecay = config.getInt("config.chat.anti-spam.heat-decay");
        blockHeat = config.getInt("config.chat.anti-spam.block-heat");
        punishHeat = config.getInt("config.chat.anti-spam.punish-heat");
        punishSpamCommand = config.getString("config.chat.anti-spam.punish-command");
        clearChat = config.getBoolean("config.chat.anti-spam.clear-chat");
        clearChatCommand = config.getString("config.chat.anti-spam.clear-chat-command");
        logSpam = config.getBoolean("config.chat.anti-swear.log-swear");
        // antiSwear
        antiSwearEnabled = config.getBoolean("config.chat.anti-swear.enabled");
        lowScore = config.getInt("config.chat.anti-swear.low-score");
        mediumLowScore = config.getInt("config.chat.anti-swear.medium-low-score");
        mediumScore = config.getInt("config.chat.anti-swear.medium-score");
        mediumHighScore = config.getInt("config.chat.anti-swear.medium-high-score");
        highScore = config.getInt("config.chat.anti-swear.high-score");
        punishScore = config.getInt("config.chat.anti-swear.punish-score");
        swearPunishCommand = config.getString("config.chat.anti-swear.punish-command");
        slurInstaPunish = config.getBoolean("config.chat.anti-swear.slur-insta-punish");
        slurPunishCommand = config.getString("config.chat.anti-swear.slur-command");
        scoreDecay = config.getInt("config.chat.anti-swear.score-decay");
        swearWhitelist = config.getStringList("config.chat.anti-swear.whitelisted");
        swearBlacklist = config.getStringList("config.chat.anti-swear.blacklisted");
        slurs = config.getStringList("config.chat.anti-swear.slurs");
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
