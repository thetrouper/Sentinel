package me.trouper.sentinel.server.functions.chatfilter.profanity;

import me.trouper.sentinel.Sentinel;

public enum Severity {
    LOW(Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.lowScore),
    MEDIUM_LOW(Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.mediumLowScore),
    MEDIUM(Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.mediumScore),
    MEDIUM_HIGH(Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.mediumHighScore),
    HIGH(Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.highScore),
    REGEX(Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.regexScore),
    SLUR(Sentinel.getInstance().getDirector().io.mainConfig.chat.profanityFilter.highScore),
    SAFE(0);

    private final int score;

    Severity(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
