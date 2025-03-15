package me.trouper.sentinel.server.functions.itemchecks;

import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;

public class EquipmentCheck extends AbstractCheck<Mob> {

    @Override
    public boolean passes(Mob mob) {
        ServerUtils.verbose("Running mob check.");
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = mob.getEquipment().getItem(slot);
            if (item != null && !new ItemCheck().passes(item)) {
                return false;
            }
        }
        return true;
    }
}
