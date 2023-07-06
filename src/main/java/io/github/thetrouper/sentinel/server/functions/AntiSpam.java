package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.discord.WebhookSender;
import io.github.thetrouper.sentinel.server.util.GPTUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
    public static void handleAntiSpam(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        if (!heatMap.containsKey(p)) heatMap.put(p, 0);
        if (heatMap.get(p) > Config.punishHeat) {
            e.setCancelled(true);
            punishSpam(p,message, lastMessageMap.get(p));
            return;
        }

        if (heatMap.get(p) > Config.blockHeat) {
            e.setCancelled(true);
            alertSpam(p, message, lastMessageMap.get(p));
            heatMap.put(p, heatMap.get(p) + Config.highGain);
            return;
        }
        if (lastMessageMap.containsKey(p)) {
            String lastMessage = lastMessageMap.get(p);
            double similarity = calculateSimilarity(message, lastMessage);
            if (similarity > 0.25) heatMap.put(p, heatMap.get(p) + Config.lowGain);
            if (similarity > 0.5) heatMap.put(p, heatMap.get(p) + Config.mediumGain);
            if (similarity > 0.9) heatMap.put(p, heatMap.get(p) + Config.highGain);
        }
        lastMessageMap.put(p, message);
    }
    public static void decayHeat() {
        for (Player p : heatMap.keySet()) {
            int heat = heatMap.get(p);
            if (heat > 0) {
                heat = heat - Config.heatDecay;
                heatMap.put(p, Math.max(0, heat));
            }
        }
    }

    public static void alertSpam(Player p, String message1, String message2) {
        TextComponent text = new TextComponent();
        p.sendMessage(TextUtils.prefix("Do not spam in chat! Please wait before sending another message."));
        String hover = TextUtils.color("§8]==-- §d§lSentinel §8--==[" +
                "\n&bPrevious: &f" + message2 +
                "\n&bCurrent: &f" + message1 +
                "\n&bSimilarity &f" + GPTUtils.calculateSimilarity(message1,message2 + "%"));
        text.setText(TextUtils.prefix(TextUtils.color
                ("&b&n" + p.getName() + "&7 might be spamming! &8(&c" + heatMap.get(p) + "&7/&4" + Config.punishHeat + "&8)")));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
    }
    public static void punishSpam(Player player, String message1, String message2) {
        boolean chatCleared = false;
        if (Config.clearChat) {
            ServerUtils.sendCommand(Config.clearChatCommand);
            chatCleared = true;
        }
        ServerUtils.sendCommand(Config.punishSpamCommand.replace("%player%", player.getName()));
        player.sendMessage(TextUtils.prefix(TextUtils.color("&cYou have been auto-punished for violating the anti-spam repetitively!")));
        TextComponent text = new TextComponent();
        text.setText(TextUtils.prefix(TextUtils.color
                ("&b&n" + player.getName() + "&7 has been auto-muted by the anti-spam! &8(&c" + heatMap.get(player) + "&7/&4" + Config.punishHeat + "&8)")));
        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
        if (Config.logSpam) WebhookSender.sendSpamLog(player,message1,message2,heatMap.get(player),chatCleared);
    }
}
