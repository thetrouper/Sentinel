/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;

/**
 * Example command
 */
public class SentinelCommand extends CustomCommand {
    public static boolean debugmode;

    public SentinelCommand() {
        super("sentinel");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        switch (args[0]) {
            case "debugmode" -> {
                debugmode = !debugmode;
                p.sendMessage(Text.prefix(Text.boolString(debugmode,"§aEnabled","§cDisabled") + "§7 debug mode."));
            }
            case "testantiswear" -> {
                HashSet<Player> players = new HashSet<>();
                players.add((Player) sender);
                String msg = "";
                for (int i = 1; i < args.length; i++) {
                    msg = msg.concat(" " + args[i]);
                }
                msg = msg.trim();
                AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(true, (Player) sender, msg, players);
                ProfanityFilter.handleProfanityFilter(e);
            }
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1,"debugmode");
        builder.addCompletion(1,"testantiswear");

    }
}
