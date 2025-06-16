package me.trouper.sentinel.startup;

import me.trouper.sentinel.server.events.QuickListener;
import me.trouper.sentinel.utils.display.BlockDisplayRaytracer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;

public class OnWorldLoad implements QuickListener {
    
    @EventHandler
    public void onLoad(WorldLoadEvent e) {
        getLogger().info("Removing Residual Block Displays From World: " + e.getWorld().getName());
        BlockDisplayRaytracer.cleanup(e.getWorld());
    }
}
