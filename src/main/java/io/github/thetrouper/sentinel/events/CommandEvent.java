package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.DeniedActions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEvent implements Listener {
    private String trusted;
    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String command = e.getMessage().substring(1).split(" ")[0];
        if (Sentinel.isDangerousCommand(command)) {
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                DeniedActions.handleDeniedAction(p,command);
            }
        }
        if (Sentinel.blockSpecificCommands) {
            if (command.contains(":")) {
                if (!Sentinel.isTrusted(p)) {
                    e.setCancelled(true);
                    DeniedActions.handleDeniedAction(p,command);
                }
            }
        }
    }
}