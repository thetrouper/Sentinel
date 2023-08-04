package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.commands.SentinelCommand;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.ActionType;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class CMDBlockUse implements Listener {
    @EventHandler
    private void onCMDBlockUse(PlayerInteractEvent e) {
        if (!Config.preventCmdBlockUse) return;
        if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        if (e.getClickedBlock() == null) return;
        Block b = e.getClickedBlock();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK) {
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                Action a = new Action.Builder()
                        .setAction(ActionType.USE_COMMAND_BLOCK)
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
    @EventHandler
    private void onCMDBlockChange(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!Config.preventCmdBlockUse) return;
        if (Config.cmdBlockOpCheck && !p.isOp()) return;
        Block b = e.getBlock();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK) {
            BlockState state = b.getState();
            CommandBlock cb = (CommandBlock) state;
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                Action a = new Action.Builder()
                        .setAction(ActionType.UPDATE_COMMAND_BLOCK)
                        .setEvent(e)
                        .setBlock(b)
                        .setCommand(cb.getCommand())
                        .setPlayer(p)
                        .setDenied(true)
                        .setPunished(Config.cmdBlockPunish)
                        .setnotifyDiscord(Config.logCmdBlocks)
                        .setNotifyTrusted(true)
                        .setNotifyConsole(true)
                        .execute();
            }
            if (Sentinel.isTrusted(p)) {
                Sentinel.log.info(p.getName() + "Updated command block: " + cb.getCommand());
                SentinelCommand.updateWhitelistedCommandBlock(p,b, cb.getCommand());
            }
        }
    }
}
