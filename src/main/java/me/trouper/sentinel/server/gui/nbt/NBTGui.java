package me.trouper.sentinel.server.gui.nbt;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.ViolationConfig;
import me.trouper.sentinel.data.storage.NBTStorage;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.PaginatedGUI;
import me.trouper.sentinel.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NBTGui extends PaginatedGUI<Map.Entry<String, NBTStorage.Metadata>> {
    
    private final NBTStorage nbtStorage;
    private static final Map<UUID, String> chosenPlayer = new HashMap<>();

    public NBTGui() {
        this.nbtStorage = main.dir().io.nbtStorage;
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
        return FormatUtils.legacyColor("&6&lCaught NBT &7(%s/%s filtered)".formatted(this.getFilteredCount(p), main.dir().io.nbtStorage.caughtItems.size()));
    }

    @Override
    protected void handleMainClick(Player p, InventoryClickEvent e) {
        e.setCancelled(true);
        MainGUI.verify(p);
        Map.Entry<String, NBTStorage.Metadata> entry;
        ItemStack item;
        List<Map.Entry<String, NBTStorage.Metadata>> filtered;
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
                e.getClickedInventory().setItem(slot, ItemBuilder.of(Material.BARRIER)
                                .displayName(Component.text("Deleted Item", NamedTextColor.RED))
                                .loreComponent(
                                        Component.text("You have deleted this item.",NamedTextColor.GRAY),
                                        Component.text("This will disappear next refresh.",NamedTextColor.GRAY)
                                )
                        .build());
            }
        }
    }

    @Override
    protected ItemStack createDisplayItem(Map.Entry<String, NBTStorage.Metadata> entry) {
        ItemStack item = NBTStorage.getItem(entry.getKey());
        if (item == null) {
            return null;
        }
        
        String name = LegacyComponentSerializer.legacySection().serialize(item.effectiveName());
        Component owner = Component.text(Bukkit.getOfflinePlayer(entry.getValue().owner).getName(), NamedTextColor.WHITE);
        
        if (name.length() >= 64) {
            name = name.substring(0,64) + "...";
        }

        return ItemBuilder.of(item.getType())
                .displayName(Component.text(FormatUtils.formatEnum(item.getType()),NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false))
                .loreComponent(
                        Component.text("NBT Name: ",NamedTextColor.GRAY).append(Text.color(name)),
                        Component.text("Size: ",NamedTextColor.GRAY).append(Component.text(FormatUtils.formatBytes(entry.getValue().byteSize),NamedTextColor.WHITE)),
                        Component.empty(),
                        Component.text("Owner: ",NamedTextColor.GRAY).append(owner),
                        Component.empty(),
                        Component.text("Left-Click to give item",NamedTextColor.YELLOW),
                        Component.text("Right-Click to delete item",NamedTextColor.YELLOW)
                )
                .build();
    }

    @Override
    protected void addFilterItems(CustomGui.GuiBuilder filterGui, Player p, Set<String> filters) {
        filterGui.define(0, createFilterToggleItem("Your NBT", Material.PLAYER_HEAD, filters.contains("OWNER")), e -> toggleFilter(p, "OWNER"));
        filterGui.define(1, createFilterToggleItem("Other Owners", Material.SPYGLASS, filters.contains("OTHER_OWNERS")), e -> toggleFilter(p, "OTHER_OWNERS"));
        filterGui.define(2, createFilterToggleItemValue("Specific Player",Material.BOW,filters.contains("USER"),chosenPlayer.getOrDefault(p.getUniqueId(),"null")),
                e -> {
                    if (e.isLeftClick()) toggleFilter(p, "USER");
                    else if (e.isRightClick()) {
                        queuePlayer(p,(cfg,value)->{
                            String s = value.getAll().toString();
                            ServerUtils.verbose("Callback Received: %s", s);
                            OfflinePlayer target = Bukkit.getOfflinePlayer(s);
                            chosenPlayer.put(p.getUniqueId(),target.getUniqueId().toString());
                        },chosenPlayer.getOrDefault(p.getUniqueId(),"null"));
                    }
                });
    }

    public static ConfigUpdater<AsyncChatEvent, ViolationConfig> updater = new ConfigUpdater<>(main.dir().io.violationConfig);
    protected void queuePlayer(Player player, BiConsumer<ViolationConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            return LegacyComponentSerializer.legacySection().serialize(e.message());
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            messageAny(player,"Value updated successfully");
            openFilterMenu(player);
        });
        message(player,Component.text("Enter the new value in chat. The value is currently set to {0}. (Click to insert)").clickEvent(ClickEvent.suggestCommand(currentValue)),Component.text(currentValue));
    }

    @Override
    protected List<Map.Entry<String, NBTStorage.Metadata>> filterEntries(Player p, FilterOperator operator) {
        Set<String> filters = activeFilters.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        ServerUtils.verbose("Filtering entries for %s. Current: ", p, filters.toString());
        return this.nbtStorage.caughtItems.entrySet().stream().filter(entry -> {
            if (filters.isEmpty()) {
                return true;
            }
            boolean result = operator == PaginatedGUI.FilterOperator.AND;
            for (String filter : filters) {
                boolean conditionMet = switch (filter) {
                    case "OWNER" -> entry.getValue().owner.equals(p.getUniqueId());
                    case "OTHER_OWNERS" -> !entry.getValue().owner.equals(p.getUniqueId());
                    case "USER" -> entry.getValue().owner.toString().equals(chosenPlayer.get(p.getUniqueId()));
                    default -> false;
                };
                result = operator.apply(result, conditionMet);
                if (operator == FilterOperator.AND && !result) return false;
                if (operator == FilterOperator.OR && result) return true;
            }
            return result;
        }).collect(Collectors.toList());
    }
    
    
}
