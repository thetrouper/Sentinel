package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReopCommand extends CustomCommand {
    public ReopCommand() {
        super("reop");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Sentinel.isTrusted(p)) {
            if (!p.isOp()) {
                p.sendMessage(Text.prefix(Sentinel.dict.get("elevating-perms")));
                Sentinel.log.info(Sentinel.dict.get("log-elevating-perms").formatted(p.getName()));
                p.setOp(true);
            } else {
                p.sendMessage(Text.prefix(Sentinel.dict.get("already-op")));
                Sentinel.log.info(Sentinel.dict.get("log-already-op").formatted(p.getName()));
                p.setOp(true);
            }
        } else {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-trust")));
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {

    }

}
