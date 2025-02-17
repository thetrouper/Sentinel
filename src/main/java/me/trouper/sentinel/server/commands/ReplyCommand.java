package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.Message;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@CommandRegistry(value = "reply", permission = @Permission("sentinel.reply"),printStackTrace = true)
public class ReplyCommand implements CustomCommand {

    public static Map<UUID, UUID> replyMap = Message.replyMap;

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        String name = sender.getName();
        Player p = sender.getServer().getPlayer(name);
        UUID senderID = p.getUniqueId();
        if (replyMap.get(senderID) == null) {
            p.sendMessage(Text.prefix(Sentinel.lang.playerInteraction.noReply));
        }
        Player r = sender.getServer().getPlayer(replyMap.get(senderID));
        UUID reciverID = r.getUniqueId();
        if (args.get(0).toString() == null) {
            p.sendMessage(Text.prefix(Sentinel.lang.playerInteraction.noMessageProvided));
        }
        String msg = args.getAll().toString();
        if (PlayerUtils.checkPermission(sender,"sentinel.message")) {
            Message.messagePlayer(p,r,msg);
            replyMap.put(senderID,reciverID);
        }
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg("[<Message>]"));
    }
}
