package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.pdk.utils.misc.Cooldown;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.FalsePositiveReporting;
import me.trouper.sentinel.server.functions.chatfilter.Report;
import me.trouper.sentinel.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandRegistry(value = "sentinelcallback", permission = @Permission("sentinel.callbacks"), printStackTrace = true)
public class CallbackCommand implements CustomCommand {

    Cooldown<UUID> fpReportCooldown = new Cooldown<>();

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        Player p = (Player) sender;
        switch (args.get(0).toString()) {
            case "fpreport" -> {
                if (fpReportCooldown.isOnCooldown(p.getUniqueId()) && !p.isOp()) {
                    p.sendMessage(Text.prefix(Sentinel.lang.cooldown.onCooldown + fpReportCooldown.getCooldown(p.getUniqueId())));
                } else {
                    long id = args.get(1).toLong();
                    Report report = FalsePositiveReporting.reports.get(id);
                    if (report == null) {
                        p.sendMessage(Text.prefix(Sentinel.lang.reports.noReport));
                        return;
                    }
                    p.sendMessage(Text.prefix(Sentinel.lang.reports.reportingFalsePositive));
                    FalsePositiveReporting.sendReport(p,report);
                    p.sendMessage(Text.prefix(Sentinel.lang.reports.falsePositiveSuccess));
                }
            }
        }
    }

    @Override
    public void dispatchCompletions(CommandSender sender, Command command, String s, CompletionBuilder b) {

    }
}
