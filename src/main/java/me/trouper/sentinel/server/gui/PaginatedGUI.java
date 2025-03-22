package me.trouper.sentinel.server.gui;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PaginatedGUI<T> {

    protected static final int ITEMS_PER_PAGE = 45;
    protected static final Map<UUID, Integer> currentPages = new HashMap<>();
    protected static final Map<UUID, Set<String>> activeFilters = new HashMap<>();
    protected static final Map<UUID, FilterOperator> chosenOperator = new HashMap<>();

    protected abstract CustomGui backGUI();
    protected boolean isAsynchronous() {
        return false;
    };
    
    
    public CustomGui createGUI(Player p) {
        ServerUtils.verbose("Creating GUI for player: %s", p.getName());
        int page = currentPages.compute(p.getUniqueId(), (k, v) -> realizePage(p, v == null ? 0 : v));
        return CustomGui.create()
                .title(getTitle(p))
                .size(54)
                .onDefine(inv -> setupPage(p, inv, isAsynchronous()))
                .defineMain(e -> handleMainClick(p, e))
                .define(45, createNavigationItem("Previous", page - 1), e -> changePage(p, -1))
                .define(49, createFilterItem(p), e -> openFilterMenu(p))
                .define(53, createNavigationItem("Next", page + 1), e -> changePage(p, 1))
                .build();
    }

    protected abstract String getTitle(Player p);

    protected void setupPage(Player p, Inventory inv, boolean runAsynchronously) {
        ServerUtils.verbose(1, "Setting up page for player: %s", p.getName());
        int page = currentPages.compute(p.getUniqueId(), (k, v) -> realizePage(p, v == null ? 0 : v));
        FilterOperator operator = chosenOperator.computeIfAbsent(p.getUniqueId(), v -> FilterOperator.AND);
        
        // Add persistent bottom items (navigation and filter)
        inv.setItem(45, createNavigationItem("Previous", realizePage(p, page - 1)));
        inv.setItem(49, createFilterItem(p));
        inv.setItem(53, createNavigationItem("Next", realizePage(p, page + 1)));

        // Fill the remaining bottom slots with red stained glass
        for (int slot : new int[]{46, 47, 48, 50, 51, 52}) {
            inv.setItem(slot, createPlaceholderItem(true));
        }

        Runnable task = ()->{
            List<T> filtered = filterEntries(p, operator);
            int totalEntries = filtered.size();
            int startIndex = page * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalEntries);
            List<T> pageEntries = filtered.subList(startIndex, endIndex);
            int pageSize = pageEntries.size();

            AtomicInteger remaining = new AtomicInteger(pageSize);

            // Process each entry and update GUI as each item loads
            for (int i = 0; i < pageSize; i++) {
                T entry = pageEntries.get(i);
                ItemStack displayItem = createDisplayItem(entry);
                int slot = i;

                Bukkit.getScheduler().runTask(Sentinel.getInstance(), () -> {
                    inv.setItem(slot, displayItem);
                    if (runAsynchronously) p.playSound(p, Sound.UI_HUD_BUBBLE_POP, SoundCategory.MASTER,1,1.1F);
                    if (remaining.decrementAndGet() == 0) {
                        // Update remaining main slots and bottom slots to lime
                        for (int bottomSlot : new int[]{46, 47, 48, 50, 51, 52}) {
                            inv.setItem(bottomSlot,  createPlaceholderItem(false));
                        }
                        if (runAsynchronously) p.playSound(p,Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER,1,0.8F);
                    }
                });
            }

            // Handle case where there are no items
            if (pageSize == 0) {
                Bukkit.getScheduler().runTask(Sentinel.getInstance(), () -> {
                    for (int bottomSlot : new int[]{46, 47, 48, 50, 51, 52}) {
                        inv.setItem(bottomSlot, createPlaceholderItem(false));
                    }
                    if (runAsynchronously) p.playSound(p,Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER,1,0.8F);
                });
            }
        };

        // Start async loading of items
        if (runAsynchronously) Bukkit.getScheduler().runTaskAsynchronously(Sentinel.getInstance(), task);
        else task.run();
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
        p.playSound(p,Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER,1,0.8F);
    }

    protected abstract void addFilterItems(CustomGui.GuiBuilder filterGui, Player p, Set<String> filters);

    protected void toggleFilter(Player p, String filter) {
        Set<String> filters = activeFilters.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        ServerUtils.verbose("%s is now toggling the %s filter. Current %s", p, filter, filters);
        if (filters.contains(filter)) {
            filters.remove(filter);
            p.playSound(p,Sound.UI_BUTTON_CLICK, SoundCategory.MASTER,1,0.8F);
        } else {
            filters.add(filter);
            p.playSound(p,Sound.UI_BUTTON_CLICK, SoundCategory.MASTER,1,1F);
        }
        ServerUtils.verbose("Current filters for %s: %s", p, filters);

        openFilterMenu(p);
    }

    protected int getFilteredCount(Player p) {
        return filterEntries(p,chosenOperator.getOrDefault(p.getUniqueId(),FilterOperator.AND)).size();
    }

    private int getFilterCount(Player p) {
        return activeFilters.get(p.getUniqueId()).size();
    }


    protected void changePage(Player p, int direction) {
        int current = currentPages.getOrDefault(p.getUniqueId(), 0);
        if (current + direction < 0) {
            p.playSound(p, Sound.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER,1,0.8F);
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

    private ItemStack createPlaceholderItem(boolean isRed) {
        Material material = isRed ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE;
        String name = isRed ? "&cComputing Entries..." : "&aAll Entries Loaded.";
        return new ItemBuilder()
                .material(material)
                .name(Text.color(name))
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