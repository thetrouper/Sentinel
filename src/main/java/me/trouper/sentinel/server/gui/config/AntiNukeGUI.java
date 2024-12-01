package me.trouper.sentinel.server.gui.config;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.server.gui.ConfigGUI;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.nuke.CommandGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.*;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AntiNukeGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Choose a check"))
            .size(54)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(53, Items.BACK, e->{
                e.getWhoClicked().openInventory(new ConfigGUI().home.getInventory());
            })
            .define(10,COMMAND_BLOCK_WHITELIST, e->{
                e.getWhoClicked().openInventory(new CBExecuteGUI().home.getInventory());
            })
            .define(12,COMMAND_BLOCK_PLACE, e->{
                e.getWhoClicked().openInventory(new CBPlaceGUI().home.getInventory());
            })
            .define(14,COMMAND_BLOCK_USE, e->{
                e.getWhoClicked().openInventory(new CBUseGUI().home.getInventory());
            })
            .define(16,COMMAND_BLOCK_EDITING, e->{
                e.getWhoClicked().openInventory(new CBEditGUI().home.getInventory());
            })
            .define(37,COMMAND_BLOCK_MINECART_USE, e->{
                e.getWhoClicked().openInventory(new CBMCUseGUI().home.getInventory());
            })
            .define(39,COMMAND_BLOCK_MINECART_PLACE, e->{
                e.getWhoClicked().openInventory(new CBMCPlaceGUI().home.getInventory());
            })
            .define(41,COMMAND_EXECUTE, e->{
                e.getWhoClicked().openInventory(new CommandGUI().home.getInventory());
            })
            .define(43,HOTBAR_ACTION, e->{
                e.getWhoClicked().openInventory(new HotbarActionGUI().home.getInventory());
            })
            .build();

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        MainGUI.verify((Player) e.getWhoClicked());
    }

    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }
    }

    private static final ItemStack COMMAND_BLOCK_EDITING = ItemBuilder.create()
            .material(Material.DEBUG_STICK)
            .name(Text.color("&bCommand Block Editing"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

    private static final ItemStack COMMAND_BLOCK_WHITELIST = ItemBuilder.create()
            .material(Material.EMERALD)
            .name(Text.color("&bCommand Block Whitelist"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

    private static final ItemStack COMMAND_BLOCK_MINECART_PLACE = ItemBuilder.create()
            .material(Material.RAIL)
            .name(Text.color("&bCommand Block Minecart Placing"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

    private static final ItemStack COMMAND_BLOCK_MINECART_USE = ItemBuilder.create()
            .material(Material.COMMAND_BLOCK_MINECART)
            .name(Text.color("&bCommand Block Minecart Using"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

    private static final ItemStack COMMAND_BLOCK_PLACE = ItemBuilder.create()
            .material(Material.CHAIN_COMMAND_BLOCK)
            .name(Text.color("&bCommand Block Placing"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

    private static final ItemStack COMMAND_BLOCK_USE = ItemBuilder.create()
            .material(Material.REPEATING_COMMAND_BLOCK)
            .name(Text.color("&bCommand Block Using"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

    private static final ItemStack COMMAND_EXECUTE = ItemBuilder.create()
            .material(Material.SPYGLASS)
            .name(Text.color("&bCommand Execution"))
            .lore(Text.color("&8&l➥&7 Dangerous Commands"))
            .lore(Text.color("&8&l➥&7 Logged Commands"))
            .lore(Text.color("&8&l➥&7 Specific Commands"))
            .build();

    private static final ItemStack HOTBAR_ACTION = ItemBuilder.create()
            .material(Material.DIAMOND_SWORD)
            .name(Text.color("&bNBT Items"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

}
