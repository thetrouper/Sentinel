package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SleepyPlayer extends AbstractExtra {
    
    public SleepyPlayer() {
        super("eepy","Make player's screen dim rapidly");
    }
    
    private final List<UUID> eepyPlayers = new ArrayList<>();
    
    @Override
    public void execute(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        eepyPlayers.add(target.getUniqueId());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!eepyPlayers.contains(target.getUniqueId())) t.cancel();
            player.sendPacket(new WrapperPlayServerEntityAnimation(target.getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.WAKE_UP));
        }, 1, 1);
        successAny(sender,"{0} is getting very eepy.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        eepyPlayers.remove(target.getUniqueId());
        successAny(sender,"Gave {0} some coffee, and they are waking up now.",target.getName());
    }
}
