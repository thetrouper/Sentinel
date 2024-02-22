package io.github.thetrouper.sentinel.server;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.data.FilterActionType;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.CompletableFuture;

public class FilterAction {

    public static void takeAction(AsyncPlayerChatEvent e, FilterActionType type, Report report, double similarity, FilterSeverity severity) {
        if (type.equals(FilterActionType.SAFE)) return;

        Player offender = e.getPlayer();
        sendWarning(type,offender,report.id());

        int current = 0;
        int max = 0;
        String message = e.getMessage();

        if (type.equals(FilterActionType.SWEAR_PUNISH) || type.equals(FilterActionType.SWEAR_BLOCK) || type.equals(FilterActionType.SLUR_PUNISH)) {
            current = ProfanityFilter.scoreMap.get(offender.getUniqueId());
            max = Sentinel.mainConfig.chat.antiSwear.punishScore;
        } else if (type.equals(FilterActionType.SPAM_BLOCK) || type.equals(FilterActionType.SPAM_PUNISH)) {
            current = AntiSpam.heatMap.get(offender.getUniqueId());
            max = Sentinel.mainConfig.chat.antiSpam.punishHeat;
        }

        sendNotifs(getNotif(type,offender,message,similarity,severity,current,max));
        if (type.isLogged()) sendConsoleLog(type,offender,message);
        if (type.isLogged()) sendDiscordLog(type,offender,message);
        if (type.punishmentCommand() != null) ServerUtils.sendCommand(type.punishmentCommand().replaceAll("%player%",offender.getName()));
    }

    public static void sendNotifs(TextComponent notif) {
        ServerUtils.forEachStaff(staff->{
            staff.sendMessage(notif);
        });
    }

    public static TextComponent getNotif(FilterActionType type, Player offender, String message, double similarity, FilterSeverity severity, int current, int max) {
        return getNotifText(type,offender,current,max).hoverEvent(getNotifHover(type,offender,message,similarity,severity));
    }

    public static TextComponent getNotifHover(FilterActionType type, Player offender, String message, double similarity, FilterSeverity severity) {
        String hover = type.chatNotificationHover();

        if (type.equals(FilterActionType.SPAM_BLOCK) || type.equals(FilterActionType.SPAM_PUNISH)) {
             hover = hover.formatted(
                     AntiSpam.lastMessageMap.get(offender.getUniqueId()),
                     message,
                     similarity
             );
        }

        if (type.equals(FilterActionType.SWEAR_BLOCK) || type.equals(FilterActionType.SWEAR_PUNISH) || type.equals(FilterActionType.SLUR_PUNISH)) {
            hover = hover.formatted(
                    message,
                    ProfanityFilter.highlightProfanity(ProfanityFilter.fullSimplify(message)),
                    Text.cleanName(severity.name())
            );
        }

        if (type.equals(FilterActionType.UNICODE_BLOCK) || type.equals(FilterActionType.URL_BLOCK)) {
            if (type.equals(FilterActionType.URL_BLOCK)) {
                hover = hover.formatted(
                        Text.regexHighlighter(message, Sentinel.advConfig.urlRegex," &e> &n","&r &e<&f")
                );
            } else {
                hover = hover.formatted(
                        Text.regexHighlighter(message, "[^\\x00-\\x7F]+"," &c","&r&f")
                );
            }
        }

        return Component.text(Text.color(hover));
    }

    public static TextComponent getNotifText(FilterActionType type, Player offender, int current, int max) {
        return Component.text(Text.prefix(type.chatNotification().formatted(
                offender.getName(),
                current,
                max
        )));
    }

    public static void sendWarning(FilterActionType type, Player offender, long report) {
        TextComponent warning = Component.text(Text.prefix(type.chatWarning()))
                .hoverEvent(Component.text(type.chatWarningHover()))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"sentinelcallback fpreport " + report));
        offender.sendMessage(warning);
    }

    public static void sendDiscordLog(FilterActionType type, Player offender, String message) {
        CompletableFuture.runAsync(()->{

            String superTitle = type.logTitle();
            String title = "%s has triggered the %s!".formatted(offender.getName(),type.logName());
            boolean isSwear = type.equals(FilterActionType.SWEAR_BLOCK) || type.equals(FilterActionType.SWEAR_PUNISH) || type.equals(FilterActionType.SLUR_PUNISH);
            boolean isSpam = type.equals(FilterActionType.SPAM_BLOCK) || type.equals(FilterActionType.SPAM_PUNISH);
            String description = """
                    %1$sUUID: `%5$s` %6$s
                    %2$s%3$sHeat: `%7$s/%8$s` %12$s
                    %2$s%3$sScore: `%9$s/%10$s` %13$s
                    %2$s%2$s%4$sMessage: `%11$s`
                    """.formatted(
                    Emojis.rightSort,
                    Emojis.space,
                    Emojis.rightArrow,
                    Emojis.rightDoubleArrow,
                    offender.getUniqueId(),
                    Emojis.target,
                    AntiSpam.heatMap.get(offender.getUniqueId()),
                    Sentinel.mainConfig.chat.antiSpam.punishHeat,
                    ProfanityFilter.scoreMap.get(offender.getUniqueId()),
                    Sentinel.mainConfig.chat.antiSwear.punishScore,
                    message,
                    isSpam ? Emojis.alarm  : "",
                    isSwear ? Emojis.alarm  : ""
            );
            DiscordEmbed.Builder embedBuilder = new DiscordEmbed.Builder()
                    .author(new DiscordEmbed.Author(superTitle,"https://builtbybit.com/resources/sentinel-anti-nuke.30130/",null))
                    .title(title)
                    .desc(description)
                    .color(type.embedColor());

            if (isSpam) embedBuilder.addField("Previous Message", AntiSpam.lastMessageMap.get(offender.getUniqueId()));
            if (isSwear) embedBuilder.addField("Fully Simplified Message", ProfanityFilter.fullSimplify(message));
            if (type.equals(FilterActionType.URL_BLOCK)) embedBuilder.addField("Caught",Text.regexHighlighter(message, Sentinel.advConfig.urlRegex," > "," < ").replaceAll("\\.","[.]"));
            if (type.equals(FilterActionType.UNICODE_BLOCK)) embedBuilder.addField("Caught: ", Text.regexHighlighter(message, "[^\\x00-\\x7F]+"," > "," < ").replaceAll("\\.","[.]"));

            if (type.punishmentCommand() != null) embedBuilder.addField("Punishment Command",type.punishmentCommand());

            ServerUtils.sendDebugMessage("Executing webhook...");
            DiscordWebhook.create()
                    .username("Sentinel Anti-Nuke | Logs")
                    .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                    .addEmbed(embedBuilder.build())
                    .send(Sentinel.mainConfig.plugin.webhook);
        });
    }

    public static void sendConsoleLog(FilterActionType type, Player offender, String message) {
        StringBuilder log = new StringBuilder();

        String superTitle = "\n]=- " + type.logTitle() + " -=[";
        String title = "\n%s has triggered the %s!\n".formatted(offender.getName(),type.logName());
        boolean isSwear = type.equals(FilterActionType.SWEAR_BLOCK) || type.equals(FilterActionType.SWEAR_PUNISH) || type.equals(FilterActionType.SLUR_PUNISH);
        boolean isSpam = type.equals(FilterActionType.SPAM_BLOCK) || type.equals(FilterActionType.SPAM_PUNISH);

        String description = """
                    ➥ UUID: %5$s %6$s
                      ➤ Heat: %7$s/%8$s %12$s
                      ➤ Score: %9$s/%10$s %13$s
                    Message: %11$s
                    """.formatted(
                Emojis.rightSort,
                Emojis.space,
                Emojis.rightArrow,
                Emojis.rightDoubleArrow,
                offender.getUniqueId(),
                "\uD83D\uDF8B",
                AntiSpam.heatMap.get(offender.getUniqueId()),
                Sentinel.mainConfig.chat.antiSpam.punishHeat,
                ProfanityFilter.scoreMap.get(offender.getUniqueId()),
                Sentinel.mainConfig.chat.antiSwear.punishScore,
                message,
                isSpam ? "\uD83D\uDD6D"  : "",
                isSwear ? "\uD83D\uDD6D"  : ""
        );

        if (isSpam) description += "\nPrevious Message: " + AntiSpam.lastMessageMap.get(offender.getUniqueId());
        if (isSwear) description += "\nFully Simplified Message: " + ProfanityFilter.fullSimplify(message);
        if (type.equals(FilterActionType.URL_BLOCK)) description += "\nCaught: " + Text.regexHighlighter(message, Sentinel.advConfig.urlRegex," > "," < ").replaceAll("\\.","[.]");
        if (type.equals(FilterActionType.UNICODE_BLOCK)) description += "\nCaught: " + Text.regexHighlighter(message, "[^\\x00-\\x7F]+"," > "," < ").replaceAll("\\.","[.]");

        log.append(superTitle);
        log.append(title);
        log.append(description);

        if (type.punishmentCommand() != null) log.append("\nPunishment Command: ").append(type.punishmentCommand());

        log.append("\n]======--- End of Log ---=======[");

        Sentinel.log.info(log.toString());
    }
}
