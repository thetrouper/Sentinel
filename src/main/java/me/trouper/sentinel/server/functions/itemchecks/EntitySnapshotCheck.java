package me.trouper.sentinel.server.functions.itemchecks;

import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;

public class EntitySnapshotCheck extends AbstractCheck<EntitySnapshot> {

    @Override
    public boolean passes(EntitySnapshot input) {
        Location loc = new Location(Bukkit.getWorlds().getFirst(), 0, 1000000, 0);
        Entity temp = input.createEntity(loc);
        boolean result = new EntityCheck().passes(temp);
        ServerUtils.verbose("Temp Entity %s Entity Check", result ? "failed" : "passed");
        temp.remove();
        return result;
    }
}
