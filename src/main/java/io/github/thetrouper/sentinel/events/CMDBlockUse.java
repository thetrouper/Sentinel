package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.TakeAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CMDBlockUse implements Listener {
    private void onCMDBlockUse(PlayerInteractEvent e) {
        if (!Config.preventCmdBlocks) return;
        if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        if (e.getClickedBlock() == null) return;
        Block b = e.getClickedBlock();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                TakeAction.useBlock(e);
            }
        }
    }
}
