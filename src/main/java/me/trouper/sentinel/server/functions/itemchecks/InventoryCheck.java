package me.trouper.sentinel.server.functions.itemchecks;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.itemchecks.AbstractCheck;
import me.trouper.sentinel.server.functions.itemchecks.ItemCheck;
import me.trouper.sentinel.utils.InventoryUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryCheck extends AbstractCheck<Inventory> {

    @Override
    public boolean passes(Inventory inventory) {
        ServerUtils.verbose("Running Inventory Check");
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType().isAir()) continue;
            if (!new ItemCheck().passes(item)) {
                ServerUtils.verbose("Inventory item failed check.");
                return false;
            }
            Inventory subInventory = InventoryUtils.getInventory(item);
            if (subInventory != null && !Sentinel.getInstance().getDirector().io.nbtConfig.allowRecursion) return false;
            if (subInventory != null && !passes(subInventory)) {
                ServerUtils.verbose("Sub-inventory failed check.");
                return false;
            }
        }
        ServerUtils.verbose("Inventory passed all checks.");
        return true;
    }
}
