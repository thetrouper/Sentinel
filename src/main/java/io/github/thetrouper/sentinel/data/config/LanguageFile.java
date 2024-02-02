package io.github.thetrouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;
import io.github.thetrouper.sentinel.Sentinel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageFile implements JsonSerializable<LanguageFile> {
    public static final File PATH = new File(Sentinel.getInstance().getDataFolder(), "/lang/" + Sentinel.mainConfig.plugin.lang);
    private final Map<String,String> dictionary = new HashMap<>() {{
        put("if-you-see-this-lang-is-broken", "Sentinel language is working!");
        put("no-permission", "§cInsufficient Permissions!");
        put("cooldown", "This action is on cooldown!");
        put("false-positive-report-success", "Successfully reported a false positive!");
        put("no-online-player", "§cYou must provide an online player to send a message to!");
        put("no-message-provided", "§cYou must provide a message to send!");
        put("elevating-perms", "Elevating your permissions...");
        put("log-elevating-perms", "Elevating the permissions of %s");
        put("already-op", "You are already a server operator!");
        put("log-already-op", "The permissions of %s are already elevated! Retrying...");
        put("no-trust", "You are not a trusted user!");
        put("no-user-reply", "§cYou have nobody to reply to!");
        put("spy-enabled", "SocialSpy is now enabled.");
        put("spy-disabled", "SocialSpy is now disabled.");
        put("action-automatic", "§7This action was preformed automatically\n§7by the §bSentinel Anti-Spam§7 algorithm.");
        put("action-automatic-reportable", "§7This action was preformed automatically \n§7by the §bSentinel Profanity Filter§7 algorithm!\n§8§o(Click to report false positive)");
        put("unicode-warn", "§cDo not send non-standard unicode in chat!");
        put("message-sent", "§d§lMessage §8» §b[§fYou §e>§f %1$s§b] §7%2$s");
        put("message-received", "§d§lMessage §8» §b[§f%1$s §e>§f You§b] §7%2$s");
        put("spy-message", "§d§lSpy §8» §b§n%1$s§7 has messaged §b§n%2$s§7.");
        put("spy-message-hover", "§8]==-- §d§lSocialSpy §8--==[\n§bSender: §f%1$S\n§bReceiver: §f%2$S\n§bMessage: §f%3$S");
        put("profanity-block-notification", "§b§n%1$s§7 has triggered the anti-swear! §8(§c%2$s§7/§4%3$s§8)");
        put("profanity-block-warn", "§cPlease do not swear in chat! Attempting to bypass this filter will result in a mute! §7§o(Hover for more info)");
        put("profanity-mute-warn", "You have been auto-muted for repeated violation of the profanity filter! §7§o(Hover for more info)");
        put("profanity-mute-notification", "§b§n%1$s§7 has been auto-muted by the anti-swear! §8(§c%2$s§7/§4%3$s§8)");
        put("profanity-filter-notification-hover", "§8]==-- §d§lSentinel §8--==[\n§bOriginal: §f%1$s\n§bSanitized: §f%2$s\n§8§o(Click to report false positive)");
        put("severity-notification-hover", "§8]==-- §d§lSentinel §8--==[\n§bOriginal: §f%1$s\n§bSanitized: §f%2$s\n§bSeverity: §c%3$s\n§7§o(click to report false positive)");
        put("slur-mute-warn", "§cYou have been insta-punished by the anti-slur! §7§o(Hover for more info)");
        put("slur-mute-notification", "§b§n%1$s§7 has been insta-muted by the anti-swear! §8(§c%2$s§7/§4%3$s§8)");
        put("spam-notification", "§b§n%1$s§7 might be spamming! §8(§c%2$s§7/§4%3$s§8)");
        put("spam-notification-hover", "§8]==-- §d§lSentinel §8--==[\n§bPrevious: §f%1$s\n§bCurrent: §f%2$s\n§bSimilarity §f%3$s");
        put("spam-block-warn", "Do not spam in chat! Please wait before sending another message.");
        put("spam-mute-warn", "§cYou have been auto-punished for violating the anti-spam repetitively!");
        put("spam-mute-notification", "§b§n%1$s§7 has been auto-muted by the anti spam! §8(§c%2$s§7/§4%3$s§8)");
    }};
    public LanguageFile() {}

    @Override
    public File getFile() {
        return PATH;
    }
    public String get(String key) {
        return dictionary.getOrDefault(key,key);
    }
    public Map<String, String> getDictionary() {
        return dictionary;
    }
    public String format(String input) {
        return input;
    }
}