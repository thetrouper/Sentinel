package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.apache.commons.lang3.builder.Diff;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DeletePlayer extends AbstractExtra {
    
    public DeletePlayer() {
        super("delete","Tell player's client to self delete");
    }
    
    @Override
    public void execute(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        player.sendPacket(new WrapperPlayServerDestroyEntities(target.getEntityId()));
        successAny(sender,"Deleting {0}.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        var playerSpawn = new WrapperPlayServerSpawnEntity(
                target.getEntityId(),
                Optional.of(target.getUniqueId()),
                EntityTypes.PLAYER,
                new Vector3d(),
                target.getLocation().getYaw(),
                target.getLocation().getPitch(),
                target.getLocation().getYaw(),
                0,
                Optional.of(new Vector3d(0,0,0)
                )
        );
        String world = target.getWorld().getName();
        Difficulty difficulty = Difficulty.valueOf(target.getWorld().getDifficulty().toString());
        GameMode gameMode = GameMode.valueOf(target.getGameMode().toString());
        ResourceLocation location = new ResourceLocation(player.getDimension().getDimensionName());
        WorldBlockPosition position = new WorldBlockPosition(location,target.getLocation().getBlockX(),target.getLocation().getBlockY(),target.getLocation().getBlockZ());
        WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn(
                player.getDimensionType(),
                world,
                difficulty,
                0L,
                gameMode,
                gameMode,
                false,
                false,
                true,
                location,
                position,
                0
        );
        player.sendPacket(respawn);
        successAny(sender,"Attempting to restore {0}. If it does not work just kick them or tell them to leave.",target.getName());
    }
}
