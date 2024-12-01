package me.trouper.sentinel.server.functions.chatfilter.regex;

import me.trouper.sentinel.Sentinel;

public enum RegexFlagType {
    URL_BLOCK(Sentinel.lang.violations.chat.regex.urlBlockName, Sentinel.lang.violations.chat.regex.urlBlockMessage),
    UNICODE_BLOCK(Sentinel.lang.violations.chat.regex.unicodeBlockName, Sentinel.lang.violations.chat.regex.unicodeBlockMessage),
    SWEAR_BLOCK(Sentinel.lang.violations.chat.regex.swearBlockName, Sentinel.lang.violations.chat.regex.swearBlockMessage),
    STRICT_BLOCK(Sentinel.lang.violations.chat.regex.strictBlockName, Sentinel.lang.violations.chat.regex.strictBlockMessage);



    private String name;
    private String blockMessage;

    RegexFlagType(String name, String blockMessage) {

    }

    public String getBlockMessage() {
        return blockMessage;
    }

    public String getName() {
        return name;
    }
}
