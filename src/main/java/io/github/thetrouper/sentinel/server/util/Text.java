package io.github.thetrouper.sentinel.server.util;


import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.config.AdvancedConfig;

import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class Text {
    public static final char SECTION_SYMBOL = (char)167;

    public static String color(String msg) {
        return msg.replace('&', SECTION_SYMBOL);
    }
    public static String prefix(String text) {
        String prefix = Sentinel.prefix;
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
    public static String replaceRepeatingLetters(String message) {
        StringBuilder result = new StringBuilder();
        char prevChar = '\0';
        int count = 0;

        for (char c : message.toCharArray()) {
            if (c == prevChar) {
                count++;
                if (count <= 3) {
                    result.append(c);
                }
            } else {
                prevChar = c;
                count = 1;
                result.append(c);
            }
        }
        return result.toString();
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
}
