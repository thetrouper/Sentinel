package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.server.util.GPTUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

import static io.github.thetrouper.sentinel.server.util.GPTUtils.calculateSimilarity;

public class AntiSpam {
    public static Map<Player, Integer> heatMap;
    public static Map<Player, String> lastMessageMap;

    public static void enableAntiSpam() {
        heatMap = new HashMap<>();
        lastMessageMap = new HashMap<>();
    }
    public static void handleAntiSpam(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (!heatMap.containsKey(player)) heatMap.put(player, 0);
        if (heatMap.get(player) > 10) {
            event.setCancelled(true);
            player.sendMessage(TextUtils.prefix("Rate limit exceeded! Please wait before sending another message."));
            return;
        }
        if (lastMessageMap.containsKey(player)) {
            String lastMessage = lastMessageMap.get(player);
            double similarity = calculateSimilarity(message, lastMessage);
            if (similarity > 0.5) heatMap.put(player, heatMap.get(player) + 3);
            if (similarity > 0.9) heatMap.put(player, heatMap.get(player) + 6);
        }
        lastMessageMap.put(player, message);
    }
    public static void decayHeat() {
        for (Player player : heatMap.keySet()) {
            int heat = heatMap.get(player);
            if (heat > 0) {
                heatMap.put(player, heat - 1);
            }
        }
    }
}
