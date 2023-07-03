package io.github.thetrouper.sentinel.discord;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.commands.InfoCommand;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.io.IOException;

public class WebhookSender {

    public static void sendHelloWorldEmbed() {
        String webhookUrl = Sentinel.webhook;

        // Create a new DiscordWebhook instance
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);

        // Create an EmbedObject and set its properties
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setDescription("Hello, World!")
                .setColor(Color.GREEN);

        // Add the EmbedObject to the webhook
        webhook.addEmbed(embed);

        try {
            // Execute the webhook
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String successOrFail(boolean bool) {
        if (bool) {
            return Emojis.success;
        } else {
            return Emojis.failure;
        }
    }
    public static void sendEmbedWarning(String player, String command, boolean denied, boolean removedOp, boolean banned) {
        ServerUtils.sendDebugMessage("Creating Command Webhook...");
        final String description =
                Emojis.rightArrow + " **Player:** " + player + " " + Emojis.member + "\\n" +
                Emojis.rightArrow + " **Command:** " + command + " " + Emojis.nuke + "\\n" +
                Emojis.rightArrow + " **Denied:** " + successOrFail(denied) + "\\n" +
                Emojis.rightArrow + " **Removed OP:** " + successOrFail(removedOp) + "\\n" +
                Emojis.rightArrow + " **Banned:** " + successOrFail(banned) + "\\n";

        DiscordWebhook webhook = new DiscordWebhook(Sentinel.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The use of a dangerous command has been detected!")
                .setDescription(description)
                .setColor(Color.RED);
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException e) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(e.toString());
        }
    }
    public static void sendEmbedWarning(String player, Block b, boolean denied, boolean removedOp, boolean banned) {
        ServerUtils.sendDebugMessage("Creating Block Webhook...");
        final String description =
                        Emojis.rightArrow + " **Player:** " + player + " " + Emojis.member + "\\n" +
                        Emojis.rightArrow + " **Block:** " + b.getType() + " " + Emojis.nuke + "\\n" +
                        Emojis.rightArrow + " **Denied:** " + successOrFail(denied) + "\\n" +
                        Emojis.rightArrow + " **Removed OP:** " + successOrFail(removedOp) + "\\n" +
                        Emojis.rightArrow + " **Banned:** " + successOrFail(banned) + "\\n";

        DiscordWebhook webhook = new DiscordWebhook(Sentinel.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The use of a dangerous block has been detected!")
                .setDescription(description)
                .setColor(Color.RED);
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException e) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(e.toString());
        }

    }
    public static void sendEmbedWarning(String player, ItemStack item, boolean denied, boolean removedOp, boolean banned) {
        ServerUtils.sendDebugMessage("Creating Webhook...");
        final String description =
                Emojis.rightArrow + " **Player:** " + player + " " + Emojis.member + "\\n" +
                        Emojis.rightArrow + " **Item:** " + item.getType() + " " + Emojis.nuke + "\\n" +
                        Emojis.rightArrow + " **Denied:** " + successOrFail(denied) + "\\n" +
                        Emojis.rightArrow + " **Removed OP:** " + successOrFail(removedOp) + "\\n" +
                        Emojis.rightArrow + " **Banned:** " + successOrFail(banned) + "\\n";

        DiscordWebhook webhook = new DiscordWebhook(Sentinel.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The use of a dangerous item has been detected!")
                .setDescription(description)
                .setColor(Color.BLUE);
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException e) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(e.toString());
        }
    }
}
