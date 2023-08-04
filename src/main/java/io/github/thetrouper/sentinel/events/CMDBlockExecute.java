package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.commands.SentinelCommand;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.meta.BlockDataMeta;

public class CMDBlockExecute implements Listener {

    @EventHandler
    private void onCMDBlockTick(ServerCommandEvent e) {
        if (e.getSender() instanceof BlockCommandSender) {
            if (!SentinelCommand.isCommandBlockWhitelisted((CommandBlock) e.getSender())) {
                e.setCancelled(true);
                CommandBlock b = (CommandBlock) e.getSender();
                Sentinel.log.info("A non whitelisted command block just attempted to execute a command! \n Command: "
                        + e.getCommand() + "\n" + "Location: " + b.getLocation().getX() + " " + b.getLocation().getX() + " " + b.getLocation().getZ());
                b.setType(Material.COMMAND_BLOCK);
                b.setCommand(e.getCommand());
            }
        }
    }
}
