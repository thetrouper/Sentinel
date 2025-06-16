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

public class ProfanityFilterGUI implements Main {
    public final CustomGui home = CustomGui.create()
            .title(OldTXT.color("&6&lSentinel &8»&0 Editing Profanity Filter"))
            .size(54)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(53, Items.BACK, e->{
                e.getWhoClicked().openInventory(new ChatGUI().home.getInventory());
            })
            .build();



    private void blankPage(Inventory inv) {
        try {
            ServerUtils.verbose("ProfanityFilterGUI#blankPage Starting");
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i,Items.BLANK);
            }
            ServerUtils.verbose("ProfanityFilterGUI#blankPage Page now blank");
            ItemStack top = Items.RED;
            if (main.dir().io.mainConfig.chat.profanityFilter.enabled) {
                top = Items.GREEN;
            }

            for (int i = 0; i < 9; i++) {
                inv.setItem(i,top);
            }
            ServerUtils.verbose("ProfanityFilterGUI#blankPage Adding GUI Items");

            inv.setItem(53,Items.BACK);
            inv.setItem(3,Items.booleanItem(main.dir().io.mainConfig.chat.profanityFilter.enabled, Items.configItem("Profanity Filter Toggle",Material.CLOCK,"Enable or Disable the whole Profanity filter")));
            inv.setItem(5,Items.booleanItem(main.dir().io.mainConfig.chat.profanityFilter.silent, Items.configItem("Silent Mode",Material.FEATHER,"Whether to notify players that their messages \nwere blocked. Enabling could help deter bypassing.")));
            inv.setItem(10,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.lowScore, Items.configItem("Low Score Gain", Material.WHITE_WOOL, "How much score will be added if the player \ndid not attempt to bypass the filter.")));
            inv.setItem(19,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.mediumLowScore, Items.configItem("Medium-Low Score Gain", Material.LIME_WOOL, "How much score will be added if the player \nused l33t speak to attempt a bypass")));
            inv.setItem(28,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.mediumScore, Items.configItem("Medium Score Gain", Material.YELLOW_WOOL, "How much score will be added if the player \nused sp/ecia|l characters to attempt a bypass")));
            inv.setItem(37,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.mediumHighScore, Items.configItem("Medium-High Score Gain", Material.ORANGE_WOOL, "How much score will be added if the player \nused reeeeeeepeating letters to attempt a bypass")));
            inv.setItem(46,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.highScore, Items.configItem("High Score Gain", Material.RED_WOOL, "How much score will be added if the player \nused pun. ctua, tion or spaces to attempt a bypass")));
            inv.setItem(29,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.regexScore, Items.configItem("Regex Score Gain", Material.DISPENSER, "How much score will be added if the player \nmatched the regex setting throughout \nthe processing of the message")));
            inv.setItem(22,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.punishScore, Items.configItem("Punish Score", Material.IRON_BARS, "If the player's score is above this \nthe punishment commands will be ran.")));
            inv.setItem(33,Items.intItem(main.dir().io.mainConfig.chat.profanityFilter.scoreDecay, Items.configItem("Score Decay", Material.DEAD_BUBBLE_CORAL_BLOCK, "How much score players will loose each minute.")));
            inv.setItem(31,Items.stringListItem(main.dir().io.mainConfig.chat.profanityFilter.profanityPunishCommands,Material.WOODEN_AXE, "Default Punishment Commands", "%player% will be replaced with the offender's name"));
            inv.setItem(40,Items.stringListItem(main.dir().io.mainConfig.chat.profanityFilter.strictPunishCommands,Material.DIAMOND_AXE, "Strict Punishment Commands", "If words from the strict words list are flagged, \nthis list will be ran instead \n%player% will be replaced with the offender's name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 3 -> {
                main.dir().io.mainConfig.chat.profanityFilter.enabled = !main.dir().io.mainConfig.chat.profanityFilter.enabled;
                main.dir().io.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 5 -> {
                main.dir().io.mainConfig.chat.profanityFilter.silent = !main.dir().io.mainConfig.chat.profanityFilter.silent;
                main.dir().io.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 10 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.lowScore = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.lowScore);
            case 19 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.mediumLowScore = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.mediumLowScore);
            case 28 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.mediumScore = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.mediumScore);
            case 37 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.mediumHighScore = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.mediumHighScore);
            case 46 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.highScore = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.highScore);
            case 29 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.regexScore = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.regexScore);
            case 22 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.punishScore = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.punishScore);
            case 33 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.profanityFilter.scoreDecay = args.getAll().toInt(),"" + main.dir().io.mainConfig.chat.profanityFilter.scoreDecay);

            case 31 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.profanityFilter.profanityPunishCommands.add(args.getAll().toString());
                    },"" + main.dir().io.mainConfig.chat.profanityFilter.profanityPunishCommands);
                    return;
                }
                main.dir().io.mainConfig.chat.profanityFilter.profanityPunishCommands.clear();
                main.dir().io.mainConfig.save();
                blankPage(e.getInventory());

            }
            case 40 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.profanityFilter.strictPunishCommands.add(args.getAll().toString());
                    },"" + main.dir().io.mainConfig.chat.profanityFilter.strictPunishCommands);
                    return;
                }
                main.dir().io.mainConfig.chat.profanityFilter.strictPunishCommands.clear();
                main.dir().io.mainConfig.save();
                blankPage(e.getInventory());
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
            successAny(player,"Value updated successfully");
            player.openInventory(home.getInventory());
        });
        message(player,Component.text("Enter the new value in chat. The value is currently set to {0}. (Click to insert)").clickEvent(ClickEvent.suggestCommand(currentValue)),Component.text(currentValue));
    }
}
