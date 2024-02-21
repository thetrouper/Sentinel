package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.pdk.utils.misc.Cooldown;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
@CommandRegistry(value = "sentinelcallback", permission = @Permission("sentinel.callbacks"), printStackTrace = true)
public class ChatClickCallback implements CustomCommand {
    Cooldown<UUID> fpReportCooldown = new Cooldown<>();
    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        Player p = (Player) sender;
        switch (args.get(0).toString()) {
            case "fpreport" -> {
                if (fpReportCooldown.isOnCooldown(p.getUniqueId()) && !p.isOp()) {
                    p.sendMessage(Text.prefix(Sentinel.language.get("cooldown") + fpReportCooldown.getCooldown(p.getUniqueId())));
                } else {
                    ReportFalsePositives.sendFalsePositiveReport(ReportFalsePositives.reports.get(args.get(1).toLong()));
                    p.sendMessage(Text.prefix(Sentinel.language.get("false-positive-report-success")));
                }
            }
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg("a_you","b_must","c_be","d_called","e_before","f_running","g_a","h_callback"));
    }
}
