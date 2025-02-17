package me.trouper.sentinel.server.functions.chatfilter.profanity;

import me.trouper.sentinel.Sentinel;

public enum Severity {
    LOW(Sentinel.mainConfig.chat.profanityFilter.lowScore),
    MEDIUM_LOW(Sentinel.mainConfig.chat.profanityFilter.mediumLowScore),
    MEDIUM(Sentinel.mainConfig.chat.profanityFilter.mediumScore),
    MEDIUM_HIGH(Sentinel.mainConfig.chat.profanityFilter.mediumHighScore),
    HIGH(Sentinel.mainConfig.chat.profanityFilter.highScore),
    REGEX(Sentinel.mainConfig.chat.profanityFilter.regexScore),
    SLUR(Sentinel.mainConfig.chat.profanityFilter.highScore),
    SAFE(0);

    private final int score;

    Severity(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
