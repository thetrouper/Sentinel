package me.trouper.sentinel.server.functions.chatfilter.regex;

import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiRegex {
    public static void handleRegex(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Regex Filter opening Event is canceled.");
        }
        ServerUtils.verbose("Handeling advanced");
        RegexResponse response = RegexResponse.generate(e);
        ServerUtils.verbose("Response got back");
        if (response.getFlagType() == null) return;
        ServerUtils.verbose("Detection happened");
        e.setCancelled(true);
        RegexAction.run(response);
    }
}
