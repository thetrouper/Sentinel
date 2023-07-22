package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.server.functions.Message;
import io.github.thetrouper.sentinel.server.util.ArrayUtils;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SocialSpyCommand extends CustomCommand {
    public static Map<UUID, Boolean> spyMap = new HashMap<>();

    public SocialSpyCommand() {
        super("socialspy");
        this.setPrintStacktrace(true);
    }

    @Override
    public void dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = sender.getName().toString();
        Player p = sender.getServer().getPlayer(name);
        UUID senderID = p.getUniqueId();
        if (!spyMap.containsKey(senderID) || !spyMap.get(senderID)) {
            sender.sendMessage(TextUtils.prefix("SocialSpy is now enabled."));
            spyMap.put(senderID,true);
        }
        if (spyMap.get(senderID)) {
            sender.sendMessage(TextUtils.prefix("SocialSpy is now disabled."));
            spyMap.put(senderID,false);
        }
    }

    @Override
    public void registerCompletions(CompletionBuilder builder) {

    }
}
