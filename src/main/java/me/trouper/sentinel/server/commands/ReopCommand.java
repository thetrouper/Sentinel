package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistry(value = "reop")
public class ReopCommand implements CustomCommand {
    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        Player p = (Player) sender;
        if (PlayerUtils.isTrusted(p) && Sentinel.getInstance().getDirector().io.mainConfig.plugin.reopCommand) {
            if (!p.isOp()) {
                p.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.permissions.elevatingPerms));
                Sentinel.getInstance().getLogger().info(Sentinel.getInstance().getDirector().io.lang.permissions.logElevatingPerms.formatted(p.getName()));
                p.setOp(true);
            } else {
                p.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.permissions.alreadyOp));
                Sentinel.getInstance().getLogger().info(Sentinel.getInstance().getDirector().io.lang.permissions.logAlreadyOp.formatted(p.getName()));
                p.setOp(true);
            }
        } else {
            p.sendMessage(Text.prefix(Sentinel.getInstance().getDirector().io.lang.permissions.noTrust));
        }

    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder completionBuilder) {

    }
}
