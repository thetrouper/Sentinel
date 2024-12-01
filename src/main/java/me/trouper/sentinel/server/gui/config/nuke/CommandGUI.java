package me.trouper.sentinel.server.gui.config.nuke;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.DangerousCMDGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.LoggedCMDGUI;
import me.trouper.sentinel.server.gui.config.nuke.checks.command.SpecificCMDGUI;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Choose a check"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(26, Items.BACK, e->{
                e.getWhoClicked().openInventory(new AntiNukeGUI().home.getInventory());
            })
            .define(11,SPECIFIC, e->{
                e.getWhoClicked().openInventory(new SpecificCMDGUI().home.getInventory());
            })
            .define(13,LOGGED, e->{
                e.getWhoClicked().openInventory(new LoggedCMDGUI().home.getInventory());
            })
            .define(15,DANGEROUS, e->{
                e.getWhoClicked().openInventory(new DangerousCMDGUI().home.getInventory());
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

    private static final ItemStack SPECIFIC = ItemBuilder.create()
            .material(Material.SPECTRAL_ARROW)
            .name(Text.color("&bSpecific Commands"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();

    private static final ItemStack LOGGED = ItemBuilder.create()
            .material(Material.SPYGLASS)
            .name(Text.color("&bLogged Commands"))
            .lore(Text.color("&8&l➥&7 Modify this check"))

            .build();

    private static final ItemStack DANGEROUS = ItemBuilder.create()
            .material(Material.TNT)
            .name(Text.color("&bDangerous Commands"))
            .lore(Text.color("&8&l➥&7 Modify this check"))
            .build();
}
