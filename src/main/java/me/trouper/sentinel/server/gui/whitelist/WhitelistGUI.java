package me.trouper.sentinel.server.gui.whitelist;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.ViolationConfig;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.PaginatedGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class WhitelistGUI extends PaginatedGUI<CommandBlockHolder> {
    
    private static final Map<UUID, String> chosenPlayer = new HashMap<>();

    @Override
    protected CustomGui backGUI() {
        return new MainGUI().home;
    }

    @Override
    protected String getTitle(Player p) {
        return Text.color("&6&lCommand Blocks &7(%s/%s filtered)".formatted(getFilterCount(p),Sentinel.getInstance().getDirector().io.commandBlocks.holders.size()));
    }

    @Override
    protected void handleMainClick(Player p, InventoryClickEvent e) {
        int slot = e.getSlot();
        if (slot >= 45) return;
        if (e.getInventory().getItem(slot) == null) return;
        int page = currentPages.compute(p.getUniqueId(), (k, v) -> realizePage(p, v == null ? 0 : v));
        List<CommandBlockHolder> filtered = filterEntries(p, chosenOperator.computeIfAbsent(p.getUniqueId(), v -> FilterOperator.AND));
        int index = page * ITEMS_PER_PAGE + slot;
        if (index < filtered.size()) {
            CommandBlockHolder holder = filtered.get(index);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 0.8F);
            openManagementMenu(p, holder);
        }
    }

    @Override
    protected ItemStack createDisplayItem(CommandBlockHolder holder) {
        Material type = holder.getType();
        String name = holder.isCart() ?
                "Minecart: " + holder.loc().toUIID() :
                String.format("X: %d, Y: %d, Z: %d",
                        (int) holder.loc().x(),
                        (int) holder.loc().y(),
                        (int) holder.loc().z());

        List<String> lore = new ArrayList<>();
        lore.add(Text.color("&7Owner: " + Bukkit.getOfflinePlayer(holder.owner()).getName()));
        lore.add(Text.color("&7Command: &f" + holder.command()));
        lore.add(Text.color("&7Type: &f" + holder.type()));
        lore.add(Text.color("&7Whitelisted: " + (holder.isWhitelisted() ? "&aYes" : "&cNo")));
        lore.add(Text.color("&7Present: " + (holder.present() ? "&aYes" : "&cNo")));
        lore.add("");
        lore.add(Text.color("&eClick to manage!"));

        return new ItemBuilder()
                .material(type)
                .name(Text.color("&b" + name))
                .lore(lore)
                .build();
    }

    @Override
    protected void addFilterItems(CustomGui.GuiBuilder filterGui, Player p, Set<String> filters) {
        filterGui.define(0, createFilterToggleItem("Your Blocks", Material.PLAYER_HEAD, filters.contains("OWNER")), e -> toggleFilter(p, "OWNER"));
        filterGui.define(1, createFilterToggleItem("Other Owners", Material.SPYGLASS, filters.contains("OTHER_OWNERS")), e -> toggleFilter(p, "OTHER_OWNERS"));
        filterGui.define(2, createFilterToggleItem("Current World", Material.TARGET, filters.contains("CURRENT_WORLD")), e -> toggleFilter(p, "CURRENT_WORLD"));
        filterGui.define(3, createFilterToggleItem("Whitelisted Blocks", Material.PAPER, filters.contains("WHITELISTED")), e -> toggleFilter(p, "WHITELISTED"));
        filterGui.define(4, createFilterToggleItem("Not Whitelisted Only", Material.BARRIER, filters.contains("NOT_WHITELISTED")), e -> toggleFilter(p, "NOT_WHITELISTED"));
        filterGui.define(5, createFilterToggleItem("Missing Command Blocks", Material.GLASS, filters.contains("NOT_PRESENT")), e -> toggleFilter(p, "NOT_PRESENT"));
        filterGui.define(6, createFilterToggleItem("Repeating Command Blocks", Material.REPEATING_COMMAND_BLOCK, filters.contains("REPEAT")), e -> toggleFilter(p, "REPEAT"));
        filterGui.define(7, createFilterToggleItem("Chain Command Blocks", Material.CHAIN_COMMAND_BLOCK, filters.contains("CHAIN")), e -> toggleFilter(p, "CHAIN"));
        filterGui.define(8, createFilterToggleItem("Impulse Command Blocks", Material.COMMAND_BLOCK, filters.contains("IMPULSE")), e -> toggleFilter(p, "IMPULSE"));
        filterGui.define(9, createFilterToggleItem("Minecart Commands", Material.COMMAND_BLOCK_MINECART, filters.contains("MINECART")), e -> toggleFilter(p, "MINECART"));
        filterGui.define(10, createFilterToggleItemValue("Specific Player",Material.BOW,filters.contains("USER"),chosenPlayer.getOrDefault(p.getUniqueId(),"null")),
                e -> {
                    if (e.isLeftClick()) toggleFilter(p, "USER");
                    else if (e.isRightClick()) {
                        queuePlayer(p,(cfg,value)->{
                            chosenPlayer.put(p.getUniqueId(),value.getAll().toString());
                        },chosenPlayer.getOrDefault(p.getUniqueId(),"null"));
                    }
                });
    }

    public static ConfigUpdater<AsyncChatEvent, ViolationConfig> updater = new ConfigUpdater<>(Sentinel.getInstance().getDirector().io.violationConfig);
    protected void queuePlayer(Player player, BiConsumer<ViolationConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            return LegacyComponentSerializer.legacySection().serialize(e.message());
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            player.sendMessage(Text.prefix("Value updated successfully"));
            openFilterMenu(player);
        });
        player.sendMessage(Component.text(Text.prefix("Enter the new value in chat. The value is currently set to &b%s&7. (Click to insert)".formatted(currentValue))).clickEvent(ClickEvent.suggestCommand(currentValue)));
    }

    @Override
    protected List<CommandBlockHolder> filterEntries(Player p, FilterOperator operator) {
        Set<String> filters = activeFilters.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        ServerUtils.verbose("Filtering entries for %s. Current: ", p, filters.toString());
        return Sentinel.getInstance().getDirector().io.commandBlocks.holders.stream()
                .filter(holder -> {
                    if (filters.isEmpty()) return true;
                    boolean result = (operator == FilterOperator.AND); // AND starts true, OR starts false
                    for (String filter : filters) {
                        boolean conditionMet = switch (filter) {
                            case "OWNER" -> holder.owner().equals(p.getUniqueId().toString());
                            case "CURRENT_WORLD" -> holder.loc().world().equals(p.getWorld().getName());
                            case "OTHER_OWNERS" -> !holder.owner().equals(p.getUniqueId().toString());
                            case "MINECART" -> holder.getType().equals(Material.COMMAND_BLOCK_MINECART);
                            case "REPEAT" -> holder.getType().equals(Material.REPEATING_COMMAND_BLOCK);
                            case "CHAIN" -> holder.getType().equals(Material.CHAIN_COMMAND_BLOCK);
                            case "IMPULSE" -> holder.getType().equals(Material.COMMAND_BLOCK);
                            case "WHITELISTED" -> holder.isWhitelisted();
                            case "NOT_WHITELISTED" -> !holder.isWhitelisted();
                            case "NOT_PRESENT" -> !holder.present();
                            case "USER" -> holder.owner().equals(chosenPlayer.get(p.getUniqueId()));
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

    private void openManagementMenu(Player p, CommandBlockHolder holder) {
        ServerUtils.verbose("Opening management menu for %s", holder.owner());
        boolean whitelisted = holder.isWhitelisted();
        CustomGui menu = CustomGui.create()
                .title(Text.color("&l ⬇ &6&lManaging Command Block"))
                .size(9)
                .defineMain(e -> e.setCancelled(true))
                .define(0, createDisplayItem(holder))
                .define(2, createActionItem(whitelisted ? "Un-Whitelist" : "Whitelist", whitelisted ? Material.BARRIER : Material.PAPER), e -> {
                    holder.setWhitelisted(!whitelisted);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1F);
                    openManagementMenu(p, holder);
                })
                .define(3, createActionItem("Teleport", Material.ENDER_PEARL), e -> {
                    if (holder.loc().isUUID()) {
                        // Handle minecart teleport
                        Entity entity = Bukkit.getEntity(holder.loc().toUIID());
                        if (entity == null) {
                            e.getInventory().setItem(e.getSlot(), new ItemBuilder()
                                    .material(Material.BARRIER)
                                    .name("&cTeleport Unavailable")
                                    .lore("&7This entity is not loaded.")
                                    .build());
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1F);
                            return;
                        }
                        p.teleport(entity.getLocation());
                    } else {
                        p.teleport(holder.loc().translate());
                    }
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5F);
                    p.closeInventory();
                })
                .define(4, createActionItem("Restore", Material.DISPENSER), e -> {
                    holder.restore();
                    p.openInventory(createGUI(p).getInventory());
                    p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1, 1F);
                })
                .define(5, createActionItem("Destroy (Shift-Click)", Material.NETHERITE_PICKAXE), e -> {
                    if (!e.isShiftClick()) return;
                    holder.destroy();
                    p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2F);
                    p.openInventory(createGUI(p).getInventory());
                })
                .define(6, createActionItem("Take Ownership", Material.NAME_TAG), e -> {
                    holder.setOwner(p.getUniqueId().toString());
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1, 1F);
                    openManagementMenu(p, holder);
                })
                .define(8, Items.BACK, e -> {
                    p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 0.8F);
                    p.openInventory(createGUI(p).getInventory());
                })
                .build();

        p.openInventory(menu.getInventory());
    }

    private ItemStack createActionItem(String name, Material mat) {
        return new ItemBuilder()
                .material(mat)
                .name(Text.color("&b" + name))
                .lore(Text.color("&7Click to " + name.toLowerCase()))
                .build();
    }

    private ItemStack createFilterToggleItem(String name, Material mat, boolean active) {
        return new ItemBuilder()
                .material(mat)
                .name(Text.color((active ? "&a" : "&c") + name))
                .lore(Text.color("&7Click to " + (active ? "disable" : "enable")))
                .build();
    }

    private ItemStack createFilterToggleItemValue(String name, Material mat, boolean active, String value) {
        return new ItemBuilder()
                .material(mat)
                .name(Text.color((active ? "&a" : "&c") + name))
                .lore(Text.color("&7Value&f: &b" + value))
                .lore(Text.color("&7Left Click to " + (active ? "disable" : "enable")))
                .lore(Text.color("&7Right Click to set value."))
                .build();
    }
}