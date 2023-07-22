package io.github.thetrouper.sentinel.events;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class CMDBlockExecute implements Listener {

    @EventHandler
    private void onCMDBlockTick(ServerCommandEvent e) {
        if (e.getSender() instanceof BlockCommandSender) {

        }
    }
}
