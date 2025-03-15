package me.trouper.sentinel.server.functions.chatfilter.unicode;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.AbstractActionHandler;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class UnicodeAction extends AbstractActionHandler<UnicodeResponse> {
    @Override
    protected void punish(UnicodeResponse response) {
        for (String punishCommand : Sentinel.getInstance().getDirector().io.mainConfig.chat.unicodeFilter.punishCommands) {
            ServerUtils.sendCommand(punishCommand.replaceAll("%player%",response.getPlayer().getName()));
        }
    }

    @Override
    protected void staffWarning(UnicodeResponse response, Node tree) {
        String messageText = Text.prefix("&b&n%s&r &7%s".formatted(
                response.getPlayer().getName(),
                response.isPunished() ? Sentinel.getInstance().getDirector().io.lang.violations.chat.unicode.autoPunishNotification : Sentinel.getInstance().getDirector().io.lang.violations.chat.unicode.preventNotification
        ));
        String hoverText = HoverFormatter.format(tree);

        PlayerUtils.forEachPlayer(player -> {
            if (player.hasPermission("sentinel.chatfilter.unicode.view")) player.sendMessage(Component.text(messageText).hoverEvent(Component.text(hoverText).asHoverEvent()));
        });
    }

    @Override
    protected void playerWarning(UnicodeResponse response) {
        String message = Text.prefix(response.isPunished() ? Sentinel.getInstance().getDirector().io.lang.violations.chat.unicode.autoPunishWarning : Sentinel.getInstance().getDirector().io.lang.violations.chat.unicode.preventWarning);
        String hoverText = Sentinel.getInstance().getDirector().io.lang.automatedActions.reportable;
        String command = "/sentinelcallback fpreport %s".formatted(response.getReport().getId());
        response.getPlayer().sendMessage(Component.text(message)
                .hoverEvent(Component.text(hoverText).asHoverEvent())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,command)));
    }

    @Override
    protected Node buildTree(UnicodeResponse response) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.chat.unicode.treeTitle);

        Node playerInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.playerInfo.formatted(response.getPlayer().getName()));
        playerInfo.addKeyValue(Sentinel.getInstance().getDirector().io.lang.violations.protections.infoNode.uuid, response.getPlayer().getUniqueId().toString());
        root.addChild(playerInfo);

        Node reportInfo = new Node(Sentinel.getInstance().getDirector().io.lang.violations.chat.unicode.reportInfoTitle);
        reportInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.chat.originalMessage, response.getOriginalMessage());
        reportInfo.addField(Sentinel.getInstance().getDirector().io.lang.violations.chat.highlightedMessage, response.getHighlightedMessage());
        root.addChild(reportInfo);

        Node actions = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.actionNodeTitle);
        actions.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.chat.denyMessage);
        if (response.isPunished()) actions.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.punishmentCommandsExecuted);
        root.addChild(actions);

        return root;
    }

    @Override
    protected boolean shouldWarnPlayer(UnicodeResponse response) {
        return !Sentinel.getInstance().getDirector().io.mainConfig.chat.unicodeFilter.silent;
    }
}
