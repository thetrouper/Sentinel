package me.trouper.sentinel.server.gui.nbt;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.Pair;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.storage.NBTStorage;
import me.trouper.sentinel.server.gui.PaginatedGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class NBTGui extends PaginatedGUI<Map.Entry<String,String>> {

    private final NBTStorage nbtStorage;

    public NBTGui() {
        this.nbtStorage = Sentinel.getInstance().getDirector().io.nbtStorage;
    }

    @Override
    protected String getTitle(Player p) {
        return Text.color("&6&lItem Ownership &7(" + getFilterCount(p) + " items)");
    }

    @Override
    protected void handleMainClick(Player p, InventoryClickEvent e) {
        int slot = e.getSlot();
        if (slot >= 45) return;
        if (e.getInventory().getItem(slot) == null) return;
        int page = currentPages.compute(p.getUniqueId(), (k, v) -> realizePage(p, v == null ? 0 : v));
        List<Map.Entry<String, String>> filtered = filterEntries(p, chosenOperator.computeIfAbsent(p.getUniqueId(), v -> FilterOperator.AND));
        int index = page * ITEMS_PER_PAGE + slot;
        if (index < filtered.size()) {
            Map.Entry<String, String> entry = filtered.get(index);
            ItemStack item = NBTStorage.toItem(entry.getKey());
            if (item != null) {
                if (e.isLeftClick()) {
                    p.getInventory().addItem(item);
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1F);
                } else if (e.isRightClick()) {
                    nbtStorage.caughtItems.remove(entry.getKey());
                    p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2F);
                    p.openInventory(createGUI(p).getInventory());
                }
            }
        }
    }

    @Override
    protected ItemStack createDisplayItem(Map.Entry<String, String> entry) {
        ItemStack item = NBTStorage.toItem(entry.getKey());
        if (item == null) return null;

        List<String> lore = new ArrayList<>();
        lore.add(Text.color("&7Owner: " + Bukkit.getOfflinePlayer(UUID.fromString(entry.getValue())).getName()));
        lore.add("");
        lore.add(Text.color("&eLeft-Click to give item"));
        lore.add(Text.color("&eRight-Click to delete item"));

        return new ItemBuilder()
                .material(item.getType())
                .name(Text.color("&b" + item.getType().name()))
                .lore(lore)
                .build();
    }

    @Override
    protected void addFilterItems(CustomGui.GuiBuilder filterGui, Player p, Set<String> filters) {
        // Add any specific filter items here if needed
    }

    @Override
    protected List<Map.Entry<String, String>> filterEntries(Player p, FilterOperator operator) {
        Set<String> filters = activeFilters.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        ServerUtils.verbose("Filtering entries for %s. Current: ", p, filters.toString());

        return nbtStorage.caughtItems.entrySet().stream()
                .filter(entry -> {
                    if (filters.isEmpty()) return true;
                    boolean result = (operator == FilterOperator.AND); // AND starts true, OR starts false
                    for (String filter : filters) {
                        boolean conditionMet = switch (filter) {
                            case "OWNER" -> entry.getValue().equals(p.getUniqueId().toString());
                            default -> false;
                        };
                        result = operator.apply(result, conditionMet);
                        // Early exit for AND (false means no need to check further)
                        if (operator == FilterOperator.AND && !result) return false;
                        // Early exit for OR (true means we already pass)
                        if (operator == FilterOperator.OR && result) return true;
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }
}
