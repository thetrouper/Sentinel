package me.trouper.sentinel.server.functions.chatfilter.profanity;

import me.trouper.sentinel.Sentinel;

public enum Severity {
    LOW(Sentinel.mainConfig.chat.swearFilter.lowScore),
    MEDIUM_LOW(Sentinel.mainConfig.chat.swearFilter.mediumLowScore),
    MEDIUM(Sentinel.mainConfig.chat.swearFilter.mediumScore),
    MEDIUM_HIGH(Sentinel.mainConfig.chat.swearFilter.mediumHighScore),
    HIGH(Sentinel.mainConfig.chat.swearFilter.highScore),
    REGEX(Sentinel.mainConfig.chat.swearFilter.regexScore),
    SLUR(Sentinel.mainConfig.chat.swearFilter.highScore),
    SAFE(0);

    private final int score;

    Severity(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
