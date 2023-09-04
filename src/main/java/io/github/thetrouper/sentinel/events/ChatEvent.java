package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.AntiUnicode;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    @EventHandler
    public static void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        if (!Sentinel.isTrusted(e.getPlayer()) || !e.getPlayer().hasPermission("sentinel.chat.antiunicode.bypass")) {
            if (Config.antiUnicode) {
                AntiUnicode.handleAntiUnicode(e);
                return;
            }
        }
        if (!Sentinel.isTrusted(e.getPlayer()) || !e.getPlayer().hasPermission("sentinel.chat.antiswear.bypass")) {
            if (Config.antiSwearEnabled) {
                ProfanityFilter.handleProfanityFilter(e);
                return;
            }
        }
        if (!Sentinel.isTrusted(e.getPlayer()) || !e.getPlayer().hasPermission("sentinel.chat.antispam.bypass")) {
            if (Config.antiSpamEnabled) {
                AntiSpam.handleAntiSpam(e);
                return;
            }
        }
    }
}
