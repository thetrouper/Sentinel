package me.trouper.sentinel.server.gui.whitelist;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class WhitelistGUI {

    private static final Map<UUID, Integer> currentPages = new HashMap<>();
    private static final Map<UUID, Set<Filter>> activeFilters = new HashMap<>();
    private static final Map<UUID, FilterOperator> chosenOperator = new HashMap<>();

    public CustomGui createGUI(Player player) {
        ServerUtils.verbose("Creating GUI for player: %s", player.getName());
        int page = currentPages.compute(player.getUniqueId(), (k,v) -> realizePage(player,realizePage(player,(v == null ? 0 : v))));
        return CustomGui.create()
                .title(Text.color("&6&lCommand Blocks &7(" + getFilterCount(player) + " filters)"))
                .size(54)
                .onDefine(inv -> setupPage(player, inv))
                .defineMain(e -> {
                    e.setCancelled(true);
                    handleMainClick(player, e);
                })
                .define(45, createNavigationItem("Previous",page - 1), e -> {
                    player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,1,0.9F);
                    changePage(player, -1);
                })
                .define(49, createFilterItem(player), e -> {
                    if (e.isShiftClick()) {
                        FilterOperator op = chosenOperator.computeIfAbsent(player.getUniqueId(),v-> FilterOperator.AND);
                        FilterOperator[] values = FilterOperator.values();
                        chosenOperator.put(player.getUniqueId(),values[(op.ordinal() + 1) % values.length]);
                        e.getClickedInventory().setItem(e.getSlot(),createFilterItem(player));
                        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,1,1.3F);
                        return;
                    }
                    openFilterMenu(player);
                })
                .define(53, createNavigationItem("Next",page + 1), e -> {
                    player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,1,1.1F);
                    changePage(player, 1);
                })
                .build();
    }

    private void setupPage(Player player, Inventory inv) {
        ServerUtils.verbose("Setting up page for player: %s", player.getName());
        int page = currentPages.compute(player.getUniqueId(), (k,v) -> realizePage(player,realizePage(player,(v == null ? 0 : v))));
        List<CommandBlockHolder> filtered = filterEntries(player,chosenOperator.computeIfAbsent(player.getUniqueId(),v->FilterOperator.AND));
        ServerUtils.verbose("Current page: %d, Total entries: %d", page, filtered.size());

        // Clear previous items
        for (int i = 0; i < 45; i++) {
            inv.setItem(i, null);
        }

        // Add paginated items
        for (int i = page * 45; i < (page + 1) * 45 && i < filtered.size(); i++) {
            CommandBlockHolder holder = filtered.get(i);
            inv.setItem(i % 45, createDisplayItem(holder));
        }

        // Add persistent bottom items
        inv.setItem(45, createNavigationItem("Previous",realizePage(player, page - 1)));
        inv.setItem(49, createFilterItem(player));
        inv.setItem(53, createNavigationItem("Next", realizePage(player,page + 1)));
    }

    private void handleMainClick(Player player, InventoryClickEvent e) {
        int slot = e.getSlot();
        if (slot >= 45) return;
        if (e.getInventory().getItem(slot) == null) return;

        int page = currentPages.compute(player.getUniqueId(), (k,v) -> realizePage(player,realizePage(player,(v == null ? 0 : v))));
        List<CommandBlockHolder> filtered = filterEntries(player,chosenOperator.computeIfAbsent(player.getUniqueId(),v->FilterOperator.AND));
        int index = page * 45 + slot;

        if (index < filtered.size()) {
            CommandBlockHolder holder = filtered.get(index);
            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_CHIME,1,0.8F);
            openManagementMenu(player, holder);
        }
    }
    
    private ItemStack createDisplayItem(CommandBlockHolder holder) {
        //ServerUtils.verbose("Creating Display Item for a command block owned by %s. Type is ", holder.owner(), holder.type());
        
        Material type = holder.getType();

        //ServerUtils.verbose("Type material is %s", type.name());

        String name = holder.loc().isUUID() ?
                "Minecart: " + holder.loc().toUIID() :
                String.format("X: %d, Y: %d, Z: %d",
                        (int) holder.loc().x(),
                        (int) holder.loc().y(),
                        (int) holder.loc().z());

        //ServerUtils.verbose("Name is %s", name);

        List<String> lore = new ArrayList<>();
        lore.add(Text.color("&7Owner: " + Bukkit.getOfflinePlayer(holder.owner()).getName()));
        //ServerUtils.verbose("Got owner");
        lore.add(Text.color("&7Command: &f" + holder.command()));
        //ServerUtils.verbose("Got command");
        lore.add(Text.color("&7Type: &f" + holder.type()));
        //ServerUtils.verbose("Got type");
        lore.add(Text.color("&7Whitelisted: " + (holder.isWhitelisted() ? "&aYes" : "&cNo")));
        //ServerUtils.verbose("Got whitelist status");
        lore.add(Text.color("&7Present: " + (holder.isPresent() ? "&aYes" : "&cNo")));
        //ServerUtils.verbose("Got Present Status");
        lore.add("");
        lore.add(Text.color("&eClick to manage!"));

        //ServerUtils.verbose("Successfully created item!");

        return new ItemBuilder()
                .material(type)
                .name(Text.color("&b" + name))
                .lore(lore)
                .build();
    }

    private void openManagementMenu(Player player, CommandBlockHolder holder) {
        ServerUtils.verbose("Opening management menu for %s", holder.owner());
        boolean whitelisted = holder.isWhitelisted();

        CustomGui menu = CustomGui.create()
                .title(Text.color("&l ⬇ &6&lManaging Command Block"))
                .size(9)
                .defineMain(e -> e.setCancelled(true))
                .define(0,createDisplayItem(holder))
                .define(2, createActionItem(whitelisted ? "Un-Whitelist" : "Whitelist",  whitelisted ? Material.BARRIER : Material.PAPER), e -> {
                    if (whitelisted) holder.removeFromWhitelist();
                    else holder.addToWhitelist();
                    player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1,1F);
                    openManagementMenu(player,holder);
                })
                .define(3, createActionItem("Teleport", Material.ENDER_PEARL), e -> {
                    if (holder.loc().isUUID()) {
                        // Handle minecart teleport
                        Entity entity = Bukkit.getEntity(holder.loc().toUIID());
                        if (entity == null) {
                            e.getInventory().setItem(e.getSlot(),ItemBuilder.create()
                                            .material(Material.BARRIER)
                                            .name("&cTeleport Unavailable")
                                            .lore("&7This entity is not loaded.")
                                    .build());
                            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS,1,1F);
                            return;
                        }
                        player.teleport(entity.getLocation());
                    } else {
                        player.teleport(holder.loc().translate());
                    }
                    player.playSound(player.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,0.5F);
                    player.closeInventory();
                })
                .define(4, createActionItem("Restore", Material.DISPENSER), e -> {
                    holder.restore();
                    player.openInventory(createGUI(player).getInventory());
                    player.playSound(player.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_RESONATE,1,1F);
                })
                .define(5, createActionItem("Destroy (Shift-Click)", Material.NETHERITE_PICKAXE), e -> {
                    if (!e.isShiftClick()) return;
                    holder.destroy();
                    player.playSound(player.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,2F);
                    player.openInventory(createGUI(player).getInventory());
                })
                .define(6,createActionItem("Take Ownership",Material.NAME_TAG), e -> {
                    CommandBlockHolder updated = new CommandBlockHolder(
                            player.getUniqueId().toString(),
                            holder.loc(),
                            holder.facing(),
                            holder.type(),
                            holder.auto(),
                            holder.conditional(),
                            holder.command()
                    );
                    holder.destroy();
                    if (whitelisted) updated.addToWhitelist();
                    updated.restore();
                    player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_TRADE,1,1F);
                    openManagementMenu(player,holder);
                })
                .define(8,Items.BACK,e->{
                    player.playSound(player.getLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,0.8F);
                    player.openInventory(createGUI(player).getInventory());
                })
                .build();

        player.openInventory(menu.getInventory());
    }

    private ItemStack createActionItem(String name, Material mat) {
        return new ItemBuilder()
                .material(mat)
                .name(Text.color("&b" + name))
                .lore(Text.color("&7Click to " + name.toLowerCase()))
                .build();
    }

    // Filter handling methods
    private enum Filter {
        OWNER, CURRENT_WORLD, OTHER_OWNERS,
        MINECART, REPEAT, CHAIN, IMPULSE,
        WHITELISTED, NOT_WHITELISTED, NOT_PRESENT
    }

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

    private List<CommandBlockHolder> filterEntries(Player player, FilterOperator operator) {
        Set<Filter> filters = activeFilters.computeIfAbsent(player.getUniqueId(), v -> new HashSet<>());
        ServerUtils.verbose("Filtering entries for %s. Current: ", player,filters.toString());
        return Sentinel.getInstance().getDirector().io.commandBlocks.existing.stream()
                .filter(holder -> {
                    if (filters.isEmpty()) return true;

                    boolean result = (operator == FilterOperator.AND); // AND starts true, OR starts false

                    for (Filter filter : filters) {
                        boolean conditionMet = switch (filter) {
                            case OWNER -> holder.owner().equals(player.getUniqueId().toString());
                            case CURRENT_WORLD -> holder.loc().world().equals(player.getWorld().getName());
                            case OTHER_OWNERS -> !holder.owner().equals(player.getUniqueId().toString());
                            case MINECART -> holder.getType().equals(Material.COMMAND_BLOCK_MINECART);
                            case REPEAT -> holder.getType().equals(Material.REPEATING_COMMAND_BLOCK);
                            case CHAIN -> holder.getType().equals(Material.CHAIN_COMMAND_BLOCK);
                            case IMPULSE -> holder.getType().equals(Material.COMMAND_BLOCK);
                            case WHITELISTED -> holder.isWhitelisted();
                            case NOT_WHITELISTED -> !holder.isWhitelisted();
                            case NOT_PRESENT -> !holder.isPresent();
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

    private void openFilterMenu(Player player) {
        ServerUtils.verbose("Creating filter menu for %s", player);
        Set<Filter> filters = activeFilters.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());

        CustomGui filterGui = CustomGui.create()
                .title(Text.color("&6&lFilters"))
                .size(27)
                .defineMain(e -> e.setCancelled(true))
                .define(0, createFilterToggleItem("Your Blocks", Material.PLAYER_HEAD, filters.contains(Filter.OWNER)),
                        e -> toggleFilter(player, Filter.OWNER))
                .define(1, createFilterToggleItem("Other Owners", Material.SPYGLASS, filters.contains(Filter.OTHER_OWNERS)),
                        e -> toggleFilter(player, Filter.OTHER_OWNERS))
                .define(2, createFilterToggleItem("Current World", Material.TARGET, filters.contains(Filter.CURRENT_WORLD)),
                        e -> toggleFilter(player, Filter.CURRENT_WORLD))
                .define(3, createFilterToggleItem("Whitelisted Blocks", Material.PAPER, filters.contains(Filter.WHITELISTED)),
                        e -> toggleFilter(player, Filter.WHITELISTED))
                .define(4, createFilterToggleItem("Not Whitelisted Only", Material.BARRIER, filters.contains(Filter.NOT_WHITELISTED)),
                        e -> toggleFilter(player, Filter.NOT_WHITELISTED))
                .define(5, createFilterToggleItem("Missing Command Blocks", Material.GLASS, filters.contains(Filter.NOT_PRESENT)),
                        e -> toggleFilter(player, Filter.NOT_PRESENT))
                .define(6, createFilterToggleItem("Repeating Command Blocks", Material.REPEATING_COMMAND_BLOCK, filters.contains(Filter.REPEAT)),
                        e -> toggleFilter(player, Filter.REPEAT))
                .define(7, createFilterToggleItem("Chain Command Blocks", Material.CHAIN_COMMAND_BLOCK, filters.contains(Filter.CHAIN)),
                        e -> toggleFilter(player, Filter.CHAIN))
                .define(8, createFilterToggleItem("Impulse Command Blocks", Material.COMMAND_BLOCK, filters.contains(Filter.IMPULSE)),
                        e -> toggleFilter(player, Filter.IMPULSE))
                .define(9, createFilterToggleItem("Minecart Commands", Material.COMMAND_BLOCK_MINECART, filters.contains(Filter.MINECART)),
                        e -> toggleFilter(player, Filter.MINECART))
                .define(26, Items.BACK,
                        e-> {
                            player.playSound(player.getLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,0.8F);
                            player.openInventory(createGUI(player).getInventory());
                })
                .build();
        
        player.openInventory(filterGui.getInventory());
    }

    private ItemStack createFilterToggleItem(String name, Material mat, boolean active) {
        return new ItemBuilder()
                .material(mat)
                .name(Text.color((active ? "&a" : "&c") + name))
                .lore(Text.color("&7Click to " + (active ? "disable" : "enable")))
                .build();
    }

    private void toggleFilter(Player player, Filter filter) {
        Set<Filter> filters = activeFilters.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        ServerUtils.verbose("%s is now toggling the %s filter. Current %s", player,filter,filters);
        if (filters.contains(filter)) filters.remove(filter);
        else filters.add(filter);
        ServerUtils.verbose("Current filters for %s: %s", player,filters);
        openFilterMenu(player);
    }

    private int getFilterCount(Player player) {
        return activeFilters.getOrDefault(player.getUniqueId(), new HashSet<>()).size();
    }

    private void changePage(Player player, int direction) {
        int current = currentPages.getOrDefault(player.getUniqueId(), 0);
        int newPage = realizePage(player, current + direction);
        currentPages.put(player.getUniqueId(), newPage);
        player.openInventory(createGUI(player).getInventory());
    }
    
    private int realizePage(Player player, int requested) {
        int validRequested = Math.max(0, requested);
        int totalEntries = filterEntries(player,
                chosenOperator.computeIfAbsent(player.getUniqueId(), v -> FilterOperator.AND)).size();
        int maxPages = Math.max(0, Math.ceilDiv(totalEntries, 45) - 1);
        return Math.min(validRequested, maxPages);
    }

    private ItemStack createNavigationItem(String direction, int pageTo) {
        return new ItemBuilder()
                .material(Material.ARROW)
                .name(Text.color("&b" + direction + "&7 Page"))
                .lore(Text.color("&7 > &b" + pageTo))
                .build();
    }

    private ItemStack createFilterItem(Player player) {
        List<String> operatorList = new ArrayList<>();
        FilterOperator chosen = chosenOperator.computeIfAbsent(player.getUniqueId(),v->FilterOperator.AND);
        for (FilterOperator value : FilterOperator.values()) {
            if (value.equals(chosen)) operatorList.add(Text.color("&b&n" + value.name()));
            else operatorList.add(Text.color("&b" + value.name()));
        }
        return new ItemBuilder()
                .material(Material.HOPPER)
                .name(Text.color("&6&lFilters"))
                .lore(Text.color("&7Filters Selected: &e" + getFilterCount(player)))
                .lore(Text.color("&7Shift-Click to cycle filter operator."))
                .lore(Text.color("&7Operator: "))
                .lore(operatorList)
                .build();
    }
}