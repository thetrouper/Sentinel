package me.trouper.sentinel.server.functions.chatfilter.unicode;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.utils.ServerUtils;

public class UnicodeFilter {

    public static void handleUnicodeFilter(AsyncChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Unicode Filter Opening: Event is canceled.");
        }

        UnicodeResponse response = UnicodeResponse.generate(e);

        if (response.isPunished() || response.isBlocked()) new UnicodeAction().run(response);
    }
}
