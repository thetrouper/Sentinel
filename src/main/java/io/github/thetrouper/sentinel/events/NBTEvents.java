package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.TakeAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class NBTEvents implements Listener {
    @EventHandler
    private void onNBTPull(InventoryCreativeEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        if (e.getCursor() == null) return;
        ItemStack i = e.getCursor();
        if (!Sentinel.isTrusted(p)) {
            if (e.getCursor().getItemMeta() == null) return;
            if (i.hasItemMeta() && i.getItemMeta() != null) {
                TakeAction.NBT(e);
            }
        }
    }
}
