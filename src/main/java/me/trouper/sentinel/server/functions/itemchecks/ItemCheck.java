package me.trouper.sentinel.server.functions.itemchecks;

import de.tr7zw.changeme.nbtapi.NBT;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.NBTConfig;
import me.trouper.sentinel.utils.InventoryUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemCheck extends AbstractCheck<ItemStack> {
    
    public List<AbstractCheck<ItemStack>> checks;
    
    public ItemCheck() {
        enchantmentCheck = new EnchantmentCheck();
    }
    
    @Override
    public boolean passes(ItemStack item) {
        try {
            return scan(item);
        } catch (Exception ex) {
            Sentinel.getInstance().getLogger().warning("Caught an exception while handling an item check: " + Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    private boolean checksPass(ItemStack item) {
        
    }
    
    private boolean scan(ItemStack item) {
        ServerUtils.verbose("Checking item: " + item.getType().name());
        NBTConfig config = Sentinel.getInstance().getDirector().io.nbtConfig;

        // No metadata? Nothing to check.
        if (item.getItemMeta() == null) {
            ServerUtils.verbose("Item passes because it has no metadata.");
            return true;
        }
        ItemMeta meta = item.getItemMeta();

        // Check for an inventory inside the item.
        Inventory inv = InventoryUtils.getInventory(item);
        if (inv != null) {
            ServerUtils.verbose("Item contains an inventory: " + inv);
            if (!new InventoryCheck().passes(inv)) {
                ServerUtils.verbose("Item failed inventory check.");
                return false;
            }
        }

        // NBT-based checks (e.g. custom consumables/tools).
        var nbt = NBT.itemStackToNBT(item);
        var components = nbt.getCompound("components");
        if (!config.allowCustomConsumables && components.getCompound("minecraft:consumable") != null) {
            ServerUtils.verbose("Item is consumable and not allowed.");
            return false;
        }
        if (!config.allowCustomTools && components.getCompound("minecraft:tool") != null) {
            ServerUtils.verbose("Item is custom tool and not allowed.");
            return false;
        }
        var entityData = components.getCompound("minecraft:entity_data");
        if (entityData != null) {
            if (item.getType().name().contains("ITEM_FRAME")) {
                var itemData = entityData.getCompound("Item");
                ItemStack heldItem = NBT.itemStackFromNBT(itemData);
                if (heldItem != null && !new ItemCheck().passes(heldItem)) {
                    ServerUtils.verbose("Item frame contents failed check.");
                    return false;
                }
            }
            if (isSpawnEgg(item)) {
                if (entityData.hasTag("DeathTime") && entityData.getInteger("DeathTime") < 1) {
                    ServerUtils.verbose("Egg death time check failed.");
                    return false;
                }
                if (entityData.hasTag("Hurttime") && entityData.getInteger("HurtTime") < 1) {
                    ServerUtils.verbose("Egg hurt time check failed.");
                    return false;
                }
            }
        }

        // Bundle check – recursively check the contained items.
        if (item.getType().name().contains("_BUNDLE") && meta instanceof BundleMeta bm) {
            for (ItemStack bundleItem : bm.getItems()) {
                if (!passes(bundleItem)) return false;
            }
        }

        // Campfire check.
        if (item.getType().name().contains("CAMPFIRE") && meta instanceof BlockStateMeta blockStateMeta) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof Campfire campfire) {
                for (int slot = 0; slot < 4; slot++) {
                    ItemStack campfireItem = campfire.getItem(slot);
                    if (campfireItem != null && !passes(campfireItem)) {
                        ServerUtils.verbose("Campfire item failed check.");
                        return false;
                    }
                }
            }
        }

        // Lectern and Chiseled Bookshelf check (by validating their inventories).
        if (item.getType().equals(Material.LECTERN) && meta instanceof BlockStateMeta blockStateMeta) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof Lectern lectern) {
                if (!new InventoryCheck().passes(lectern.getInventory())) {
                    ServerUtils.verbose("Lectern inventory failed check.");
                    return false;
                }
            }
        }
        if (item.getType().equals(Material.CHISELED_BOOKSHELF) && meta instanceof BlockStateMeta blockStateMeta) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof ChiseledBookshelf bookshelf) {
                if (!new InventoryCheck().passes(bookshelf.getInventory())) {
                    ServerUtils.verbose("Chiseled bookshelf inventory failed check.");
                    return false;
                }
            }
        }

        // Spawner check.
        if (item.getType().equals(Material.SPAWNER) && meta instanceof BlockStateMeta blockStateMeta) {
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
        if (item.getType() == Material.TRIAL_SPAWNER && meta instanceof BlockStateMeta blockStateMeta) {
            BlockState bs = blockStateMeta.getBlockState();
            if (bs instanceof TrialSpawner trialSpawner) {
                if (!new TrialSpawnerCheck().passes(trialSpawner)) return false;
            }
        }

        // Spawn egg checks.
        if (isSpawnEgg(item)) {
            if (!SpawnEggCheck.matches(item)) {
                ServerUtils.verbose("Spawn egg match check failed.");
                return false;
            }
            if (!new SpawnEggCheck().passes(item)) {
                ServerUtils.verbose("Spawn egg check failed.");
                return false;
            }
        }

        // Name, lore, potion, attribute and enchantment checks.
        if (!config.allowName && meta.hasDisplayName()) {
            ServerUtils.verbose("Custom names not allowed.");
            return false;
        }
        if (!config.allowLore && meta.hasLore()) {
            ServerUtils.verbose("Custom lore not allowed.");
            return false;
        }
        if (!config.allowPotions &&
                (item.getType().equals(Material.POTION) ||
                        item.getType().equals(Material.SPLASH_POTION) ||
                        item.getType().equals(Material.LINGERING_POTION))) {
            ServerUtils.verbose("Potions not allowed.");
            return false;
        }
        if (!config.allowAttributes && meta.hasAttributeModifiers()) {
            ServerUtils.verbose("Attribute modifiers not allowed.");
            return false;
        }
        if (config.globalMaxEnchant != 0 && new EnchantmentCheck().hasIllegalEnchants(item)) {
            ServerUtils.verbose("Illegal enchantments found.");
            return false;
        }
        // Recursion check for use-remainder items.
        if (meta.hasUseRemainder()) {
            if (!config.allowRecursion) {
                ServerUtils.verbose("Recursion not allowed.");
                return false;
            }
            if (meta.getUseRemainder() != null && !passes(meta.getUseRemainder())) {
                ServerUtils.verbose("Use remainder item failed check.");
                return false;
            }
        }

        ServerUtils.verbose("Item passed all checks.");
        return true;
    }
    
    private boolean isSpawnEgg(ItemStack item) {
        return item.getType().name().toLowerCase().contains("spawn_egg");
    }
}
