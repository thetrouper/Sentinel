package io.github.thetrouper.sentinel.server.functions;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.FilterAction;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static io.github.thetrouper.sentinel.server.util.Text.SECTION_SYMBOL;

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
        String message = Text.removeFirstColor(e.getMessage());
        String highlighted = highlightProfanity(message);
        String severity = ProfanityFilter.checkSeverity(message);
        if (!scoreMap.containsKey(p)) scoreMap.put(p, 0);
        // Old: if (scoreMap.get(p) > Config.punishScore) punishSwear(p,highlighted,message,e);
        if (scoreMap.get(p) > Config.punishScore) FilterAction.filterAction(p,e,highlighted,severity, null, FAT.SWEAR);

        switch (severity) {
            case "low" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.lowScore);
                scoreMap.put(p, scoreMap.get(p) + Config.lowScore);
                e.setCancelled(true);
                // Old: blockSwear(p,highlighted,message,severity,e);
                FilterAction.filterAction(p,e,highlighted,severity, null, FAT.BLOCK_SWEAR);
            }
            case "medium-low" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.mediumLowScore);
                scoreMap.put(p, scoreMap.get(p) + Config.mediumLowScore);
                e.setCancelled(true);
                // Old: blockSwear(p,highlighted,message,severity,e);
                FilterAction.filterAction(p,e,highlighted,severity, null, FAT.BLOCK_SWEAR);
            }
            case "medium" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.mediumScore);
                scoreMap.put(p, scoreMap.get(p) + Config.mediumScore);
                e.setCancelled(true);
                // Old: blockSwear(p,highlighted,message,severity,e);
                FilterAction.filterAction(p,e,highlighted,severity, null, FAT.BLOCK_SWEAR);
            }
            case "medium-high" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.mediumHighScore);
                scoreMap.put(p, scoreMap.get(p) + Config.mediumHighScore);
                e.setCancelled(true);
                // Old: blockSwear(p,highlighted,message,severity,e);
                FilterAction.filterAction(p,e,highlighted,severity, null, FAT.BLOCK_SWEAR);
            }
            case "high" -> {
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.highScore);
                scoreMap.put(p, scoreMap.get(p) + Config.highScore);
                e.setCancelled(true);
                // Old: blockSwear(p,highlighted,message,severity,e);
                FilterAction.filterAction(p,e,highlighted,severity, null, FAT.BLOCK_SWEAR);
            }
            case "slur" -> {
                // Insta-Punish
                ServerUtils.sendDebugMessage("AntiSwear Flag, Message: " + message + " Concentrated: " + fullSimplify(message) +  " Severity: " + severity + " Previous Score: " + scoreMap.get(p) +" Adding Score: " + Config.highScore);
                scoreMap.put(p, scoreMap.get(p) + Config.highScore);
                e.setCancelled(true);
                // Old: punishSlur(p,highlighted,message,e);
                FilterAction.filterAction(p,e,highlighted,severity, null,FAT.SLUR);
            }
        }
    }

      /*
    public static void punishSwear(Player player, String highlightedMSG, String origMessage, AsyncPlayerChatEvent e) {
        ServerUtils.sendCommand(Config.swearPunishCommand.replace("%player%", player.getName()));
        String fpreport = ReportFalsePositives.generateReport(e);
        TextComponent offender = new TextComponent();
        String hoverPlayer = Sentinel.dict.get("action-automatic-reportable");
        offender.setText(Text.prefix(Sentinel.dict.get("profanity-mute-warn")));
        offender.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverPlayer)));
        offender.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + fpreport));
        player.spigot().sendMessage(offender);

        TextComponent text = new TextComponent();
        text.setText(Text.prefix(Sentinel.dict.get("profanity-mute-notification").formatted(player.getName(),scoreMap.get(player),Config.punishScore)));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("filter-notification-hover").formatted(origMessage,highlightedMSG))));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + fpreport));

        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
        if (Config.logSwear) WebhookSender.sendSwearLog(player,origMessage,scoreMap.get(player));
    }


    public static void punishSlur(Player player, String highlightedMSG, String origMessage, AsyncPlayerChatEvent e) {
        if (!Config.strictInstaPunish) return;

        ServerUtils.sendCommand(Config.strictPunishCommand.replace("%player%", player.getName()));
        String fpreport = ReportFalsePositives.generateReport(e);
        TextComponent offender = new TextComponent();
        String hoverPlayer = Sentinel.dict.get("action-automatic-reportable");
        offender.setText(Text.prefix((Sentinel.dict.get("slur-mute-warn"))));
        offender.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverPlayer)));
        offender.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + fpreport));
        player.spigot().sendMessage(offender);
        TextComponent text = new TextComponent();
        text.setText(Text.prefix(Sentinel.dict.get("slur-mute-notification").formatted(player.getName(),scoreMap.get(player),Config.punishScore)));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("filter-notification-hover").formatted(origMessage,highlightedMSG))));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + fpreport));

        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
        if (Config.logSwear) WebhookSender.sendSlurLog(player,origMessage,scoreMap.get(player));
    }
    public static void blockSwear(Player player, String highlightedMSG, String origMessage, String severity, AsyncPlayerChatEvent e) {
        String FPReport = ReportFalsePositives.generateReport(e);
        TextComponent offender = new TextComponent();
        String hover = Sentinel.dict.get("action-automatic-reportable");
        offender.setText(Text.prefix((Sentinel.dict.get("swear-block-warn"))));
        offender.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
        offender.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + FPReport));
        player.spigot().sendMessage(offender);

        TextComponent staff = new TextComponent();
        staff.setText(Text.prefix(Sentinel.dict.get("swear-block-notification").formatted(player.getName(),scoreMap.get(player),Config.punishScore)));
        staff.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("severity-notification-hover").formatted(origMessage,highlightedMSG,severity))));
        staff.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + FPReport));

        ServerUtils.forEachStaff(staffmember -> {
            staffmember.spigot().sendMessage(staff);
        });
    }
*/
    public static String highlightProfanity(String text) {
        String highlightedSwears = highlightSwears(fullSimplify(text),  "&e",  "&f");
        String highlightedText = highlightSlurs(highlightedSwears,  "&c",  "&f");
        return Text.color(highlightedText);
    }
    public static String highlightProfanity(String text, String start, String end) {
        String highlightedSwears = highlightSwears(fullSimplify(text), start, end);
        String highlightedText = highlightSlurs(highlightedSwears, start, end);
        return Text.color(highlightedText);
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
        ServerUtils.sendDebugMessage("ProfanityFilter:  Lowercased: " + lowercasedText);

        // 2:
        String cleanedText = removeFalsePositives(lowercasedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Removed False positives: " + cleanedText));

        // 3:
        if (containsSwears(cleanedText)) return "low";
        if (containsSlurs(cleanedText)) return "slur";

        // 4:
        String convertedText = convertLeetSpeakCharacters(cleanedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Leet Converted: " + convertedText));

        // 5:
        if (containsSwears(convertedText)) return "medium-low";
        if (containsSlurs(cleanedText)) return "slur";

        // 6:
        String strippedText = stripSpecialCharacters(convertedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Specials Removed: " + strippedText));

        // 7:
        if (containsSwears(strippedText)) return "medium";
        if (containsSlurs(strippedText)) return "slur";

        // 8:
        String simplifiedText = simplifyRepeatingLetters(strippedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Removed Repeating: " + simplifiedText));

        // 9:
        if (containsSwears(simplifiedText)) return "medium-high";
        if (containsSlurs(simplifiedText)) return "slur";

        // 10:
        String finalText = removePeriodsAndSpaces(simplifiedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Remove Punctuation: " + finalText));

        // 11:
        if (containsSwears(finalText)) return "high";
        if (containsSlurs(finalText)) return "slur";

        return "safe";
    }


    public static boolean ContainsProfanity(String text) {
        return containsSwears(text) || containsSlurs(text);
    }
    private static boolean containsSwears(String text) {
        ServerUtils.sendDebugMessage("ProfanityFilter: Checking for swears");
        for (String swear : swearBlacklist) {
            if (text.contains(swear)) return true;
        }
        return false;
    }
    private static boolean containsSlurs(String text) {
        ServerUtils.sendDebugMessage("ProfanityFilter: Checking for slurs");
        for (String slur : slurs) {
            if (text.contains(slur)) return true;
        }
        return false;
    }
    public static String removeFalsePositives(String text) {
        for (String falsePositive : swearWhitelist) {
            text = text.replace(falsePositive, "");
        }
        return text;
    }
    public static String convertLeetSpeakCharacters(String text) {
        text = Text.fromLeetString(text);
        return text;
    }

    public static String stripSpecialCharacters(String text) {
        text = text.replaceAll("[^A-Za-z0-9.,!?;:'\"()\\[\\]{}]", "").trim();
        return text;
    }

    public static String simplifyRepeatingLetters(String text) {
        text = Text.replaceRepeatingLetters(text);
        return text;
    }

    public static String removePeriodsAndSpaces(String text) {
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
