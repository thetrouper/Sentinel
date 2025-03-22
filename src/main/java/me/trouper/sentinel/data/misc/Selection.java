package me.trouper.sentinel.data.misc;

import me.trouper.sentinel.utils.display.BlockDisplayRaytracer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Selection {

    private Location pos1;
    private Location pos2;

    public void setPos1(Location loc) { this.pos1 = loc; }
    public void setPos2(Location loc) { this.pos2 = loc; }

    public Location getPos1() { return pos1; }
    public Location getPos2() { return pos2; }

    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }

    public void forEachBlock(Consumer<Block> action) {
        for (Block block : getBlocks()) {
            action.accept(block);
        }
    }
    
    public Set<Block> getBlocks() {
        Location pos1 = this.getPos1();
        Location pos2 = this.getPos2();

        World world = pos1.getWorld();
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        Set<Block> blocks = new HashSet<>();
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x,y,z));
                }
            }
        }
        
        return blocks;
    }
    
    public void display(Player beholder) {
        if (!beholder.isOnline()
                || this.pos1 == null
                || this.pos2 == null
                || (beholder.getLocation().distance(this.pos1) > 64 && beholder.getLocation().distance(this.pos2) > 64)) return;
        BlockDisplayRaytracer.outline(Material.LIGHT_BLUE_STAINED_GLASS,this.getPos1(),this.getPos2(),0.1,2, List.of(beholder));
    }
}
