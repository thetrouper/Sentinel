package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.Message;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@CommandRegistry(value = "reply", permission = @Permission("sentinel.reply"))
public class ReplyCommand implements CustomCommand {
    public static Map<UUID, UUID> replyMap = Message.replyMap;
    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        String name = sender.getName();
        Player p = sender.getServer().getPlayer(name);
        UUID senderID = p.getUniqueId();
        if (replyMap.get(senderID) == null) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-user-reply")));
        }
        Player r = sender.getServer().getPlayer(replyMap.get(senderID));
        UUID reciverID = r.getUniqueId();
        if (args.get(0).toString() == null) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-message-provided")));
        }
        String msg = args.getAll().toString();
        if (p.hasPermission("sentinel.message")) {
            Message.messagePlayer(p,r,msg);
            replyMap.put(senderID,reciverID);
        } else {
            sender.sendMessage(Text.prefix(Sentinel.dict.get("no-permission")));
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg("[<Message>]"));
    }
}
