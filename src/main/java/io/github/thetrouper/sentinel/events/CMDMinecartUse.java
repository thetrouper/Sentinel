package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.TakeAction;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CMDMinecartUse implements Listener {
    @EventHandler
    private void onCMDBlockMinecartUse(PlayerInteractEntityEvent e) {
        if (!Config.preventCmdBlocks) return;
        if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        if (e.getRightClicked().getType() == EntityType.MINECART_COMMAND) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                TakeAction.useEntity(e);
            }
        }
    }
}
