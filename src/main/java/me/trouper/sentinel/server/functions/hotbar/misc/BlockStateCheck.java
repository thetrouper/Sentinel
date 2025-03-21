package me.trouper.sentinel.server.functions.hotbar.misc;

import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.server.functions.hotbar.entities.EntitySnapshotCheck;
import me.trouper.sentinel.server.functions.hotbar.items.ItemCheck;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.spawner.TrialSpawnerConfiguration;

public class BlockStateCheck extends AbstractCheck<ItemStack> {
    @Override
    public boolean passes(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        
        if (!(meta instanceof BlockStateMeta blockStateMeta)) {
            ServerUtils.verbose("Item passes due to not being a block state meta");
            return true;
        }
        
        if (item.getType().name().contains("CAMPFIRE")  ) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof Campfire campfire) {
                for (int slot = 0; slot < 4; slot++) {
                    org.bukkit.inventory.ItemStack campfireItem = campfire.getItem(slot);
                    if (campfireItem != null && !new ItemCheck().passes(campfireItem)) {
                        ServerUtils.verbose("Campfire item failed check.");
                        return false;
                    }
                }
            }
        }

        // Lectern and Chiseled Bookshelf check (by validating their inventories).
        if (item.getType().equals(Material.LECTERN)) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof Lectern lectern) {
                if (!new InventoryCheck().passes(lectern.getInventory())) {
                    ServerUtils.verbose("Lectern inventory failed check.");
                    return false;
                }
            }
        }
        if (item.getType().equals(Material.CHISELED_BOOKSHELF)) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof ChiseledBookshelf bookshelf) {
                if (!new InventoryCheck().passes(bookshelf.getInventory())) {
                    ServerUtils.verbose("Chiseled bookshelf inventory failed check.");
                    return false;
                }
            }
        }

        // Spawner check.
        if (item.getType().equals(Material.SPAWNER)) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof CreatureSpawner spawner) {
                if (spawner.getSpawnedEntity() != null) {
                    if (spawner.getSpawnedEntity().getEntityType().equals(EntityType.FALLING_BLOCK) ||
                            spawner.getSpawnedEntity().getEntityType().equals(EntityType.COMMAND_BLOCK_MINECART)) {
                        ServerUtils.verbose("Spawner contains disallowed entity type.");
                        return false;
                    }
                    if (!new EntitySnapshotCheck().passes(spawner.getSpawnedEntity())) {
                        ServerUtils.verbose("Spawner entity snapshot check failed.");
                        return false;
                    }
                }
            }
        }

        // Trial Spawner check.
        if (item.getType() == Material.TRIAL_SPAWNER) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof TrialSpawner spawner) {
                ServerUtils.verbose("Running trial spawner check.");
                if (spawner.getNormalConfiguration() != null) {
                    TrialSpawnerConfiguration config = spawner.getNormalConfiguration();
                    if (config.getSpawnedEntity() != null && !new EntitySnapshotCheck().passes(config.getSpawnedEntity())) {
                        ServerUtils.verbose("Trial Spawner failed check: Normal entity snapshot not allowed.");
                        return false;
                    }
                }
                if (spawner.getOminousConfiguration() != null) {
                    TrialSpawnerConfiguration config = spawner.getOminousConfiguration();
                    if (config.getSpawnedEntity() != null && !new EntitySnapshotCheck().passes(config.getSpawnedEntity())) {
                        ServerUtils.verbose("Trial Spawner failed check: Ominous entity snapshot not allowed.");
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
}
