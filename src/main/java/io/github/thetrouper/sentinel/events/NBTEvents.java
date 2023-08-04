package io.github.thetrouper.sentinel.events;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.server.Action;
import io.github.thetrouper.sentinel.server.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class NBTEvents implements Listener {
    @EventHandler
    private void onNBTPull(InventoryCreativeEvent e) {
        if (Config.preventNBT) {
            if (!(e.getWhoClicked() instanceof Player p)) {
                return;
            }
            if (e.getCursor() == null) return;
            ItemStack i = e.getCursor();
            if (!Sentinel.isTrusted(p)) {
                if (e.getCursor().getItemMeta() == null) return;
                if (i.hasItemMeta() && i.getItemMeta() != null) {
                    Action a = new Action.Builder()
                            .setEvent(e)
                            .setAction(ActionType.NBT)
                            .setPlayer(Bukkit.getPlayer(e.getWhoClicked().getName()))
                            .setItem(e.getCursor())
                            .setDenied(Config.preventNBT)
                            .setDeoped(Config.deop)
                            .setPunished(Config.nbtPunish)
                            .setRevertGM(Config.preventNBT)
                            .setNotifyConsole(true)
                            .setNotifyTrusted(true)
                            .setnotifyDiscord(Config.logNBT)
                            .execute();
                }
            }
        }
    }
}

