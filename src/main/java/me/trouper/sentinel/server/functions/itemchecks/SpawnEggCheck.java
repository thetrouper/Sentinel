package me.trouper.sentinel.server.functions.itemchecks;

import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class SpawnEggCheck extends AbstractCheck<ItemStack> {

    @Override
    public boolean passes(ItemStack item) {
        ServerUtils.verbose("Running spawn egg checks on item: ",item.getType().name());
        if (item.hasItemMeta() && item.getItemMeta() instanceof SpawnEggMeta sem) {
            if (sem.getSpawnedEntity() != null && !new EntitySnapshotCheck().passes(sem.getSpawnedEntity())) {
                return false;
            }
        }
        return true;
    }

    public static boolean matches(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta() instanceof SpawnEggMeta sem) {
            String eggEntityName = item.getType().name().replace("_SPAWN_EGG", "");
            return sem.getSpawnedEntity() != null &&
                    sem.getSpawnedEntity().getEntityType().name().equals(eggEntityName);
        }
        return false;
    }
}
