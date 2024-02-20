package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.server.FilterAction;
import io.github.thetrouper.sentinel.server.util.GPTUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class AntiSpam {
    public static Map<Player, Integer> heatMap;
    public static Map<Player, String> lastMessageMap;

    public static void enableAntiSpam() {
        heatMap = new HashMap<>();
        lastMessageMap = new HashMap<>();
    }

    public static void handleAntiSpam(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = Text.removeFirstColor(e.getMessage());

        if (!lastMessageMap.containsKey(p)) {
            lastMessageMap.put(p,"/* Placeholder Message from Sentinel */");
            ServerUtils.sendDebugMessage("AntiSpam: " + p.getName() + " did not have a previous message, setting to placeholder!");
        }

        if (!heatMap.containsKey(p)) {
            heatMap.put(p,0);
            ServerUtils.sendDebugMessage("AntiSpam: " + p.getName() + " did not have a heat, setting it to 0!");
        }

        if (lastMessageMap.containsKey(p)) {
            String lastMessage = lastMessageMap.get(p);
            double similarity = GPTUtils.calcSim(message, lastMessage);
            ServerUtils.sendDebugMessage("AntiSpam: " + p.getName() + " has a heat of " + heatMap.get(p) + "/" + Sentinel.mainConfig.chat.antiSpam.punishHeat + ". Current Message: \"" + message + "\" Last message: \"" + lastMessage + "\"");
            if (similarity > 90) {
                heatMap.put(p, heatMap.get(p) + Sentinel.mainConfig.chat.antiSpam.highGain);
                ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 90% for " + p.getName() + ". Adding " + Sentinel.mainConfig.chat.antiSpam.highGain);
            } else if (similarity > 50) {
                heatMap.put(p, heatMap.get(p) + Sentinel.mainConfig.chat.antiSpam.mediumGain);
                ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 50% for " + p.getName() + ". Adding " + Sentinel.mainConfig.chat.antiSpam.mediumGain);
            } else if (similarity > 25) {
                heatMap.put(p, heatMap.get(p) + Sentinel.mainConfig.chat.antiSpam.lowGain);
                ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 25% for " + p.getName() + ". Adding " + Sentinel.mainConfig.chat.antiSpam.lowGain);
            }
        }

        lastMessageMap.put(p, message);

        if (heatMap.get(p) > Sentinel.mainConfig.chat.antiSpam.punishHeat) {
            e.setCancelled(true);
            FilterAction.filterPunish(e,FAT.SPAM_PUNISH,GPTUtils.calcSim(e.getMessage(),lastMessageMap.get(p)), null);
            return;
        }

        if (heatMap.get(p) > Sentinel.mainConfig.chat.antiSpam.blockHeat) {
            e.setCancelled(true);
            FilterAction.filterPunish(e,FAT.BLOCK_SPAM, GPTUtils.calcSim(e.getMessage(),lastMessageMap.get(p)), null);
            heatMap.put(p, heatMap.get(p) + Sentinel.mainConfig.chat.antiSpam.highGain);
            return;
        }

        heatMap.put(p,heatMap.get(p) + Sentinel.mainConfig.chat.antiSpam.defaultGain);
    }
    public static void decayHeat() {
        for (Player p : heatMap.keySet()) {
            int heat = heatMap.get(p);
            if (heat > 0) {
                heat = heat - Sentinel.mainConfig.chat.antiSpam.heatDecay;
                heatMap.put(p, Math.max(0, heat));
            }
            //ServerUtils.sendDebugMessage("AntiSpam: Decaying heat for " + p.getName() + ": " + heatMap.get(p));
        }
    }
}
