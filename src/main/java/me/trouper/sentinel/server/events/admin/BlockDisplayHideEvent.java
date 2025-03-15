package me.trouper.sentinel.server.events.admin;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class BlockDisplayHideEvent implements CustomListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof BlockDisplay && entity.getScoreboardTags().contains("./Sentinel/ Block Display")) {
                player.hideEntity(Sentinel.getInstance(), entity);
            }
        }
    }
}
