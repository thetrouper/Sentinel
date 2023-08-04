package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.ActionType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CMDMinecartUse implements Listener {
    @EventHandler
    private void onCMDBlockMinecartUse(PlayerInteractEntityEvent e) {
        if (!Config.preventCmdCartUse) return;
        if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        if (e.getRightClicked().getType() == EntityType.MINECART_COMMAND) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                Action a = new Action.Builder()
                        .setAction(ActionType.USE_MINECART_COMMAND)
                        .setEvent(e)
                        .setPlayer(p)
                        .setDenied(true)
                        .setPunished(Config.cmdBlockPunish)
                        .setnotifyDiscord(Config.logCmdBlocks)
                        .setNotifyTrusted(true)
                        .setNotifyConsole(true)
                        .execute();
            }
        }
    }
}
