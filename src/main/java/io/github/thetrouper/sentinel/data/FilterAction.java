package io.github.thetrouper.sentinel.data;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.GPTUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static io.github.thetrouper.sentinel.server.functions.AntiSpam.heatMap;
import static io.github.thetrouper.sentinel.server.functions.AntiSpam.lastMessageMap;
import static io.github.thetrouper.sentinel.server.functions.ProfanityFilter.*;

public class FilterAction {

    public static void filterAction(Player offender, AsyncPlayerChatEvent e, String highlighted, String severity, Double similarity, FAT type) {
        String report = ReportFalsePositives.generateReport(e);

        TextComponent warn = new TextComponent();
        warn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("action-automatic-reportable"))));
        warn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        DecimalFormat fs = new DecimalFormat("##.#");
        fs.setRoundingMode(RoundingMode.DOWN);

        TextComponent notif = new TextComponent();
        notif.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText((type != FAT.SPAM && type != FAT.BLOCK_SPAM ? Sentinel.dict.get("severity-notification-hover").formatted(e.getMessage(), highlighted, severity) : Sentinel.dict.get("spam-notification-hover").formatted(e.getMessage(),lastMessageMap.get(offender),fs.format(similarity))))));
        notif.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        warn.setText(Text.prefix(Sentinel.dict.get(type.getWarnTranslationKey())));
        offender.spigot().sendMessage(warn);

        String notiftext = Sentinel.dict.get(type.getNotifTranslationKey());

        notif.setText(Text.prefix((type != FAT.SPAM && type != FAT.BLOCK_SPAM ? notiftext.formatted(offender.getName(), scoreMap.get(offender), Config.punishScore) : notiftext.formatted(offender.getName(),heatMap.get(offender),Config.punishHeat))));
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
            sendDiscordLog(offender,e,type);
            sendConsoleLog(offender,e,type);
        }
        if (type == FAT.SPAM && Config.logSpam) {
            sendDiscordLog(offender,e,type);
            sendConsoleLog(offender,e,type);
        }
    }
    public static void sendConsoleLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        String log = "]=-" + type.getTitle() + "-=[\n" +
                "Player: " + offender.getName() +
                (type != FAT.BLOCK_SPAM && type != FAT.SPAM ? "> Score: `" + scoreMap.get(offender) + "/" + Config.punishScore : "> Heat: `" + heatMap.get(offender) + "/" + Config.punishHeat) + "\n" +
                "> UUID: " + offender.getUniqueId() + "\n" +
                (type != FAT.BLOCK_SPAM && type != FAT.SPAM ? "Message: " + e.getMessage() : "Previous: " + lastMessageMap.get(offender)) + "\n" +
                (type != FAT.BLOCK_SPAM && type != FAT.SPAM ? "Reduced: " + fullSimplify(e.getMessage()) : "Current: " + e.getMessage()) + "\n" +
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
                                Emojis.space + Emojis.arrowRight + (type != FAT.BLOCK_SPAM ? "Score: `" + scoreMap.get(offender) + "/" + Config.punishScore : "Heat: `" + heatMap.get(offender) + "/" + Config.punishHeat) + "`\n" +
                                Emojis.space + Emojis.arrowRight + "UUID: `" + offender.getUniqueId() + "`\n" +
                                Emojis.rightSort + "Executed: " + executed + " " + Emojis.mute + "\n"
                )
                .addField((type != FAT.BLOCK_SPAM ? "Message: " : "Previous: "), (type != FAT.BLOCK_SPAM ? e.getMessage() : lastMessageMap.get(offender)) + Emojis.alarm, false)
                .addField((type != FAT.BLOCK_SPAM ? "Reduced: " : "Current: "), (type != FAT.BLOCK_SPAM ? highlightProfanity(e.getMessage(), "||", "||") : e.getMessage()) + " " + Emojis.noDM, false)
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
