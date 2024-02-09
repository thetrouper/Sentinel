package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

public class CMDBlockExecute implements CustomListener {

    @EventHandler
    private void onCommandBlock(ServerCommandEvent e) {
        if (!(e.getSender() instanceof BlockCommandSender s)) return;

    }
}
