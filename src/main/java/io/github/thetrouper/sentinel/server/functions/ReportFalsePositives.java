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
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportFalsePositives {
    public static Map<Long,Report> reports = new HashMap<>();

    public static Report initializeReport(AsyncPlayerChatEvent event) {
        final long reportID = Randomizer.generateID();
        LinkedHashMap<String,String> steps = new LinkedHashMap<>();
        steps.put("Original Message", "`%s`".formatted(event.getMessage()));
        SchedulerUtils.later(1200,()->{
            reports.remove(reportID);
        });
        return new Report(reportID,event,steps);
    }

    public static void sendFalsePositiveReport(Report report) {
        DiscordEmbed.Builder embed = DiscordEmbed.create()
                .author(new DiscordEmbed.Author("Anti-Swear False Positive","",null))
                .title("A player has reported a false positive")
                .desc(String.format("""
                                %1$sPlayer: %2$s %3$s
                                %4$s %5$sUUID: `%6$s`
                                ## **Filter Steps Taken:**
                                """,
                        Emojis.rightSort,
                        report.event().getPlayer().getName(),
                        Emojis.target,
                        Emojis.space,
                        Emojis.rightArrow,
                        report.event().getPlayer().getUniqueId()
                ));

        report.stepsTaken().forEach((key, value)->{
            embed.addField(new DiscordEmbed.Field(key,value));
        });

        DiscordWebhook.create()
                .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                .username("Sentinel Anti-Nuke | Logs")
                .addEmbed(embed.build())
                .send(Sentinel.mainConfig.plugin.webhook);

        reports.remove(report.id());
    }
}
