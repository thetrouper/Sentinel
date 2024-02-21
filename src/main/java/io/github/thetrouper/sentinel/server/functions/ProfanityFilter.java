package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.server.FilterAction;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfanityFilter {
    public static Map<Player, Integer> scoreMap;
    private static final List<String> swearBlacklist = Sentinel.swearConfig.swears;
    private static final List<String> swearWhitelist = Sentinel.fpConfig.swearWhitelist;
    private static final List<String> slurs = Sentinel.strictConfig.strict;

    public static void enableAntiSwear() {
        scoreMap = new HashMap<>();
    }

    public static void handleProfanityFilter(AsyncPlayerChatEvent event, Report report) {
        Player player = event.getPlayer();
        String message = Text.removeFirstColor(event.getMessage());
        FilterSeverity severity = checkSeverity(message,report);

        if (severity.equals(FilterSeverity.SAFE)) return;

        scoreMap.putIfAbsent(player, 0);
        int previousScore = scoreMap.get(player);
        ServerUtils.sendDebugMessage(String.format("AntiSwear Flag, Message: %s Concentrated: %s Severity: %s Previous Score: %d Adding Score: %d",
                message, fullSimplify(message), severity, previousScore, severity.getScore()));
        event.setCancelled(true);

        int newScore = previousScore + severity.getScore();
        scoreMap.put(player, newScore);

        if (newScore > Sentinel.mainConfig.chat.antiSwear.punishScore) {
            FilterAction.filterPunish(event, FAT.SWEAR_PUNISH, null, severity);
            return;
        }

        FilterAction.filterPunish(event, getFAT(severity), null, severity);
    }


    private static FAT getFAT(FilterSeverity severity) {
        return switch (severity) {
            case SAFE -> FAT.SAFE;
            case LOW, MEDIUM_LOW, MEDIUM, MEDIUM_HIGH, HIGH -> FAT.BLOCK_SWEAR;
            case SLUR -> FAT.SLUR_PUNISH;
        };
    }

    public static String highlightProfanity(String text) {
        return highlightProfanity(text, "&e", "&f");
    }

    public static String highlightProfanity(String text, String start, String end) {
        String highlightedSwears = highlightSwears(fullSimplify(text), start, end);
        return Text.color(highlightSlurs(highlightedSwears, start, end));
    }

    private static String highlightSwears(String text, String start, String end) {
        for (String swear : swearBlacklist) {
            text = text.replace(swear, start + swear + end);
        }
        return text;
    }

    private static String highlightSlurs(String text, String start, String end) {
        for (String slur : slurs) {
            text = text.replace(slur, start + slur + end);
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
        return removePeriodsAndSpaces(simplifiedText);
    }
    public static FilterSeverity checkSeverity(String text, Report report) {
        // 1:
        String lowercasedText = text.toLowerCase();
        report.stepsTaken().put("Lowercased", lowercasedText);
        ServerUtils.sendDebugMessage("ProfanityFilter:  Lowercased: " + lowercasedText);

        // 2:
        String cleanedText = removeFalsePositives(lowercasedText);
        report.stepsTaken().put("Remove False Positives", cleanedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Removed False positives: " + cleanedText));

        // 3:
        if (containsSwears(cleanedText)) return FilterSeverity.LOW;
        if (containsSlurs(cleanedText)) return FilterSeverity.SLUR;

        // 4:
        String convertedText = convertLeetSpeakCharacters(cleanedText);
        report.stepsTaken().put("Convert LeetSpeak", convertedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Leet Converted: " + convertedText));

        // 5:
        if (containsSwears(convertedText)) return FilterSeverity.MEDIUM_LOW;
        if (containsSlurs(cleanedText)) return FilterSeverity.SLUR;

        // 6:
        String strippedText = stripSpecialCharacters(convertedText);
        report.stepsTaken().put("Remove Special Characters", strippedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Specials Removed: " + strippedText));

        // 7:
        if (containsSwears(strippedText)) return FilterSeverity.MEDIUM;
        if (containsSlurs(strippedText)) return FilterSeverity.SLUR;

        // 8:
        String simplifiedText = simplifyRepeatingLetters(strippedText);
        report.stepsTaken().put("Remove Repeats", simplifiedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Removed Repeating: " + simplifiedText));

        // 9:
        if (containsSwears(simplifiedText)) return FilterSeverity.MEDIUM_HIGH;
        if (containsSlurs(simplifiedText)) return FilterSeverity.SLUR;

        // 10:
        String finalText = removePeriodsAndSpaces(simplifiedText);
        report.stepsTaken().put("Remove Punctuation", finalText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Remove Punctuation: " + finalText));

        // 11:
        if (containsSwears(finalText)) return FilterSeverity.HIGH;
        if (containsSlurs(finalText)) return FilterSeverity.SLUR;

        return FilterSeverity.SAFE;
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
        text = text.replaceAll(Sentinel.advConfig.falsePosRegex,"");
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
                score = score - Sentinel.mainConfig.chat.antiSwear.scoreDecay;
                scoreMap.put(p, Math.max(0, score));
            }
        }
    }
}
