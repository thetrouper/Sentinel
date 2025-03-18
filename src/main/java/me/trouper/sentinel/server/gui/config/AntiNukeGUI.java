package me.trouper.sentinel.server.gui.config;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockEdit;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.command.CommandBlockUse;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.jigsaw.JigsawBlockUse;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockBreak;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockPlace;
import me.trouper.sentinel.server.events.violations.blocks.structure.StructureBlockUse;
import me.trouper.sentinel.server.events.violations.command.DangerousCommand;
import me.trouper.sentinel.server.events.violations.command.LoggedCommand;
import me.trouper.sentinel.server.events.violations.command.SpecificCommand;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartBreak;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartEdit;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartPlace;
import me.trouper.sentinel.server.events.violations.entities.CommandMinecartUse;
import me.trouper.sentinel.server.events.violations.players.CreativeHotbar;
import me.trouper.sentinel.server.events.violations.whitelist.CommandBlockExecute;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AntiNukeGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Choose a check"))
            .size(9*5)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define((9*5)-1, Items.BACK, e->{
                e.getWhoClicked().openInventory(new ConfigGUI().home.getInventory());
            })
            .define(10,getCheckItem(Material.COMMAND_BLOCK,"Command Block Break"),
                    e->e.getWhoClicked().openInventory(new CommandBlockBreak().getConfigGui().getInventory()))
            .define(11,getCheckItem(Material.COMMAND_BLOCK,"Command Block Edit"),
                    e->e.getWhoClicked().openInventory(new CommandBlockEdit().getConfigGui().getInventory()))
            .define(12,getCheckItem(Material.COMMAND_BLOCK,"Command Block Place"),
                    e->e.getWhoClicked().openInventory(new CommandBlockPlace().getConfigGui().getInventory()))
            .define(13,getCheckItem(Material.COMMAND_BLOCK,"Command Block Use"),
                    e->e.getWhoClicked().openInventory(new CommandBlockUse().getConfigGui().getInventory()))
            .define(14,getCheckItem(Material.JIGSAW,"Jigsaw Block Break"),
                    e->e.getWhoClicked().openInventory(new JigsawBlockBreak().getConfigGui().getInventory()))
            .define(15,getCheckItem(Material.JIGSAW,"Jigsaw Block Place"),
                    e->e.getWhoClicked().openInventory(new JigsawBlockPlace().getConfigGui().getInventory()))
            .define(16,getCheckItem(Material.JIGSAW,"Jigsaw Block Use"),
                    e->e.getWhoClicked().openInventory(new JigsawBlockUse().getConfigGui().getInventory()))
            .define(19,getCheckItem(Material.STRUCTURE_BLOCK,"Structure Block Break"),
                    e->e.getWhoClicked().openInventory(new StructureBlockBreak().getConfigGui().getInventory()))
            .define(20,getCheckItem(Material.STRUCTURE_BLOCK,"Structure Block Place"),
                    e->e.getWhoClicked().openInventory(new StructureBlockPlace().getConfigGui().getInventory()))
            .define(21,getCheckItem(Material.STRUCTURE_BLOCK,"Structure Block Use"),
                    e->e.getWhoClicked().openInventory(new StructureBlockUse().getConfigGui().getInventory()))
            .define(22,getCheckItem(Material.TNT,"Dangerous Commands"),
                    e->e.getWhoClicked().openInventory(new DangerousCommand().getConfigGui().getInventory()))
            .define(23,getCheckItem(Material.ENDER_PEARL,"Specific Commands"),
                    e->e.getWhoClicked().openInventory(new SpecificCommand().getConfigGui().getInventory()))
            .define(24,getCheckItem(Material.SPYGLASS,"Logged Commands"),
                    e->e.getWhoClicked().openInventory(new LoggedCommand().getConfigGui().getInventory()))
            .define(25,getCheckItem(Material.COMMAND_BLOCK_MINECART,"Command Minecart Break"),
                    e->e.getWhoClicked().openInventory(new CommandMinecartBreak().getConfigGui().getInventory()))
            .define(29,getCheckItem(Material.COMMAND_BLOCK_MINECART,"Command Minecart Place"),
                    e->e.getWhoClicked().openInventory(new CommandMinecartPlace().getConfigGui().getInventory()))
            .define(30,getCheckItem(Material.COMMAND_BLOCK_MINECART,"Command Minecart Use"),
                    e->e.getWhoClicked().openInventory(new CommandMinecartUse().getConfigGui().getInventory()))
            .define(31,getCheckItem(Material.COMMAND_BLOCK_MINECART,"Command Minecart Edit"),
                    e->e.getWhoClicked().openInventory(new CommandMinecartEdit().getConfigGui().getInventory()))
            .define(32,getCheckItem(Material.DIAMOND_SWORD,"NBT Item Pull"),
                    e->e.getWhoClicked().openInventory(new CreativeHotbar().getConfigGui().getInventory()))
            .define(33,getCheckItem(Material.EMERALD,"Command Block Whitelist"),
                    e->e.getWhoClicked().openInventory(new CommandBlockExecute().getConfigGui().getInventory()))
            .build();

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        MainGUI.verify((Player) e.getWhoClicked());
    }

    private void blankPage(Inventory inv) {
        ServerUtils.verbose("Making anti-nuke page");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }
    }

    private static ItemStack getCheckItem(Material item, String name) {
        return ItemBuilder.create()
                .material(item)
                .name(Text.color("&b" + name))
                .lore(Text.color("&8&l➥&7 Modify this check"))
                .build();
    }
}
