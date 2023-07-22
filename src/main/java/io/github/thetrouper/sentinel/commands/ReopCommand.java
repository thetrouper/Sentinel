package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

public class ReopCommand extends CustomCommand {
    public ReopCommand() {
        super("reop");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Sentinel.isTrusted(p)) {
            p.sendMessage(TextUtils.prefix("Elevating your permissions..."));
            Sentinel.log.info("Elevating the permissions of " + p.getName());
            p.setOp(true);
        } else {
            p.sendMessage(TextUtils.prefix("§cYou are not trusted!"));
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {

    }

}
