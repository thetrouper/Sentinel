package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Action;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CMDBlockUse implements Listener {
    @EventHandler
    private void onCMDBlockUse(PlayerInteractEvent e) {
        ServerUtils.sendDebugMessage("CommandBlockUse: Detected Interaction");
        if (!Config.preventCmdBlockUse) return;
        ServerUtils.sendDebugMessage("CommandBlockUse: Enabled");
        if (Config.cmdBlockOpCheck && !e.getPlayer().isOp()) return;
        ServerUtils.sendDebugMessage("CommandBlockUse: Player is op");
        if (e.getClickedBlock() == null) return;
        ServerUtils.sendDebugMessage("CommandBlockUse: Block isn't null");
        Block b = e.getClickedBlock();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK) {
            ServerUtils.sendDebugMessage("CommandBlockUse: Block is a command block");
            Player p = e.getPlayer();
            if (!Sentinel.isTrusted(p)) {
                ServerUtils.sendDebugMessage("CommandBlockUse: Not trusted, preforming action");
                e.setCancelled(true);
                Action a = new Action.Builder()
                        .setAction(ActionType.USE_COMMAND_BLOCK)
                        .setEvent(e)
                        .setBlock(b)
                        .setPlayer(p)
                        .setDenied(true)
                        .setPunished(Config.cmdBlockPunish)
                        .setDeoped(Config.deop)
                        .setnotifyDiscord(Config.logCmdBlocks)
                        .setNotifyTrusted(true)
                        .setNotifyConsole(true)
                        .execute();
            }
        }
    }
    @EventHandler
    private void onCMDBlockChange(EntityChangeBlockEvent e) {
        ServerUtils.sendDebugMessage("CommandBlockChange: Detected change block");
        if (!(e.getEntity() instanceof Player p)) return;
        ServerUtils.sendDebugMessage("CommandBlockChange: Changer is a player");
        if (!Config.preventCmdBlockUse) return;
        ServerUtils.sendDebugMessage("CommandBlockChange: Enabled");
        if (Config.cmdBlockOpCheck && !p.isOp()) return;
        ServerUtils.sendDebugMessage("CommandBlockChange: Player is op");
        Block b = e.getBlock();
        if (b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK) {
            ServerUtils.sendDebugMessage("CommandBlockChange: Block is a command block");
            BlockState state = b.getState();
            CommandBlock cb = (CommandBlock) state;
            if (!Sentinel.isTrusted(p)) {
                ServerUtils.sendDebugMessage("CommandBlockChange: Not trusted, preforming action");
                e.setCancelled(true);
                Action a = new Action.Builder()
                        .setAction(ActionType.UPDATE_COMMAND_BLOCK)
                        .setEvent(e)
                        .setBlock(b)
                        .setCommand(cb.getCommand())
                        .setPlayer(p)
                        .setDenied(true)
                        .setPunished(Config.cmdBlockPunish)
                        .setDeoped(Config.deop)
                        .setnotifyDiscord(Config.logCmdBlocks)
                        .setNotifyTrusted(true)
                        .setNotifyConsole(true)
                        .execute();
            }
        }
    }
}
