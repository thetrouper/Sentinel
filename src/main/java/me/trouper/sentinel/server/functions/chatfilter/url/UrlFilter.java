package me.trouper.sentinel.server.functions.chatfilter.url;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.utils.ServerUtils;

public class UrlFilter {
    public static void handleUrlFilter(AsyncChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Url Filter Opening: Event is canceled.");
        }

        UrlResponse response = UrlResponse.generate(e);

        if (response.isPunished() || response.isBlocked()) new UrlAction().run(response);
    }
}
