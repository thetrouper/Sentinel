package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;

public enum FilterSeverity {
    LOW(Sentinel.mainConfig.chat.antiSwear.lowScore),
    MEDIUM_LOW(Sentinel.mainConfig.chat.antiSwear.mediumLowScore),
    MEDIUM(Sentinel.mainConfig.chat.antiSwear.mediumScore),
    MEDIUM_HIGH(Sentinel.mainConfig.chat.antiSwear.mediumHighScore),
    HIGH(Sentinel.mainConfig.chat.antiSwear.highScore),
    SLUR(Sentinel.mainConfig.chat.antiSwear.highScore),
    SAFE(0);

    private final int score;

    FilterSeverity(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
