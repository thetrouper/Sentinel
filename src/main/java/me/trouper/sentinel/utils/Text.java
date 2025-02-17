package me.trouper.sentinel.utils;


import me.trouper.sentinel.Sentinel;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Text {

    public static String removeColors(String input) {
        return input.replaceAll("§[0-9a-frn]", "");
    }

    public static String regexHighlighter(String input, String regex, String startString, String endString) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(result, startString + matcher.group() + endString);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static final char SECTION_SYMBOL = (char)167;

    public static String color(String msg) {
        return msg.replace('&', SECTION_SYMBOL);
    }

    public static String prefix(String text) {
        String prefix = Sentinel.mainConfig.plugin.prefix;
        return color(prefix + text);
    }

    public static String removeFirstColor(String input) {
        if (input.startsWith("\u00a7")) {
            if (input.length() > 2) {
                return input.substring(2);
            } else {
                return "";
            }
        } else {
            return input;
        }
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

    public static String fromLeetString(String s) {
        Map<String, String> dictionary = Sentinel.advConfig.leetPatterns;
        String msg = s;

        for (String key : dictionary.keySet()) {
            if (!s.contains(key)) continue;
            try {
                if (key.equals("$")) {
                    msg = msg.replaceAll("\\$", "s");
                }
                else {
                    msg = msg.replaceAll(key, dictionary.get(key));
                }
            }
            catch (PatternSyntaxException ex) {
                String regex = "[" + key + "]";
                msg = msg.replaceAll(regex, dictionary.get(key));
            }
        }
        return msg;
    }

    public static String cleanName(String type) {
        return type.replaceAll("_"," ").toLowerCase();
    }
}
