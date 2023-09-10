package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.functions.AntiSpam;
import io.github.thetrouper.sentinel.server.functions.AntiUnicode;
import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    @EventHandler
    public static void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        ServerUtils.sendDebugMessage(TextUtils.prefix("Chat event detected!"));
        if (!Sentinel.isTrusted(e.getPlayer()) || !e.getPlayer().hasPermission("sentinel.chat.antiunicode.bypass")) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Permission bypass failed, checking for unicode"));
            if (Config.antiUnicode) {
                ServerUtils.sendDebugMessage(TextUtils.prefix("Enabled, Continuing unicode check!"));
                AntiUnicode.handleAntiUnicode(e);
            }
        }
        if (!Sentinel.isTrusted(e.getPlayer()) || !e.getPlayer().hasPermission("sentinel.chat.antiswear.bypass")) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Permission bypass failed, checking for swears"));
            if (Config.antiSwearEnabled) {
                ServerUtils.sendDebugMessage(TextUtils.prefix("Enabled, Continuing swear check!"));
                ProfanityFilter.handleProfanityFilter(e);
            }
        }
        if (!Sentinel.isTrusted(e.getPlayer()) || !e.getPlayer().hasPermission("sentinel.chat.antispam.bypass")) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Permission bypass failed, checking for spam"));
            if (Config.antiSpamEnabled) {
                ServerUtils.sendDebugMessage(TextUtils.prefix("Enabled, Continuing spam check!"));
                AntiSpam.handleAntiSpam(e);
            }
        }
    }
}
