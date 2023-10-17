package io.github.thetrouper.sentinel.server;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.discord.WebhookSender;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.Color;
import java.io.IOException;

import static io.github.thetrouper.sentinel.server.functions.ProfanityFilter.scoreMap;

public class FilterAction {

    public static void filterAction(Player offender, AsyncPlayerChatEvent e, String highlighted, String severity,String type) {
        String report = ReportFalsePositives.generateReport(e);

        TextComponent warn = new TextComponent();
        warn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("action-automatic-reportable"))));
        warn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));

        TextComponent notif = new TextComponent();
        notif.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("severity-notification-hover").formatted(e.getMessage(),highlighted,severity))));
        notif.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sentinelcallback fpreport " + report));
        switch (type) {
            case "BLOCK" -> {
                warn.setText(Text.prefix((Sentinel.dict.get("swear-block-warn"))));
                offender.spigot().sendMessage(warn);

                notif.setText(Text.prefix(Sentinel.dict.get("swear-block-notification").formatted(offender.getName(),scoreMap.get(offender),Config.punishScore)));
                ServerUtils.forEachStaff(staffmember -> {
                    staffmember.spigot().sendMessage(notif);
                });
            }
            case "SWEAR" -> {
                ServerUtils.sendCommand(Config.swearPunishCommand.replace("%player%", offender.getName()));
                warn.setText(Text.prefix(Sentinel.dict.get("profanity-mute-warn")));

                offender.spigot().sendMessage(warn);

                notif.setText(Text.prefix(Sentinel.dict.get("profanity-mute-notification").formatted(offender.getName(),scoreMap.get(offender),Config.punishScore)));
                ServerUtils.forEachStaff(staff -> {
                    staff.spigot().sendMessage(notif);
                });
                if (Config.logSwear) WebhookSender.sendSwearLog(offender,e.getMessage(),scoreMap.get(offender));
            }
            case "SLUR" -> {
                ServerUtils.sendCommand(Config.strictPunishCommand.replace("%player%", offender.getName()));
                warn.setText(Text.prefix((Sentinel.dict.get("slur-mute-warn"))));

                offender.spigot().sendMessage(warn);

                notif.setText(Text.prefix(Sentinel.dict.get("slur-mute-notification").formatted(offender.getName(),scoreMap.get(offender),Config.punishScore)));
                ServerUtils.forEachStaff(staff -> {
                    staff.spigot().sendMessage(notif);
                });
                if (Config.logSwear) {
                    sendLog(offender,e,type);
                }
            }
        }
    }

    private static void sendLog(Player offender, AsyncPlayerChatEvent e, String type) {
        String supertitle = "Chat filter Punishment";
        String title = offender.getName() + " has triggered the chat filter auto-punish!";
        Color color = Color.white;
        String executed = "Generic mute command not implemented yet! (report this please, you shouldn't be seeing this)";
        switch (type) {
            case "SWEAR" -> {
                supertitle = "Anti-Swear Punishment";
                title = offender.getName() + " has triggered the anti-swear!";
                color = Color.orange;
                executed = Config.swearPunishCommand;
            }
            case "SLUR" -> {
                supertitle = "Anti-Slur Punishment";
                title = offender.getName() + " has triggered the anti-slur!";
                color = Color.red;
                executed = Config.strictPunishCommand;
            }

        }
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor(supertitle,"","")
                .setTitle(title)
                .setDescription(
                        Emojis.rightSort + "Player: " + offender.getName() + " " + Emojis.target + "\\n" +
                                Emojis.space + Emojis.arrowRight + "Score: `" + scoreMap.get(offender) + "/" + Config.punishScore + "`\\n" +
                                Emojis.space + Emojis.arrowRight + "UUID: `" + offender.getUniqueId() + "`\\n" +
                                Emojis.rightSort + "Executed: " + executed + " " + Emojis.mute + "\\n"
                )
                .addField("Original Message", "||" + e.getMessage() + "|| " + Emojis.alarm, false)
                .addField("Sanitized Message", ProfanityFilter.highlightProfanity(e.getMessage(),"||", "||") + " " + Emojis.noDM, false)
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
