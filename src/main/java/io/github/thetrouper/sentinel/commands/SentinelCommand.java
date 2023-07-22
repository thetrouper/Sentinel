/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

/**
 * Example command
 */
public class SentinelCommand extends CustomCommand {
    public static boolean debugmode;

    public SentinelCommand() {
        super("sentinel");
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        switch (args[0]) {
            case "debugmode" -> {
                debugmode = !debugmode;
                p.sendMessage(TextUtils.prefix(TextUtils.boolString(debugmode,"§aEnabled","§cDisabled") + "§7 debug mode."));
            }
            case "whitelistcommandblock" -> {
            }
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1,"debugmode");
    }
}
