package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;

public enum FAT {
    BLOCK_SWEAR("Sentinel Profanity Filter",null,"swear-block-warn", "swear-block-notification", null,0x000000),
    BLOCK_SPAM("Sentinel Anti-Spam", null, "spam-block-warn", "spam-notification",null,0x000000),
    SWEAR_PUNISH("Sentinel Anti-Swear Log","Anti-Swear", "profanity-mute-warn", "profanity-mute-notification", Sentinel.mainConfig.chat.antiSwear.swearPunishCommand, 0xFFB000),
    SLUR_PUNISH("Sentinel Anti-Slur Log", "Anti-Slur", "slur-mute-warn", "slur-mute-notification", Sentinel.mainConfig.chat.antiSwear.strictPunishCommand, 0xFF0000),
    SPAM_PUNISH("Sentinel Anti-Spam Log", "Anti-Spam", "spam-mute-warn", "spam-mute-notification", Sentinel.mainConfig.chat.antiSpam.spamPunishCommand, 0xFF8000);

    private final String title;
    private final String name;
    private final String warnTranslationKey;
    private final String notifTranslationKey;
    private final String executedCommand;
    private final int embedColor;

    FAT(String title, String name, String warnTranslationKey, String notifTranslationKey, String executedCommand, int embedColor) {
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
    public int getColor() {
        return embedColor;
    }
}

