package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.server.FilterAction;
import io.github.thetrouper.sentinel.server.util.GPTUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class AntiSpam {
    public static Map<Player, Integer> heatMap = new HashMap<>();
    public static Map<Player, String> lastMessageMap = new HashMap<>();

    public static void handleAntiSpam(AsyncPlayerChatEvent e, Report report) {
        Player p = e.getPlayer();
        String message = Text.removeFirstColor(e.getMessage());
        String lastMessage = lastMessageMap.getOrDefault(p,"/* Placeholder Message from Sentinel */");
        int currentHeat = heatMap.getOrDefault(p,0);
        double similarity = GPTUtils.calcSim(message, lastMessage);

        int addHeat = Sentinel.mainConfig.chat.antiSpam.defaultGain;

        ServerUtils.sendDebugMessage("AntiSpam: " + p.getName() + " has a heat of " + currentHeat + "/" + Sentinel.mainConfig.chat.antiSpam.punishHeat + ". Current Message: \"" + message + "\" Last message: \"" + lastMessage + "\"");
        if (similarity > 90) {
            addHeat = Sentinel.mainConfig.chat.antiSpam.highGain;
            ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 90% for " + p.getName() + ". Adding " + Sentinel.mainConfig.chat.antiSpam.highGain);
        } else if (similarity > 50) {
            addHeat = Sentinel.mainConfig.chat.antiSpam.mediumGain;
            ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 50% for " + p.getName() + ". Adding " + Sentinel.mainConfig.chat.antiSpam.mediumGain);
        } else if (similarity > 25) {
            addHeat = Sentinel.mainConfig.chat.antiSpam.lowGain;
            ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 25% for " + p.getName() + ". Adding " + Sentinel.mainConfig.chat.antiSpam.lowGain);
        }

        report.stepsTaken().put("Anti-Spam", "Heat: %s\nMessage: `%s`".formatted(currentHeat,message));

        if (currentHeat > Sentinel.mainConfig.chat.antiSpam.punishHeat) {
            e.setCancelled(true);
            report.stepsTaken().replace("Anti-Spam", "Heat: %s\nMessage: `%s` %s".formatted(currentHeat,message, Emojis.alarm));
            FilterAction.filterPunish(e,FAT.SPAM_PUNISH,GPTUtils.calcSim(e.getMessage(),lastMessage), null,report.id());
            return;
        }

        if (currentHeat > Sentinel.mainConfig.chat.antiSpam.blockHeat) {
            e.setCancelled(true);
            report.stepsTaken().replace("Anti-Spam", "Heat: %s\nMessage: `%s` %s".formatted(currentHeat,message, Emojis.alarm));
            FilterAction.filterPunish(e,FAT.BLOCK_SPAM, GPTUtils.calcSim(e.getMessage(),lastMessage), null,report.id());
            heatMap.put(p, currentHeat + Sentinel.mainConfig.chat.antiSpam.highGain);
            return;
        }

        heatMap.put(p,currentHeat + addHeat);
    }
    public static void decayHeat() {
        for (Player p : heatMap.keySet()) {
            int heat = heatMap.getOrDefault(p,0);
            if (heat > 0) {
                heat = heat - Sentinel.mainConfig.chat.antiSpam.heatDecay;
                heatMap.put(p, Math.max(0, heat));
            }
            //ServerUtils.sendDebugMessage("AntiSpam: Decaying heat for " + p.getName() + ": " + heatMap.get(p));
        }
    }
}
