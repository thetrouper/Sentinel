package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.utils.SchedulerUtils;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.regex.AntiRegex;
import me.trouper.sentinel.server.functions.chatfilter.profanity.AntiProfanity;
import me.trouper.sentinel.server.functions.chatfilter.spam.AntiSpam;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.chat.ProfanityFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.RegexFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.SpamFilterGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.*;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.DangerousCMDGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.LoggedCMDGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.SpecificCMDGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.Consumer;

public class ChatEvent implements CustomListener {

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        ServerUtils.verbose("Chat event sanity check:\n Canceled %s".formatted(e.isCancelled()));
        handleEvent(e);
    }

    public static void handleEvent(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        if (PlayerUtils.isTrusted(e.getPlayer().getUniqueId().toString())) {
            if (MainGUI.awaitingCallback.contains(e.getPlayer().getUniqueId())) {
                ServerUtils.verbose("Attempting to cancel events for callback!");
                e.setCancelled(true);
                MainGUI.awaitingCallback.remove(e.getPlayer().getUniqueId());
            }

            ServerUtils.verbose("Handling Chat Event for callbacks");
            SchedulerUtils.later(0,()->{
                RegexFilterGUI.updater.invokeCallbacks(e);
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
            return;
        }

        Player p = e.getPlayer();

        ServerUtils.verbose("Chat event start after trust check:\n Canceled %s".formatted(e.isCancelled()));

        handle(p,
                "sentinel.chat.regex.bypass",
                Sentinel.mainConfig.chat.useAntiUnicode, "unicode",
                e,
                AntiRegex::handleRegex);

        ServerUtils.verbose("Chat event middle after regex:\n Canceled %s".formatted(e.isCancelled()));

        handle(p,
                "sentinel.chat.spam.bypass",
                Sentinel.mainConfig.chat.spamFilter.enabled,
                "spam",
                e,
                AntiSpam::handleAntiSpam);

        ServerUtils.verbose("Chat event middle after spam:\n Canceled %s".formatted(e.isCancelled()));

        handle(p,
                "sentinel.chat.swear.bypass",
                Sentinel.mainConfig.chat.swearFilter.enabled,
                "swear",
                e,
                AntiProfanity::handleProfanityFilter);

        ServerUtils.verbose("Chat event ending after swear:\n Canceled %s".formatted(e.isCancelled()));
    }

    private static void handle(Player p, String permission, boolean isEnabled, String eventType, AsyncPlayerChatEvent e, Consumer<AsyncPlayerChatEvent> handler) {
        ServerUtils.verbose("Handeling a chat filter:\n Canceled %s\nType: %s".formatted(e.isCancelled(),eventType));
        if (e.isCancelled()) return;
        if (p.hasPermission(permission)) return;
        ServerUtils.verbose("ChatEvent: Permission bypass failed, checking for " + eventType);
        if (!isEnabled) return;
        ServerUtils.verbose("ChatEvent: " + eventType + " check enabled, continuing!");
        handler.accept(e);
    }
}
