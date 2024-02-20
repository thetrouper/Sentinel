package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.server.FilterAction;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedBlockers {

    public static void handleAdvanced(AsyncPlayerChatEvent e) {
        //if (Sentinel.isTrusted(e.getPlayer())) return;
        if (Sentinel.mainConfig.chat.useAntiUnicode) handleAntiUnicode(e);
        if (Sentinel.mainConfig.chat.useAntiURL) handleAntiURL(e);
        if (Sentinel.mainConfig.chat.useStrictRegex) handleStrictRegex(e);
        if (Sentinel.mainConfig.chat.useSwearRegex) handleSwearRegex(e);
    }

    public static void handleAntiUnicode(AsyncPlayerChatEvent e) {
        String message = Text.removeFirstColor(e.getMessage());
        ServerUtils.sendDebugMessage("AdvBlocker: Checking for unicode: " + message);
        String nonAllowed = message.replaceAll(Sentinel.advConfig.allowedCharRegex, "").trim();
        if (nonAllowed.length() != 0) {
            ServerUtils.sendDebugMessage("AdvBlocker: Caught Unicode: " + nonAllowed);
            e.setCancelled(true);
            FilterAction.filterPunish(e,FAT.BLOCK_UNICODE,null,null);
        }
    }

    public static void handleSwearRegex(AsyncPlayerChatEvent e) {
        String urlRegex = Sentinel.advConfig.swearRegex;

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());

        if (matcher.find()) {
            e.setCancelled(true);
            FilterAction.filterPunish(e,FAT.SWEAR_PUNISH,null,FilterSeverity.HIGH);
        }
    }

    public static void handleStrictRegex(AsyncPlayerChatEvent e) {
        String urlRegex = Sentinel.advConfig.strictRegex;

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());

        if (matcher.find()) {
            e.setCancelled(true);
            FilterAction.filterPunish(e, FAT.SLUR_PUNISH,null, FilterSeverity.SLUR);
        }
    }

    public static void handleAntiURL(AsyncPlayerChatEvent e) {
        String urlRegex = Sentinel.advConfig.urlRegex;

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());
        ServerUtils.sendDebugMessage("AdvBlocker: Checking for URLs against regex `%1$s`:%2$s".formatted(urlRegex, e.getMessage()));

        if (matcher.find()) {
            e.setCancelled(true);
            ServerUtils.sendDebugMessage("AdvBlocker: Caught URL: " + Text.regexHighlighter(e.getMessage(),Sentinel.advConfig.urlRegex," > "," < "));

            FilterAction.filterPunish(e,FAT.BLOCK_URL,null,null);
        }
    }
}
