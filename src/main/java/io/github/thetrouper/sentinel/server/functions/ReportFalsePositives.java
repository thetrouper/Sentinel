package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import io.github.thetrouper.sentinel.server.util.Randomizer;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportFalsePositives {
    public static Map<String,AsyncPlayerChatEvent> reportMap = new HashMap<>();
    public static String generateReport(AsyncPlayerChatEvent e) {
        final long reportLong = Randomizer.generateID();
        final String reportID = Long.toString(reportLong);
        ServerUtils.sendDebugMessage("FP Report: Generating chat filter report");
        reportMap.put(reportID,e);
        ServerUtils.sendDebugMessage(("FP Report: Generated chat filter report. ID:" + reportID + " Message: \"" + reportMap.get(reportID).getMessage() + "\" Expires in 60 seconds"));
        Bukkit.getScheduler().runTaskLater(Sentinel.getInstance(),()->{
            reportMap.remove(reportID);
            ServerUtils.sendDebugMessage("FP Report:  Chat filter Report expired. ID: " + reportID);
        },60000);
        return reportID;
    }
    public static void sendFalsePositiveReport(String reportID) {
        AsyncPlayerChatEvent e = reportMap.get(reportID);
        String orig = e.getMessage();

        String lowercasedText = orig.toLowerCase();
        String remFP = ProfanityFilter.removeFalsePositives(lowercasedText);
        String convertedLeet = ProfanityFilter.convertLeetSpeakCharacters(remFP);
        String remSpecials = ProfanityFilter.stripSpecialCharacters(convertedLeet);
        String simplifyRep = ProfanityFilter.simplifyRepeatingLetters(remSpecials);
        String sanitized = ProfanityFilter. removePeriodsAndSpaces(simplifyRep);

        sendEmbed(e.getPlayer(),orig,lowercasedText,remFP,convertedLeet,remSpecials,simplifyRep,sanitized);

    }

    public static void sendEmbed(Player player,
                                 String message,
                                 String lowercased,
                                 String remFP,
                                 String convertedLeet,
                                 String remSpecials,
                                 String simplifyRep,
                                 String sanitized) {
        ServerUtils.sendDebugMessage("Creating FalsePositive Webhook...");
        DiscordWebhook webhook = new DiscordWebhook(MainConfig.Plugin.webhook);
        webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
        webhook.setUsername("Sentinel Anti-Nuke | Logs");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor("Anti-Swear False Positive","","")
                .setTitle("Flag Report:")
                .setDescription(
                        Emojis.rightSort + "Player: " + player.getName() + " " + Emojis.target + "\\n" +
                                Emojis.space + Emojis.arrowRight + "UUID: `" + player.getUniqueId() + "`\\n"
                )
                .addField("Original Message", "`" + message + "` " + (ProfanityFilter.ContainsProfanity(message) ? Emojis.alarm : ""), false)
                .addField("Lowercase", "`" + lowercased + "` " + (ProfanityFilter.ContainsProfanity(lowercased) ? Emojis.alarm : ""), false)
                .addField("Removed FPs", "`" + remFP + "` " + (ProfanityFilter.ContainsProfanity(remFP) ? Emojis.alarm : ""), false)
                .addField("Converted Leet", "`" + convertedLeet + "` " + (ProfanityFilter.ContainsProfanity(convertedLeet) ? Emojis.alarm : ""), false)
                .addField("Removed Specials", "`" + remSpecials + "` " + (ProfanityFilter.ContainsProfanity(remSpecials) ? Emojis.alarm : ""), false)
                .addField("Simplify Repeats", "`" + simplifyRep + "` " + (ProfanityFilter.ContainsProfanity(simplifyRep) ? Emojis.alarm : ""), false)
                .addField("Fully Sanitized Message", ProfanityFilter.highlightProfanity(sanitized,"`", "`") + " " + Emojis.noDM, false)
                .setColor(Color.green)
                .setThumbnail("https://crafatar.com/avatars/" + player.getUniqueId() + "?size=64&&overlay");
        webhook.addEmbed(embed);
        try {
            ServerUtils.sendDebugMessage("FP Report: Executing webhook...");
            webhook.execute();
        } catch (IOException e) {
            ServerUtils.sendDebugMessage("FP Report: Epic webhook failure!!!");
            Sentinel.log.info(e.toString());
        }
    }
}
