package me.trouper.sentinel.server.functions.chatfilter.profanity;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.Main;

public enum Severity implements Main {
    LOW(main.dir().io.mainConfig.chat.profanityFilter.lowScore),
    MEDIUM_LOW(main.dir().io.mainConfig.chat.profanityFilter.mediumLowScore),
    MEDIUM(main.dir().io.mainConfig.chat.profanityFilter.mediumScore),
    MEDIUM_HIGH(main.dir().io.mainConfig.chat.profanityFilter.mediumHighScore),
    HIGH(main.dir().io.mainConfig.chat.profanityFilter.highScore),
    REGEX(main.dir().io.mainConfig.chat.profanityFilter.regexScore),
    SLUR(main.dir().io.mainConfig.chat.profanityFilter.highScore),
    SAFE(0);

    private final int score;

    Severity(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
