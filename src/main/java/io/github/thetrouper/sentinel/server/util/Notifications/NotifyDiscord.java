package io.github.thetrouper.sentinel.server.util.Notifications;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.io.IOException;

public class NotifyDiscord {
    public static void command(Player player, String command, boolean denied, boolean deoped, boolean banned, boolean logged) {
        ServerUtils.sendDebugMessage("Creating Command Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The use of a dangerous command has been detected!")
                .setDescription(
                        Emojis.rightSort + " **Player:** " + player.getName() + " " + Emojis.member + "\\n" +
                        Emojis.rightSort + " **Command:** " + command + " " + Emojis.nuke + "\\n"
                )
                .addField("Actions:",
                        Emojis.rightSort + " **Denied:** " + TextUtils.boolString(denied,Emojis.success, Emojis.failure) + "\\n" +
                                Emojis.rightSort + " **De-oped:** " + TextUtils.boolString(deoped,Emojis.success, Emojis.failure) + "\\n" +
                                Emojis.rightSort + " **Banned:** " + TextUtils.boolString(banned,Emojis.success, Emojis.failure) + "\\n" +
                                Emojis.rightSort + "**Logged:** "  + TextUtils.boolString(logged,Emojis.success, Emojis.failure), false
                )
                .setThumbnail("https://crafatar.com/avatars/" + player.getUniqueId() + "?size=64&&overlay")
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
    public static void NBT(Player player, ItemStack item, boolean removed, boolean deoped, boolean gms, boolean banned, boolean logged, String logFileName) {
        ServerUtils.sendDebugMessage("Creating NBT Webhook...");

        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The use of a dangerous item has been detected!")
                .setDescription(
                        Emojis.rightSort + " **Player:** " + player.getName() + " " + Emojis.member + "\\n" +
                                Emojis.rightSort + " **Item:** " + item.getType().toString().toLowerCase() + " " + Emojis.nuke + "\\n" +
                                Emojis.space + Emojis.rightDoubleArrow + "**NBT:** Uploaded to /Sentinel/LoggedNBT/" + logFileName
                )
                .addField("Actions:",
                        Emojis.rightSort + " **Removed:** " + TextUtils.boolString(removed,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **De-oped:** " + TextUtils.boolString(deoped,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **GM Reverted:** " + TextUtils.boolString(gms,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **Banned:** " + TextUtils.boolString(banned,Emojis.success, Emojis.failure)  + "\\n"+
                                Emojis.rightSort + " **Logged:** " + TextUtils.boolString(logged,Emojis.success, Emojis.failure), false
                )
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
    public static void placeBlock(Player player, Block b, boolean deleted, boolean deoped, boolean banned, boolean logged) {
        ServerUtils.sendDebugMessage("Creating placeBlock Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The placing of a dangerous block has been detected!")
                .setDescription(
                        Emojis.rightSort + " **Player:** " + player.getName() + " " + Emojis.member + "\\n" +
                                Emojis.rightSort + " **Block:** " + b.getType().toString().toLowerCase() + " " + Emojis.nuke + "\\n"
                )
                .addField("Actions:",
                        Emojis.rightSort + " **Deleted:** " + TextUtils.boolString(deleted,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **De-oped:** " + TextUtils.boolString(deoped,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **Banned:** " + TextUtils.boolString(banned,Emojis.success, Emojis.failure)  + "\\n"+
                                Emojis.rightSort + " **Logged:** " + TextUtils.boolString(logged,Emojis.success, Emojis.failure),  false
                )
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
    public static void usedBlock(Player player, Block b, boolean denied, boolean deoped, boolean banned, boolean logged) {
        ServerUtils.sendDebugMessage("Creating useBlock Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The use of a dangerous block has been detected!")
                .setDescription(
                        Emojis.rightSort + " **Player:** " + player.getName() + " " + Emojis.member + "\\n" +
                        Emojis.rightSort + " **Block:** " + b.getType() + " " + Emojis.nuke + "\\n"
                )
                .addField("Actions:",
                        Emojis.rightSort + " **Denied:** " + TextUtils.boolString(denied,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **De-oped:** " + TextUtils.boolString(deoped,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **Banned:** " + TextUtils.boolString(banned,Emojis.success, Emojis.failure)  + "\\n"+
                                Emojis.rightSort + " **Logged:** " + TextUtils.boolString(logged,Emojis.success, Emojis.failure),  false
                )
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
    public static void usedEntity(Player player, Entity e, boolean denied, boolean deoped, boolean banned, boolean logged) {
        ServerUtils.sendDebugMessage("Creating useEntity Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Nuke has been triggered","","")
                .setTitle("The use of a dangerous entity has been detected!")
                .setDescription(
                        Emojis.rightSort + " **Player:** " + player.getName() + " " + Emojis.member + "\\n" +
                        Emojis.rightSort + " **Entity:** " + e.getType() + " " + Emojis.nuke + "\\n"
                )
                .addField("Actions:",
                        Emojis.rightSort + " **Denied:** " + TextUtils.boolString(denied,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **De-oped:** " + TextUtils.boolString(deoped,Emojis.success, Emojis.failure)  + "\\n" +
                                Emojis.rightSort + " **Banned:** " + TextUtils.boolString(banned,Emojis.success, Emojis.failure)  + "\\n"+
                                Emojis.rightSort + " **Logged:** " + TextUtils.boolString(logged,Emojis.success, Emojis.failure),  false
                )
                .setColor(Color.RED);
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("Executing webhook...");
            webhook.execute();
        } catch (IOException ex) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Epic webhook failure!!!"));
            Sentinel.log.info(ex.toString());
        }
    }

}
