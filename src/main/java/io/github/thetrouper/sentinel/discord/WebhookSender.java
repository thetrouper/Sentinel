package io.github.thetrouper.sentinel.discord;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.io.IOException;

public class WebhookSender {

    public static void sendSpamLog(Player player, String message1, String message2, int finalHeat, boolean chatCleared) {
        ServerUtils.sendDebugMessage("Creating spamLog Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Spam Punishment","","")
                .setTitle("Punish Report:")
                .setDescription(
                        Emojis.rightSort + "Player: " + player.getName() + " " + Emojis.target + "\\n" +
                        Emojis.space + Emojis.arrowRight + "Heat: `" + finalHeat + "/" + Config.punishHeat + "`\\n" +
                        Emojis.space + Emojis.arrowRight + "UUID: `" + player.getUniqueId() + "`\\n" +
                        Emojis.rightSort + "Executed: " + Config.spamPunishCommand + " " + Emojis.mute + "\\n" +
                        Emojis.space + Emojis.arrowRight + "Chat Cleared: " + (chatCleared ? Emojis.success : Emojis.failure) + "\\n"
                )
                .addField("Previous Message", "||" + message1 + "|| " + Emojis.activity, false)
                .addField("Current Message", "||" + message2 + "|| " + Emojis.alarm, false)
                .setColor(Color.RED)
                .setThumbnail("https://crafatar.com/avatars/" + player.getUniqueId() + "?size=64&&overlay");
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException e) {
            ServerUtils.sendDebugMessage(Text.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(e.toString());
        }
    }
}
