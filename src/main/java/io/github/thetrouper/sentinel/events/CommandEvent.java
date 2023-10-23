package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Action;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
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
        String fullcommand = e.getMessage();
        ServerUtils.sendDebugMessage("CommandEvent: Checking command");
        if (Sentinel.isDangerousCommand(command)) {
            ServerUtils.sendDebugMessage("CommandEvent: Command is dangerous");
            if (!Sentinel.isTrusted(p)) {
                e.setCancelled(true);
                ServerUtils.sendDebugMessage("CommandEvent: Command is canceled");
                Action a = new Action.Builder()
                        .setAction(ActionType.DANGEROUS_COMMAND)
                        .setEvent(e)
                        .setPlayer(p)
                        .setCommand(fullcommand)
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
            ServerUtils.sendDebugMessage("CommandEvent: Checking command for specific");
            if (command.contains(":")) {
                ServerUtils.sendDebugMessage("CommandEvent: Failed check");
                if (!Sentinel.isTrusted(p)) {
                    e.setCancelled(true);
                    ServerUtils.sendDebugMessage(("CommandEvent: Not trusted, preforming action"));
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
            ServerUtils.sendDebugMessage("CommandEvent: Is logged command, logging");
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