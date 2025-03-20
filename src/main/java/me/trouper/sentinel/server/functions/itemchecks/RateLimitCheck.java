package me.trouper.sentinel.server.functions.itemchecks;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.itzispyder.pdk.utils.misc.Pair;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RateLimitCheck extends AbstractCheck<Pair<Player,ItemStack>> {


    public static Map<UUID, Integer> dataUsed = new HashMap<>();
    public static Map<UUID, Integer> itemsUsed = new HashMap<>();

    @Override
    public boolean passes(Pair<Player,ItemStack> input) {
        Player player = input.left;
        UUID uuid = player.getUniqueId();
        ItemStack item = input.right;
                
        return itemLimit(player,uuid,item) && dataLimit(player,uuid,item);
    }

    private boolean itemLimit(Player player, UUID uuid, ItemStack item) {
        int currentUsed = itemsUsed.getOrDefault(uuid,0);
        ServerUtils.verbose("Current Player used items: " + currentUsed);
        currentUsed++;
        itemsUsed.put(uuid,currentUsed);
        return currentUsed <= Sentinel.getInstance().getDirector().io.nbtConfig.rateLimit.rateLimitItems;
    }


    private boolean dataLimit(Player player, UUID uuid, ItemStack item) {
        int itemData = 0;
        int currentData = dataUsed.getOrDefault(uuid,0);

        ServerUtils.verbose("Current Player used data: " + currentData);
        try {
            NBTItem nbt = new NBTItem(item);
            itemData = nbt.toString().length();
            ServerUtils.verbose("Item data: " + itemData);
            currentData += itemData;
        } catch (Exception e) {
            Sentinel.getInstance().getLogger().warning("Could not determine size of item. Blocking.");
            Sentinel.getInstance().getLogger().warning(Arrays.toString(e.getStackTrace()));
            return false;
        }

        dataUsed.put(uuid,currentData);

        ServerUtils.verbose("New Player used data: " + currentData);

        return currentData <= Sentinel.getInstance().getDirector().io.nbtConfig.rateLimit.rateLimitBytes;
    }

    public static void decayData() {
        for (UUID uuid : dataUsed.keySet()) {
            int currentData = dataUsed.get(uuid);
            if (currentData > 0) {
                currentData -= Sentinel.getInstance().getDirector().io.nbtConfig.rateLimit.byteDecay;
                dataUsed.put(uuid, Math.max(0, currentData));
            }
        }
    }

    public static void decayItems() {
        for (UUID uuid : itemsUsed.keySet()) {
            int currentItems = itemsUsed.get(uuid);
            if (currentItems > 0) {
                currentItems -= Sentinel.getInstance().getDirector().io.nbtConfig.rateLimit.itemDecay;
                itemsUsed.put(uuid, Math.max(0, currentItems));
            }
        }
    }
}
