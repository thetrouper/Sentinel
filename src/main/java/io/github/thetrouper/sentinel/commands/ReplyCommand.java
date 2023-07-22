package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.server.functions.Message;
import io.github.thetrouper.sentinel.server.util.ArrayUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
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
        String name = sender.getName().toString();
        Player p = sender.getServer().getPlayer(name);
        UUID senderID = p.getUniqueId();
        if (replyMap.get(senderID) == null) {
            p.sendMessage(TextUtils.prefix("§cYou have nobody to reply to!"));
        }
        Player r = sender.getServer().getPlayer(replyMap.get(senderID));
        UUID reciverID = r.getUniqueId();
        if (args[0] == null) {
            p.sendMessage(TextUtils.prefix("§cYou must provide a message to send!"));
        }
        String msg = String.join(" ", Arrays.asList(args));
        if (p.hasPermission("sentinel.message")) {
            Message.messagePlayer(p,r,msg);
            replyMap.put(senderID,reciverID);
        } else {
            sender.sendMessage(TextUtils.prefix("Invalid Permissions!"));
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
        builder.addCompletion(1,builder.args.length >= 2,new String[]{
            "[<message>]"
        });
    }
}
