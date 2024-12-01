package me.trouper.sentinel.utils.trees;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.Emojis;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmbedFormatter {

    public static void sendEmbed(DiscordEmbed embed) {
        sendEmbed(embed,Sentinel.mainConfig.plugin.webhook);
    }

    public static boolean sendEmbed(DiscordEmbed embed, String spec) {
        DiscordWebhook webhook = new DiscordWebhook("Sentinel Anti-Nuke Webhook Logger", "https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/1epkvziv.png","", false, embed);
        AtomicBoolean success = new AtomicBoolean(false);
        SchedulerUtils.later(0,()->{
            try {
                webhook.send(spec);
            } catch (IOException e) {
                Sentinel.log.warning(e.getMessage());
                success.set(false);
                return;
            }
            success.set(true);
        });
        return success.get();
    }

    public static DiscordEmbed format(Node node) {
        DiscordEmbed.Builder eb = new DiscordEmbed.Builder();
        StringBuilder desc = new StringBuilder();

        formatNode(eb, node, desc,0);
        eb.color(0xFFAB4D);
        return eb.desc(desc.toString()).build();
    }

    private static void formatNode(DiscordEmbed.Builder eb, Node node, StringBuilder desc, int level) {
        eb.author("Sentinel | Anti-Nuke","https://trouper.me/sentinel",null);
        if (level == 0) {
            eb.title("Incoming from server: %s".formatted(Sentinel.mainConfig.plugin.identifier));
        } else {
            desc.repeat(Emojis.space,level - 1).append("**").append(node.title).append("**\n");
        }

        for (String text : node.texts) {
            text = text.replace("<hs>"," > ");
            text = text.replace("<he>"," < ");
            if (level == 0) {
                desc.append(text).append("\n");
            } else {
                desc.repeat(Emojis.space,level - 1).append(Emojis.rightSort).append(text).append("\n");
            }
        }

        for (Map.Entry<String, String> entry : node.values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = key.replace("<hs>"," > ");
            key = key.replace("<he>"," < ");
            value = value.replace("<hs>"," > ");
            value = value.replace("<he>"," < ");
            if (level == 0) {
                desc.append(key).append(": `").append(value).append("`\n");
            } else {
                desc.repeat(Emojis.space,level - 1).append(Emojis.rightSort).append(key).append(": `").append(value).append("`\n");
            }
        }

        for (Map.Entry<String, String> entry : node.fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = key.replace("<hs>"," > ");
            key = key.replace("<he>"," < ");
            value = value.replace("<hs>"," > ");
            value = value.replace("<he>"," < ");
            if (level == 0) {
                desc.append("**").append(key).append("**:\n `").append(value).append("`\n");
            } else {
                desc.repeat(Emojis.space,level - 1).append(Emojis.rightArrow).append("**").append(key).append("**:\n `").append(value).append("`\n");
            }
        }

        for (Node child : node.children) {
            formatNode(eb, child, desc,level + 1);
        }
    }
}
