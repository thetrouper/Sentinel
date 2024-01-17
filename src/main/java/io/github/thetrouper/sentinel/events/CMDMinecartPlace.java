package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class CMDMinecartPlace implements CustomListener {

    @EventHandler
    private void onCMDMinecartPlace(PlayerInteractEvent e) {
        ServerUtils.sendDebugMessage("MinecartCommandPlace: Detected interaction");
        if (Sentinel.mainConfig.plugin.preventCmdCartPlace) {
            ServerUtils.sendDebugMessage("MinecartCommandPlace: Enabled");
            if (Sentinel.mainConfig.plugin.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
            ServerUtils.sendDebugMessage("MinecartCommandPlace: Player is op");
            if (e.getItem() == null) return;
            ServerUtils.sendDebugMessage("MinecartCommandPlace: Item isn't null");
            if (e.getClickedBlock() == null) return;
            ServerUtils.sendDebugMessage("MinecartCommandPlace: Clicked block isn't null");
            if (!e.getItem().getType().equals(Material.COMMAND_BLOCK_MINECART)) return;
            ServerUtils.sendDebugMessage("MinecartCommandPlace: Item is a minecart command");
            if (e.getClickedBlock().getType() == Material.RAIL || e.getClickedBlock().getType() == Material.POWERED_RAIL || e.getClickedBlock().getType() == Material.ACTIVATOR_RAIL || e.getClickedBlock().getType() == Material.DETECTOR_RAIL) {
                ServerUtils.sendDebugMessage("MinecartCommandPlace: Clicked block is a rail");
                Player p = e.getPlayer();
                if (!Sentinel.isTrusted(p)) {
                    ServerUtils.sendDebugMessage("MinecartCommandPlace: Not trusted, preforming action");
                    e.setCancelled(true);
                    p.getInventory().remove(Material.COMMAND_BLOCK_MINECART);
                    Action a = new Action.Builder()
                            .setAction(ActionType.PLACE_MINECART_COMMAND)
                            .setEvent(e)
                            .setPlayer(p)
                            .setBlock(e.getClickedBlock())
                            .setDenied(Sentinel.mainConfig.plugin.preventCmdCartPlace)
                            .setPunished(Sentinel.mainConfig.plugin.cmdBlockPunish)
                            .setDeoped(Sentinel.mainConfig.plugin.deop)
                            .setNotifyConsole(true)
                            .setNotifyTrusted(true)
                            .setnotifyDiscord(Sentinel.mainConfig.plugin.logCmdBlocks)
                            .execute();
                }
            }
        }
    }
}
