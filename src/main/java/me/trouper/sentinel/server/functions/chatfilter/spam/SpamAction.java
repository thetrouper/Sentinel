package me.trouper.sentinel.server.functions.chatfilter.spam;

import me.trouper.sentinel.server.functions.chatfilter.AbstractActionHandler;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class SpamAction extends AbstractActionHandler<SpamResponse> {

    @Override
    public void punish(SpamResponse response) {
        for (String spamPunishCommand : main.dir().io.mainConfig.chat.spamFilter.punishCommands) {
            ServerUtils.sendCommand(spamPunishCommand.replaceAll("%player%", response.getEvent().getPlayer().getName()));
        }
    }

    @Override
    public void staffWarning(SpamResponse response, Node tree) {
        Component message = Text.getMessageAny(
                Text.Pallet.INFO,
                response.isPunished() ?
                        main.dir().io.lang.violations.chat.spam.autoPunishNotification :
                        main.dir().io.lang.violations.chat.spam.preventNotification,
                response.getEvent().getPlayer().getName(),
                SpamFilter.heatMap.get(response.getEvent().getPlayer().getUniqueId()),
                main.dir().io.mainConfig.chat.spamFilter.punishHeat
        );

        PlayerUtils.forEachPlayer(player -> {
            if (!player.hasPermission("sentinel.chatfilter.spam.view")) return;
            Text.message(Text.Pallet.INFO, player, message.hoverEvent(HoverFormatter.format(tree).asHoverEvent()));
        });
    }

    @Override
    public void playerWarning(SpamResponse response) {
        Component message = Text.getMessageAny(
                Text.Pallet.INFO,
                response.isPunished() ? main.dir().io.lang.violations.chat.spam.autoPunishWarning :
                        main.dir().io.lang.violations.chat.spam.preventWarning
        );
        String hoverText = main.dir().io.lang.automatedActions.reportable;
        String command = "/sentinelcallback fpreport %s".formatted(response.getReport().getId());
        Text.message(Text.Pallet.INFO, response.getEvent().getPlayer(), message
                .hoverEvent(Component.text(hoverText).asHoverEvent())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command))
        );
    }

    @Override
    public Node buildTree(SpamResponse response)  {
        Node root = new Node("Sentinel");
        root.addTextLine(Text.format(Text.Pallet.NEUTRAL, Component.text(main.dir().io.lang.violations.chat.spam.treeTitle),response.getPlayer().name()));

        Node playerInfo = new Node(main.dir().io.lang.violations.protections.infoNode.playerInfo);
        playerInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.uuid, response.getEvent().getPlayer().getUniqueId().toString());
        playerInfo.addKeyValue(main.dir().io.lang.violations.chat.spam.heat, "%s/%s".formatted(SpamFilter.heatMap.get(response.getEvent().getPlayer().getUniqueId()),main.dir().io.mainConfig.chat.spamFilter.punishHeat));
        root.addChild(playerInfo);

        Node reportInfo = new Node(main.dir().io.lang.violations.chat.spam.reportInfoTitle);
        reportInfo.addField(main.dir().io.lang.violations.chat.spam.previousMessage, response.getPreviousMessage());
        reportInfo.addField(main.dir().io.lang.violations.chat.spam.currentMessage, response.getCurrentMessage());
        reportInfo.addKeyValue(main.dir().io.lang.violations.chat.spam.similarity, "%s/%s".formatted((int) Math.round(response.getSimilarity()),main.dir().io.mainConfig.chat.spamFilter.blockSimilarity));
        root.addChild(reportInfo);

        Node actions = new Node(main.dir().io.lang.violations.protections.actionNode.actionNodeTitle);
        actions.addTextLine(main.dir().io.lang.violations.chat.denyMessage);
        if (response.isPunished()) actions.addTextLine(main.dir().io.lang.violations.protections.actionNode.punishmentCommandsExecuted);
        root.addChild(actions);

        return root;
    }

    @Override
    protected boolean shouldWarnPlayer(SpamResponse response) {
        return !main.dir().io.mainConfig.chat.spamFilter.silent;
    }
}