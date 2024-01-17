package io.github.thetrouper.sentinel.cmds;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandRegistry(value = "socialspy", permission = @Permission("sentinel.spy"))
public class SocialSpyCommand implements CustomCommand {

    public static Map<UUID, Boolean> spyMap = new HashMap<>();
    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        String name = sender.getName();
        Player p = sender.getServer().getPlayer(name);
        UUID senderID = p.getUniqueId();
        if (!spyMap.containsKey(senderID) || !spyMap.get(senderID)) {
            sender.sendMessage(Text.prefix(Sentinel.dict.get("spy-enabled")));
            spyMap.put(senderID,true);
        } else if (spyMap.get(senderID)) {
            sender.sendMessage(Text.prefix(Sentinel.dict.get("spy-disabled")));
            spyMap.put(senderID,false);
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder completionBuilder) {

    }
}
