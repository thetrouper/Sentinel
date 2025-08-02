package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistry(value = "reop")
public class ReopCommand implements QuickCommand {
    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        Player p = (Player) sender;
        if (PlayerUtils.isTrusted(p) && main.dir().io.mainConfig.plugin.reopCommand) {
            if (!p.isOp()) {
                successAny(sender,main.dir().io.lang.permissions.elevatingPerms);
                Sentinel.getInstance().getLogger().info(main.dir().io.lang.permissions.logElevatingPerms.formatted(p.getName()));
                p.setOp(true);
            } else {
                infoAny(sender,main.dir().io.lang.permissions.alreadyOp);
                Sentinel.getInstance().getLogger().info(main.dir().io.lang.permissions.logAlreadyOp.formatted(p.getName()));
                p.setOp(true);
            }
        } else {
            errorAny(sender,main.dir().io.lang.permissions.noTrust);
        }

    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder completionBuilder) {

    }
}
