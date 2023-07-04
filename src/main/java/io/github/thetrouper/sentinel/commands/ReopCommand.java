package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.exceptions.CmdExHandler;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (Config.reopCommand) {
            String name = sender.getName().toString();
            Player p = sender.getServer().getPlayer(name);
            if (Sentinel.isTrusted(p)) {
                sender.sendMessage(TextUtils.prefix("Elevating your permissions..."));
                p.setOp(true);
                Sentinel.log.info("Sentinel has elevated the permissions of " + name + "!");
                } else {
                    sender.sendMessage(TextUtils.prefix("You are not trusted!"));
                }
            } else {
                sender.sendMessage(TextUtils.prefix("This command is not enabled!"));
            }
            return true;
        } catch (Exception ex) {
            CmdExHandler handler = new CmdExHandler(ex,command);
            sender.sendMessage(handler.getErrorMessage());
            return true;
        }
    }
}
