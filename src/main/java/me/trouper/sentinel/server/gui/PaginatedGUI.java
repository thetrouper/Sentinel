package me.trouper.sentinel.server.gui;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class PaginatedGUI<T> {

    protected static final int ITEMS_PER_PAGE = 45;
    protected static final Map<UUID, Integer> currentPages = new HashMap<>();
    protected static final Map<UUID, Set<String>> activeFilters = new HashMap<>();
    protected static final Map<UUID, FilterOperator> chosenOperator = new HashMap<>();

    protected abstract CustomGui backGUI();
    
    public CustomGui createGUI(Player p) {
        ServerUtils.verbose("Creating GUI for player: %s", p.getName());
        int page = currentPages.compute(p.getUniqueId(), (k, v) -> realizePage(p, v == null ? 0 : v));
        return CustomGui.create()
                .title(getTitle(p))
                .size(54)
                .onDefine(inv -> setupPage(p, inv))
                .defineMain(e -> handleMainClick(p, e))
                .define(45, createNavigationItem("Previous", page - 1), e -> changePage(p, -1))
                .define(49, createFilterItem(p), e -> openFilterMenu(p))
                .define(53, createNavigationItem("Next", page + 1), e -> changePage(p, 1))
                .build();
    }

    protected abstract String getTitle(Player p);

    protected void setupPage(Player p, Inventory inv) {
        ServerUtils.verbose(1,"Setting up page for player: %s", p.getName());
        int page = currentPages.compute(p.getUniqueId(), (k, v) -> realizePage(p, v == null ? 0 : v));
        List<T> filtered = filterEntries(p, chosenOperator.computeIfAbsent(p.getUniqueId(), v -> FilterOperator.AND));
        ServerUtils.verbose(1,"Current page: %d, Total entries: %d", page, filtered.size());

        // Clear previous items
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            inv.setItem(i, null);
        }

        // Add paginated items
        for (int i = page * ITEMS_PER_PAGE; i < (page + 1) * ITEMS_PER_PAGE && i < filtered.size(); i++) {
            T item = filtered.get(i);
            inv.setItem(i % ITEMS_PER_PAGE, createDisplayItem(item));
        }

        // Add persistent bottom items
        inv.setItem(45, createNavigationItem("Previous", realizePage(p, page - 1)));
        inv.setItem(49, createFilterItem(p));
        inv.setItem(53, createNavigationItem("Next", realizePage(p, page + 1)));
    }

    protected abstract void handleMainClick(Player p, InventoryClickEvent e);

    protected abstract ItemStack createDisplayItem(T item);

    protected void openFilterMenu(Player p) {
        ServerUtils.verbose(1,"Creating filter menu for %s", p);
        Set<String> filters = activeFilters.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());

        CustomGui.GuiBuilder filterGui = CustomGui.create()
                .title(Text.color("&6&lFilters"))
                .size(27)
                .defineMain(e -> e.setCancelled(true))
                .define(26, Items.BACK, e -> {
                    p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 0.8F);
                    p.openInventory(createGUI(p).getInventory());
                });

        addFilterItems(filterGui, p, filters);

        p.openInventory(filterGui.build().getInventory());
    }

    protected abstract void addFilterItems(CustomGui.GuiBuilder filterGui, Player p, Set<String> filters);

    protected void toggleFilter(Player p, String filter) {
        Set<String> filters = activeFilters.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        ServerUtils.verbose("%s is now toggling the %s filter. Current %s", p, filter, filters);
        if (filters.contains(filter)) filters.remove(filter);
        else filters.add(filter);
        ServerUtils.verbose("Current filters for %s: %s", p, filters);
        openFilterMenu(p);
    }

    protected int getFilterCount(Player p) {
        return activeFilters.getOrDefault(p.getUniqueId(), new HashSet<>()).size();
    }

    protected void changePage(Player p, int direction) {
        int current = currentPages.getOrDefault(p.getUniqueId(), 0);
        if (current + direction < 0) {
            p.openInventory(backGUI().getInventory());
            return;
        }
        int newPage = realizePage(p, current + direction);
        currentPages.put(p.getUniqueId(), newPage);
        p.openInventory(createGUI(p).getInventory());
    }

    protected int realizePage(Player p, int requested) {
        int validRequested = Math.max(0, requested);
        int totalEntries = filterEntries(p, chosenOperator.computeIfAbsent(p.getUniqueId(), v -> FilterOperator.AND)).size();
        int maxPages = Math.max(0, Math.ceilDiv(totalEntries, ITEMS_PER_PAGE) - 1);
        return Math.min(validRequested, maxPages);
    }

    private ItemStack createNavigationItem(String direction, int pageTo) {
        if (pageTo < 0) {
            return Items.BACK;
        }
        return new ItemBuilder()
                .material(Material.ARROW)
                .name(Text.color("&b" + direction + "&7 Page"))
                .lore(Text.color("&7 > &b" + pageTo))
                .build();
    }

    private ItemStack createFilterItem(Player p) {
        List<String> operatorList = new ArrayList<>();
        FilterOperator chosen = chosenOperator.computeIfAbsent(p.getUniqueId(), v -> FilterOperator.AND);
        for (FilterOperator value : FilterOperator.values()) {
            if (value.equals(chosen)) operatorList.add(Text.color("&b&n" + value.name()));
            else operatorList.add(Text.color("&b" + value.name()));
        }
        return new ItemBuilder()
                .material(Material.HOPPER)
                .name(Text.color("&6&lFilters"))
                .lore(Text.color("&7Filters Selected: &e" + getFilterCount(p)))
                .lore(Text.color("&7Shift-Click to cycle filter operator."))
                .lore(Text.color("&7Operator: "))
                .lore(operatorList)
                .build();
    }

    protected abstract List<T> filterEntries(Player p, FilterOperator operator);

    public enum FilterOperator {
        AND,  // All conditions must be met
        OR,   // At least one condition must be met
        NAND, // At least one condition must NOT be met
        XOR;  // Exactly one condition must be met

        public boolean apply(boolean currentValue, boolean newCondition) {
            return switch (this) {
                case AND -> currentValue & newCondition;
                case OR -> currentValue | newCondition;
                case NAND -> !(currentValue & newCondition);
                case XOR -> currentValue ^ newCondition;
            };
        }
    }
}