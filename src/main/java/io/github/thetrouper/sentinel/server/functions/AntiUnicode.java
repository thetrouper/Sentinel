package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiUnicode {
    public static void handleAntiUnicode(AsyncPlayerChatEvent e) {
        String message = Text.removeFirstColor(e.getMessage());
        String nonAllowed = message.replaceAll("[A-Za-z0-9\\[,./?><|\\]()*&^%$#@!~`{}:;'\"-_]", "").trim();
        if (nonAllowed.length() != 0) {
            e.getPlayer().sendMessage(Text.prefix(Sentinel.dict.get("unicode-warn")));
            e.setCancelled(true);
        }
    }
}
