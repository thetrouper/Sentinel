/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.server.functions.ProfanityFilter;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.Cooldown;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Example command
 */
public class ChatClickCallback extends CustomCommand {
    public static Cooldown<UUID> fpReportCooldown;
    public ChatClickCallback() {
        super("sentinelcallback");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        switch (args[0]) {
            case "fpreport" -> {
                if (fpReportCooldown.isOnCooldown(p.getUniqueId()) && !p.isOp()) {
                    p.sendMessage(TextUtils.prefix("This action is on cooldown! " + fpReportCooldown.getCooldown(p.getUniqueId())));
                } else {
                    ReportFalsePositives.sendFalsePositiveReport(args[1]);
                    p.sendMessage(TextUtils.prefix("Successfully reported a false positive!"));
                }
            }
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1,"you");
        builder.addCompletion(1,"must");
        builder.addCompletion(1,"be");
        builder.addCompletion(1,"called");
        builder.addCompletion(1,"before");
        builder.addCompletion(1,"running");
        builder.addCompletion(1,"a");
        builder.addCompletion(1,"callback");
    }
}
