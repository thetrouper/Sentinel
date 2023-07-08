package io.github.thetrouper.sentinel.discord;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebhookSender {

    public static void sendTestEmbed() {
        String webhookUrl = Config.webhook;
        // Create a new DiscordWebhook instance
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);

        // Create an EmbedObject and set its properties
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Test Success!", "", "")
                .setDescription("eeeee")
                .setColor(Color.GREEN);
        webhook.addEmbed(embed);
        webhook.addAttachment("text.txt","Text Here hehehehaw!");
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
                        Emojis.rightSort + "Executed: " + Config.punishSpamCommand + " " + Emojis.mute + "\\n" +
                        Emojis.space + Emojis.arrowRight + "Chat Cleared: " + successOrFail(chatCleared) + "\\n"
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
            ServerUtils.sendDebugMessage(TextUtils.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(e.toString());
        }
    }
    public static void sendSwearLog(Player player, String message, int finalScore) {
        ServerUtils.sendDebugMessage("Creating swearLog Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Swear Punishment","","")
                .setTitle("Punish Report:")
                .setDescription(
                        Emojis.rightSort + "Player: " + player.getName() + " " + Emojis.target + "\\n" +
                                Emojis.space + Emojis.arrowRight + "Score: `" + finalScore + "/" + Config.punishScore + "`\\n" +
                                Emojis.space + Emojis.arrowRight + "UUID: `" + player.getUniqueId() + "`\\n" +
                        Emojis.rightSort + "Executed: " + Config.swearPunishCommand + " " + Emojis.mute + "\\n"
                )
                .addField("Original Message", "||" + message + "|| " + Emojis.alarm, false)
                .addField("Sanitized Message", ProfanityFilter.highlightProfanity(message,"||", "||") + " " + Emojis.noDM, false)
                .setColor(Color.orange)
                .setThumbnail("https://crafatar.com/avatars/" + player.getUniqueId() + "?size=64&&overlay");
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException e) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(e.toString());
        }
    }
    public static void sendSlurLog(Player player, String message, int finalScore) {
        ServerUtils.sendDebugMessage("Creating swearLog Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Slur Punishment","","")
                .setTitle(player.getName() + " has triggered the anti-slur!")
                .setDescription(
                        Emojis.rightSort + "Player: " + player.getName() + " " + Emojis.target + "\\n" +
                                Emojis.space + Emojis.arrowRight + "Score: `" + finalScore + "/" + Config.punishScore + "`\\n" +
                                Emojis.space + Emojis.arrowRight + "UUID: `" + player.getUniqueId() + "`\\n" +
                                Emojis.rightSort + "Executed: " + Config.slurPunishCommand + " " + Emojis.mute + "\\n"
                )
                .addField("Original Message", "||" + message + "|| " + Emojis.alarm, false)
                .addField("Sanitized Message", ProfanityFilter.highlightProfanity(message,"||", "||") + " " + Emojis.noDM, false)
                .setColor(Color.orange)
                .setThumbnail("https://crafatar.com/avatars/" + player.getUniqueId() + "?size=64&&overlay");
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException e) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(e.toString());
        }
    }


    public static String successOrFail(boolean bool) {
        if (bool) {
            return Emojis.success;
        } else {
            return Emojis.failure;
        }
    }
}
