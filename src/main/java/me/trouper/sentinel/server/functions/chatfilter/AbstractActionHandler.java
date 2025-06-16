package me.trouper.sentinel.server.functions.chatfilter;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.Main;
import me.trouper.sentinel.utils.trees.ConsoleFormatter;
import me.trouper.sentinel.utils.trees.EmbedFormatter;
import me.trouper.sentinel.utils.trees.Node;

public abstract class AbstractActionHandler<T extends FilterResponse> implements Main {

    public void run(T response) {
        main.dir().reportHandler.reports.put(response.getReport().getId(), response.getReport());
        Node tree = buildTree(response);

        if (response.isBlocked()) {
            restrictMessage(response.getEvent(),!shouldWarnPlayer(response));
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
        Sentinel.getInstance().getLogger().info(ConsoleFormatter.format(tree));
    }

    protected void discordNotification(Node tree) {
        DiscordEmbed embed = EmbedFormatter.format(tree);
        EmbedFormatter.sendEmbed(embed);
    }

    protected void restrictMessage(AsyncChatEvent event, boolean silent) {
        if (silent) {
            event.viewers().clear();
            event.viewers().add(event.getPlayer());
        } else {
            event.setCancelled(true);
        }
    }
}
