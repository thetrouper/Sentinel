package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistry(value = "reop")
public class ReopCommand implements CustomCommand {
    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        Player p = (Player) sender;
        if (Sentinel.isTrusted(p) && Sentinel.mainConfig.plugin.reopCommand) {
            if (!p.isOp()) {
                p.sendMessage(Text.prefix(Sentinel.lang.permissions.elevatingPerms));
                Sentinel.log.info(Sentinel.lang.permissions.logElevatingPerms.formatted(p.getName()));
                p.setOp(true);
            } else {
                p.sendMessage(Text.prefix(Sentinel.lang.permissions.alreadyOp));
                Sentinel.log.info(Sentinel.lang.permissions.logAlreadyOp.formatted(p.getName()));
                p.setOp(true);
            }
        } else {
            p.sendMessage(Text.prefix(Sentinel.lang.permissions.noTrust));
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder completionBuilder) {

    }
}
