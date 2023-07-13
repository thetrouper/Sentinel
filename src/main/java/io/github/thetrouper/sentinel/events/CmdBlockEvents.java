package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.TakeAction;
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
    @EventHandler
    private void onCMDBlockPlace(BlockPlaceEvent e) {
        if (!Config.preventCmdBlocks) return;
        if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        Block b = e.getBlockPlaced();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK ) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                TakeAction.placeBlock(e);
            }
        }
    }
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
    @EventHandler
    private void onCMDBlockMinecartPlace(PlayerInteractEvent e) {
        if (!Config.preventCmdBlocks) {
            if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
            if (e.getItem() == null) return;
            if (e.getClickedBlock() == null) return;
            if (!e.getItem().getType().equals(Material.COMMAND_BLOCK_MINECART)) return;
            if (e.getClickedBlock().getType() == Material.RAIL || e.getClickedBlock().getType() == Material.POWERED_RAIL || e.getClickedBlock().getType() == Material.ACTIVATOR_RAIL || e.getClickedBlock().getType() == Material.DETECTOR_RAIL) {
                Player p = e.getPlayer();
                if (!Sentinel.isTrusted(p)) {
                    e.setCancelled(true);
                    p.getInventory().remove(Material.COMMAND_BLOCK_MINECART);
                    TakeAction.useBlock(e);
                }
            }
        }
    }
}
