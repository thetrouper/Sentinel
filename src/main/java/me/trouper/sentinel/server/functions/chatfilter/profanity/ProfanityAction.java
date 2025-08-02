package me.trouper.sentinel.server.functions.chatfilter.profanity;

import me.trouper.sentinel.server.functions.chatfilter.AbstractActionHandler;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class ProfanityAction extends AbstractActionHandler<ProfanityResponse> {

    @Override
    public void punish(ProfanityResponse response) {
        if (response.getSeverity().equals(Severity.SLUR)) {
            for (String slurCommand : main.dir().io.mainConfig.chat.profanityFilter.strictPunishCommands) {
                ServerUtils.sendCommand(slurCommand.replaceAll("%player%", response.getPlayer().getName()));
            }
        }
        for (String swearCommand : main.dir().io.mainConfig.chat.profanityFilter.profanityPunishCommands) {
            ServerUtils.sendCommand(swearCommand.replaceAll("%player%", response.getPlayer().getName()));
        }
    }

    @Override
    public void staffWarning(ProfanityResponse response, Node tree) {
        Component message = Text.getMessageAny(
                Text.Pallet.INFO,response.isPunished() ? 
                main.dir().io.lang.violations.chat.profanity.autoPunishNotification :
                main.dir().io.lang.violations.chat.profanity.preventNotification,
                response.getPlayer().getName(),
                ProfanityFilter.scoreMap.getOrDefault(response.getPlayer().getUniqueId(), 0),
                main.dir().io.mainConfig.chat.profanityFilter.punishScore
        );

        PlayerUtils.forEachPlayer(player -> {
            if (!player.hasPermission("sentinel.chatfilter.profanity.view")) return;
            Text.message(Text.Pallet.INFO,player,message.hoverEvent(HoverFormatter.format(tree).asHoverEvent()));
        });
    }

    @Override
    public void playerWarning(ProfanityResponse response) {
        Component message = Text.getMessageAny(
                Text.Pallet.INFO,
                response.isPunished() ? main.dir().io.lang.violations.chat.profanity.autoPunishWarning :
                        main.dir().io.lang.violations.chat.profanity.preventWarning
        );
        String hoverText = main.dir().io.lang.automatedActions.reportable;
        String command = "/sentinelcallback fpreport " + response.getReport().getId();
        Text.message(Text.Pallet.INFO,response.getPlayer(),message
                .hoverEvent(Component.text(hoverText).asHoverEvent())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,command))
        );
    }

    @Override
    public Node buildTree(ProfanityResponse response)  {
        Node root = new Node(); 
        root.addTextLine(Text.format(Text.Pallet.NEUTRAL,main.dir().io.lang.violations.chat.profanity.treeTitle,response.getPlayer().name()));

        Node playerInfo = new Node(main.dir().io.lang.violations.protections.infoNode.playerInfo.formatted(response.getPlayer().getName()));
        playerInfo.addKeyValue(main.dir().io.lang.violations.protections.infoNode.uuid, response.getPlayer().getUniqueId().toString());
        playerInfo.addKeyValue(main.dir().io.lang.violations.chat.profanity.score, "%s/%s".formatted(ProfanityFilter.scoreMap.getOrDefault(response.getPlayer().getUniqueId(),0),main.dir().io.mainConfig.chat.profanityFilter.punishScore));
        root.addChild(playerInfo);

        Node reportInfo = new Node(main.dir().io.lang.violations.chat.profanity.reportInfoTitle);
        reportInfo.addField(main.dir().io.lang.violations.chat.originalMessage, response.getOriginalMessage());
        reportInfo.addField(Component.text(main.dir().io.lang.violations.chat.profanity.processedMessage), Node.parseLegacyText(response.getProcessedMessage()));
        reportInfo.addKeyValue(main.dir().io.lang.violations.chat.profanity.severity, response.getSeverity().toString());
        root.addChild(reportInfo);

        Node actions = new Node(main.dir().io.lang.violations.protections.actionNode.actionNodeTitle);
        actions.addTextLine(main.dir().io.lang.violations.chat.denyMessage);
        if (response.isPunished()) actions.addTextLine(main.dir().io.lang.violations.protections.actionNode.punishmentCommandsExecuted);
        root.addChild(actions);

        return root;
    }

    @Override
    protected boolean shouldWarnPlayer(ProfanityResponse response) {
        return !main.dir().io.mainConfig.chat.profanityFilter.silent;
    }
}
