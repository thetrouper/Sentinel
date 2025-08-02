package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class EntitySpamCrash extends AbstractExtra {
    
    public EntitySpamCrash() {
        super("entity","Spam player with bogus entities");
    }
    
    private final Map<UUID, EntityCache> entityCrash = new HashMap<>();
    
    public class EntityCache {
        private final int startingId;
        private int highestId;
        
        public EntityCache(int startingId) {
            this.highestId = startingId;
            this.startingId = startingId;
        }

        public int getStartingId() {
            return startingId;
        }

        public int getHighestId() {
            return highestId;
        }

        public void setHighestId(int highestId) {
            this.highestId = highestId;
        }
        
        public void incrementHighest() {
            this.highestId = this.highestId + 1;
        }
    }
    
    @Override
    public void execute(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        EntityCache cache = new EntityCache(target.getWorld().getEntityCount());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!target.isOnline() || !entityCrash.containsKey(target.getUniqueId())) {
                t.cancel();
            }
            for (int i = 0; i < 100; i++) {
                cache.incrementHighest();
                WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                        cache.getHighestId(),
                        Optional.of(UUID.randomUUID()),
                        EntityTypes.ENDER_DRAGON,
                        new Vector3d(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ()),
                        0F,
                        0F,
                        0F,
                        0,
                        Optional.of(new Vector3d(0, 0, 0))
                );
                player.sendPacket(packet);
            }
        }, 1, 1);
        successAny(sender,"Summoning entities on {0}.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        if (!entityCrash.containsKey(player.getUUID())) {
            errorAny(sender,"{0} is not being crashed.",target.getName());
            return;
        }
        EntityCache cache = entityCrash.get(player.getUUID());
        int lowest = cache.getStartingId();
        int highest = cache.getHighestId();

        int[] entities = new int[highest - lowest];
        for (int i = lowest; i < highest; i++) {
            entities[i - lowest] = i;
        }

        player.sendPacket(new WrapperPlayServerDestroyEntities(entities));
        entityCrash.remove(player.getUUID());
        successAny(sender,"Attempting to save {0}.",target.getName());
    }
}
