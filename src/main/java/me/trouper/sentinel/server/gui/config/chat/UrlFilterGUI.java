package me.trouper.sentinel.server.gui.config.chat;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.ChatGUI;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class UrlFilterGUI implements CustomListener {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Editing Unicode Filter"))
            .size(36)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(35, Items.BACK, e->{
                e.getWhoClicked().openInventory(new ChatGUI().home.getInventory());
            })
            .build();


    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Items.BLANK);
        }

        ItemStack top = Items.RED;
        if (Sentinel.mainConfig.chat.urlFilter.enabled) {
            top = Items.GREEN;
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i,top);
        }

        inv.setItem(3,Items.booleanItem(Sentinel.mainConfig.chat.urlFilter.enabled, Items.configItem("Unicode Filter Toggle", Material.CLOCK,"Enable or Disable the whole Unicode filter")));
        inv.setItem(5,Items.booleanItem(Sentinel.mainConfig.chat.urlFilter.silent, Items.configItem("Silent Mode",Material.FEATHER,"Whether to notify players that their messages \nwere blocked. Enabling could help deter bypassing.")));
        inv.setItem(19,Items.booleanItem(Sentinel.mainConfig.chat.urlFilter.punished,Items.configItem("Punished",Material.IRON_BARS,"Toggles execution of punishment commands.")));
        inv.setItem(21,Items.stringItem(Sentinel.mainConfig.chat.urlFilter.regex,Items.configItem("Allowed Char Regex",Material.DISPENSER,"Toggles execution of punishment commands.")));
        inv.setItem(23,Items.stringListItem(Sentinel.mainConfig.chat.urlFilter.punishCommands,Material.DIAMOND_AXE,"Punishment Commands","Commands which will be executed if punishment is enabled."));
        inv.setItem(25,Items.stringListItem(Sentinel.mainConfig.chat.urlFilter.whitelist,Material.PAPER,"Whitelist","URLs which will not flag the filter."));

    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;

        switch (e.getSlot()) {
            case 3 -> {
                Sentinel.mainConfig.chat.urlFilter.enabled = !Sentinel.mainConfig.chat.urlFilter.enabled;
                Sentinel.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 5 -> {
                Sentinel.mainConfig.chat.urlFilter.silent = !Sentinel.mainConfig.chat.urlFilter.silent;
                Sentinel.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 19 -> {
                Sentinel.mainConfig.chat.urlFilter.punished = !Sentinel.mainConfig.chat.urlFilter.punished;
                Sentinel.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 21 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.urlFilter.regex = args.getAll().toString(),Sentinel.mainConfig.chat.urlFilter.regex);

            case 23 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.urlFilter.punishCommands.add(args.getAll().toString());
                    },"" + Sentinel.mainConfig.chat.urlFilter.punishCommands);
                    return;
                }
                Sentinel.mainConfig.chat.urlFilter.punishCommands.clear();
                blankPage(e.getInventory());
                Sentinel.mainConfig.save();
            }

            case 25 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.urlFilter.whitelist.add(args.getAll().toString());
                    },"" + Sentinel.mainConfig.chat.urlFilter.whitelist);
                    return;
                }
                Sentinel.mainConfig.chat.urlFilter.whitelist.clear();
                blankPage(e.getInventory());
                Sentinel.mainConfig.save();

            }
        }
    }

    public static ConfigUpdater<AsyncChatEvent, MainConfig> updater = new ConfigUpdater<>(Sentinel.mainConfig);

    private void queuePlayer(Player player, BiConsumer<MainConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            return LegacyComponentSerializer.legacySection().serialize(e.message());
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            cfg.save();
            player.sendMessage(Text.prefix("Value updated successfully"));
            player.openInventory(home.getInventory());
        });
        player.sendMessage(Component.text(Text.prefix("Enter the new value in chat. The value is currently set to &b%s&7. (Click to insert)".formatted(currentValue))).clickEvent(ClickEvent.suggestCommand(currentValue)));
    }
}
