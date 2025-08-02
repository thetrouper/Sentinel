package me.trouper.sentinel.utils;


public final class OldTXT {
    
    public static final char SECTION_SYMBOL = (char)167;

    public static String color(String msg) {
        return msg.replace('&', SECTION_SYMBOL);
    }

}
