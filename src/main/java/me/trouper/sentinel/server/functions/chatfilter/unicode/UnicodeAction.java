package me.trouper.sentinel.server.functions.chatfilter.unicode;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.AbstractActionHandler;
import me.trouper.sentinel.server.functions.chatfilter.profanity.ProfanityFilter;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class UnicodeAction extends AbstractActionHandler<UnicodeResponse> {
    @Override
    protected void punish(UnicodeResponse response) {
        for (String punishCommand : Sentinel.mainConfig.chat.unicodeFilter.punishCommands) {
            ServerUtils.sendCommand(punishCommand.replaceAll("%player%",response.getPlayer().getName()));
        }
    }

    @Override
    protected void staffWarning(UnicodeResponse response, Node tree) {
        String messageText = Text.prefix("&b&n%s&r &7%s".formatted(
                response.getPlayer().getName(),
                response.isPunished() ? Sentinel.lang.violations.chat.unicode.autoPunishNotification : Sentinel.lang.violations.chat.unicode.preventNotification
        ));
        String hoverText = HoverFormatter.format(tree);

        ServerUtils.forEachStaff(player -> player.sendMessage(Component.text(messageText).hoverEvent(Component.text(hoverText).asHoverEvent())));
    }

    @Override
    protected void playerWarning(UnicodeResponse response) {
        String message = Text.prefix(response.isPunished() ? Sentinel.lang.violations.chat.unicode.autoPunishWarning : Sentinel.lang.violations.chat.unicode.preventWarning);
        String hoverText = Sentinel.lang.automatedActions.actionAutomaticReportable;
        String command = "/sentinelcallback fpreport %s".formatted(response.getReport().getId());
        response.getPlayer().sendMessage(Component.text(message)
                .hoverEvent(Component.text(hoverText).asHoverEvent())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,command)));
    }

    @Override
    protected Node buildTree(UnicodeResponse response) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.chat.unicode.treeTitle);

        Node playerInfo = new Node(Sentinel.lang.violations.chat.unicode.playerInfoTitle.formatted(response.getPlayer().getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.unicode.uuid, response.getPlayer().getUniqueId().toString());
        root.addChild(playerInfo);

        Node reportInfo = new Node(Sentinel.lang.violations.chat.unicode.reportInfoTitle);
        reportInfo.addField(Sentinel.lang.violations.chat.unicode.originalMessage, response.getOriginalMessage());
        reportInfo.addField(Sentinel.lang.violations.chat.unicode.highlightedMessage, response.getHighlightedMessage());
        root.addChild(reportInfo);

        Node actions = new Node(Sentinel.lang.violations.chat.unicode.actionTitle);
        actions.addTextLine(Sentinel.lang.violations.chat.unicode.blockAction);
        if (response.isPunished()) actions.addTextLine(Sentinel.lang.violations.chat.unicode.commandAction);
        root.addChild(actions);

        return root;
    }

    @Override
    protected boolean shouldWarnPlayer(UnicodeResponse response) {
        return !Sentinel.mainConfig.chat.unicodeFilter.silent;
    }
}
