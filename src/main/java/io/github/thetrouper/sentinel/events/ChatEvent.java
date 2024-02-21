package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
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

        ReportFalsePositives.reports.put(report.id(),report);
    }

    private static void handleEventIfNotBypassed(Player p, String permission, boolean isEnabled, String eventType, AsyncPlayerChatEvent e, Consumer<AsyncPlayerChatEvent> handler) {
        if (!Sentinel.isTrusted(p) || !p.hasPermission(permission)) {
            ServerUtils.sendDebugMessage("ChatEvent: Permission bypass failed, checking for " + eventType);
            if (isEnabled) {
                ServerUtils.sendDebugMessage("ChatEvent: " + eventType + " check enabled, continuing!");
                handler.accept(e);
            }
        }
    }
}
