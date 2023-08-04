package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.ActionType;
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
                Action a = new Action.Builder()
                        .setAction(ActionType.DANGEROUS_COMMAND)
                        .setEvent(e)
                        .setPlayer(p)
                        .setCommand(command)
                        .setDenied(true)
                        .setDeoped(Config.deop)
                        .setPunished(Config.commandPunish)
                        .setnotifyDiscord(Config.logDangerous)
                        .setNotifyConsole(true)
                        .setNotifyTrusted(true)
                        .execute();
            }
        }
        if (Config.blockSpecific) {
            ServerUtils.sendDebugMessage(TextUtils.prefix("Checking command for specific"));
            if (command.contains(":")) {
                ServerUtils.sendDebugMessage(TextUtils.prefix("Checking is specific"));
                if (!Sentinel.isTrusted(p)) {
                    e.setCancelled(true);
                    ServerUtils.sendDebugMessage(TextUtils.prefix("Command is canceled"));
                    Action a = new Action.Builder()
                            .setAction(ActionType.SPECIFIC_COMMAND)
                            .setEvent(e)
                            .setPlayer(p)
                            .setCommand(command)
                            .setDenied(true)
                            .setDeoped(Config.deop)
                            .setPunished(Config.specificPunish)
                            .setnotifyDiscord(Config.logSpecific)
                            .setNotifyConsole(true)
                            .setNotifyTrusted(true)
                            .execute();
                }
            }
        }
        if (Sentinel.isLoggedCommand(command)) {
            Action a = new Action.Builder()
                    .setAction(ActionType.LOGGED_COMMAND)
                    .setEvent(e)
                    .setPlayer(p)
                    .setCommand(command)
                    .setDenied(false)
                    .setDeoped(false)
                    .setPunished(false)
                    .setnotifyDiscord(true)
                    .setNotifyConsole(true)
                    .setNotifyTrusted(true)
                    .execute();
        }
    }
}