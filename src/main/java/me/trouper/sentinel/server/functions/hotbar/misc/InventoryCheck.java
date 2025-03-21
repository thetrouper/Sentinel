package me.trouper.sentinel.server.functions.hotbar.misc;

import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.server.functions.hotbar.items.ItemCheck;
import me.trouper.sentinel.utils.InventoryUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryCheck extends AbstractCheck<Inventory> {

    @Override
    public boolean passes(Inventory inv) {
        ServerUtils.verbose("Running Inventory Check");

        for (ItemStack i : inv.getContents()) {
            if (i == null || i.getType().isAir()) continue;
            if (!new ItemCheck().passes(i)) {
                ServerUtils.verbose("Inventory item failed check.");
                return false;
            }
            Inventory subInventory = InventoryUtils.getInventory(i);
            if (subInventory != null && !config.allowRecursion) {
                ServerUtils.verbose("Recursion is disabled. Failing check.");
                return false;
            }
            if (subInventory != null && !passes(subInventory)) {
                ServerUtils.verbose("Sub-inventory failed check.");
                return false;
            }
        }
        ServerUtils.verbose("Inventory passed all checks.");
        return true;
    } 
}
