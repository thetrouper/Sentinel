package io.github.thetrouper.sentinel.server;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static io.github.thetrouper.sentinel.server.functions.AntiSpam.heatMap;
import static io.github.thetrouper.sentinel.server.functions.AntiSpam.lastMessageMap;
import static io.github.thetrouper.sentinel.server.functions.ProfanityFilter.*;

public class FilterAction {

    public static void filterAction(Player offender, AsyncPlayerChatEvent e, String highlighted, FilterSeverity severity, Double similarity, FAT type) {
        String report = ReportFalsePositives.generateReport(e);
        DecimalFormat fs = new DecimalFormat("##.#");
        fs.setRoundingMode(RoundingMode.DOWN);

        TextComponent warn = createTextComponent(Text.prefix(Sentinel.language.get(type.getWarnTranslationKey())));
        warn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.language.get("action-automatic-reportable"))));
        warn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        TextComponent notif = createTextComponent(Text.prefix((type != FAT.SPAM_PUNISH && type != FAT.BLOCK_SPAM ?
                Sentinel.language.get("severity-notification-hover").formatted(e.getMessage(), highlighted, severity.name().toLowerCase().replace("_"," ")) :
                Sentinel.language.get("spam-notification-hover").formatted(e.getMessage(), lastMessageMap.get(offender), fs.format(similarity)))));
        notif.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.language.get("severity-notification-hover"))));
        notif.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        sendMessages(offender, warn, notif, type);

        if (shouldLogSwears(type)) {
            sendDiscordLog(offender, e, type);
            sendConsoleLog(offender, e, type);
        }
    }

    private static void sendMessages(Player offender, TextComponent warn, TextComponent notif, FAT type) {
        offender.spigot().sendMessage(warn);

        String notifText = Sentinel.language.get(type.getNotifTranslationKey());
        notif.setText(Text.prefix((type != FAT.SPAM_PUNISH && type != FAT.BLOCK_SPAM ?
                notifText.formatted(offender.getName(), scoreMap.get(offender), Sentinel.mainConfig.chat.antiSwear.punishScore) :
                notifText.formatted(offender.getName(), heatMap.get(offender), Sentinel.mainConfig.chat.antiSpam.punishHeat))));

        ServerUtils.forEachStaff(staffmember -> staffmember.spigot().sendMessage(notif));

        if (type.getExecutedCommand() != null) {
            ServerUtils.sendCommand(type.getExecutedCommand().replace("%player%", offender.getName()));
        }
    }

    private static TextComponent createTextComponent(String text) {
        TextComponent component = new TextComponent();
        component.setText(text);
        return component;
    }

    private static boolean shouldLogSwears(FAT type) {
        return (type == FAT.SWEAR_PUNISH || type == FAT.SLUR_PUNISH) && Sentinel.mainConfig.chat.antiSwear.logSwears
                || (type == FAT.SPAM_PUNISH && Sentinel.mainConfig.chat.antiSpam.logSpam);
    }

    public static void sendConsoleLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        StringBuilder log = new StringBuilder().append("]=- %s -=[".formatted(type.getTitle()));
        log.append("\nPlayer: %s".formatted(offender.getName()));
        log.append("\n> UUID: %s".formatted(offender.getUniqueId()));
        switch (type) {
            case SPAM_PUNISH -> {
                log.append("\n> Heat: %1$s/%2$s".formatted(heatMap.get(offender),Sentinel.mainConfig.chat.antiSpam.punishHeat));
                log.append("\nMessage: %s".formatted(e.getMessage()));
                log.append("\nReduced: %s".formatted(fullSimplify(e.getMessage())));
            }
            case SWEAR_PUNISH -> {
                log.append("\n> Score: %1$s/%2$s".formatted(heatMap.get(offender),Sentinel.mainConfig.chat.antiSwear.punishScore));
                log.append("\nPrevious: %s".formatted(lastMessageMap.get(offender)));
                log.append("\nCurrent: %s".formatted(e.getMessage()));
            }
            default -> {
                log.append("\nYou shouldn't be seeing this! Please report this message, and the context surrounding it!");
                log.append("\n> Heat: %1$s/%2$s".formatted(heatMap.get(offender),Sentinel.mainConfig.chat.antiSpam.punishHeat));
                log.append("\nMessage: %s".formatted(e.getMessage()));
                log.append("\nReduced: %s".formatted(fullSimplify(e.getMessage())));
                log.append("\n> Score: %1$s/%2$s".formatted(heatMap.get(offender),Sentinel.mainConfig.chat.antiSwear.punishScore));
                log.append("\nPrevious: %s".formatted(lastMessageMap.get(offender)));
                log.append("\nCurrent: %s".formatted(e.getMessage()));
            }
        }
        log.append("\nExecuted: %s".formatted(type.getExecutedCommand()));
        Sentinel.log.info(String.valueOf(log));
    }

    private static void sendDiscordLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        String supertitle = type.getTitle();
        String title = offender.getName() + " has triggered the " + type.getName() + "!";

        String executed = type.getExecutedCommand() != null ? type.getExecutedCommand() : "Nothing, its a standard flag. You shouldn't be seeing this, please report it.";
        StringBuilder description = new StringBuilder();

        String historyTitle = "You found a bug! :D";
        String historyValue = "Congratulations.";

        String currentTitle = "Now go report it!";
        String currentValue = ">:(";

        description.append("%1$sPlayer: `%2$s` %3$s".formatted(Emojis.rightSort,offender.getName(),Emojis.target));
        switch (type) {
            case SPAM_PUNISH -> {
                description.append("%1$s%2$sHeat: `%3$s/%4$s`".formatted(
                        Emojis.space,
                        Emojis.arrowRight,
                        heatMap.get(offender),
                        Sentinel.mainConfig.chat.antiSpam.punishHeat
                ));
                historyTitle = "Previous: ";
                historyValue = lastMessageMap.get(offender);

                currentTitle = "Current: ";
                currentValue = e.getMessage();
            }
            case SWEAR_PUNISH -> {
                description.append("%1$s%2$sScore: `%3$s/%4$s`".formatted(
                        Emojis.space,
                        Emojis.arrowRight,
                        scoreMap.get(offender),
                        Sentinel.mainConfig.chat.antiSwear.punishScore
                ));
                historyTitle = "Message: ";
                historyValue = e.getMessage();

                currentTitle = "Reduced: ";
                currentValue = highlightProfanity(e.getMessage(),"||", "||");
            }
        }

        WebhookMessage message = new WebhookMessageBuilder()
                .setUsername("Sentinel Anti-Nuke | Logs")
                .setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png").
                addEmbeds(new WebhookEmbedBuilder()
                        .setAuthor(new WebhookEmbed.EmbedAuthor(supertitle,null,"https://builtbybit.com/resources/sentinel-anti-nuke.30130/"))
                        .setTitle(new WebhookEmbed.EmbedTitle(title,null))
                        .setDescription(String.valueOf(description))
                        .addField(new WebhookEmbed.EmbedField(true,historyTitle,historyValue))
                        .addField(new WebhookEmbed.EmbedField(true,currentTitle,currentValue))
                        .addField(new WebhookEmbed.EmbedField(false,"Executed: ", executed))
                        .setThumbnailUrl("https://crafatar.com/avatars/" + offender.getUniqueId() + "?size=64&&overlay")
                        .setColor(type.getColor().getRGB())
                        .build())
                .build();

        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            Sentinel.webclient.send(message);
        } catch (Exception ex) {
            ServerUtils.sendDebugMessage("Filter Actions: Epic webhook failure!!!");
            Sentinel.log.info(ex.toString());
        }



    }
    /*
    public static void filterAction(Player offender, AsyncPlayerChatEvent e, String highlighted, String severity, Double similarity, FAT type) {
        String report = ReportFalsePositives.generateReport(e);

        TextComponent warn = new TextComponent();
        warn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.language.get("action-automatic-reportable"))));
        warn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        DecimalFormat fs = new DecimalFormat("##.#");
        fs.setRoundingMode(RoundingMode.DOWN);

        TextComponent notif = new TextComponent();
        notif.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText((type != FAT.SPAM_PUNISH && type != FAT.BLOCK_SPAM ? Sentinel.language.get("severity-notification-hover").formatted(e.getMessage(), highlighted, severity) : Sentinel.language.get("spam-notification-hover").formatted(e.getMessage(),lastMessageMap.get(offender),fs.format(similarity))))));
        notif.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        warn.setText(Text.prefix(Sentinel.language.get(type.getWarnTranslationKey())));
        offender.spigot().sendMessage(warn);

        String notiftext = Sentinel.language.get(type.getNotifTranslationKey());

        notif.setText(Text.prefix((type != FAT.SPAM_PUNISH && type != FAT.BLOCK_SPAM ? notiftext.formatted(offender.getName(), scoreMap.get(offender), Sentinel.mainConfig.chat.antiSwear.punishScore) : notiftext.formatted(offender.getName(),heatMap.get(offender),Sentinel.mainConfig.chat.antiSpam.punishHeat))));

        ServerUtils.forEachStaff(staffmember -> {
            staffmember.spigot().sendMessage(notif);
        });

        if (type.getExecutedCommand() != null) {
            ServerUtils.sendCommand(type.getExecutedCommand().replace("%player%", offender.getName()));
        }

        if (type == FAT.SWEAR_PUNISH && Sentinel.mainConfig.chat.antiSwear.logSwears) {
            sendDiscordLog(offender,e,type);
            sendConsoleLog(offender,e,type);
        }

        if (type == FAT.SLUR_PUNISH && Sentinel.mainConfig.chat.antiSwear.logSwears) {
            sendDiscordLog(offender,e,type);
            sendConsoleLog(offender,e,type);
        }
        if (type == FAT.SPAM_PUNISH && Sentinel.mainConfig.chat.antiSpam.logSpam) {
            sendDiscordLog(offender,e,type);
            sendConsoleLog(offender,e,type);
        }
    }*/



}
