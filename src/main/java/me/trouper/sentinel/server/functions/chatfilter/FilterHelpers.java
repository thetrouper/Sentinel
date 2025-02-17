package me.trouper.sentinel.server.functions.chatfilter;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.profanity.Severity;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterHelpers {

    public static Severity checkSlur(String text, Severity backup) {
        if (containsSlurs(text)) return Severity.SLUR;
        if (containsSwears(text)) return backup;
        return Severity.SAFE;
    }

    public static boolean containsSwears(String text) {
        ServerUtils.verbose("ProfanityFilter: Checking for swears");
        for (String swear : Sentinel.swearConfig.swears) {
            if (text.contains(swear)) return true;
        }

        Pattern pattern = Pattern.compile(Sentinel.swearConfig.regexSwears, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        return matcher.find() && Sentinel.swearConfig.useRegex;
    }

    public static boolean containsSlurs(String text) {
        ServerUtils.verbose("ProfanityFilter: Checking for slurs");
        for (String slur : Sentinel.strictConfig.strict) {
            if (text.contains(slur)) return true;
        }

        Pattern pattern = Pattern.compile(Sentinel.strictConfig.regexStrict, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        return matcher.find() && Sentinel.strictConfig.useRegex;
    }

    public static String removeFalsePositives(String text) {
        for (String falsePositive : Sentinel.fpConfig.swearWhitelist) {
            text = text.replace(falsePositive, "");
        }
        text = text.replaceAll(Sentinel.fpConfig.regexWhitelist,"");
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

    public static String highlightProfanity(String text, String start, String end) {
        String highlightedSwears = highlightSwears(fullSimplify(text), start, end);
        return Text.color(highlightSlurs(highlightedSwears, start, end));
    }

    private static String highlightSwears(String text, String start, String end) {
        for (String swear : Sentinel.swearConfig.swears) {
            text = text.replace(swear, start + swear + end);
        }
        return text;
    }

    private static String highlightSlurs(String text, String start, String end) {
        for (String slur : Sentinel.strictConfig.strict) {
            text = text.replace(slur, start + slur + end);
        }
        return text;
    }

    public static String fullSimplify(String text) {
        String lowercasedText = text.toLowerCase();
        String cleanedText = FilterHelpers.removeFalsePositives(lowercasedText);
        String convertedText = FilterHelpers.convertLeetSpeakCharacters(cleanedText);
        String strippedText = FilterHelpers.stripSpecialCharacters(convertedText);
        String simplifiedText = FilterHelpers.simplifyRepeatingLetters(strippedText);
        return FilterHelpers.removePeriodsAndSpaces(simplifiedText);
    }

    public static void restrictMessage(AsyncChatEvent event, boolean silent) {
        if (silent) {
            event.viewers().clear();
            event.viewers().add(event.getPlayer());
        } else {
            event.setCancelled(true);
        }
    }
}
