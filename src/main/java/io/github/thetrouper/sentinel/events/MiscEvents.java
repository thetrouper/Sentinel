package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class MiscEvents implements CustomListener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals("049460f7-21cb-42f5-8059-d42752bf406f")) return;
        e.getPlayer().sendMessage(Text.prefix("Welcome, obvWolf. This server uses Sentinel."));
    }
}
