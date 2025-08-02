package me.trouper.sentinel.server.functions.hotbar.items;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.server.functions.hotbar.entities.EntitySnapshotCheck;
import me.trouper.sentinel.server.functions.hotbar.nbt.EntityDataCheck;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class SpawnEggCheck extends AbstractCheck<ItemStack> {

    @Override
    public boolean passes(ItemStack item) {
        ServerUtils.verbose("Running spawn egg checks on item: ",item.getType().name());
        if (!item.getType().name().toLowerCase().contains("spawn_egg")) return true;
        if (!SpawnEggCheck.entityMatches(item)) {
            ServerUtils.verbose("Spawn egg entity doesn't match item type.");
            return false;
        }
        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        ReadWriteNBT components = nbt.getCompound("components");
        if (components != null) {
            var entityData = components.getCompound("minecraft:entity_data");
            if (!new EntityDataCheck().passes(entityData)) {
                ServerUtils.verbose("Spawn egg entity data check failed.");
                return false;
            }
        }

        if (item.hasItemMeta() && item.getItemMeta() instanceof SpawnEggMeta sem) {
            if (sem.getSpawnedEntity() != null && !new EntitySnapshotCheck().passes(sem.getSpawnedEntity())) {
                ServerUtils.verbose("Spawn egg entity snapshot check failed.");
                return false;
            }
        }
        
        return true;
    }

    public static boolean entityMatches(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta() instanceof SpawnEggMeta sem) {
            String eggEntityName = item.getType().name().replace("_SPAWN_EGG", "");
            return sem.getSpawnedEntity() != null &&
                    sem.getSpawnedEntity().getEntityType().name().equals(eggEntityName);
        }
        return false;
    }
}
