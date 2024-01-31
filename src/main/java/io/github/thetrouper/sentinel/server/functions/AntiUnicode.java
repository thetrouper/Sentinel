package io.github.thetrouper.sentinel.server.functions;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiUnicode {
    public static void handleAntiUnicode(AsyncPlayerChatEvent e) {
        String message = Text.removeFirstColor(e.getMessage());
        String nonAllowed = message.replaceAll("[A-Za-z0-9\\[,./?><|\\]§()*&^%$#@!~`{}:;'\"-_]", "").trim();
        if (message.matches("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")) {

        }
        if (nonAllowed.length() != 0) {
            e.getPlayer().sendMessage(Text.prefix(Sentinel.language.get("unicode-warn")));
            e.setCancelled(true);
        }
    }
}
