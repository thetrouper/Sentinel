package me.trouper.sentinel.server.functions;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class ViolationController {
    

    public static void handleViolation(String message, boolean punish, boolean deopUser, boolean logToDiscord, Player perp, List<String> punishCommands, Node tree) {
        Node actions = new Node(Sentinel.lang.violations.violationMessages.actions);
        actions.addTextLine(Sentinel.lang.violations.violationMessages.eventCancelled);

        if (punish) {
            for (String punishCommand : punishCommands) {
                ServerUtils.sendCommand(punishCommand.replaceAll("%player%", perp.getName()));
            }
            actions.addTextLine(Sentinel.lang.violations.violationMessages.punishmentCommandsExecuted);
        }

        if (deopUser) {
            perp.setOp(false);
            actions.addTextLine(Sentinel.lang.violations.violationMessages.userOpStripped);
        }

        if (logToDiscord) actions.addTextLine(Sentinel.lang.violations.violationMessages.loggedToDiscord);
        tree.addChild(actions);

        if (logToDiscord) {
            EmbedFormatter.sendEmbed(EmbedFormatter.format(tree));
        }

        ServerUtils.forEachPlayer(trusted -> {
            if (PlayerUtils.isTrusted(trusted)) {
                trusted.sendMessage(Component.text(Text.prefix(message)).hoverEvent(Component.text(HoverFormatter.format(tree)).asHoverEvent()));
            }
        });

        Sentinel.log.info(ConsoleFormatter.format(tree));
    }
}
