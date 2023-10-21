package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.FilterAction;
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
            ServerUtils.sendDebugMessage("AntiSpam: " + p.getName() + " has a heat of " + heatMap.get(p) + "/" + Config.punishHeat + ". Current Message: \"" + message + "\" Last message: \"" + lastMessage + "\"");
            if (similarity > 90) {
                heatMap.put(p, heatMap.get(p) + Config.highGain);
                ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 90% for " + p.getName() + ". Adding " + Config.highGain);
            } else if (similarity > 50) {
                heatMap.put(p, heatMap.get(p) + Config.mediumGain);
                ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 50% for " + p.getName() + ". Adding " + Config.mediumGain);
            } else if (similarity > 25) {
                heatMap.put(p, heatMap.get(p) + Config.lowGain);
                ServerUtils.sendDebugMessage("AntiSpam: Similarity: " + similarity + ", is greater than 25% for " + p.getName() + ". Adding " + Config.lowGain);
            }
        }

        if (heatMap.get(p) > Config.punishHeat) {
            e.setCancelled(true);
            FilterAction.filterAction(p,e,null,null, GPTUtils.calcSim(e.getMessage(),lastMessageMap.get(p)), FAT.SPAM);
            return;
        }

        if (heatMap.get(p) > Config.blockHeat) {
            e.setCancelled(true);
            FilterAction.filterAction(p,e,null,null, GPTUtils.calcSim(e.getMessage(),lastMessageMap.get(p)), FAT.BLOCK_SPAM);
            heatMap.put(p, heatMap.get(p) + Config.highGain);
            return;
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
            //ServerUtils.sendDebugMessage("AntiSpam: Decaying heat for " + p.getName() + ": " + heatMap.get(p));
        }
    }
    /*
    public static void alertSpam(Player p, String message1, String message2) {
        TextComponent text = new TextComponent();
        double similarity = GPTUtils.calculateSimilarity(message1,message2 + "%");
        DecimalFormat fs = new DecimalFormat("##.#");
        fs.setRoundingMode(RoundingMode.DOWN);
        TextComponent warning = new TextComponent();
        warning.setText(Text.prefix(Sentinel.dict.get("spam-warning")));
        warning.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("action-automatic")));
        p.spigot().sendMessage(warning);
        text.setText(Text.prefix(Sentinel.dict.get("spam-notification").formatted(p.getName(),heatMap.get(p),Config.punishHeat)));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("spam-notification-hover").formatted(message1,message2,fs.format(similarity)))));
        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
    }
    public static void punishSpam(Player p, String message1, String message2) {
        boolean chatCleared = false;
        if (Config.clearChat) {
            ServerUtils.sendCommand(Config.chatClearCommand);
            chatCleared = true;
        }
        ServerUtils.sendCommand(Config.spamPunishCommand.replace("%player%", p.getName()));
        TextComponent warning = new TextComponent();
        warning.setText(Text.prefix(Sentinel.dict.get("spam-punished")));
        warning.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Sentinel.dict.get("action-automatic"))));
        p.spigot().sendMessage(warning);
        TextComponent text = new TextComponent();
        text.setText(Text.prefix(Sentinel.dict.get("spam-punish-notification").formatted(p.getName(),heatMap.get(p),Config.punishHeat)));
        ServerUtils.forEachStaff(staff -> {
            staff.spigot().sendMessage(text);
        });
        if (Config.logSpam) WebhookSender.sendSpamLog(p,message1,message2,heatMap.get(p),chatCleared);
    }
    */
}
