package me.trouper.sentinel.server.functions.hotbar.entities;

import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.server.functions.hotbar.items.ItemCheck;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;

public class EquipmentCheck extends AbstractCheck<Mob> {

    @Override
    public boolean passes(Mob mob) {
        ServerUtils.verbose("Running mob check.");
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (mob.getEquipment().getItem(slot).isEmpty()) continue;
            ItemStack item = mob.getEquipment().getItem(slot);
            if (!new ItemCheck().passes(item)) {
                ServerUtils.verbose("Equipment slot did not pass.");
                return false;
            }
        }
        return true;
    }
}
