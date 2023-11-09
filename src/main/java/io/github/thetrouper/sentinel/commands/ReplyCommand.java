package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.Message;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class ReplyCommand extends CustomCommand {
    public static Map<UUID, UUID> replyMap = MessageCommand.replyMap;

    public ReplyCommand() {
        super("reply");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = sender.getName();
        Player p = sender.getServer().getPlayer(name);
        UUID senderID = p.getUniqueId();
        if (replyMap.get(senderID) == null) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-user-reply")));
        }
        Player r = sender.getServer().getPlayer(replyMap.get(senderID));
        UUID reciverID = r.getUniqueId();
        if (args[0] == null) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-message-provided")));
        }
        String msg = String.join(" ", Arrays.asList(args));
        if (p.hasPermission("sentinel.message")) {
            Message.messagePlayer(p,r,msg);
            replyMap.put(senderID,reciverID);
        } else {
            sender.sendMessage(Text.prefix(Sentinel.dict.get("no-permission")));
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1,builder.args.length >= 2, "[<message>]");
    }
}
