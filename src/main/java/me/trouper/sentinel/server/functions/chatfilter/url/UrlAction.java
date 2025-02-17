package me.trouper.sentinel.server.functions.chatfilter.url;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.AbstractActionHandler;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class UrlAction extends AbstractActionHandler<UrlResponse> {
    @Override
    protected void punish(UrlResponse response) {
        for (String punishCommand : Sentinel.mainConfig.chat.urlFilter.punishCommands) {
            ServerUtils.sendCommand(punishCommand.replaceAll("%player%",response.getPlayer().getName()));
        }
    }

    @Override
    protected void staffWarning(UrlResponse response, Node tree) {
        String messageText = Text.prefix("&b&n%s&r &7%s".formatted(
                response.getPlayer().getName(),
                response.isPunished() ? Sentinel.lang.violations.chat.url.autoPunishNotification : Sentinel.lang.violations.chat.url.preventNotification
        ));
        String hoverText = HoverFormatter.format(tree);

        ServerUtils.forEachPlayer(player -> {
            if (player.hasPermission("sentinel.chatfilter.url.view")) player.sendMessage(Component.text(messageText).hoverEvent(Component.text(hoverText).asHoverEvent()));
        });
    }

    @Override
    protected void playerWarning(UrlResponse response) {
        String message = Text.prefix(response.isPunished() ? Sentinel.lang.violations.chat.url.autoPunishWarning : Sentinel.lang.violations.chat.url.preventWarning);
        String hoverText = Sentinel.lang.automatedActions.reportable;
        String command = "/sentinelcallback fpreport %s".formatted(response.getReport().getId());
        response.getPlayer().sendMessage(Component.text(message)
                .hoverEvent(Component.text(hoverText).asHoverEvent())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,command)));
    }

    @Override
    protected Node buildTree(UrlResponse response) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.chat.url.treeTitle);

        Node playerInfo = new Node(Sentinel.lang.violations.chat.url.playerInfoTitle.formatted(response.getPlayer().getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.url.uuid, response.getPlayer().getUniqueId().toString());
        root.addChild(playerInfo);

        Node reportInfo = new Node(Sentinel.lang.violations.chat.url.reportInfoTitle);
        reportInfo.addField(Sentinel.lang.violations.chat.url.originalMessage, response.getOriginalMessage());
        reportInfo.addField(Sentinel.lang.violations.chat.url.highlightedMessage, response.getHighlightedMessage());
        root.addChild(reportInfo);

        Node actions = new Node(Sentinel.lang.violations.chat.url.actionTitle);
        actions.addTextLine(Sentinel.lang.violations.chat.url.blockAction);
        if (response.isPunished()) actions.addTextLine(Sentinel.lang.violations.chat.url.commandAction);
        root.addChild(actions);

        return root;
    }

    @Override
    protected boolean shouldWarnPlayer(UrlResponse response) {
        return !Sentinel.mainConfig.chat.urlFilter.silent;
    }
}
