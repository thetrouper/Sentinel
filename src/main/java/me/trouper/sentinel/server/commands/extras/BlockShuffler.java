package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlockShuffler extends AbstractExtra {
    public BlockShuffler() {
        super("blocks","Cube of random");
    }
    
    private final List<UUID> scrambleBlocks = new ArrayList<>();

    @Override
    public void execute(CommandSender sender, Player target) {
        scrambleBlocks.add(target.getUniqueId());
        User user = PacketEvents.getAPI().getPlayerManager().getUser(target);

        int x = target.getLocation().getBlockX();
        int y = target.getLocation().getBlockY();
        int z = target.getLocation().getBlockZ();
        
        List<Material> blockMaterials = new ArrayList<>(Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .toList());
        Collections.shuffle(blockMaterials);
        ConcurrentLinkedQueue<WrappedBlockState> stateQueue = new ConcurrentLinkedQueue<>();
         
        blockMaterials.forEach(material -> stateQueue.add(SpigotConversionUtil.fromBukkitBlockData(Bukkit.createBlockData(material))));

        int radius = 8;
        Bukkit.getScheduler().runTaskTimerAsynchronously(main.getPlugin(),task -> {            
            for (int xValue = x - radius; xValue < x + radius; xValue++) {
                for (int yValue = y - radius; yValue < y + radius; yValue++) {
                    for (int zValue = z - radius; zValue < z + radius; zValue++) {
                        if (!scrambleBlocks.contains(target.getUniqueId())) {
                            task.cancel();
                            return;
                        }
                        var blockState = stateQueue.poll();
                        stateQueue.offer(blockState);
                        
                        WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(new Vector3i(xValue,yValue,zValue),blockState.getGlobalId());
                        blockChange.setBlockState(blockState);
                        user.sendPacket(blockChange);
                    }
                }
            }
        },0,5);
       
        successAny(sender,"Scrambling {0}'s blocks.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        scrambleBlocks.remove(target.getUniqueId());
        errorAny(sender,"Stopped scrambling {0}'s blocks.",target.getUniqueId());
    }
}
