package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.functions.CMDBlockWhitelist;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

public class CMDBlockExecute implements CustomListener {

    @EventHandler
    private void onCommandBlock(ServerCommandEvent e) {
        ServerUtils.sendDebugMessage("Handling command block event: " + e.getCommand());
        if (!Sentinel.mainConfig.plugin.cmdBlockWhitelist) return;
        ServerUtils.sendDebugMessage("Whitelist not disabled ");
        if (!(e.getSender() instanceof BlockCommandSender s)) return;
        ServerUtils.sendDebugMessage("Sender is command block");
        Block cmdBlock = s.getBlock();
        if (CMDBlockWhitelist.canRun(cmdBlock)) return;
        ServerUtils.sendDebugMessage("Command block cant run.");
        Action a = new Action.Builder()
                .setEvent(e)
                .setAction(ActionType.COMMAND_BLOCK_EXECUTE)
                .setBlock(cmdBlock)
                .setDenied(true)
                .setNotifyDiscord(Sentinel.mainConfig.plugin.logUnauthorizedCmdBlocks)
                .setNotifyTrusted(true)
                .setNotifyConsole(true)
                .setLoggedCommand(e.getCommand())
                .execute();
        if (Sentinel.mainConfig.plugin.deleteUnauthorizedCmdBlocks) cmdBlock.setType(Material.AIR);
    }
}
