package me.trouper.sentinel.utils.trees;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.Emojis;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmbedFormatter {

    public static boolean sendEmbed(DiscordEmbed embed) {
        return sendEmbed(embed, Sentinel.getInstance().getDirector().io.mainConfig.plugin.webhook);
    }

    public static boolean sendEmbed(DiscordEmbed embed, String spec) {
        DiscordWebhook webhook = new DiscordWebhook(
                "Sentinel Anti-Nuke Webhook Logger",
                "https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/i9vsvqjg.png",
                "",
                false,
                embed
        );

        AtomicBoolean success = new AtomicBoolean(false);
        SchedulerUtils.later(0, () -> {
            try {
                webhook.send(spec);
                success.set(true);
            } catch (IOException e) {
                Sentinel.getInstance().getLogger().info("Discord declined the web request: " + e.getMessage());
                Sentinel.getInstance().getLogger().info("Please insure your webhook URL is correct, otherwise nothing will be logged to discord.");
                success.set(false);
            }
        });
        return success.get();
    }

    public static DiscordEmbed format(Node node) {
        DiscordEmbed.Builder eb = new DiscordEmbed.Builder();
        StringBuilder desc = new StringBuilder();

        formatNode(eb, node, desc, 0);
        eb.color(0xFFAB4D);
        return eb.desc(desc.toString()).build();
    }

    private static void formatNode(DiscordEmbed.Builder eb, Node node, StringBuilder desc, int level) {
        eb.author("Sentinel | Anti-Nuke", "https://trouper.me/sentinel", null);
        eb.thumbnail("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/v5rxlx0d.png");

        if (level == 0) {
            eb.title("Incoming from server: %s".formatted(
                    Sentinel.getInstance().getDirector().io.mainConfig.plugin.identifier));
        } else {
            desc.repeat(Emojis.space, level - 1)
                    .append("**")
                    .append(componentToPlainText(node.title))
                    .append("**\n");
        }

        // Process text lines
        for (Component text : node.texts) {
            String plainText = processComponentForDiscord(text);
            if (level == 0) {
                desc.append(plainText).append("\n");
            } else {
                desc.repeat(Emojis.space, level - 1)
                        .append(Emojis.rightSort)
                        .append(plainText)
                        .append("\n");
            }
        }

        // Process key-value pairs
        for (Map.Entry<Component, Component> entry : node.values.entrySet()) {
            String key = processComponentForDiscord(entry.getKey());
            String value = processComponentForDiscord(entry.getValue());

            if (level == 0) {
                desc.append(key).append(": `").append(value).append("`\n");
            } else {
                desc.repeat(Emojis.space, level - 1)
                        .append(Emojis.rightSort)
                        .append(key)
                        .append(": `")
                        .append(value)
                        .append("`\n");
            }
        }

        // Process fields
        for (Map.Entry<Component, Component> entry : node.fields.entrySet()) {
            String key = processComponentForDiscord(entry.getKey());
            String value = processComponentForDiscord(entry.getValue());

            if (level == 0) {
                desc.append("**").append(key).append("**:\n `").append(value).append("`\n");
            } else {
                desc.repeat(Emojis.space, level - 1)
                        .append(Emojis.rightArrow)
                        .append("**")
                        .append(key)
                        .append("**:\n  `")
                        .append(value)
                        .append("`\n");
            }
        }

        // Process children
        for (Node child : node.children) {
            formatNode(eb, child, desc, level + 1);
        }
    }

    private static String componentToPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    private static String processComponentForDiscord(Component component) {
        String text = componentToPlainText(component);
        
        text = text.replace("█HS█", " **");
        text = text.replace("█HE█", "** ");
        return text;
    }
}