package me.trouper.sentinel.server.functions.hotbar.entities;

import de.tr7zw.changeme.nbtapi.NBT;
import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.server.functions.hotbar.items.ItemCheck;
import me.trouper.sentinel.server.functions.hotbar.misc.InventoryCheck;
import me.trouper.sentinel.utils.InventoryUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.concurrent.atomic.AtomicBoolean;

public class EntityCheck extends AbstractCheck<Entity> {

    @Override
    public boolean passes(Entity entity) {
        if (entity instanceof Item itemEntity) {
            if (!new ItemCheck().passes(itemEntity.getItemStack())) {
                ServerUtils.verbose("Entity failed check: Item not allowed.");
                return false;
            }
        }
        Inventory inv = InventoryUtils.getInventory(entity);
        if (inv != null && !new InventoryCheck().passes(inv)) {
            ServerUtils.verbose("Entity inventory failed check.");
            return false;
        }
        if (entity instanceof Villager villager) {
            for (MerchantRecipe recipe : villager.getRecipes()) {
                if (!new ItemCheck().passes(recipe.getResult())) {
                    ServerUtils.verbose("Villager recipe failed check.");
                    return false;
                }
            }
        }
        if (entity instanceof Mob mob) {
            if (!new EquipmentCheck().passes(mob)) {
                ServerUtils.verbose("Mob equipment failed check.");
                return false;
            }
        }
        if (!entity.getPassengers().isEmpty()) {
            if (!config.allowRecursion) {
                ServerUtils.verbose("Entity recursion not allowed.");
                return false;
            }
            for (Entity passenger : entity.getPassengers()) {
                if (!passes(passenger)) {
                    ServerUtils.verbose("Entity passenger failed check.");
                    return false;
                }
            }
        }
        AtomicBoolean failsTiming = new AtomicBoolean(false);
        NBT.get(entity, nbt -> {
            if (nbt.hasTag("DeathTime") && nbt.getInteger("DeathTime") < 1) {
                ServerUtils.verbose("Entity death time check failed.");
                failsTiming.set(true);
            }
            if (nbt.hasTag("HurtTime") && nbt.getInteger("HurtTime") < 1) {
                ServerUtils.verbose("Entity hurt time check failed.");
                failsTiming.set(true);
            }
        });
        if (failsTiming.get()) {
            ServerUtils.verbose("Entity timing check failed.");
            return false;
        }
        return true;
    }
}
