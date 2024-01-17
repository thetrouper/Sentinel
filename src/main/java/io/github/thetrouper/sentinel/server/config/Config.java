/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.server.config;

import io.github.thetrouper.sentinel.Sentinel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config loader
 */
public abstract class Config {

    private static final FileConfiguration mainConfig = Sentinel.getInstance().getConfig();
    private static final FileConfiguration nbtConfig = getConfig("nbt-config.yml");
    private static final FileConfiguration falsePositives = getConfig("false-positives.yml");
    private static final FileConfiguration strictWords = getConfig("strict.yml");
    private static final FileConfiguration swearWords = getConfig("swears.yml");

    public static List<String> getPunishCommands() {
        return punishCommands;
    }

    /**
     * Config plugin section
     */
    public class Plugin {
        public static String getPrefix() {
            return mainConfig.getString("config.plugin.prefix");
        }
    }
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

    // NBT

    public static boolean allowName;
    public static boolean allowLore;
    public static boolean allowAttributes;
    public static int globalMaxEnchant;
    public static int maxMending;
    public static int maxUnbreaking;
    public static int maxVanishing;
    public static int maxAquaAffinity;
    public static int maxBlastProtection;
    public static int maxCurseOfBinding;
    public static int maxDepthStrider;
    public static int maxFeatherFalling;
    public static int maxFireProtection;
    public static int maxFrostWalker;
    public static int maxProjectileProtection;
    public static int maxProtection;
    public static int maxRespiration;
    public static int maxSoulSpeed;
    public static int maxThorns;
    public static int maxSwiftSneak;
    public static int maxBaneOfArthropods;
    public static int maxEfficiency;
    public static int maxFireAspect;
    public static int maxLooting;
    public static int maxImpaling;
    public static int maxKnockback;
    public static int maxSharpness;
    public static int maxSmite;
    public static int maxSweepingEdge;
    public static int maxChanneling;
    public static int maxFlame;
    public static int maxInfinity;
    public static int maxLoyalty;
    public static int maxRiptide;
    public static int maxMultishot;
    public static int maxPiercing;
    public static int maxPower;
    public static int maxPunch;
    public static int maxQuickCharge;
    public static int maxFortune;
    public static int maxLuckOfTheSea;
    public static int maxLure;
    public static int maxSilkTouch;

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
    public static FileConfiguration getConfig(String fileName) {
        File configFile = new File(Sentinel.getInstance().getDataFolder(), fileName);

        if (!configFile.exists()) {
            Sentinel.getInstance().saveResource(fileName, false);
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }
    public static void loadConfiguration() {

        Sentinel.prefix = mainConfig.getString("config.plugin.prefix");
        Sentinel.key = mainConfig.getString("config.plugin.key");
        lang = mainConfig.getString("config.plugin.lang");
        webhook = mainConfig.getString("config.plugin.webhook");
        trustedPlayers = mainConfig.getStringList("config.plugin.trusted");
        blockSpecific = mainConfig.getBoolean("config.plugin.block-specific");
        preventNBT = mainConfig.getBoolean("config.plugin.prevent-nbt");
        preventCmdBlockPlace = mainConfig.getBoolean("config.plugin.prevent-cmdblock-place");
        preventCmdBlockUse = mainConfig.getBoolean("config.plugin.prevent-cmdblock-use");
        preventCmdBlockChange = mainConfig.getBoolean("config.plugin.prevent-cmdblock-change");
        preventCmdCartPlace = mainConfig.getBoolean("config.plugin.prevent-cmdcart-place");
        preventCmdCartUse = mainConfig.getBoolean("config.plugin.prevent-cmdcart-use");
        cmdBlockOpCheck = mainConfig.getBoolean("config.plugin.cmdblock-op-check");
        dangerous = mainConfig.getStringList("config.plugin.dangerous");
        logDangerous = mainConfig.getBoolean("config.plugin.log-dangerous");
        logCmdBlocks = mainConfig.getBoolean("config.plugin.log-cmdblocks");
        logNBT = mainConfig.getBoolean("config.plugin.log-nbt");
        logSpecific = mainConfig.getBoolean("config.plugin.log-specific");
        logged = mainConfig.getStringList("config.plugin.logged");
        deop = mainConfig.getBoolean("config.plugin.deop");
        nbtPunish = mainConfig.getBoolean("config.plugin.nbt-punish");
        cmdBlockPunish = mainConfig.getBoolean("config.plugin.cmdblock-punish");
        commandPunish = mainConfig.getBoolean("config.plugin.command-punish");
        specificPunish = mainConfig.getBoolean("config.plugin.specific-punish");
        punishCommands = mainConfig.getStringList("config.plugin.punish-commands");
        reopCommand = mainConfig.getBoolean("config.plugin.reop-command");

        // NBT
        allowName = nbtConfig.getBoolean("nbt.allow-name");
        allowLore = nbtConfig.getBoolean("nbt.allow-lore");
        allowAttributes = nbtConfig.getBoolean("nbt.allow-attributes");
        globalMaxEnchant = nbtConfig.getInt("nbt.global-max-enchant");

        // ALL
        maxMending = nbtConfig.getInt("nbt.max-mending");
        maxUnbreaking = nbtConfig.getInt("nbt.max-unbreaking");
        maxVanishing = nbtConfig.getInt("nbt.max-vanishing");

        // ARMOR
        maxAquaAffinity = nbtConfig.getInt("nbt.max-aqua-affinity");
        maxBlastProtection = nbtConfig.getInt("nbt.max-blast-protection");
        maxCurseOfBinding = nbtConfig.getInt("nbt.max-curse-of-binding");
        maxDepthStrider = nbtConfig.getInt("nbt.max-depth-strider");
        maxFeatherFalling = nbtConfig.getInt("nbt.max-feather-falling");
        maxFireProtection = nbtConfig.getInt("nbt.max-fire-protection");
        maxFrostWalker = nbtConfig.getInt("nbt.max-frost-walker");
        maxProjectileProtection = nbtConfig.getInt("nbt.max-projectile-protection");
        maxProtection = nbtConfig.getInt("nbt.max-protection");
        maxRespiration = nbtConfig.getInt("nbt.max-respiration");
        maxSoulSpeed = nbtConfig.getInt("nbt.max-soul-speed");
        maxThorns = nbtConfig.getInt("nbt.max-thorns");
        maxSwiftSneak = nbtConfig.getInt("nbt.max-swift-sneak");

        // MELEE WEAPONS
        maxBaneOfArthropods = nbtConfig.getInt("nbt.max-bane-of-arthropods");
        maxEfficiency = nbtConfig.getInt("nbt.max-efficiency");
        maxFireAspect = nbtConfig.getInt("nbt.max-fire-aspect");
        maxLooting = nbtConfig.getInt("nbt.max-looting");
        maxImpaling = nbtConfig.getInt("nbt.max-impaling");
        maxKnockback = nbtConfig.getInt("nbt.max-knockback");
        maxSharpness = nbtConfig.getInt("nbt.max-sharpness");
        maxSmite = nbtConfig.getInt("nbt.max-smite");
        maxSweepingEdge = nbtConfig.getInt("nbt.max-sweeping-edge");

        // RANGED WEAPONS
        maxChanneling = nbtConfig.getInt("nbt.max-channeling");
        maxFlame = nbtConfig.getInt("nbt.max-flame");
        maxInfinity = nbtConfig.getInt("nbt.max-infinity");
        maxLoyalty = nbtConfig.getInt("nbt.max-loyalty");
        maxRiptide = nbtConfig.getInt("nbt.max-riptide");
        maxMultishot = nbtConfig.getInt("nbt.max-multishot");
        maxPiercing = nbtConfig.getInt("nbt.max-piercing");
        maxPower = nbtConfig.getInt("nbt.max-power");
        maxPunch = nbtConfig.getInt("nbt.max-punch");
        maxQuickCharge = nbtConfig.getInt("nbt.max-quick-charge");

        // TOOLS
        maxEfficiency = nbtConfig.getInt("nbt.max-efficiency");
        maxFortune = nbtConfig.getInt("nbt.max-fortune");
        maxLuckOfTheSea = nbtConfig.getInt("nbt.max-luck-of-the-sea");
        maxLure = nbtConfig.getInt("nbt.max-lure");
        maxSilkTouch = nbtConfig.getInt("nbt.max-silk-touch");

        // Chat Filter Setup & AntiSpam
        antiUnicode = mainConfig.getBoolean("config.chat.anti-unicode");
        antiSpamEnabled = mainConfig.getBoolean("config.chat.anti-spam.enabled");
        defaultGain = mainConfig.getInt("config.chat.anti-spam.default-gain");
        lowGain = mainConfig.getInt("config.chat.anti-spam.low-gain");
        mediumGain = mainConfig.getInt("config.chat.anti-spam.medium-gain");
        highGain = mainConfig.getInt("config.chat.anti-spam.high-gain");
        heatDecay = mainConfig.getInt("config.chat.anti-spam.heat-decay");
        blockHeat = mainConfig.getInt("config.chat.anti-spam.block-heat");
        punishHeat = mainConfig.getInt("config.chat.anti-spam.punish-heat");
        clearChat = mainConfig.getBoolean("config.chat.anti-spam.clear-chat");
        chatClearCommand = mainConfig.getString("config.chat.anti-spam.chat-clear-command");
        spamPunishCommand = mainConfig.getString("config.chat.anti-spam.punish-command");
        logSpam = mainConfig.getBoolean("config.chat.anti-spam.log-spam");
        antiSwearEnabled = mainConfig.getBoolean("config.chat.anti-swear.enabled");
        lowScore = mainConfig.getInt("config.chat.anti-swear.low-score");
        mediumLowScore = mainConfig.getInt("config.chat.anti-swear.medium-low-score");
        mediumScore = mainConfig.getInt("config.chat.anti-swear.medium-score");
        mediumHighScore = mainConfig.getInt("config.chat.anti-swear.medium-high-score");
        highScore = mainConfig.getInt("config.chat.anti-swear.high-score");
        scoreDecay = mainConfig.getInt("config.chat.anti-swear.score-decay");
        punishScore = mainConfig.getInt("config.chat.anti-swear.punish-score");
        strictInstaPunish = mainConfig.getBoolean("config.chat.anti-swear.strict-insta-punish");
        swearPunishCommand = mainConfig.getString("config.chat.anti-swear.punish-command");
        strictPunishCommand = mainConfig.getString("config.chat.anti-swear.strict-command");
        logSwear = mainConfig.getBoolean("config.chat.anti-swear.log-swear");
        swearWhitelist = falsePositives.getStringList("false-positives");
        swearBlacklist = swearWords.getStringList("blacklisted");
        slurs = strictWords.getStringList("strict");
        leetPatterns = loadLeetPatterns();
        logSwear = mainConfig.getBoolean("config.chat.anti-swear.log-swear");

    }
    private static Map<String, String> loadLeetPatterns() {
        Map<String, String> dictionary = new HashMap<>();
        ConfigurationSection section = mainConfig.getConfigurationSection("config.chat.anti-swear.leet-patterns");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                dictionary.put(key, section.getString(key));
            }
        }

        return dictionary;
    }

}
