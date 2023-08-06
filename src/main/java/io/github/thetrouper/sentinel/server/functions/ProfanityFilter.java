package io.github.thetrouper.sentinel.server.functions;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.discord.WebhookSender;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfanityFilter {
    public static Map<Player, Integer> scoreMap;
    private static final List<String> swearBlacklist = Config.swearBlacklist;
    private static final List<String> swearWhitelist = Config.swearWhitelist;
    private static final List<String> slurs = Config.slurs;

    public static void enableAntiSwear() {
        scoreMap = new HashMap<>();
    }
    public static void handleProfanityFilter(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = TextUtils.removeFirstColor(e.getMessage());
        if (!scoreMap.containsKey(p)) scoreMap.put(p, 0);
        if (scoreMap.get(p) > Config.punishScore) punishSwear(p,highlightProfanity(message),message);
        String severity = ProfanityFilter.checkSeverity(message);
        switch (severity) {
            case "low" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.lowScore);
                scoreMap.put(p, scoreMap.get(p) + Config.lowScore);
                e.setCancelled(true);
                p.sendMessage(TextUtils.prefix("§cPlease do not swear in chat! Attempting to bypass this filter will result in a mute!"));
                blockSwear(p,highlightProfanity(message),message,severity);
            }
            case "medium-low" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.mediumLowScore);
                scoreMap.put(p, scoreMap.get(p) + Config.mediumLowScore);
                e.setCancelled(true);
                blockSwear(p,highlightProfanity(message),message,severity);
            }
            case "medium" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.mediumScore);
                scoreMap.put(p, scoreMap.get(p) + Config.mediumScore);
                e.setCancelled(true);
                blockSwear(p,highlightProfanity(message),message,severity);
            }
            case "medium-high" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.mediumHighScore);
                scoreMap.put(p, scoreMap.get(p) + Config.mediumHighScore);
                e.setCancelled(true);
                blockSwear(p,highlightProfanity(message),message,severity);
            }
            case "high" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.highScore);
                scoreMap.put(p, scoreMap.get(p) + Config.highScore);
                e.setCancelled(true);
                blockSwear(p,highlightProfanity(message),message,severity);
            }
            case "slur" -> {
                // Insta-Punish
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.highScore);
                scoreMap.put(p, scoreMap.get(p) + Config.highScore);
                e.setCancelled(true);
                punishSlur(p,highlightProfanity(message),message);
            }
        }
    }
    public static void punishSwear(Player player, String highlightedMSG, String origMessage) {
        ServerUtils.sendCommand(Config.swearPunishCommand.replace("%player%", player.getName()));
        player.sendMessage(TextUtils.prefix(("§cYou have been auto-muted for violating the anti-swear repetitively!")));
        String hover = ("§bOriginal: §f" + origMessage + "\n§bSanitized: §f" + highlightedMSG + "\n§7§o(click to copy)");
        TextComponent text = new TextComponent();
        text.setText(TextUtils.prefix(
                ("§b§n" + player.getName() + "§7 has been auto-muted by the anti-swear! §8(§c" + scoreMap.get(player) + "§7/§4" + Config.punishScore + "§8)")));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, origMessage));

        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
        if (Config.logSwear) WebhookSender.sendSwearLog(player,origMessage,scoreMap.get(player));
    }
    public static void punishSlur(Player player, String highlightedMSG, String origMessage) {
        if (!Config.strictInstaPunish) return;
        ServerUtils.sendCommand(Config.strictPunishCommand.replace("%player%", player.getName()));
        player.sendMessage(TextUtils.prefix(("§cYou have been insta-muted for saying a slur!")));
        String hover = ("§bOriginal: §f" + origMessage + "\n§bSanitized: §f" + highlightedMSG + "\n§7§o(click to copy)");
        TextComponent text = new TextComponent();
        text.setText(TextUtils.prefix(
                ("§b§n" + player.getName() + "§7 has been insta-muted by the anti-swear! §8(§e" + scoreMap.get(player) + "§7/§4" + Config.punishScore + "§8)")));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, origMessage));

        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
        if (Config.logSwear) WebhookSender.sendSlurLog(player,origMessage,scoreMap.get(player));
    }
    public static void blockSwear(Player player, String highlightedMSG, String origMessage, String severity) {
        player.sendMessage(TextUtils.prefix(("§cPlease do not swear in chat! Attempting to bypass this filter will result in a mute!")));
        String hover = ("§bOriginal: §f" + origMessage + "\n§bSanitized: §f" + highlightedMSG + "\n§bSeverity: §c" + severity + "\n§7§o(click to copy)");
        TextComponent text = new TextComponent();
        text.setText(TextUtils.prefix(
                ("§b§n" + player.getName() + "§7 has triggered the anti-swear! §8(§c" + scoreMap.get(player) + "§7/§4" + Config.punishScore + "§8)")));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, origMessage));

        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
    }

    public static String highlightProfanity(String text) {
        String highlightedSwears = highlightSwears(fullSimplify(text), "§e", "§f");
        String highlightedText = highlightSlurs(highlightedSwears, "§c", "§f");
        return highlightedText;
    }
    public static String highlightProfanity(String text, String start, String end) {
        String highlightedSwears = highlightSwears(fullSimplify(text), start, end);
        String highlightedText = highlightSlurs(highlightedSwears, start, end);
        return highlightedText;
    }

    private static String highlightSwears(String text, String start, String end) {
        for (String swear : swearBlacklist) {
            if (text.contains(swear)) {text = text.replace(swear, start + swear + end);}
        }
        return text;
    }

    private static String highlightSlurs(String text, String start, String end) {
        for (String slur : slurs) {
            if (text.contains(slur)) {
                text = text.replace(slur, start + slur + end);
            }
        }
        return text;
    }

    /**
     * 1: lowercase the text
     * 1.4: Separate the string into words
     * 1.5: Remove all verified clean english words
     * 1.6: Put it back into one string
     * 2: remove the known false positives
     * 3: Check for swears and return "low" if true
     * 4: Convert LeetSpeak Characters
     * 5: Check for swears and return "medium-low" if true
     * 6: Strip all special characters
     * 7: Check for swears and return "medium" if true
     * 8: simplify repeating letters
     * 9: Check for swears and return "medium-high" if true
     * 10: remove periods and spaces
     * 11: Check for swears and return "high" if true
     */
    public static String fullSimplify(String text) {
        String lowercasedText = text.toLowerCase();
        String cleanedText = removeFalsePositives(lowercasedText);
        String convertedText = convertLeetSpeakCharacters(cleanedText);
        String strippedText = stripSpecialCharacters(convertedText);
        String simplifiedText = simplifyRepeatingLetters(strippedText);
        String finalText = removePeriodsAndSpaces(simplifiedText);
        return finalText;
    }
    public static String checkSeverity(String text) {
        // 1:
        String lowercasedText = text.toLowerCase();
        ServerUtils.sendDebugMessage(TextUtils.prefix("Debug: [AntiSwear] Lowercased: " + lowercasedText));

        // 2:
        String cleanedText = removeFalsePositives(lowercasedText);
        ServerUtils.sendDebugMessage(TextUtils.prefix("Debug: [AntiSwear] Removed False positives: " + cleanedText));

        // 3:
        if (containsSwears(cleanedText)) return "low";
        if (containsSlurs(cleanedText)) return "slur";

        // 4:
        String convertedText = convertLeetSpeakCharacters(cleanedText);
        ServerUtils.sendDebugMessage(TextUtils.prefix("Debug: [AntiSwear] Leet Converted: " + convertedText));

        // 5:
        if (containsSwears(convertedText)) return "medium-low";
        if (containsSlurs(cleanedText)) return "slur";

        // 6:
        String strippedText = stripSpecialCharacters(convertedText);
        ServerUtils.sendDebugMessage(TextUtils.prefix("Debug: [AntiSwear] Specials Removed: " + strippedText));

        // 7:
        if (containsSwears(strippedText)) return "medium";
        if (containsSlurs(strippedText)) return "slur";

        // 8:
        String simplifiedText = simplifyRepeatingLetters(strippedText);
        ServerUtils.sendDebugMessage(TextUtils.prefix("Debug: [AntiSwear] Removed Repeating: " + simplifiedText));

        // 9:
        if (containsSwears(simplifiedText)) return "medium-high";
        if (containsSlurs(simplifiedText)) return "slur";

        // 10:
        String finalText = removePeriodsAndSpaces(simplifiedText);
        ServerUtils.sendDebugMessage(TextUtils.prefix("Debug: [AntiSwear] Remove Punctuation: " + finalText));

        // 11:
        if (containsSwears(finalText)) return "high";
        if (containsSlurs(finalText)) return "slur";

        return "safe";
    }

    private static String removeFalsePositives(String text) {
        for (String falsePositive : swearWhitelist) {
            text = text.replace(falsePositive, "");
        }
        return text;
    }

    private static boolean containsSwears(String text) {
        for (String swear : swearBlacklist) {
            if (text.contains(swear)) return true;
        }
        return false;
    }
    private static boolean containsSlurs(String text) {
        for (String slur : slurs) {
            if (text.contains(slur)) return true;
        }
        return false;
    }

    private static String convertLeetSpeakCharacters(String text) {
        text = TextUtils.fromLeetString(text);
        return text;
    }

    private static String stripSpecialCharacters(String text) {
        text = text.replaceAll("[^A-Za-z0-9.,!?;:'\"()\\[\\]{}]", "").trim();
        return text;
    }

    private static String simplifyRepeatingLetters(String text) {
        text = TextUtils.replaceRepeatingLetters(text);
        return text;
    }

    private static String removePeriodsAndSpaces(String text) {
        return text.replaceAll("[^A-Za-z0-9]", "").replace(" ", "");
    }
    public static void decayScore() {
        for (Player p : scoreMap.keySet()) {
            int score = scoreMap.get(p);
            if (score > 0) {
                score = score - Config.scoreDecay;
                scoreMap.put(p, Math.max(0, score));
            }
        }
    }
}
