package io.github.thetrouper.sentinel.server;

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

        TextComponent warn = createTextComponent(Text.prefix(Sentinel.dict.get(type.getWarnTranslationKey())));
        warn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("action-automatic-reportable"))));
        warn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        TextComponent notif = createTextComponent(Text.prefix((type != FAT.SPAM_PUNISH && type != FAT.BLOCK_SPAM ?
                Sentinel.dict.get("severity-notification-hover").formatted(e.getMessage(), highlighted, severity.name().toLowerCase().replace("_"," ")) :
                Sentinel.dict.get("spam-notification-hover").formatted(e.getMessage(), lastMessageMap.get(offender), fs.format(similarity)))));
        notif.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("severity-notification-hover"))));
        notif.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        sendMessages(offender, warn, notif, type);

        if (shouldLogSwears(type)) {
            sendDiscordLog(offender, e, type);
            sendConsoleLog(offender, e, type);
        }
    }

    private static void sendMessages(Player offender, TextComponent warn, TextComponent notif, FAT type) {
        offender.spigot().sendMessage(warn);

        String notifText = Sentinel.dict.get(type.getNotifTranslationKey());
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
        String log = "]=-" + type.getTitle() + "-=[\n" +
                "Player: " + offender.getName() +
                (type != FAT.BLOCK_SPAM && type != FAT.SPAM_PUNISH ? "> Score: `" + scoreMap.get(offender) + "/" + Sentinel.mainConfig.chat.antiSwear.punishScore :
                        "> Heat: `" + heatMap.get(offender) + "/" + Sentinel.mainConfig.chat.antiSpam.punishHeat) + "\n" +
                "> UUID: " + offender.getUniqueId() + "\n" +
                (type != FAT.BLOCK_SPAM && type != FAT.SPAM_PUNISH ? "Message: " + e.getMessage() : "Previous: " + lastMessageMap.get(offender)) + "\n" +
                (type != FAT.BLOCK_SPAM && type != FAT.SPAM_PUNISH ? "Reduced: " + fullSimplify(e.getMessage()) : "Current: " + e.getMessage()) + "\n" +
                (type.getExecutedCommand() != null ? "Executed: " + type.getExecutedCommand() : "Executed: Nothing, its a standard flag. You shouldn't be seeing this, please report it.");
        Sentinel.log.info(log);
    }

    private static void sendDiscordLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        String supertitle = type.getTitle();
        String title = offender.getName() + " has triggered the " + type.getName() + "!";
        String executed = type.getExecutedCommand() != null ? type.getExecutedCommand() : "Nothing, its a standard flag. You shouldn't be seeing this, please report it.";

        DiscordWebhook webhook = new DiscordWebhook(Sentinel.mainConfig.plugin.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor(supertitle, "", "")
                .setTitle(title)
                .setDescription(
                        Emojis.rightSort + "Player: " + offender.getName() + " " + Emojis.target + "\\n" +
                                Emojis.space + Emojis.arrowRight + (type != FAT.BLOCK_SPAM ?
                                    "Score: `" + scoreMap.get(offender) + "/" + Sentinel.mainConfig.chat.antiSwear.punishScore :
                                    "Heat: `" + heatMap.get(offender) + "/" + Sentinel.mainConfig.chat.antiSpam.punishHeat) + "`\\n" +
                                Emojis.space + Emojis.arrowRight + "UUID: `" + offender.getUniqueId() + "`\\n" +
                                Emojis.rightSort + "Executed: " + executed + " " + Emojis.mute + "\\n"
                )
                .addField((type != FAT.BLOCK_SPAM && type != FAT.SPAM_PUNISH ? "Message: " : "Previous: "),
                        (type != FAT.BLOCK_SPAM && type != FAT.SPAM_PUNISH ? e.getMessage() : lastMessageMap.get(offender)) + Emojis.alarm, false)
                .addField((type != FAT.BLOCK_SPAM && type != FAT.SPAM_PUNISH ? "Reduced: " : "Current: "),
                        (type != FAT.BLOCK_SPAM && type != FAT.SPAM_PUNISH ? highlightProfanity(e.getMessage(), "||", "||") : e.getMessage()) + " " + Emojis.noDM, false)
                .setColor(type.getColor())
                .setThumbnail("https://crafatar.com/avatars/" + offender.getUniqueId() + "?size=64&&overlay");

        webhook.addEmbed(embed);

        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException ex) {
            ServerUtils.sendDebugMessage("Filter Actions: Epic webhook failure!!!");
            Sentinel.log.info(ex.toString());
        }
    }
    /*
    public static void filterAction(Player offender, AsyncPlayerChatEvent e, String highlighted, String severity, Double similarity, FAT type) {
        String report = ReportFalsePositives.generateReport(e);

        TextComponent warn = new TextComponent();
        warn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("action-automatic-reportable"))));
        warn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        DecimalFormat fs = new DecimalFormat("##.#");
        fs.setRoundingMode(RoundingMode.DOWN);

        TextComponent notif = new TextComponent();
        notif.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText((type != FAT.SPAM_PUNISH && type != FAT.BLOCK_SPAM ? Sentinel.dict.get("severity-notification-hover").formatted(e.getMessage(), highlighted, severity) : Sentinel.dict.get("spam-notification-hover").formatted(e.getMessage(),lastMessageMap.get(offender),fs.format(similarity))))));
        notif.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        warn.setText(Text.prefix(Sentinel.dict.get(type.getWarnTranslationKey())));
        offender.spigot().sendMessage(warn);

        String notiftext = Sentinel.dict.get(type.getNotifTranslationKey());

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
