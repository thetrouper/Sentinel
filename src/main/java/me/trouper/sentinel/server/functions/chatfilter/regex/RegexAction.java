package me.trouper.sentinel.server.functions.chatfilter.regex;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.FalsePositiveReporting;
import me.trouper.sentinel.server.functions.chatfilter.profanity.AntiProfanity;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.HoverFormatter;
import me.trouper.sentinel.utils.trees.Node;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class RegexAction {
    public static void run(RegexResponse response) {
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

    public static void punish(RegexResponse response) {
        if (response.getFlagType().equals(RegexFlagType.STRICT_BLOCK)) {
            for (String slurCommand : Sentinel.mainConfig.chat.swearFilter.strictPunishCommands) {
                ServerUtils.sendCommand(slurCommand.replaceAll("%player%", response.getEvent().getPlayer().getName()));
            }
        }
        for (String swearCommand : Sentinel.mainConfig.chat.swearFilter.swearPunishCommands) {
            ServerUtils.sendCommand(swearCommand.replaceAll("%player%", response.getEvent().getPlayer().getName()));
        }
    }

    public static void staffWarning(RegexResponse report, Node tree) {
        String messageText = Text.prefix("&b&n%s&r &7%s".formatted(
                report.getEvent().getPlayer().getName(),
                report.isPunished() ? Sentinel.lang.violations.chat.regex.autoPunish : Sentinel.lang.violations.chat.regex.regexTrigger
        ));
        String hoverText = HoverFormatter.format(tree);

        ServerUtils.forEachStaff(player -> player.sendMessage(Component.text(messageText).hoverEvent(Component.text(hoverText).asHoverEvent())));
    }

    public static void playerWarning(RegexResponse response) {
        String message = Text.prefix(response.getFlagType().getBlockMessage()) ;
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

    private static Node getTree(RegexResponse response)  {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.chat.regex.treeTitle);

        Node playerInfo = new Node(Sentinel.lang.violations.chat.regex.playerInfoTitle.formatted(response.getEvent().getPlayer().getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.regex.uuid, response.getEvent().getPlayer().getUniqueId().toString());
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.regex.score, "%s".formatted(AntiProfanity.scoreMap.get(response.getEvent().getPlayer().getUniqueId())));
        root.addChild(playerInfo);

        Node reportInfo = new Node(Sentinel.lang.violations.chat.regex.reportInfoTitle.formatted(response.getFlagType().getName()));
        reportInfo.addField(Sentinel.lang.violations.chat.regex.originalMessage, response.getOriginalMessage());
        reportInfo.addField(Sentinel.lang.violations.chat.regex.flaggedMessage, response.getHighlightedMessage());
        root.addChild(reportInfo);

        Node actions = new Node(Sentinel.lang.violations.chat.regex.actionTitle);
        actions.addTextLine(Sentinel.lang.violations.chat.regex.blockAction);
        if (response.isPunished()) actions.addTextLine(Sentinel.lang.violations.chat.regex.commandAction);
        root.addChild(actions);

        return root;
    }
}
