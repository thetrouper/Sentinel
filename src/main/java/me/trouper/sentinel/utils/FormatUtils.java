package me.trouper.sentinel.utils;

import me.trouper.sentinel.Sentinel;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FormatUtils {
    public static String formatEnum(Enum<?> obj) {
        if (obj == null) return "Null";
        String name = obj.name();
        String[] words = name.toLowerCase().split("_");

        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return formatted.toString().trim();
    }

    public static Component formatLoc(Location loc) {
        return Text.color("&#aaaaaa(&#ffaaaa%s&#aaaaaa,&#aaffaa%s&#aaaaaa,&#aaaaff%s&#aaaaaa)".formatted(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()));
    }

    public static String formatType(String type) {
        return type.replaceAll("_"," ").toLowerCase();
    }

    public static String legacyColor(String msg) {
        return msg.replaceAll("&","§");
    }

    public static String formatBytes(int bytes) {
        if (bytes < 0) throw new IllegalArgumentException("Byte size must be non-negative.");

        String[] units = {"B", "KB", "MB", "GB", "TB", "PB", "EB"};
        double size = bytes;
        int unitIndex = 0;

        while (size >= 1000 && unitIndex < units.length - 1) {
            size /= 1000.0;
            unitIndex++;
        }

        String formatted;
        if (size >= 100) {
            formatted = String.format("%.0f", size);
        } else if (size >= 10) {
            formatted = String.format("%.1f", size);
        } else {
            formatted = String.format("%.2f", size);
        }

        return formatted + units[unitIndex];
    }
    
    public static String regexHighlighter(String input, String regex, String startString, String endString) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(result, startString + matcher.group() + endString);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static String replaceRepeatingLetters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder simplifiedText = new StringBuilder();
        char currentChar = input.charAt(0);
        int count = 1;

        for (int i = 1; i < input.length(); i++) {
            char nextChar = input.charAt(i);

            if (Character.toLowerCase(nextChar) == Character.toLowerCase(currentChar)) {
                count++;
            } else {
                simplifiedText.append(currentChar);

                if (count > 1) {
                    simplifiedText.append(currentChar);
                }

                currentChar = nextChar;
                count = 1;
            }
        }

        simplifiedText.append(currentChar);

        if (count > 1) {
            simplifiedText.append(currentChar);
        }

        return simplifiedText.toString();
    }
}
