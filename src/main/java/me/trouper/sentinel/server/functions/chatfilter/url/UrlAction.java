package me.trouper.sentinel.server.functions.chatfilter.url;

import me.trouper.sentinel.server.functions.chatfilter.AbstractActionHandler;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class UrlAction extends AbstractActionHandler<UrlResponse> {
    @Override
    protected void punish(UrlResponse response) {
        for (String punishCommand : main.dir().io.mainConfig.chat.urlFilter.punishCommands) {
            ServerUtils.sendCommand(punishCommand.replaceAll("%player%", response.getPlayer().getName()));
        }
    }

    @Override
    protected void staffWarning(UrlResponse response, Node tree) {
        Component message = Text.getMessageAny(
                Text.Pallet.INFO,
                response.isPunished() ?
                        main.dir().io.lang.violations.chat.url.autoPunishNotification :
                        main.dir().io.lang.violations.chat.url.preventNotification,
                response.getPlayer().getName()
        );

        PlayerUtils.forEachPlayer(player -> {
            if (!player.hasPermission("sentinel.chatfilter.url.view")) return;
            Text.message(Text.Pallet.INFO, player, message.hoverEvent(HoverFormatter.format(tree).asHoverEvent()));
        });
    }

    @Override
    protected void playerWarning(UrlResponse response) {
        Component message = Text.getMessageAny(
                Text.Pallet.INFO,
                response.isPunished() ? main.dir().io.lang.violations.chat.url.autoPunishWarning :
                        main.dir().io.lang.violations.chat.url.preventWarning
        );
        String hoverText = main.dir().io.lang.automatedActions.reportable;
        String command = "/sentinelcallback fpreport %s".formatted(response.getReport().getId());
        Text.message(Text.Pallet.INFO, response.getPlayer(), message
                .hoverEvent(Component.text(hoverText).asHoverEvent())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command))
        );
    }

    @Override
    protected Node buildTree(UrlResponse response) {
        Node root = new Node("Sentinel");
        root.addTextLine(Text.format(Text.Pallet.NEUTRAL,Component.text(main.dir().io.lang.violations.chat.url.treeTitle),response.getPlayer().name()));

        Node playerInfo = new Node(main.dir().io.lang.violations.protections.infoNode.playerInfo.formatted(response.getPlayer().getName()));
        playerInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.uuid, response.getPlayer().getUniqueId().toString());
        root.addChild(playerInfo);

        Node reportInfo = new Node(main.dir().io.lang.violations.chat.url.reportInfoTitle);
        reportInfo.addField(main.dir().io.lang.violations.chat.originalMessage, response.getOriginalMessage());
        reportInfo.addField(Component.text(main.dir().io.lang.violations.chat.highlightedMessage), Node.parseLegacyText(response.getHighlightedMessage()));
        root.addChild(reportInfo);

        Node actions = new Node(main.dir().io.lang.violations.protections.actionNode.actionNodeTitle);
        actions.addTextLine(main.dir().io.lang.violations.chat.denyMessage);
        if (response.isPunished()) actions.addTextLine(main.dir().io.lang.violations.protections.actionNode.punishmentCommandsExecuted);
        root.addChild(actions);

        return root;
    }

    @Override
    protected boolean shouldWarnPlayer(UrlResponse response) {
        return !main.dir().io.mainConfig.chat.urlFilter.silent;
    }
}