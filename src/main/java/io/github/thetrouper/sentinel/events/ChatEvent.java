package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.utils.ArrayUtils;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.server.functions.AdvancedBlockers;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.Consumer;

public class ChatEvent implements CustomListener {
    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        handleChatEvent(e);
    }
    public static void handleChatEvent(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;

        Player p = e.getPlayer();

        Report report = ReportFalsePositives.initializeReport(e);

        ServerUtils.sendDebugMessage("""
        Creating a chat report...
        ID %s
        Player %s
        Message %s
        Fields %s
        """.formatted(report.id(),report.event().getPlayer(),report.event().getMessage(), report.stepsTaken().toString()));

        handleEventIfNotBypassed(p,
                "sentinel.chat.antiunicode.bypass",
                Sentinel.mainConfig.chat.useAntiUnicode, "unicode",
                e,
                (event)->{
            AdvancedBlockers.handleAdvanced(event,report);
                });

        handleEventIfNotBypassed(p,
                "sentinel.chat.antispam.bypass",
                Sentinel.mainConfig.chat.antiSpam.antiSpamEnabled,
                "spam",
                e,
                (event)->{
            AntiSpam.handleAntiSpam(event,report);
                });

        handleEventIfNotBypassed(p,
                "sentinel.chat.antiswear.bypass",
                Sentinel.mainConfig.chat.antiSwear.antiSwearEnabled,
                "swear",
                e,
                (event)->{
            ProfanityFilter.handleProfanityFilter(event,report);
                });

        ServerUtils.sendDebugMessage("""
        Adding a report to the list...
        ID %s
        Fields %s
        """.formatted(report.id(), report.stepsTaken().toString()));
        ReportFalsePositives.reports.put(report.id(),report);
        AntiSpam.lastMessageMap.put(p.getUniqueId(), e.getMessage());
    }

    private static void handleEventIfNotBypassed(Player p, String permission, boolean isEnabled, String eventType, AsyncPlayerChatEvent e, Consumer<AsyncPlayerChatEvent> handler) {
        if (!Sentinel.isTrusted(p) || !p.hasPermission(permission)) {
            ServerUtils.sendDebugMessage("ChatEvent: Permission bypass failed, checking for " + eventType);
            if (e.isCancelled()) return;
            if (!isEnabled) return;
            ServerUtils.sendDebugMessage("ChatEvent: " + eventType + " check enabled, continuing!");
            handler.accept(e);
        }
    }
}
