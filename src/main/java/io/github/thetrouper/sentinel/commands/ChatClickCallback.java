/**
 * This file is for tutorial purposes made by ImproperIssues. Distribute if you want :)
 */

package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.Cooldown;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Example command
 */
public class ChatClickCallback extends CustomCommand {
    public static Cooldown<UUID> fpReportCooldown = new Cooldown<>();
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
                    p.sendMessage(Text.prefix(Sentinel.dict.get("cooldown") + fpReportCooldown.getCooldown(p.getUniqueId())));
                } else {
                    ReportFalsePositives.sendFalsePositiveReport(args[1]);
                    p.sendMessage(Text.prefix(Sentinel.dict.get("false-positive-report-success")));
                }
            }
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1,"a_you","b_must","c_be","d_called","e_before","f_running","g_a","h_callback");
    }
}
