package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.Message;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandRegistry(value = "sentinelmessage",permission = @Permission("sentinel.message"),printStackTrace = true)
public class MessageCommand implements CustomCommand {
    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        Player p = (Player) sender;
        Player r = null;
        if (args.getSize() == 0) {
            p.sendMessage(Text.prefix(Sentinel.lang.playerInteraction.noOnlinePlayer));
            return;
        }
        if (args.getSize() == 1) {
            p.sendMessage(Text.prefix(Sentinel.lang.playerInteraction.noMessageProvided));
            return;
        }
        r = Bukkit.getPlayer(args.get(0).toString());

        String msg = args.getAll(1).toString().trim();

        if (PlayerUtils.checkPermission(sender,"sentinel.message") && r != null) {
            Message.messagePlayer(p,r,msg);
        } else if (r == null) p.sendMessage(Text.prefix((Sentinel.lang.playerInteraction.noOnlinePlayer)));
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(
                player -> {
                    players.add(player.getName());
                }
        );
        b.then(b.arg(players)
                .then(b.arg("[<Message>]")));
    }
}
