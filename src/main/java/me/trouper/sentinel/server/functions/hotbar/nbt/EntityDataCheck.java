package me.trouper.sentinel.server.functions.hotbar.nbt;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.server.functions.hotbar.items.ItemCheck;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.inventory.ItemStack;

public class EntityDataCheck extends AbstractCheck<ReadWriteNBT> {
    @Override
    public boolean passes(ReadWriteNBT entityData) {
        if (entityData == null) {
            ServerUtils.verbose("Entity Data check passed. There was no data.");
            return true;
        }

        ReadWriteNBT itemData = entityData.getCompound("Item");
        if (itemData != null) {
            ServerUtils.verbose("Entity data holds an item");
            ItemStack heldItem = NBT.itemStackFromNBT(itemData);
            if (heldItem != null && !new ItemCheck().passes(heldItem)) {
                ServerUtils.verbose("Item contents failed check.");
                return false;
            }
        }

        if (entityData.hasTag("DeathTime") && entityData.getInteger("DeathTime") < 1) {
            ServerUtils.verbose("Death time check failed.");
            return false;
        }
        if (entityData.hasTag("HurtTime") && entityData.getInteger("HurtTime") < 1) {
            ServerUtils.verbose("Hurt time check failed.");
            return false;
        }

        ServerUtils.verbose("Entity Data check passed. There was no flagging.");
        return true;
    }
}
