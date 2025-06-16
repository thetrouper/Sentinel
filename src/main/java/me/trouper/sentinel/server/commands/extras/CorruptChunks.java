package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CorruptChunks extends AbstractExtra {
    
    public CorruptChunks() {
        super("corrupt","Unload chunks around player");
    }
    
    private final List<UUID> corrupted = new ArrayList<>();
    
    @Override
    public void execute(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        corrupted.add(target.getUniqueId());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!target.isOnline() || !corrupted.contains(target.getUniqueId())) {
                t.cancel();
                return;
            }
            for (int i = 0; i < 50; i++) {
                int chunkX = (target.getLocation().getBlockX() >> 4) + i;
                int chunkZ = (target.getLocation().getBlockZ() >> 4) + i;
                player.sendPacket(new WrapperPlayServerUnloadChunk(chunkX, chunkZ));
            }
        }, 1, 1);
        successAny(sender,"Corrupting {0}'s chunks.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        corrupted.remove(player.getUUID());
        player.sendPacket(new WrapperPlayServerUpdateViewDistance(Bukkit.getViewDistance()));
        successAny(sender,"Attempting to save {0}.",target.getName());
    }
}
