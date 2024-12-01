package me.trouper.sentinel.server.gui;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.server.gui.config.AntiNukeGUI;
import me.trouper.sentinel.server.gui.config.ChatGUI;
import me.trouper.sentinel.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ConfigGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Config Home"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(12, Items.ANTI_NUKE_CONFIG,e->{
                e.getWhoClicked().openInventory(new AntiNukeGUI().home.getInventory());
            })
            .define(14,Items.CHAT_CONFIG,e->{
                e.getWhoClicked().openInventory(new ChatGUI().home.getInventory());
            })
            .define(26,Items.BACK,e->{
                e.getWhoClicked().openInventory(new MainGUI().home.getInventory());
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
}
