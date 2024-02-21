package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.server.util.Randomizer;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportFalsePositives {
    public static Map<String,AsyncPlayerChatEvent> reportMap = new HashMap<>();
    public static Map<Long,Report> reports = new HashMap<>();

    public static Report initializeReport(AsyncPlayerChatEvent e) {
        final long reportID = Randomizer.generateID();
        HashMap<String,String> steps = new HashMap<>();
        steps.put("Original Message", e.getMessage());
        return new Report(reportID,e,steps);
    }

    public static String generateReport(AsyncPlayerChatEvent e) {
        final long reportLong = Randomizer.generateID();
        final String reportID = Long.toString(reportLong);
        ServerUtils.sendDebugMessage("FP Report: Generating chat filter report");
        reportMap.put(reportID,e);
        ServerUtils.sendDebugMessage(("FP Report: Generated chat filter report. ID:" + reportID + " Message: \"" + reportMap.get(reportID).getMessage() + "\" Expires in 60 seconds"));
        SchedulerUtils.later(60000,()->{
            reportMap.remove(reportID);
            ServerUtils.sendDebugMessage("FP Report:  Chat filter Report expired. ID: " + reportID);
        });
        return reportID;
    }


    public static void sendFalsePositiveReport(Report report) {
        DiscordEmbed.Builder embed = DiscordEmbed.create()
                .author(new DiscordEmbed.Author("Anti-Swear False Positive","",null))
                .title("Flag Report:")
                .desc(String.format("""
                                %1$sPlayer: %2$s %3$s\n
                                %4$s %5$sUUID: `%2$s`\n
                                """,
                        Emojis.rightSort,
                        report.event().getPlayer().getName(),
                        Emojis.target,
                        Emojis.space,
                        Emojis.arrowRight
                ));

        report.stepsTaken().forEach((key, value)->{
            embed.addField(new DiscordEmbed.Field(key,value));
        });

        DiscordWebhook.create()
                .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                .username("Sentinel Anti-Nuke | Logs")
                .addEmbed(embed.build())
                .send(Sentinel.mainConfig.plugin.webhook);
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
        try {
            sendEmbed(e.getPlayer(),orig,lowercasedText,remFP,convertedLeet,remSpecials,simplifyRep,sanitized);
        } catch (Exception ex) {
            e.getPlayer().sendMessage(Text.prefix("Report Failed!"));
            Sentinel.log.warning(ex.getMessage());
        }
    }

    public static void sendEmbed(Player player,
                                 String message,
                                 String lowercased,
                                 String remFP,
                                 String convertedLeet,
                                 String remSpecials,
                                 String simplifyRep,
                                 String sanitized) throws IOException {
        ServerUtils.sendDebugMessage("Creating FalsePositive Webhook...");
        DiscordWebhook.create()
                .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                .username("Sentinel Anti-Nuke | Logs")
                .addEmbed(DiscordEmbed.create()
                        .author(new DiscordEmbed.Author("Anti-Swear False Positive","",null))
                        .title("Flag Report:")
                        .desc(String.format("%1$sPlayer: %2$s %3$s\n%4$s %5$sUUID: `%2$s`\n",
                                Emojis.rightSort,
                                player.getName(),
                                Emojis.target,
                                Emojis.space,
                                Emojis.arrowRight
                        ))
                        .addField("Original Message", "`" + message + "` " + (ProfanityFilter.ContainsProfanity(message) ? Emojis.alarm : ""), false)
                        .addField("Lowercase", "`" + lowercased + "` " + (ProfanityFilter.ContainsProfanity(lowercased) ? Emojis.alarm : ""), false)
                        .addField("Removed FPs", "`" + remFP + "` " + (ProfanityFilter.ContainsProfanity(remFP) ? Emojis.alarm : ""), false)
                        .addField("Converted Leet", "`" + convertedLeet + "` " + (ProfanityFilter.ContainsProfanity(convertedLeet) ? Emojis.alarm : ""), false)
                        .addField("Removed Specials", "`" + remSpecials + "` " + (ProfanityFilter.ContainsProfanity(remSpecials) ? Emojis.alarm : ""), false)
                        .addField("Simplify Repeats", "`" + simplifyRep + "` " + (ProfanityFilter.ContainsProfanity(simplifyRep) ? Emojis.alarm : ""), false)
                        .addField("Fully Sanitized Message", ProfanityFilter.highlightProfanity(sanitized,"`", "`") + " " + Emojis.noDM, false)
                        .color(0x00FF00)
                        .thumbnail("https://crafatar.com/avatars/" + player.getUniqueId() + "?size=64&&overlay")
                        .build())
                .build().send(Sentinel.mainConfig.plugin.webhook);

    }
}
