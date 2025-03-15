package me.trouper.sentinel.server.gui;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.gui.config.ConfigGUI;
import me.trouper.sentinel.server.gui.whitelist.WhitelistGUI;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainGUI {

    public static Set<UUID> awaitingCallback = new HashSet<>();

    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Home"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(11,Items.CREDITS)
            .define(13,Items.WHITELIST,this::openWhitelist)
            .define(15,Items.CONFIG,this::openConfig)
            .build();

    private void openWhitelist(InventoryClickEvent e) {
        e.getWhoClicked().openInventory(new WhitelistGUI().createGUI((Player) e.getWhoClicked()).getInventory());
    }
    
    private void openConfig(InventoryClickEvent e) {
        e.getWhoClicked().openInventory(new ConfigGUI().home.getInventory());
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        verify((Player) e.getWhoClicked());
    }

    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }
    }

    public static boolean verify(Player p) {
        if (PlayerUtils.isTrusted(p)) return true;
        Sentinel.getInstance().getLogger().info("WARNING: %s has just attempted to use the GUI without authorization. This has been prevented by Sentinel, as we are NOT Vulcan AntiCheat.");
        p.closeInventory();
        return false;
    }

}
