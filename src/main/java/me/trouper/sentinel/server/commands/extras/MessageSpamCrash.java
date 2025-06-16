package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageSpamCrash extends AbstractExtra {

    public MessageSpamCrash() {
        super("spam","Crash player by spamming chat message");
    }
    
    private final List<UUID> chatCrash = new ArrayList<>();
    
    @Override
    public void execute(CommandSender sender, Player target) {
        chatCrash.add(target.getUniqueId());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!target.isOnline() || !chatCrash.contains(target.getUniqueId())) {
                t.cancel();
                return;
            }
            for (int i = 0; i < 4000; i++) {
                target.sendMessage(":3 Baiiiiii!!!!"); // This "sendMessage" can stay
            }
        }, 1, 1);
        successAny(sender,"Filling the logs of {0}.",target);
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        chatCrash.remove(target.getUniqueId());
        successAny(sender,"Attempting to save {0}.",target.getName());
    }
}
