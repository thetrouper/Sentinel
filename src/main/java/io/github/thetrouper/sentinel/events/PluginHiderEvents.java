package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class PluginHiderEvents implements CustomListener {

    private final String[] aliases = TabCompleteEvent.VERSION_ALIASES;
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (Sentinel.isTrusted(p)) return;

        String message = e.getMessage();

        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        for (String alias : aliases) {
            if (!message.startsWith(alias)) continue;
            e.setCancelled(true);
            p.sendMessage(Text.color(Sentinel.language.get("no-plugins-for-u")));
        }
    }
}
