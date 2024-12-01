package me.trouper.sentinel.server.functions.chatfilter.profanity;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.FalsePositiveReporting;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class ProfanityAction {
    public static void run(ProfanityResponse response) {
        FalsePositiveReporting.reports.put(response.getReport().getId(),response.getReport());
        Node tree = getTree(response);

        if (response.isPunished()) {
            punish(response);
            discordNotification(tree);
        }
        staffWarning(response,tree);
        playerWarning(response);
        consoleLog(tree);
    }

    public static void punish(ProfanityResponse response) {
        if (response.getSeverity().equals(Severity.SLUR)) {
            for (String slurCommand : Sentinel.mainConfig.chat.swearFilter.strictPunishCommands) {
                ServerUtils.sendCommand(slurCommand.replaceAll("%player%", response.getEvent().getPlayer().getName()));
            }
        }
        for (String swearCommand : Sentinel.mainConfig.chat.swearFilter.swearPunishCommands) {
            ServerUtils.sendCommand(swearCommand.replaceAll("%player%", response.getEvent().getPlayer().getName()));
        }
    }

    public static void staffWarning(ProfanityResponse report, Node tree) {
        String messageText = Text.prefix("&b&n%s&r &7%s &8(&4%s&7/&c%s&8)".formatted(
                report.getEvent().getPlayer().getName(),
                report.isPunished() ? Sentinel.lang.violations.chat.profanity.autoPunish : Sentinel.lang.violations.chat.profanity.prevent,
                AntiProfanity.scoreMap.getOrDefault(report.getEvent().getPlayer().getUniqueId(), 0),
                Sentinel.mainConfig.chat.swearFilter.punishScore
        ));
        String hoverText = HoverFormatter.format(tree);

        ServerUtils.forEachStaff(player -> player.sendMessage(Component.text(messageText).hoverEvent(Component.text(hoverText).asHoverEvent())));
    }

    public static void playerWarning(ProfanityResponse response) {
        String message = Text.prefix(!response.isPunished() ? Sentinel.lang.violations.chat.profanity.preventWarning : Sentinel.lang.violations.chat.profanity.autoPunishWarning);
        String hoverText = Sentinel.lang.automatedActions.actionAutomaticReportable;
        String command = "/sentinelcallback fpreport %s".formatted(response.getReport().getId());
        response.getEvent().getPlayer().sendMessage(Component.text(message)
                .hoverEvent(Component.text(hoverText).asHoverEvent())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,command)));
    }

    public static void consoleLog(Node tree) {
        Sentinel.log.info(ConsoleFormatter.format(tree));
    }

    public static void discordNotification(Node tree) {
        DiscordEmbed embed = EmbedFormatter.format(tree);
        EmbedFormatter.sendEmbed(embed);
    }

    private static Node getTree(ProfanityResponse response)  {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.chat.profanity.treeTitle);

        Node playerInfo = new Node(Sentinel.lang.violations.chat.profanity.playerInfoTitle.formatted(response.getEvent().getPlayer().getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.profanity.uuid, response.getEvent().getPlayer().getUniqueId().toString());
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.profanity.score, "%s/%s".formatted(AntiProfanity.scoreMap.getOrDefault(response.getEvent().getPlayer().getUniqueId(),0),Sentinel.mainConfig.chat.swearFilter.punishScore));
        root.addChild(playerInfo);

        Node reportInfo = new Node(Sentinel.lang.violations.chat.profanity.reportInfoTitle);
        reportInfo.addField(Sentinel.lang.violations.chat.profanity.originalMessage, response.getOriginalMessage());
        reportInfo.addField(Sentinel.lang.violations.chat.profanity.processedMessage, response.getProcessedMessage());
        reportInfo.addKeyValue(Sentinel.lang.violations.chat.profanity.severity, response.getSeverity().toString());
        root.addChild(reportInfo);

        Node actions = new Node(Sentinel.lang.violations.chat.profanity.actionTitle);
        actions.addTextLine(Sentinel.lang.violations.chat.profanity.blockAction);
        if (response.isPunished()) actions.addTextLine(Sentinel.lang.violations.chat.profanity.commandAction);
        root.addChild(actions);

        return root;
    }
}
