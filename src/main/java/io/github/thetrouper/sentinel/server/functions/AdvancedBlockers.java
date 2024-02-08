package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.server.FilterAction;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Async;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedBlockers {

    public static void handleAdvanced(AsyncPlayerChatEvent e) {
        if (!Sentinel.advConfig.allowedCharRegex.isBlank()) handleAntiUnicode(e);
        if (!Sentinel.advConfig.urlRegex.isBlank()) handleAntiURL(e);
        if (!Sentinel.advConfig.strictRegex.isBlank()) handleStrictRegex(e);
        if (!Sentinel.advConfig.swearRegex.isBlank()) handleSwearRegex(e);
    }
    public static void handleAntiUnicode(AsyncPlayerChatEvent e) {
        String message = Text.removeFirstColor(e.getMessage());
        String nonAllowed = message.replaceAll(Sentinel.advConfig.allowedCharRegex, "").trim();
        if (nonAllowed.length() != 0) {
            e.getPlayer().sendMessage(Text.prefix(Sentinel.language.get("unicode-warn")));
            e.setCancelled(true);
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

        if (matcher.find()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Text.prefix(Sentinel.language.get("unicode-warn")));
        }
    }

}
