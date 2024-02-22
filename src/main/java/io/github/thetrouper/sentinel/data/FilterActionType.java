package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;

public record FilterActionType(String logTitle, String logName, String chatWarning, String chatWarningHover, String chatNotification, String chatNotificationHover, String punishmentCommand, int embedColor) {
    public static final FilterActionType UNICODE_BLOCK = new FilterActionType("Sentinel Anti-Unicode Log","Anti-Unicode","unicode-warn","action-automatic-reportable","unicode-notification","unicode-notification-hover", null,0xFF0000);
    public static final FilterActionType URL_BLOCK = new FilterActionType("Sentinel Anti-URL Log","Anti-URL","url-warn","action-automatic-reportable","url-notification","url-notification-hover", null,0xFF0000);
    public static final FilterActionType SPAM_BLOCK = new FilterActionType("Sentinel Anti-Spam Log","Anti-Spam","spam-warn","action-automatic-reportable","spam-notification","spam-notification-hover", null,0xFF0000);
    public static final FilterActionType SPAM_PUNISH = new FilterActionType("Sentinel Anti-Spam Log","Anti-Spam","spam-mute-warn","action-automatic-reportable","spam-mute-notification","spam-notification-hover", Sentinel.mainConfig.chat.antiSpam.spamPunishCommand,0xFF0000);
    public static final FilterActionType SWEAR_BLOCK = new FilterActionType("Sentinel Profanity Filter Log","Anti-Swear","profanity-warn","action-automatic-reportable","profanity-notification","profanity-notification-hover", null,0xFF0000);
    public static final FilterActionType SWEAR_PUNISH = new FilterActionType("Sentinel Profanity Filter Log","Anti-Swear","profanity-mute-warn","action-automatic-reportable","profanity-mute-notification","profanity-notification-hover", Sentinel.mainConfig.chat.antiSwear.swearPunishCommand,0xFF0000);
    public static final FilterActionType SLUR_PUNISH = new FilterActionType("Sentinel Profanity Filter Log","Anti-Slur","slur-mute-warn","action-automatic-reportable","slur-mute-notification","profanity-notification-hover", Sentinel.mainConfig.chat.antiSwear.strictPunishCommand,0xFF0000);
    public static final FilterActionType SAFE = new FilterActionType("ERROR",null,null,null,null,null,null,0x00AA00);
}
