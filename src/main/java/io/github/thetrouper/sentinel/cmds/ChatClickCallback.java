package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.pdk.utils.misc.Cooldown;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatClickCallback implements CustomCommand {
    Cooldown<UUID> fpReportCooldown = new Cooldown<>();
    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        Player p = (Player) sender;
        switch (args.get(0).toString()) {
            case "fpreport" -> {
                if (fpReportCooldown.isOnCooldown(p.getUniqueId()) && !p.isOp()) {
                    p.sendMessage(Text.prefix(Sentinel.dict.get("cooldown") + fpReportCooldown.getCooldown(p.getUniqueId())));
                } else {
                    ReportFalsePositives.sendFalsePositiveReport(args.get(1).toString());
                    p.sendMessage(Text.prefix(Sentinel.dict.get("false-positive-report-success")));
                }
            }
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg("a_you","b_must","c_be","d_called","e_before","f_running","g_a","h_callback"));
    }
}
