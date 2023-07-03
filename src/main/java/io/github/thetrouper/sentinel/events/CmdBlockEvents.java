package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.DeniedActions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CmdBlockEvents implements Listener {
    @EventHandler
    private void onCMDBlockUse(PlayerInteractEvent e) {
        if (!Sentinel.preventCmdBlocks) return;
        if (e.getClickedBlock() == null) return;
        Block b = e.getClickedBlock();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                DeniedActions.handleDeniedAction(p,b);
            }
        }
    }
    @EventHandler
    private void onCMDBlockPlace(BlockPlaceEvent e) {
        if (!Sentinel.preventCmdBlocks) return;
        Block b = e.getBlockPlaced();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK ) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                DeniedActions.handleDeniedAction(p,b);
            }
        }
    }
    @EventHandler
    private void onCMDBlockMinecartUse(PlayerInteractEntityEvent e) {
        if (!Sentinel.preventCmdBlocks) return;
        if (e.getRightClicked().getType() == EntityType.MINECART_COMMAND) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                Block b = p.getLocation().getBlock();
                DeniedActions.handleDeniedAction(p,b);
            }
        }
    }
}
