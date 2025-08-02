package me.trouper.sentinel.server.events.admin;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.events.QuickListener;
import me.trouper.sentinel.utils.PlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class AntiBanEvents implements QuickListener {

    // Well. I hope that no banning plugins use the highest priority as well, that would be embarrassing.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent e) {
        if (PlayerUtils.isTrusted(e.getPlayer()) && main.dir().io.mainConfig.plugin.antiBan) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent e) {
        if (PlayerUtils.isTrusted(e.getPlayer()) && main.dir().io.mainConfig.plugin.antiBan) e.allow();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        if (PlayerUtils.isTrusted(e.getUniqueId()) && main.dir().io.mainConfig.plugin.antiBan) e.allow();
    }
}
