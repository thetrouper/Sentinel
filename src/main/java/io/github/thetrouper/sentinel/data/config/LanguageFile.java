package io.github.thetrouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;
import io.github.thetrouper.sentinel.Sentinel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Filter;

public class LanguageFile implements JsonSerializable<LanguageFile> {
    public static final File PATH = new File(Sentinel.getInstance().getDataFolder(), "/lang/" + Sentinel.mainConfig.plugin.lang);
    public LanguageFile() {}

    @Override
    public File getFile() {
        return PATH;
    }

    public String brokenLang = "Sentinel language is working!";

    public Permissions permissions = new Permissions();
    public class Permissions {
        public String noPermission = "§cInsufficient Permissions!";
        public String elevatingPerms = "Elevating your permissions...";
        public String logElevatingPerms = "Elevating the permissions of %s";
        public String alreadyOp = "You are already a server operator!";
        public String logAlreadyOp = "The permissions of %s are already elevated! Retrying...";
        public String noTrust = "You are not a trusted user!";
        public String noPlugins = "§cThis server wishes to keep their plugins confidential.";
    }

    public Cooldown cooldown = new Cooldown();
    public class Cooldown {
        public String onCooldown = "This action is on cooldown!";
    }

    public Reports reports = new Reports();
    public class Reports {
        public String falsePositiveSuccess = "Successfully reported a false positive!";
        public String reportingFalsePositive = "Sending report to staff...";
        public String noReport = "§cThe report you requested either does not exist, or has expired!";
    }

    public PlayerInteraction playerInteraction = new PlayerInteraction();
    public class PlayerInteraction {
        public String noOnlinePlayer = "§cYou must provide an online player to send a message to!";
        public String noMessageProvided = "§cYou must provide a message to send!";
        public String noReply = "§cYou have nobody to reply to!";
        public String messageSent = "§d§lMessage §8» §b[§fYou §e>§f %1$s§b] §7%2$s";
        public String messageReceived = "§d§lMessage §8» §b[§f%1$s §e>§f You§b] §7%2$s";
    }

    public SocialSpy socialSpy = new SocialSpy();
    public class SocialSpy {
        public String enabled = "SocialSpy is now enabled.";
        public String disabled = "SocialSpy is now disabled.";
        public String spyMessage = "§d§lSpy §8» §b§n%1$s§7 has messaged §b§n%2$s§7.";
        public String spyMessageHover = "§8]==-- §d§lSocialSpy §8--==[\n§bSender: §f%1$S\n§bReceiver: §f%2$S\n§bMessage: §f%3$S";
    }

    public AutomatedActions automatedActions = new AutomatedActions();
    public class AutomatedActions {
        public String actionAutomatic = "§7This action was preformed automatically\n§7by the §bSentinel Chat Filter§7 algorithm.";
        public String actionAutomaticReportable = "§7This action was preformed automatically \n§7by the §bSentinel Chat Filter§7 algorithm!\n§8§o(Click to report false positive)";
    }

    public ProfanityFilter profanityFilter = new ProfanityFilter();
    public class ProfanityFilter {
        public String profanityNotification = "§b§n%1$s§7 has triggered the anti-swear! §8(§c%2$s§7/§4%3$s§8)";
        public String profanityWarn = "§cPlease do not swear in chat! Attempting to bypass this filter will result in a mute! §7§o(Hover for more info)";
        public String profanityMuteWarn = "You have been auto-muted for repeated violation of the profanity filter! §7§o(Hover for more info)";
        public String profanityMuteNotification = "§b§n%1$s§7 has been auto-muted by the profanity filter! §8(§c%2$s§7/§4%3$s§8)";
        public String profanityNotificationHover = "§8]==-- §d§lSentinel §8--==[\n§bOriginal: §f%1$s\n§bSanitized: §f%2$s\n§bSeverity: §c%3$s\n§7§o(click to report false positive)";
    }

    public SlurFilter slurFilter = new SlurFilter();
    public class SlurFilter {
        public String slurMuteWarn = "§cYou have been insta-punished by the anti-slur! §7§o(Hover for more info)";
        public String slurMuteNotification = "§b§n%1$s§7 has been insta-muted by the anti-swear! §8(§c%2$s§7/§4%3$s§8)";
    }

    public SpamFilter spamFilter = new SpamFilter();
    public class SpamFilter {
        public String spamNotification = "§b§n%1$s§7 might be spamming! §8(§c%2$s§7/§4%3$s§8)";
        public String spamNotificationHover = "§8]==-- §d§lSentinel §8--==[\n§bPrevious: §f%1$s\n§bCurrent: §f%2$s\n§bSimilarity §f%3$s";
        public String spamWarn = "Do not spam in chat! Please wait before sending another message.";
        public String spamMuteWarn = "§cYou have been auto-punished for violating the anti-spam repetitively!";
        public String spamMuteNotification = "§b§n%1$s§7 has been auto-muted by the anti spam! §8(§c%2$s§7/§4%3$s§8)";
    }

    public URLFilter urlFilter = new URLFilter();
    public class URLFilter {
        public String urlWarn = "§cDo not send urls in chat!";
        public String urlNotification = "§b§n%1$s§7 has triggered the anti-URL.";
        public String urlNotificationHover = "§8]==-- §d§lSentinel §8--==[\n§bDetected: §f%1$s";
    }

    public UnicodeFilter unicodeFilter = new UnicodeFilter();
    public class UnicodeFilter {
        public String unicodeWarn = "§cDo not send non-standard unicode in chat!";
        public String unicodeNotification = "§b§n%1$s§7 has triggered the anti-unicode.";
        public String unicodeNotificationHover = "§8]==-- §d§lSentinel §8--==[\n§bMessage: §f%1$s";
    }

}