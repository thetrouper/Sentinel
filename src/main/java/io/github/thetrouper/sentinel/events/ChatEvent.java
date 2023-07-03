package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {
    @EventHandler
    public static void onChat(AsyncPlayerChatEvent e) {
        AntiSpam.handleAntiSpam(e);
    }
}
