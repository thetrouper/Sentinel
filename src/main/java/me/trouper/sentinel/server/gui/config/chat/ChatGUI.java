package me.trouper.sentinel.server.gui.config.chat;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.ConfigGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChatGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Edit a Filter"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(26,Items.BACK,e->{
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked(), Sound.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER,1,1.3F);
                e.getWhoClicked().openInventory(new ConfigGUI().home.getInventory());
            })
            .define(16,PROFANITY_FILTER,e->{
                e.getWhoClicked().openInventory(new ProfanityFilterGUI().home.getInventory());
            })
            .define(14,SPAM_FILTER,e->{
                e.getWhoClicked().openInventory(new SpamFilterGUI().home.getInventory());
            })
            .define(12,URL_FILTER,e->{
                ServerUtils.verbose("URL Filter Launching");
                e.getWhoClicked().openInventory(new UrlFilterGUI().home.getInventory());
            })
            .define(10,UNICODE_FILTER,e->{
                ServerUtils.verbose("Unicode Filter Launching");
                e.getWhoClicked().openInventory(new UnicodeFilterGUI().home.getInventory());
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

    private static final ItemStack PROFANITY_FILTER = ItemBuilder.create()
            .material(Material.COAL)
            .name(Text.color("&bProfanity Filter"))
            .lore(Text.color("&8&l➥&7 Edit Score Settings"))
            .build();

    private static final ItemStack SPAM_FILTER = ItemBuilder.create()
            .material(Material.PORKCHOP)
            .name(Text.color("&bSpam Filter"))
            .lore(Text.color("&8&l➥&7 Edit Heat Settings"))
            .build();

    private static final ItemStack UNICODE_FILTER = ItemBuilder.create()
            .material(Material.PAPER)
            .name(Text.color("&bUnicode Filter"))
            .lore(Text.color("&8&l➥&7 Edit regex"))
            .build();

    private static final ItemStack URL_FILTER = ItemBuilder.create()
            .material(Material.CHAIN)
            .name(Text.color("&bURL Filter"))
            .lore(Text.color("&8&l➥&7 Edit regex"))
            .build();
}
