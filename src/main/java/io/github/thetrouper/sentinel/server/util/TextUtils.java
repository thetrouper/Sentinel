package io.github.thetrouper.sentinel.server.util;


import io.github.thetrouper.sentinel.Sentinel;

public class TextUtils {
    public static String prefix(String text) {
        String prefix = Sentinel.prefix;
        return prefix + text;
    }

}
