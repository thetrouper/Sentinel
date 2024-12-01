package me.trouper.sentinel.server.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SocialSpyCommand implements CustomCommand {
    public static Map<UUID, Boolean> spyMap = new HashMap<>();

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        String name = sender.getName();
        Player p = sender.getServer().getPlayer(name);
        UUID senderID = p.getUniqueId();
        if (!spyMap.containsKey(senderID) || !spyMap.get(senderID)) {
            sender.sendMessage(Text.prefix(Sentinel.lang.socialSpy.enabled));
            spyMap.put(senderID,true);
        } else if (spyMap.get(senderID)) {
            sender.sendMessage(Text.prefix(Sentinel.lang.socialSpy.disabled));
            spyMap.put(senderID,false);
        }
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder completionBuilder) {

    }
}
