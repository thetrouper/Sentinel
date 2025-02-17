package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.server.functions.chatfilter.unicode.UnicodeFilter;
import me.trouper.sentinel.server.functions.chatfilter.url.UrlFilter;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.chat.ProfanityFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.SpamFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.UnicodeFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.UrlFilterGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.*;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.DangerousCMDGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.LoggedCMDGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.SpecificCMDGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.function.Consumer;

public class ChatEvent implements CustomListener {

    @EventHandler
    private void onChat(AsyncChatEvent e) {
        ServerUtils.verbose("Chat event sanity check:\n Canceled %s".formatted(e.isCancelled()));
        handleEvent(e);
    }

    public void handleEvent(AsyncChatEvent e) {
        if (e.isCancelled()) return;
        if (PlayerUtils.isTrusted(e.getPlayer().getUniqueId().toString())) {
            if (MainGUI.awaitingCallback.contains(e.getPlayer().getUniqueId())) {
                ServerUtils.verbose("Attempting to cancel events for callback!");
                e.setCancelled(true);
                MainGUI.awaitingCallback.remove(e.getPlayer().getUniqueId());
                ServerUtils.verbose("Handling Chat Event for callbacks");
                SchedulerUtils.later(0,()->{
                    UnicodeFilterGUI.updater.invokeCallbacks(e);
                    UrlFilterGUI.updater.invokeCallbacks(e);
                    ProfanityFilterGUI.updater.invokeCallbacks(e);
                    SpamFilterGUI.updater.invokeCallbacks(e);
                    DangerousCMDGUI.updater.invokeCallbacks(e);
                    LoggedCMDGUI.updater.invokeCallbacks(e);
                    SpecificCMDGUI.updater.invokeCallbacks(e);
                    CBEditGUI.updater.invokeCallbacks(e);
                    CBMCPlaceGUI.updater.invokeCallbacks(e);
                    CBMCUseGUI.updater.invokeCallbacks(e);
                    CBPlaceGUI.updater.invokeCallbacks(e);
                    CBUseGUI.updater.invokeCallbacks(e);
                    HotbarActionGUI.updater.invokeCallbacks(e);
                });
            }
            return;
        }

        Player p = e.getPlayer();

        ServerUtils.verbose("Chat event start after trust check:\n Canceled %s".formatted(e.isCancelled()));

        handle(p,
                "sentinel.chatfilter.unicode.bypass",
                Sentinel.mainConfig.chat.unicodeFilter.enabled, "unicode",
                e,
                UnicodeFilter::handleUnicodeFilter);

        ServerUtils.verbose("Chat event middle after unicode:\n Canceled %s".formatted(e.isCancelled()));

        handle(p,
                "sentinel.chatfilter.url.bypass",
                Sentinel.mainConfig.chat.urlFilter.enabled, "url",
                e,
                UrlFilter::handleUrlFilter);

        ServerUtils.verbose("Chat event middle after URL:\n Canceled %s".formatted(e.isCancelled()));

        handle(p,
                "sentinel.chatfilter.spam.bypass",
                Sentinel.mainConfig.chat.spamFilter.enabled,
                "spam",
                e,
                SpamFilter::handleSpamFilter);

        ServerUtils.verbose("Chat event middle after spam:\n Canceled %s".formatted(e.isCancelled()));

        handle(p,
                "sentinel.chatfilter.swear.bypass",
                Sentinel.mainConfig.chat.profanityFilter.enabled,
                "swear",
                e,
                ProfanityFilter::handleProfanityFilter);

        ServerUtils.verbose("Chat event ending after swear:\n Canceled %s".formatted(e.isCancelled()));
    }

    private static void handle(Player p, String permission, boolean isEnabled, String eventType, AsyncChatEvent e, Consumer<AsyncChatEvent> handler) {
        ServerUtils.verbose("Handeling a chat filter:\n Canceled %s\nType: %s".formatted(e.isCancelled(),eventType));
        if (e.isCancelled()) return;
        if (p.hasPermission(permission)) return;
        ServerUtils.verbose("ChatEvent: Permission bypass failed, checking for " + eventType);
        if (!isEnabled) return;
        ServerUtils.verbose("ChatEvent: " + eventType + " check enabled, continuing!");
        handler.accept(e);
    }
}
