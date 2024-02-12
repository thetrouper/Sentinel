package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.functions.CMDBlockWhitelist;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

public class CMDBlockExecute implements CustomListener {

    @EventHandler
    private void onCommandBlock(ServerCommandEvent e) {
        if (!(e.getSender() instanceof BlockCommandSender s)) return;
        Block cmdBlock = s.getBlock();
        if (CMDBlockWhitelist.canRun(cmdBlock)) return;
        Action a = new Action.Builder()
                .setEvent(e)
                .setAction(ActionType.COMMAND_BLOCK_EXECUTE)
                .setBlock(cmdBlock)
                .setDenied(true)
                .setNotifyDiscord(Sentinel.mainConfig.plugin.logUnauthorizedCmdBlocks)
                .setNotifyTrusted(true)
                .setNotifyConsole(true)
                .setDenied(true)
                .setLoggedCommand(e.getCommand())
                .execute();
    }
}
