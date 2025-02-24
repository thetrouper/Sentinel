package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Set;

public class CommandExecute extends AbstractViolation {

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;
        String label = e.getMessage().substring(1).split(" ")[0];
        String args = e.getMessage();

        Set<String> status = getCommandStatus(label);

        if (status.contains("SPECIFIC") && Sentinel.violationConfig.commandExecute.specific.enabled) {
            e.setCancelled(true);
            ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                    .setEvent(e)
                    .setPlayer(p)
                    .cancel(true)
                    .punish(Sentinel.violationConfig.commandExecute.specific.punish)
                    .setPunishmentCommands(Sentinel.violationConfig.commandExecute.specific.punishmentCommands)
                    .logToDiscord(Sentinel.violationConfig.commandExecute.specific.logToDiscord);

            runActions(
                    Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.run, Sentinel.lang.violations.protections.rootName.specificCommand),
                    Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.run, Sentinel.lang.violations.protections.rootName.specificCommand),
                    generateCommandInfo(args, p),
                    config
            );
            return;
        }

        if (status.contains("DANGEROUS") && Sentinel.violationConfig.commandExecute.dangerous.enabled) {
            e.setCancelled(true);
            ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                    .setEvent(e)
                    .setPlayer(p)
                    .deop(Sentinel.violationConfig.commandExecute.dangerous.deop)
                    .cancel(true)
                    .punish(Sentinel.violationConfig.commandExecute.dangerous.punish)
                    .setPunishmentCommands(Sentinel.violationConfig.commandExecute.dangerous.punishmentCommands)
                    .logToDiscord(Sentinel.violationConfig.commandExecute.dangerous.logToDiscord);

            runActions(
                    Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.run, Sentinel.lang.violations.protections.rootName.dangerousCommand),
                    Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.run, Sentinel.lang.violations.protections.rootName.dangerousCommand),
                    generateCommandInfo(args, p),
                    config
            );
            return;
        }

        if (status.contains("LOGGED") && Sentinel.violationConfig.commandExecute.logged.enabled) {
            ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                    .setPlayer(p)
                    .logToDiscord(Sentinel.violationConfig.commandExecute.logged.logToDiscord);

            runActions(
                    Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.run, Sentinel.lang.violations.protections.rootName.loggedCommand),
                    Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.run, Sentinel.lang.violations.protections.rootName.loggedCommand),
                    generateCommandInfo(args, p),
                    config
            );
            return;
        }
    }

    public static Set<String> getCommandStatus(String label) {
        Set<String> commandTypes = new HashSet<>();

        if (label.startsWith("/")) {
            label = label.substring(1);
        }

        if (label.contains(":")) {
            commandTypes.add("SPECIFIC");
        }

        for (String loggedCommand : Sentinel.violationConfig.commandExecute.logged.commands) {
            if (loggedCommand.equals(label)) commandTypes.add("LOGGED");
        }

        for (String dangerousCommand : Sentinel.violationConfig.commandExecute.dangerous.commands) {
            if (dangerousCommand.equals(label)) commandTypes.add("DANGEROUS");
        }

        return commandTypes;
    }
}