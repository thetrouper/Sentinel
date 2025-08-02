package me.trouper.sentinel.server.events.violations.players;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.events.QuickListener;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockEdit;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockUse;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockUse;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockUse;
import me.trouper.sentinel.server.events.violations.command.DangerousCommand;
import me.trouper.sentinel.server.events.violations.command.LoggedCommand;
import me.trouper.sentinel.server.events.violations.command.SpecificCommand;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartBreak;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartPlace;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartUse;
import me.trouper.sentinel.server.events.violations.whitelist.CommandBlockExecute;
import me.trouper.sentinel.server.events.violations.whitelist.CommandMinecartExecute;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter;
import me.trouper.sentinel.server.functions.chatfilter.unicode.UnicodeFilter;
import me.trouper.sentinel.server.functions.chatfilter.url.UrlFilter;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.chat.ProfanityFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.SpamFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.UnicodeFilterGUI;
import me.trouper.sentinel.server.gui.config.chat.UrlFilterGUI;
import me.trouper.sentinel.server.gui.nbt.NBTGui;
import me.trouper.sentinel.server.gui.whitelist.WhitelistGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.function.Consumer;

public class ChatEvent implements QuickListener {

    @EventHandler
    private void onChat(AsyncChatEvent e) {
        ServerUtils.verbose("Chat event sanity check:\n Canceled %s", e.isCancelled()
);
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
                    WhitelistGUI.updater.invokeCallbacks(e);
                    NBTGui.updater.invokeCallbacks(e);
                    DangerousCommand.updater.invokeCallbacks(e);
                    LoggedCommand.updater.invokeCallbacks(e);
                    SpecificCommand.updater.invokeCallbacks(e);
                    CommandBlockBreak.updater.invokeCallbacks(e);
                    CommandBlockEdit.updater.invokeCallbacks(e);
                    CommandBlockPlace.updater.invokeCallbacks(e);
                    CommandBlockUse.updater.invokeCallbacks(e);
                    JigsawBlockBreak.updater.invokeCallbacks(e);
                    JigsawBlockPlace.updater.invokeCallbacks(e);
                    JigsawBlockUse.updater.invokeCallbacks(e);
                    StructureBlockBreak.updater.invokeCallbacks(e);
                    StructureBlockPlace.updater.invokeCallbacks(e);
                    StructureBlockUse.updater.invokeCallbacks(e);
                    CommandMinecartBreak.updater.invokeCallbacks(e);
                    CommandMinecartPlace.updater.invokeCallbacks(e);
                    CommandMinecartUse.updater.invokeCallbacks(e);
                    CommandBlockExecute.updater.invokeCallbacks(e);
                    CommandMinecartExecute.updater.invokeCallbacks(e);
                    CreativeHotbar.updater.invokeCallbacks(e);
                });
            }
            return;
        }

        Player p = e.getPlayer();

        ServerUtils.verbose("Chat event start after trust check:\n Canceled %s", e.isCancelled()
);

        handle(p,
                "sentinel.chatfilter.unicode.bypass",
                main.dir().io.mainConfig.chat.unicodeFilter.enabled, "unicode",
                e,
                UnicodeFilter::handleUnicodeFilter);

        ServerUtils.verbose("Chat event middle after unicode:\n Canceled %s", e.isCancelled()
);

        handle(p,
                "sentinel.chatfilter.url.bypass",
                main.dir().io.mainConfig.chat.urlFilter.enabled, "url",
                e,
                UrlFilter::handleUrlFilter);

        ServerUtils.verbose("Chat event middle after URL:\n Canceled %s", e.isCancelled()
);

        handle(p,
                "sentinel.chatfilter.spam.bypass",
                main.dir().io.mainConfig.chat.spamFilter.enabled,
                "spam",
                e,
                SpamFilter::handleSpamFilter);

        ServerUtils.verbose("Chat event middle after spam:\n Canceled %s", e.isCancelled()
);

        handle(p,
                "sentinel.chatfilter.profanity.bypass",
                main.dir().io.mainConfig.chat.profanityFilter.enabled,
                "swear",
                e,
                ProfanityFilter::handleProfanityFilter);

        ServerUtils.verbose("Chat event ending after swear:\n Canceled %s", e.isCancelled()
);
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
