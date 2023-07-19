package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiUnicode {
    public static void handleAntiUnicode(AsyncPlayerChatEvent e) {
        String message = TextUtils.removeFirstColor(e.getMessage());
        String nonAllowed = message.replaceAll("[A-Za-z0-9\\[,./?><|\\]()*&^%$#@!~`{}:;'\"-_]", "").trim();
        if (nonAllowed.length() != 0) {
            e.getPlayer().sendMessage(TextUtils.prefix("§cDo not send non standard unicode in chat!"));
            e.setCancelled(true);
        }
    }
}
