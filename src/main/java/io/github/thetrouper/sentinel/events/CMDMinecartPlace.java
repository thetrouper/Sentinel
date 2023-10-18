package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Action;
import io.github.thetrouper.sentinel.data.ActionType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CMDMinecartPlace implements Listener {

    @EventHandler
    private void onCMDMinecartPlace(PlayerInteractEvent e) {
        if (!Config.preventCmdCartPlace) {
            if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
            if (e.getItem() == null) return;
            if (e.getClickedBlock() == null) return;
            if (!e.getItem().getType().equals(Material.COMMAND_BLOCK_MINECART)) return;
            if (e.getClickedBlock().getType() == Material.RAIL || e.getClickedBlock().getType() == Material.POWERED_RAIL || e.getClickedBlock().getType() == Material.ACTIVATOR_RAIL || e.getClickedBlock().getType() == Material.DETECTOR_RAIL) {
                Player p = e.getPlayer();
                if (!Sentinel.isTrusted(p)) {
                    e.setCancelled(true);
                    p.getInventory().remove(Material.COMMAND_BLOCK_MINECART);
                    Action a = new Action.Builder()
                            .setAction(ActionType.PLACE_MINECART_COMMAND)
                            .setEvent(e)
                            .setPlayer(p)
                            .setBlock(e.getClickedBlock())
                            .setDenied(Config.preventCmdCartPlace)
                            .setPunished(Config.cmdBlockPunish)
                            .setDeoped(Config.deop)
                            .setNotifyConsole(true)
                            .setNotifyTrusted(true)
                            .setnotifyDiscord(Config.logCmdBlocks)
                            .execute();
                }
            }
        }
    }
}
