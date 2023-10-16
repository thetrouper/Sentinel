package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.functions.Message;
import io.github.thetrouper.sentinel.server.util.ArrayUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCommand extends CustomCommand {
    public static Map<UUID, UUID> replyMap = new HashMap<>();

    public MessageCommand() {
        super("msg");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        Player r = null;
        if (args.length == 0) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-online-player")));
        }
        if (args.length == 1) {
            p.sendMessage(Text.prefix(Sentinel.dict.get("no-message-provided")));
        }
        r = Bukkit.getPlayer(args[0]);
        String msg = "";
        for (int i = 1; i < args.length; i++) {
            msg = msg.concat(" " + args[i]);
        }
        msg = msg.trim();
        if (p.hasPermission("sentinel.message") && r != null) {
            Message.messagePlayer(p,r,msg);
        } else if (r == null) {
            p.sendMessage(Text.prefix((Sentinel.dict.get("no-online-player"))));
        }
        else {
            sender.sendMessage(Text.prefix(Sentinel.dict.get("no-permission")));
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {
                builder.addCompletion(1, ArrayUtils.toNewList(Bukkit.getOnlinePlayers(), Player::getName));
                builder.addCompletion(2,builder.args.length >= 2,new String[]{
                        "[<message>]"
                });
    }
}
