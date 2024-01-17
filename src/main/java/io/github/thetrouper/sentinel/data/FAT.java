package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;

import java.awt.*;

public enum FAT {
    BLOCK_SWEAR("Sentinel Profanity Filter",null,"swear-block-warn", "swear-block-notification", null,null),
    BLOCK_SPAM("Sentinel Anti-Spam", null, "spam-block-warn", "spam-notification",null,null),
    SWEAR_PUNISH("Sentinel Anti-Swear Log","Anti-Swear", "profanity-mute-warn", "profanity-mute-notification", Sentinel.mainConfig.chat.antiSwear.swearPunishCommand, Color.orange),
    SLUR_PUNISH("Sentinel Anti-Slur Log", "Anti-Slur", "slur-mute-warn", "slur-mute-notification", Sentinel.mainConfig.chat.antiSwear.strictPunishCommand, Color.red),
    SPAM_PUNISH("Sentinel Anti-Spam Log", "Anti-Spam", "spam-mute-warn", "spam-mute-notification", Sentinel.mainConfig.chat.antiSpam.spamPunishCommand, Color.pink);

    private final String title;
    private final String name;
    private final String warnTranslationKey;
    private final String notifTranslationKey;
    private final String executedCommand;
    private final Color embedColor;

    FAT(String title, String name, String warnTranslationKey, String notifTranslationKey, String executedCommand, Color embedColor) {
        this.title = title;
        this.name = name;
        this.warnTranslationKey = warnTranslationKey;
        this.notifTranslationKey = notifTranslationKey;
        this.executedCommand = executedCommand;
        this.embedColor = embedColor;
    }
    public String getTitle() {
        return title;
    }
    public String getName() {
        return name;
    }
    public String getWarnTranslationKey() {
        return warnTranslationKey;
    }

    public String getNotifTranslationKey() {
        return notifTranslationKey;
    }

    public String getExecutedCommand() {
        return executedCommand;
    }
    public Color getColor() {
        return embedColor;
    }
}

