package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.trees.Node;
import me.trouper.sentinel.utils.FileUtils;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.server.functions.ViolationController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Set;

public class CommandExecuteEvent implements CustomListener {
    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;
        String label = e.getMessage().substring(1).split(" ")[0];
        String args = e.getMessage();

        Set<String> status = getCommandStatus(label);

        if (status.contains("SPECIFIC") && Sentinel.violationConfig.commandExecute.specific.enabled) {
            e.setCancelled(true);
            Node log = getLog(p, args, "specific");
            ViolationController.handleViolation(
                    Sentinel.lang.violations.commandExecute.specificCommandViolation.formatted(p.getName()),
                    Sentinel.violationConfig.commandExecute.specific.punish,
                    false,
                    Sentinel.violationConfig.commandExecute.specific.logToDiscord,
                    p,
                    Sentinel.violationConfig.commandExecute.specific.punishmentCommands,
                    log
            );
            return;
        }

        if (status.contains("DANGEROUS") && Sentinel.violationConfig.commandExecute.dangerous.enabled) {
            e.setCancelled(true);
            Node log = getLog(p, args, "dangerous");
            ViolationController.handleViolation(
                    Sentinel.lang.violations.commandExecute.dangerousCommandViolation.formatted(p.getName()),
                    Sentinel.violationConfig.commandExecute.dangerous.punish,
                    Sentinel.violationConfig.commandExecute.dangerous.deop,
                    Sentinel.violationConfig.commandExecute.dangerous.logToDiscord,
                    p,
                    Sentinel.violationConfig.commandExecute.dangerous.punishmentCommands,
                    log
            );
            return;
        }

        if (status.contains("LOGGED") && Sentinel.violationConfig.commandExecute.logged.enabled) {
            Node log = getLog(p, args, "logged");
            ViolationController.handleViolation(
                    Sentinel.lang.violations.commandExecute.loggedCommandViolation.formatted(p.getName()),
                    false,
                    false,
                    Sentinel.violationConfig.commandExecute.logged.logToDiscord,
                    p,
                    null,
                    log
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

    private Node getLog(Player p, String command, String status) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.commandExecute.specificCommandDetection.formatted(status));

        Node playerInfo = new Node(Sentinel.lang.violations.commandExecute.playerInfoTitle.formatted(p.getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.commandExecute.uuid, p.getUniqueId().toString());
        playerInfo.addField(Sentinel.lang.violations.commandExecute.location, Sentinel.lang.violations.commandExecute.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));
        root.addChild(playerInfo);

        Node violationInfo = new Node(Sentinel.lang.violations.commandExecute.violationInfoTitle);
        if (command.length() <= 128) {
            violationInfo.addField(Sentinel.lang.violations.commandExecute.commandField, command);
        } else {
            violationInfo.addKeyValue(Sentinel.lang.violations.commandExecute.commandUploadedTo, FileUtils.createCommandLog(command));
        }
        root.addChild(violationInfo);

        return root;
    }



}
