package me.trouper.sentinel.server.functions.chatfilter;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.FalsePositiveReporting;
import me.trouper.sentinel.server.functions.helpers.FilterHelpers;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.Node;

public abstract class AbstractActionHandler<T extends FilterResponse> {

    public void run(T response) {
        FalsePositiveReporting.reports.put(response.getReport().getId(), response.getReport());
        Node tree = buildTree(response);

        if (response.isBlocked()) {
            FilterHelpers.restrictMessage(response.getEvent(),!shouldWarnPlayer(response));
        }
        if (response.isPunished()) {
            punish(response);
            discordNotification(tree);
        }
        if (shouldWarnPlayer(response)) {
            playerWarning(response);
        }

        staffWarning(response, tree);
        consoleLog(tree);
    }

    protected abstract void punish(T response);
    protected abstract void staffWarning(T response, Node tree);
    protected abstract void playerWarning(T response);
    protected abstract Node buildTree(T response);
    protected abstract boolean shouldWarnPlayer(T response);

    protected void consoleLog(Node tree) {
        Sentinel.log.info(ConsoleFormatter.format(tree));
    }

    protected void discordNotification(Node tree) {
        DiscordEmbed embed = EmbedFormatter.format(tree);
        EmbedFormatter.sendEmbed(embed);
    }
}
