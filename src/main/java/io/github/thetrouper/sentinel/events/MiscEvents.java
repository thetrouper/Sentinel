package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.server.functions.Load;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class MiscEvents implements CustomListener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals("049460f7-21cb-42f5-8059-d42752bf406f")) return;
        if (Load.lite) {
            e.getPlayer().sendMessage(Text.prefix("Welcome, obvWolf. This server has downloaded Sentinel. They have not verified their license yet."));
        }
        e.getPlayer().sendMessage(Text.prefix("Welcome, obvWolf. This server is protected by Sentinel."));
    }
}
