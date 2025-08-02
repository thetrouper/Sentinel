package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.pdk.utils.misc.Cooldown;
import me.trouper.sentinel.server.functions.helpers.Report;
import me.trouper.sentinel.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandRegistry(value = "sentinelcallback", permission = @Permission("sentinel.callbacks"), printStackTrace = true)
public class CallbackCommand implements QuickCommand {

    Cooldown<UUID> fpReportCooldown = new Cooldown<>();

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        Player p = (Player) sender;
        switch (args.get(0).toString()) {
            case "fpreport" -> {
                if (!PlayerUtils.checkPermission(sender,"sentinel.callbacks.fpreport")) return;
                if (fpReportCooldown.isOnCooldown(p.getUniqueId()) && !p.isOp()) {
                    warningAny(sender,main.lang().cooldown.onCooldown,fpReportCooldown.getCooldownSec(p.getUniqueId()));
                    return;
                }
                long id = args.get(1).toLong();
                Report report = main.dir().reportHandler.reports.get(id);
                if (report == null) {
                    errorAny(sender,main.lang().reports.noReport);
                    return;
                }
                infoAny(sender,main.lang().reports.reportingFalsePositive);
                main.dir().reportHandler.sendReport(p,report);
                successAny(sender,main.lang().reports.falsePositiveSuccess);
            }
        }
    }

    @Override
    public void dispatchCompletions(CommandSender sender, Command command, String s, CompletionBuilder b) {

    }
}
