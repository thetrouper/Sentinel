package me.trouper.sentinel.server.functions.hotbar.items;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.server.functions.hotbar.misc.BlockStateCheck;
import me.trouper.sentinel.server.functions.hotbar.misc.InventoryCheck;
import me.trouper.sentinel.server.functions.hotbar.nbt.ComponentCheck;
import me.trouper.sentinel.utils.InventoryUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemCheck extends AbstractCheck<ItemStack> {
    
    
    @Override
    public boolean passes(ItemStack item) {
        try {
            return scan(item);
        } catch (Exception ex) {
            Sentinel.getInstance().getLogger().warning("Caught an exception while handling an item check: " + Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    
    private boolean scan(ItemStack item) {
        ServerUtils.verbose("Checking item: " + item.getType().name());

        // No metadata? Nothing to check.
        if (item.getItemMeta() == null) {
            ServerUtils.verbose("Item passes because it has no metadata.");
            return true;
        }
        
        if (!new MetaCheck().passes(item)) {
            ServerUtils.verbose("Item failed metadata check.");
            return false;
        }
        
        
        // NBT-based checks
        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        ReadWriteNBT components = nbt.getCompound("components");
        if (components != null) {
            if (!new ComponentCheck().passes(components)) {
                ServerUtils.verbose("Components check failed.");
                return false;
            }
        }
        
        // Spawn egg checks.
        if (!new SpawnEggCheck().passes(item)) {
            ServerUtils.verbose("Spawn egg check failed.");
            return false;
        }

        if (!new BlockStateCheck().passes(item)) {
            ServerUtils.verbose("Block State check failed.");
            return false;
        }

        // Check for an inventory inside the item.
        Inventory inv = InventoryUtils.getInventory(item);
        if (inv != null) {
            ServerUtils.verbose("Item contains an inventory: " + inv);
            if (!new InventoryCheck().passes(inv)) {
                ServerUtils.verbose("Item failed inventory check.");
                return false;
            }
        }

        ServerUtils.verbose("Item passed all checks.");
        return true;
    }
}
