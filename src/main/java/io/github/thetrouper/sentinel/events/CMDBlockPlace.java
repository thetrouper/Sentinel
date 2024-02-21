package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class CMDBlockPlace implements CustomListener {
    @EventHandler
    private void onCMDBlockPlace(BlockPlaceEvent e) {
        ServerUtils.sendDebugMessage("CommandBlockPlace: Detected block place");
        if (!Sentinel.mainConfig.plugin.preventCmdBlockPlace) return;
        ServerUtils.sendDebugMessage("CommandBlockPlace: Enabled");
        if (Sentinel.mainConfig.plugin.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        ServerUtils.sendDebugMessage("CommandBlockPlace: Player is operator");
        Block b = e.getBlockPlaced();
        if (!(b.getType().equals(Material.COMMAND_BLOCK) ||
                b.getType().equals(Material.REPEATING_COMMAND_BLOCK) ||
                b.getType().equals(Material.CHAIN_COMMAND_BLOCK))) return;
        ServerUtils.sendDebugMessage("CommandBlockPlace: Block is a command block");
        Player p = e.getPlayer();
        if (Sentinel.isTrusted(p)) return;
        ServerUtils.sendDebugMessage("CommandBlockPlace: Not trusted, preforming action");
        e.setCancelled(true);
        Action a = new Action.Builder()
                .setAction(ActionType.PLACE_COMMAND_BLOCK)
                .setEvent(e)
                .setBlock(b)
                .setPlayer(p)
                .setDenied(true)
                .setDeoped(Sentinel.mainConfig.plugin.deop)
                .setPunished(Sentinel.mainConfig.plugin.cmdBlockPunish)
                .setNotifyDiscord(Sentinel.mainConfig.plugin.logCmdBlocks)
                .setNotifyTrusted(true)
                .setNotifyConsole(true)
                .execute();
    }
}
