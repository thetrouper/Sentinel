package me.trouper.sentinel.server.functions.chatfilter.spam;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.AbstractActionHandler;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class SpamAction extends AbstractActionHandler<SpamResponse> {

    @Override
    public void punish(SpamResponse response) {
        for (String spamPunishCommand : Sentinel.mainConfig.chat.spamFilter.punishCommands) {
            ServerUtils.sendCommand(spamPunishCommand.replaceAll("%player%", response.getEvent().getPlayer().getName()));
        }
    }

    @Override
    public void staffWarning(SpamResponse report, Node tree) {
        String messageText = Text.prefix("&b&n%s&r &7%s &8(&4%s&7/&c%s&8)".formatted(
                report.getEvent().getPlayer().getName(),
                report.isPunished() ? Sentinel.lang.violations.chat.spam.autoPunishNotification : Sentinel.lang.violations.chat.spam.preventNotification,
                SpamFilter.heatMap.get(report.getEvent().getPlayer().getUniqueId()),
                Sentinel.mainConfig.chat.spamFilter.punishHeat
        ));
        String hoverText = HoverFormatter.format(tree);

        ServerUtils.forEachPlayer(player -> {
            if (player.hasPermission("sentinel.chatfilter.spam.view")) player.sendMessage(Component.text(messageText).hoverEvent(Component.text(hoverText).asHoverEvent()));
        });
    }

    @Override
    public void playerWarning(SpamResponse response) {
        String message = Text.prefix(response.isPunished() ? Sentinel.lang.violations.chat.spam.autoPunishWarning : Sentinel.lang.violations.chat.spam.preventWarning) ;
        String hoverText = Sentinel.lang.automatedActions.reportable;
        String command = "/sentinelcallback fpreport %s".formatted(response.getReport().getId());
        response.getEvent().getPlayer().sendMessage(Component.text(message)
                        .hoverEvent(Component.text(hoverText).asHoverEvent())
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,command)));
    }

    @Override
    public Node buildTree(SpamResponse response)  {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.chat.spam.treeTitle);

        Node playerInfo = new Node(Sentinel.lang.violations.protections.infoNode.playerInfo.formatted(response.getEvent().getPlayer().getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.protections.infoNode.uuid, response.getEvent().getPlayer().getUniqueId().toString());
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.spam.heat, "%s/%s".formatted(SpamFilter.heatMap.get(response.getEvent().getPlayer().getUniqueId()),Sentinel.mainConfig.chat.spamFilter.punishHeat));
        root.addChild(playerInfo);

        Node reportInfo = new Node(Sentinel.lang.violations.chat.spam.reportInfoTitle);
        reportInfo.addField(Sentinel.lang.violations.chat.spam.previousMessage, response.getPreviousMessage());
        reportInfo.addField(Sentinel.lang.violations.chat.spam.currentMessage, response.getCurrentMessage());
        reportInfo.addKeyValue(Sentinel.lang.violations.chat.spam.similarity, "%s/%s".formatted((int) Math.round(response.getSimilarity()),Sentinel.mainConfig.chat.spamFilter.blockSimilarity));
        root.addChild(reportInfo);

        Node actions = new Node(Sentinel.lang.violations.protections.actionNode.actionNodeTitle);
        actions.addTextLine(Sentinel.lang.violations.chat.denyMessage);
        if (response.isPunished()) actions.addTextLine(Sentinel.lang.violations.protections.actionNode.punishmentCommandsExecuted);
        root.addChild(actions);

        return root;
    }

    @Override
    protected boolean shouldWarnPlayer(SpamResponse response) {
        return !Sentinel.mainConfig.chat.spamFilter.silent;
    }
}
