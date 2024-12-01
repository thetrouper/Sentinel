package me.trouper.sentinel.server.functions.chatfilter.spam;

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

public class SpamAction {

    public static void run(SpamResponse response) {
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

    public static void punish(SpamResponse response) {
        for (String spamPunishCommand : Sentinel.mainConfig.chat.spamFilter.punishCommands) {
            ServerUtils.sendCommand(spamPunishCommand.replaceAll("%player%", response.getEvent().getPlayer().getName()));
        }
    }

    public static void staffWarning(SpamResponse report, Node tree) {
        String messageText = Text.prefix("&b&n%s&r &7%s &8(&4%s&7/&c%s&8)".formatted(
                report.getEvent().getPlayer().getName(),
                report.isPunished() ? Sentinel.lang.violations.chat.spam.autoPunish : Sentinel.lang.violations.chat.spam.spamWarning,
                AntiSpam.heatMap.get(report.getEvent().getPlayer().getUniqueId()),
                Sentinel.mainConfig.chat.spamFilter.punishHeat
        ));
        String hoverText = HoverFormatter.format(tree);

        ServerUtils.forEachStaff(player -> player.sendMessage(Component.text(messageText).hoverEvent(Component.text(hoverText).asHoverEvent())));
    }

    public static void playerWarning(SpamResponse response) {
        String message = Text.prefix(!response.isPunished() ? Sentinel.lang.violations.chat.spam.preventWarning : Sentinel.lang.violations.chat.spam.autoPunishWarning) ;
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

    private static Node getTree(SpamResponse response)  {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.chat.spam.treeTitle);

        Node playerInfo = new Node(Sentinel.lang.violations.chat.spam.playerInfoTitle.formatted(response.getEvent().getPlayer().getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.spam.uuid, response.getEvent().getPlayer().getUniqueId().toString());
        playerInfo.addKeyValue(Sentinel.lang.violations.chat.spam.heat, "%s/%s".formatted(AntiSpam.heatMap.get(response.getEvent().getPlayer().getUniqueId()),Sentinel.mainConfig.chat.spamFilter.punishHeat));
        root.addChild(playerInfo);

        Node reportInfo = new Node(Sentinel.lang.violations.chat.spam.reportInfoTitle);
        reportInfo.addField(Sentinel.lang.violations.chat.spam.previousMessage, response.getPreviousMessage());
        reportInfo.addField(Sentinel.lang.violations.chat.spam.currentMessage, response.getCurrentMessage());
        reportInfo.addKeyValue(Sentinel.lang.violations.chat.spam.similarity, "%s/%s".formatted((int) Math.round(response.getSimilarity()),Sentinel.mainConfig.chat.spamFilter.blockSimilarity));
        root.addChild(reportInfo);

        Node actions = new Node(Sentinel.lang.violations.chat.spam.actionTitle);
        actions.addTextLine(Sentinel.lang.violations.chat.spam.blockAction);
        if (response.isPunished()) actions.addTextLine(Sentinel.lang.violations.chat.spam.commandAction);
        root.addChild(actions);

        return root;
    }
}
