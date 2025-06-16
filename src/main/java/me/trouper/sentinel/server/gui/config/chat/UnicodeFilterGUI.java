package me.trouper.sentinel.server.gui.config.chat;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.server.Main;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.OldTXT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class UnicodeFilterGUI implements Main {

    public final CustomGui home = CustomGui.create()
            .title(OldTXT.color("&6&lSentinel &8»&0 Editing Unicode Filter"))
            .size(36)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(35,Items.BACK, e->{
                e.getWhoClicked().openInventory(new ChatGUI().home.getInventory());
            })
            .build();


    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Items.BLANK);
        }
        ServerUtils.verbose("Unicode Filter GUI blank!");
        ItemStack top = Items.RED;
        if (main.dir().io.mainConfig.chat.unicodeFilter.enabled) {
            top = Items.GREEN;
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i,top);
        }

        inv.setItem(3,Items.booleanItem(main.dir().io.mainConfig.chat.unicodeFilter.enabled, Items.configItem("Unicode Filter Toggle", Material.CLOCK,"Enable or Disable the whole Unicode filter")));
        inv.setItem(5,Items.booleanItem(main.dir().io.mainConfig.chat.unicodeFilter.silent, Items.configItem("Silent Mode",Material.FEATHER,"Whether to notify players that their messages \nwere blocked. Enabling could help deter bypassing.")));
        inv.setItem(20,Items.booleanItem(main.dir().io.mainConfig.chat.unicodeFilter.punished,Items.configItem("Punished",Material.IRON_BARS,"Toggles execution of punishment commands.")));
        inv.setItem(22,Items.stringItem(main.dir().io.mainConfig.chat.unicodeFilter.regex,Items.configItem("Allowed Char Regex",Material.DISPENSER,"Toggles execution of punishment commands.")));
        inv.setItem(24,Items.stringListItem(main.dir().io.mainConfig.chat.unicodeFilter.punishCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands which will be executed if punishment is enabled."));

    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;

        switch (e.getSlot()) {
            case 3 -> {
                main.dir().io.mainConfig.chat.unicodeFilter.enabled = !main.dir().io.mainConfig.chat.unicodeFilter.enabled;
                main.dir().io.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 5 -> {
                main.dir().io.mainConfig.chat.unicodeFilter.silent = !main.dir().io.mainConfig.chat.unicodeFilter.silent;
                main.dir().io.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 20 -> {
                main.dir().io.mainConfig.chat.unicodeFilter.punished = !main.dir().io.mainConfig.chat.unicodeFilter.punished;
                main.dir().io.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 22 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.unicodeFilter.regex = args.getAll().toString(),main.dir().io.mainConfig.chat.unicodeFilter.regex);

            case 24 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.unicodeFilter.punishCommands.add(args.getAll().toString());
                    },"" + main.dir().io.mainConfig.chat.unicodeFilter.punishCommands);
                    return;
                }
                main.dir().io.mainConfig.chat.unicodeFilter.punishCommands.clear();
                blankPage(e.getInventory());
                main.dir().io.mainConfig.save();

            }
        }
    }

    public static ConfigUpdater<AsyncChatEvent, MainConfig> updater = new ConfigUpdater<>(main.dir().io.mainConfig);

    private void queuePlayer(Player player, BiConsumer<MainConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            return LegacyComponentSerializer.legacySection().serialize(e.message());
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            cfg.save();
            messageAny(player,"Value updated successfully");
            player.openInventory(home.getInventory());
        });
        message(player,Component.text("Enter the new value in chat. The value is currently set to {0}. (Click to insert)").clickEvent(ClickEvent.suggestCommand(currentValue)),Component.text(currentValue));
    }
}
