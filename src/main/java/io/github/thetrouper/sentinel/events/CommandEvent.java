package io.github.thetrouper.sentinel.events;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEvent implements CustomListener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (Sentinel.isTrusted(p)) return;
        String command = e.getMessage().substring(1).split(" ")[0];
        String fullcommand = e.getMessage();
        ServerUtils.sendDebugMessage("CommandEvent: Checking command");
        if (Sentinel.isDangerousCommand(fullcommand)) {
            ServerUtils.sendDebugMessage("CommandEvent: Command is dangerous");
            e.setCancelled(true);
            ServerUtils.sendDebugMessage("CommandEvent: Command is canceled");
            Action a = new Action.Builder()
                    .setAction(ActionType.DANGEROUS_COMMAND)
                    .setEvent(e)
                    .setPlayer(p)
                    .setCommand(fullcommand)
                    .setDenied(true)
                    .setDeoped(Sentinel.mainConfig.plugin.deop)
                    .setPunished(Sentinel.mainConfig.plugin.commandPunish)
                    .setnotifyDiscord(Sentinel.mainConfig.plugin.logDangerous)
                    .setNotifyConsole(true)
                    .setNotifyTrusted(true)
                    .execute();
        }
        if (Sentinel.mainConfig.plugin.blockSpecific) {
            ServerUtils.sendDebugMessage("CommandEvent: Checking command for specific");
            if (command.contains(":")) {
                ServerUtils.sendDebugMessage("CommandEvent: Failed check");
                e.setCancelled(true);
                ServerUtils.sendDebugMessage(("CommandEvent: Not trusted, preforming action"));
                Action a = new Action.Builder()
                        .setAction(ActionType.SPECIFIC_COMMAND)
                        .setEvent(e)
                        .setPlayer(p)
                        .setCommand(fullcommand)
                        .setDenied(true)
                        .setDeoped(Sentinel.mainConfig.plugin.deop)
                        .setPunished(Sentinel.mainConfig.plugin.specificPunish)
                        .setnotifyDiscord(Sentinel.mainConfig.plugin.logSpecific)
                        .setNotifyConsole(true)
                        .setNotifyTrusted(true)
                        .execute();

            }
        }
        if (Sentinel.isLoggedCommand(fullcommand)) {
            ServerUtils.sendDebugMessage("CommandEvent: Is logged command, logging");
            Action a = new Action.Builder()
                    .setAction(ActionType.LOGGED_COMMAND)
                    .setEvent(e)
                    .setPlayer(p)
                    .setCommand(fullcommand)
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