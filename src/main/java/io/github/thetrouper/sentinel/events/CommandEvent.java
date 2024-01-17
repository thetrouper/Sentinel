package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.config.MainConfig;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEvent implements CustomListener {
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
                        .setDeoped(MainConfig.Plugin.deop)
                        .setPunished(MainConfig.Plugin.commandPunish)
                        .setnotifyDiscord(MainConfig.Plugin.logDangerous)
                        .setNotifyConsole(true)
                        .setNotifyTrusted(true)
                        .execute();
            }
        }
        if (MainConfig.Plugin.blockSpecific) {
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
                            .setDeoped(MainConfig.Plugin.deop)
                            .setPunished(MainConfig.Plugin.specificPunish)
                            .setnotifyDiscord(MainConfig.Plugin.logSpecific)
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