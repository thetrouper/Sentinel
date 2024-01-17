package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.Message;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistry(value = "sentinelmessage",permission = @Permission("sentinel.message"))
public class MessageCommand implements CustomCommand {
    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        Player p = (Player) sender;
        Player r = null;
        if (args.getSize() == 0) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-online-player")));
            return;
        }
        if (args.getSize() == 1) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-message-provided")));
            return;
        }
        r = Bukkit.getPlayer(args.get(0).toString());

        String msg = args.getAll(1).toString().trim();

        if (p.hasPermission("sentinel.message") && r != null) {
            Message.messagePlayer(p,r,msg);
        } else if (r == null) p.sendMessage(Text.prefix((Sentinel.dict.get("no-online-player"))));
        else sender.sendMessage(Text.prefix(Sentinel.dict.get("no-permission")));
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg(ServerUtils.unVanishedPlayers())
                .then(b.arg("[<Message>]")));
    }
}
