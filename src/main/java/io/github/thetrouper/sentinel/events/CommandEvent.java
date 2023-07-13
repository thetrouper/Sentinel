package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.TakeAction;
import io.github.thetrouper.sentinel.server.util.Notifications.NotifyConsole;
import io.github.thetrouper.sentinel.server.util.Notifications.NotifyDiscord;
import io.github.thetrouper.sentinel.server.util.Notifications.NotifyTrusted;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
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
        ServerUtils.sendDebugMessage(TextUtils.prefix("Checking command"));
        if (Sentinel.isDangerousCommand(command)) {
            ServerUtils.sendDebugMessage(TextUtils.prefix( "Command is dangerous"));
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                ServerUtils.sendDebugMessage(TextUtils.prefix("Command is canceled"));
                TakeAction.command(e);
            }
        }
        if (Config.blockSpecificCommands) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Checking command for specific"));
            if (command.contains(":")) {
                ServerUtils.sendDebugMessage(TextUtils.prefix("Checking is specific"));
                if (!Sentinel.isTrusted(p)) {
                    e.setCancelled(true);
                    ServerUtils.sendDebugMessage(TextUtils.prefix("Command is canceled"));
                    TakeAction.specific(e);
                }
            }
        }
        if (Sentinel.isLoggedCommand(command)) {
            NotifyConsole.command(p,command,false,false,false,true);
            NotifyDiscord.command(p,command,false,false,false,true);
            NotifyTrusted.command(p,command,false,false,false,true);
        }
    }
}