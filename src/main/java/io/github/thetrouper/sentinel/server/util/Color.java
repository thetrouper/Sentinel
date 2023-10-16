package io.github.thetrouper.sentinel.server.util;

public class Color {
    public static String color(String tocolor) {
        return tocolor.replace("&","\u00A7");
    }
}
