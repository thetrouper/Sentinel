package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.io.IOException;

import static io.github.thetrouper.sentinel.server.functions.ProfanityFilter.fullSimplify;
import static io.github.thetrouper.sentinel.server.functions.ProfanityFilter.scoreMap;

public class FilterAction {

    public static void filterAction(Player offender, AsyncPlayerChatEvent e, String highlighted, String severity, FAT type) {
        String report = ReportFalsePositives.generateReport(e);

        TextComponent warn = new TextComponent();
        warn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("action-automatic-reportable"))));
        warn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        TextComponent notif = new TextComponent();
        notif.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("severity-notification-hover").formatted(e.getMessage(), highlighted, severity))));
        notif.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        warn.setText(Text.prefix(Sentinel.dict.get(type.getWarnTranslationKey())));
        offender.spigot().sendMessage(warn);

        notif.setText(Text.prefix(Sentinel.dict.get(type.getNotifTranslationKey()).formatted(offender.getName(), scoreMap.get(offender), Config.punishScore)));
        ServerUtils.forEachStaff(staffmember -> {
            staffmember.spigot().sendMessage(notif);
        });

        if (type.getExecutedCommand() != null) {
            ServerUtils.sendCommand(type.getExecutedCommand().replace("%player%", offender.getName()));
        }

        if (type == FAT.SWEAR && Config.logSwear) {
            sendDiscordLog(offender,e,type);
            sendConsoleLog(offender,e,type);
        }

        if (type == FAT.SLUR && Config.logSwear) {
            sendDiscordLog(offender, e, type);
            sendConsoleLog(offender, e, type);
        }
        if (type == FAT.SPAM && Config.logSpam) {
            // BOOKMARK
            // STATE: (bool?t:f) imp spam log for console & discord for prev/curr||Mess/Redu
        }
    }
    public static void sendConsoleLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        String log = "]=-" + type.getTitle() + "-=[\n" +
                "Player: " + offender.getName() +
                "> Score: (" + scoreMap.get(offender) + "/" + Config.punishScore + ")\n" +
                "> UUID: " + offender.getUniqueId() + "\n" +
                "Message: " + e.getMessage() + "\n" +
                "Reduced: " + fullSimplify(e.getMessage()) + "\n" +
                (type.getExecutedCommand() != null ? "Executed: " + type.getExecutedCommand() : "Executed: Nothing, its a standard flag. You shouldn't be seeing this, please report it.");
        Sentinel.log.info(log);
    }

    private static void sendDiscordLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        String supertitle = type.getTitle();
        String title = offender.getName() + " has triggered the " + type.getName() + "!";
        Color color = Color.white;
        String executed = type.getExecutedCommand() != null ? type.getExecutedCommand() : "Nothing, its a standard flag. You shouldn't be seeing this, please report it.";

        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor(supertitle, "", "")
                .setTitle(title)
                .setDescription(
                        Emojis.rightSort + "Player: " + offender.getName() + " " + Emojis.target + "\n" +
                                Emojis.space + Emojis.arrowRight + "Score: `" + scoreMap.get(offender) + "/" + Config.punishScore + "`\n" +
                                Emojis.space + Emojis.arrowRight + "UUID: `" + offender.getUniqueId() + "`\n" +
                                Emojis.rightSort + "Executed: " + executed + " " + Emojis.mute + "\n"
                )
                .addField("Original Message", "||" + e.getMessage() + "|| " + Emojis.alarm, false)
                .addField("Reduced Message", ProfanityFilter.highlightProfanity(e.getMessage(), "||", "||") + " " + Emojis.noDM, false)
                .setColor(color)
                .setThumbnail("https://crafatar.com/avatars/" + offender.getUniqueId() + "?size=64&&overlay");

        webhook.addEmbed(embed);

        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException ex) {
            ServerUtils.sendDebugMessage(Text.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(ex.toString());
        }
    }
}
