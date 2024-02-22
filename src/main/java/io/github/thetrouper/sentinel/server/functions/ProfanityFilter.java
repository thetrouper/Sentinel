package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.*;
import io.github.thetrouper.sentinel.server.FilterAction;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfanityFilter {
    public static Map<UUID, Integer> scoreMap = new HashMap<>();
    private static final List<String> swearBlacklist = Sentinel.swearConfig.swears;
    private static final List<String> swearWhitelist = Sentinel.fpConfig.swearWhitelist;
    private static final List<String> slurs = Sentinel.strictConfig.strict;

    public static void handleProfanityFilter(AsyncPlayerChatEvent event, Report report) {
        Player player = event.getPlayer();
        String message = Text.removeFirstColor(event.getMessage());
        FilterSeverity severity = checkSeverity(message,report);

        if (severity.equals(FilterSeverity.SAFE)) return;

        scoreMap.putIfAbsent(player.getUniqueId(), 0);
        int previousScore = scoreMap.get(player.getUniqueId());
        ServerUtils.sendDebugMessage(String.format("AntiSwear Flag, Message: %s Concentrated: %s Severity: %s Previous Score: %d Adding Score: %d",
                message, fullSimplify(message), severity, previousScore, severity.getScore()));
        event.setCancelled(true);

        int newScore = previousScore + severity.getScore();
        scoreMap.put(player.getUniqueId(), newScore);

        if (newScore > Sentinel.mainConfig.chat.antiSwear.punishScore) {
            FilterAction.takeAction(event,FilterActionType.SWEAR_PUNISH,report,0,severity);
            return;
        }
        FilterAction.takeAction(event,getFilterActionType(severity),report,0,severity);
        //FilterAction.filterPunish(event, getFAT(severity), null, severity,report.id());
    }

    public static FilterActionType getFilterActionType(FilterSeverity severity) {
        return switch (severity) {
            case SLUR -> FilterActionType.SLUR_PUNISH;
            case LOW,MEDIUM_LOW,MEDIUM,MEDIUM_HIGH,HIGH -> FilterActionType.SWEAR_BLOCK;
            case SAFE -> FilterActionType.SAFE;
        };
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
        FilterSeverity severity = FilterSeverity.SAFE;
        // 1:
        String lowercasedText = text.toLowerCase();
        report.stepsTaken().put("Lowercased", lowercasedText);
        ServerUtils.sendDebugMessage("ProfanityFilter:  Lowercased: " + lowercasedText);

        // 2:
        String cleanedText = removeFalsePositives(lowercasedText);
        report.stepsTaken().put("Remove False Positives", cleanedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Removed False positives: " + cleanedText));

        // 3:
        severity = checkProfanity(cleanedText,FilterSeverity.LOW);
        if (severity != FilterSeverity.SAFE) {
            report.stepsTaken().replace("Remove False Positives", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return severity;
        }

        // 4:
        String convertedText = convertLeetSpeakCharacters(cleanedText);
        report.stepsTaken().put("Convert LeetSpeak", convertedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Leet Converted: " + convertedText));

        // 5:
        severity = checkProfanity(convertedText,FilterSeverity.MEDIUM_LOW);
        if (severity != FilterSeverity.SAFE) {
            report.stepsTaken().replace("Convert LeetSpeak", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return severity;
        }
        // 6:
        String strippedText = stripSpecialCharacters(convertedText);
        report.stepsTaken().put("Remove Special Characters", strippedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Specials Removed: " + strippedText));

        // 7:
        severity = checkProfanity(strippedText,FilterSeverity.MEDIUM);
        if (severity != FilterSeverity.SAFE) {
            report.stepsTaken().replace("Remove Special Characters", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return severity;
        }
        // 8:
        String simplifiedText = simplifyRepeatingLetters(strippedText);
        report.stepsTaken().put("Remove Repeats", simplifiedText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Removed Repeating: " + simplifiedText));

        // 9:
        severity = checkProfanity(simplifiedText,FilterSeverity.MEDIUM_HIGH);
        if (severity != FilterSeverity.SAFE) {
            report.stepsTaken().replace("Remove Repeats", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return severity;
        }
        // 10:
        String finalText = removePeriodsAndSpaces(simplifiedText);
        report.stepsTaken().put("Remove Punctuation", finalText);
        ServerUtils.sendDebugMessage(("ProfanityFilter: Remove Punctuation: " + finalText));

        // 11:
        severity = checkProfanity(finalText,FilterSeverity.HIGH);
        if (severity != FilterSeverity.SAFE) {
            report.stepsTaken().replace("Remove Punctuation", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return severity;
        }

        return severity;
    }


    public static FilterSeverity checkProfanity(String text, FilterSeverity severity) {
        if (containsSlurs(text)) return FilterSeverity.SLUR;
        if (containsSwears(text)) return severity;
        return FilterSeverity.SAFE;
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
        for (UUID uuid : scoreMap.keySet()) {
            int score = scoreMap.get(uuid);
            if (score > 0) {
                score = score - Sentinel.mainConfig.chat.antiSwear.scoreDecay;
                scoreMap.put(uuid, Math.max(0, score));
            }
        }
    }
}
