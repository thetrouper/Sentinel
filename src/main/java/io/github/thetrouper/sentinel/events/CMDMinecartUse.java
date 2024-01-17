package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CMDMinecartUse implements CustomListener {
    @EventHandler
    private void onCMDBlockMinecartUse(PlayerInteractEntityEvent e) {
        ServerUtils.sendDebugMessage("MinecartCommandUse: Detected Interaction with entity");
        if (!Sentinel.mainConfig.plugin.preventCmdCartUse) return;
        ServerUtils.sendDebugMessage("MinecartCommandUse: Enabled");
        if (Sentinel.mainConfig.plugin.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        ServerUtils.sendDebugMessage("MinecartCommandUse: Player op");
        if (e.getRightClicked().getType() == EntityType.MINECART_COMMAND) {
            ServerUtils.sendDebugMessage("MinecartCommandUse: Entity is minecart command");
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                ServerUtils.sendDebugMessage("MinecartCommandUse: Not trusted, preforming action");
                e.setCancelled(true);
                Action a = new Action.Builder()
                        .setAction(ActionType.USE_MINECART_COMMAND)
                        .setEvent(e)
                        .setPlayer(p)
                        .setDenied(true)
                        .setPunished(Sentinel.mainConfig.plugin.cmdBlockPunish)
                        .setnotifyDiscord(Sentinel.mainConfig.plugin.logCmdBlocks)
                        .setNotifyTrusted(true)
                        .setNotifyConsole(true)
                        .execute();
            }
        }
    }
}
