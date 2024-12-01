package me.trouper.sentinel.server.functions.chatfilter;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import me.trouper.sentinel.data.Emojis;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.server.functions.Randomizer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FalsePositiveReporting {
    public static Map<Long, Report> reports = new HashMap<>();

    public static Report initializeReport(String message) {
        final long reportID = Randomizer.generateID();
        LinkedHashMap<String,String> steps = new LinkedHashMap<>();
        steps.put("Original Message", "`%s`".formatted(message));

        SchedulerUtils.later(1200,()->{
            reports.remove(reportID);
        });
        return new Report(reportID,message,steps);
    }

    public static void sendReport(Player sender, Report report) {
        DiscordEmbed.Builder embed = DiscordEmbed.create()
                .author(new DiscordEmbed.Author("Anti-Swear False Positive","",null))
                .title("A player has reported a false positive")
                .desc(String.format("""
                                %1$sPlayer: %2$s %3$s
                                %4$s %5$sUUID: `%6$s`
                                ## **Filter Steps Taken:**
                                """,
                        Emojis.rightSort,
                        sender.getName(),
                        Emojis.target,
                        Emojis.space,
                        Emojis.rightArrow,
                        sender.getUniqueId()
                ));

        report.getStepsTaken().forEach((key, value)->{
            embed.addField(new DiscordEmbed.Field(key,value));
        });

        EmbedFormatter.sendEmbed(embed.build());

        reports.remove(report.getId());
    }
}
