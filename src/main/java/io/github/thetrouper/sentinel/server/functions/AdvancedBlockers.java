package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.server.FilterAction;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedBlockers {

    public static void handleAdvanced(AsyncPlayerChatEvent e, Report report) {
        //if (Sentinel.isTrusted(e.getPlayer())) return;
        if (Sentinel.mainConfig.chat.useAntiUnicode) handleAntiUnicode(e,report);
        if (Sentinel.mainConfig.chat.useAntiURL) handleAntiURL(e,report);
        if (Sentinel.mainConfig.chat.useStrictRegex) handleStrictRegex(e,report);
        if (Sentinel.mainConfig.chat.useSwearRegex) handleSwearRegex(e,report);
    }

    public static void handleAntiUnicode(AsyncPlayerChatEvent e, Report report) {
        if (e.isCancelled()) return;
        String message = Text.removeFirstColor(e.getMessage());
        report.stepsTaken().put("Anti-Unicode", "`%s`".formatted(message));
        ServerUtils.sendDebugMessage("AdvBlocker: Checking for unicode: " + message);
        String nonAllowed = message.replaceAll(Sentinel.advConfig.allowedCharRegex, "").trim();

        if (nonAllowed.length() != 0) {
            ServerUtils.sendDebugMessage("AdvBlocker: Caught Unicode: " + nonAllowed);
            e.setCancelled(true);
            report.stepsTaken().replace("Anti-Unicode", "`%s` %s".formatted(message, Emojis.alarm));
            FilterAction.filterPunish(e,FAT.BLOCK_UNICODE,null,null,report.id());
        }
    }

    public static void handleSwearRegex(AsyncPlayerChatEvent e, Report report) {
        if (e.isCancelled()) return;
        String swearRegex = Sentinel.advConfig.swearRegex;

        Pattern pattern = Pattern.compile(swearRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());

        report.stepsTaken().put("Anti-Swear Regex", "`%s`".formatted(e.getMessage()));

        if (matcher.find()) {
            e.setCancelled(true);
            String highlighted = Text.regexHighlighter(swearRegex,e.getMessage()," > "," < ");
            report.stepsTaken().replace("Anti-Swear Regex", "`%s` %s".formatted(highlighted, Emojis.alarm));
            FilterAction.filterPunish(e,FAT.SWEAR_PUNISH,null,FilterSeverity.HIGH,report.id());
        }
    }

    public static void handleStrictRegex(AsyncPlayerChatEvent e, Report report) {
        if (e.isCancelled()) return;
        String strictRegex = Sentinel.advConfig.strictRegex;

        Pattern pattern = Pattern.compile(strictRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());

        report.stepsTaken().put("Strict Regex", "`%s`".formatted(e.getMessage()));



        if (matcher.find()) {
            e.setCancelled(true);
            String highlighted = Text.regexHighlighter(strictRegex,e.getMessage()," > "," < ");
            report.stepsTaken().replace("Strict Regex", "`%s` %s".formatted(highlighted, Emojis.alarm));
            FilterAction.filterPunish(e, FAT.SLUR_PUNISH,null, FilterSeverity.SLUR,report.id());
        }
    }

    public static void handleAntiURL(AsyncPlayerChatEvent e, Report report) {
        if (e.isCancelled()) return;
        String urlRegex = Sentinel.advConfig.urlRegex;

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());
        ServerUtils.sendDebugMessage("AdvBlocker: Checking for URLs against regex `%1$s`:%2$s".formatted(urlRegex, e.getMessage()));

        report.stepsTaken().put("Anti-URL", "`%s`".formatted(
                e.getMessage()
        ));

        if (matcher.find()) {
            e.setCancelled(true);
            String highlighted = Text.regexHighlighter(e.getMessage(),Sentinel.advConfig.urlRegex," > "," < ");
            ServerUtils.sendDebugMessage("AdvBlocker: Caught URL: " + highlighted);
            report.stepsTaken().replace("Anti-URL", "`%s` %s".formatted(highlighted, Emojis.alarm));

            FilterAction.filterPunish(e,FAT.BLOCK_URL,null,null,report.id());
        }
    }
}
