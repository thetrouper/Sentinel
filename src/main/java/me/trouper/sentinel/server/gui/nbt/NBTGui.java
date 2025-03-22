package me.trouper.sentinel.server.gui.nbt;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.storage.NBTStorage;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.PaginatedGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class NBTGui
        extends PaginatedGUI<Map.Entry<String, String>> {
    private final NBTStorage nbtStorage;

    public NBTGui() {
        this.nbtStorage = Sentinel.getInstance().getDirector().io.nbtStorage;
    }

    @Override
    protected boolean isAsynchronous() {
        return true;
    }

    @Override
    protected CustomGui backGUI() {
        return new MainGUI().home;
    }

    @Override
    protected String getTitle(Player p) {
        return Text.color("&6&lCaught NBT &7(%s/%s filtered)".formatted(this.getFilteredCount(p), Sentinel.getInstance().getDirector().io.nbtStorage.caughtItems.size()));
    }

    @Override
    protected void handleMainClick(Player p, InventoryClickEvent e) {
        e.setCancelled(true);
        MainGUI.verify(p);
        Map.Entry<String, String> entry;
        ItemStack item;
        List<Map.Entry<String, String>> filtered;
        int slot = e.getSlot();
        if (slot >= 45) {
            return;
        }
        if (e.getInventory().getItem(slot) == null) {
            return;
        }
        int page = currentPages.compute(p.getUniqueId(), (k, v) -> this.realizePage(p, v == null ? 0 : v));
        int index = page * 45 + slot;
        if (index < (filtered = this.filterEntries(p, chosenOperator.computeIfAbsent(p.getUniqueId(), v -> PaginatedGUI.FilterOperator.AND))).size() && (item = NBTStorage.getItem((entry = filtered.get(index)).getKey())) != null) {
            if (e.isLeftClick()) {
                p.getInventory().addItem(item);
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
            } else if (e.isRightClick() && this.nbtStorage.deleteItem(entry.getKey())) {
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 2.0f);
                e.getClickedInventory().setItem(slot, ItemBuilder.create()
                                .material(Material.STRUCTURE_VOID)
                                .name(Text.color("&cDeleted Item"))
                                .lore(Text.color("&7You have deleted this item."))
                                .lore(Text.color("&7This will disappear next refresh."))
                        .build());
            }
        }
    }

    @Override
    protected ItemStack createDisplayItem(Map.Entry<String, String> entry) {
        ItemStack item = NBTStorage.getItem(entry.getKey());
        if (item == null) {
            return null;
        }
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Text.color("&7NBT Name: " + LegacyComponentSerializer.legacyAmpersand().serialize(item.effectiveName())));
        lore.add("");
        lore.add(Text.color("&7Owner: " + Bukkit.getOfflinePlayer(UUID.fromString(entry.getValue())).getName()));
        lore.add("");
        lore.add(Text.color("&eLeft-Click to give item"));
        lore.add(Text.color("&eRight-Click to delete item"));
        return new ItemBuilder().material(item.getType()).name(Text.color("&b" + item.getType().name())).lore(lore).build();
    }

    @Override
    protected void addFilterItems(CustomGui.GuiBuilder filterGui, Player p, Set<String> filters) {
    }

    @Override
    protected List<Map.Entry<String, String>> filterEntries(Player p, PaginatedGUI.FilterOperator operator) {
        Set filters = activeFilters.computeIfAbsent(p.getUniqueId(), k -> new HashSet());
        ServerUtils.verbose("Filtering entries for %s. Current: ", p, filters.toString());
        return this.nbtStorage.caughtItems.entrySet().stream().filter(entry -> {
            if (filters.isEmpty()) {
                return true;
            }
            boolean result = operator == PaginatedGUI.FilterOperator.AND;
            Iterator iterator = filters.iterator();
            while (iterator.hasNext()) {
                String filter;
                boolean conditionMet = switch (filter = (String)iterator.next()) {
                    case "OWNER" -> (entry.getValue()).equals(p.getUniqueId().toString());
                    default -> false;
                };
                result = operator.apply(result, conditionMet);
                if (operator == PaginatedGUI.FilterOperator.AND && !result) {
                    return false;
                }
                if (operator != PaginatedGUI.FilterOperator.OR || !result) continue;
                return true;
            }
            return result;
        }).collect(Collectors.toList());
    }
}
