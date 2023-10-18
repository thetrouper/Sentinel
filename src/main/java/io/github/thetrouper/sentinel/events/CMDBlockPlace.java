package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Action;
import io.github.thetrouper.sentinel.data.ActionType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class CMDBlockPlace implements Listener {
    @EventHandler
    private void onCMDBlockPlace(BlockPlaceEvent e) {
        if (!Config.preventCmdBlockPlace) return;
        if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        Block b = e.getBlockPlaced();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK ) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                Action a = new Action.Builder()
                        .setAction(ActionType.PLACE_COMMAND_BLOCK)
                        .setEvent(e)
                        .setBlock(b)
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
