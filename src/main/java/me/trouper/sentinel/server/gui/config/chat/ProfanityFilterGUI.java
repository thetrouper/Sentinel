package me.trouper.sentinel.server.gui.config.chat;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.ChatGUI;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class ProfanityFilterGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Editing Profanity Filter"))
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
            if (Sentinel.mainConfig.chat.swearFilter.enabled) {
                top = Items.GREEN;
            }

            for (int i = 0; i < 9; i++) {
                inv.setItem(i,top);
            }
            ServerUtils.verbose("ProfanityFilterGUI#blankPage Adding GUI Items");

            inv.setItem(53,Items.BACK);
            inv.setItem(4,Items.booleanItem(Sentinel.mainConfig.chat.swearFilter.enabled, Items.configItem("Profanity Filter Toggle",Material.CLOCK,"Enable or Disable the whole Profanity filter")));
            inv.setItem(10,Items.intItem(Sentinel.mainConfig.chat.swearFilter.lowScore, Items.configItem("Low Score Gain", Material.WHITE_WOOL, "How much score will be added if the player \ndid not attempt to bypass the filter.")));
            inv.setItem(19,Items.intItem(Sentinel.mainConfig.chat.swearFilter.mediumLowScore, Items.configItem("Medium-Low Score Gain", Material.LIME_WOOL, "How much score will be added if the player \nused l33t speak to attempt a bypass")));
            inv.setItem(28,Items.intItem(Sentinel.mainConfig.chat.swearFilter.mediumScore, Items.configItem("Medium Score Gain", Material.YELLOW_WOOL, "How much score will be added if the player \nused sp/ecia|l characters to attempt a bypass")));
            inv.setItem(37,Items.intItem(Sentinel.mainConfig.chat.swearFilter.mediumHighScore, Items.configItem("Medium-High Score Gain", Material.ORANGE_WOOL, "How much score will be added if the player \nused reeeeeeepeating letters to attempt a bypass")));
            inv.setItem(46,Items.intItem(Sentinel.mainConfig.chat.swearFilter.highScore, Items.configItem("High Score Gain", Material.RED_WOOL, "How much score will be added if the player \nused pun. ctua, tion or spaces to attempt a bypass")));
            inv.setItem(29,Items.intItem(Sentinel.mainConfig.chat.swearFilter.regexScore, Items.configItem("Regex Score Gain", Material.DISPENSER, "How much score will be added if the player \nmatched the regex setting throughout \nthe processing of the message")));
            inv.setItem(22,Items.intItem(Sentinel.mainConfig.chat.swearFilter.punishScore, Items.configItem("Punish Score", Material.IRON_BARS, "If the player's score is above this \nthe punishment commands will be ran.")));
            inv.setItem(33,Items.intItem(Sentinel.mainConfig.chat.swearFilter.scoreDecay, Items.configItem("Score Decay", Material.DEAD_BUBBLE_CORAL_BLOCK, "How much score players will loose each minute.")));
            inv.setItem(31,Items.stringListItem(Sentinel.mainConfig.chat.swearFilter.swearPunishCommands,Material.WOODEN_AXE, "Default Punishment Commands", "%player% will be replaced with the offender's name"));
            inv.setItem(40,Items.stringListItem(Sentinel.mainConfig.chat.swearFilter.strictPunishCommands,Material.DIAMOND_AXE, "Strict Punishment Commands", "If words from the strict words list are flagged, \nthis list will be ran instead \n%player% will be replaced with the offender's name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 4 -> {
                Sentinel.mainConfig.chat.swearFilter.enabled = !Sentinel.mainConfig.chat.swearFilter.enabled;
                blankPage(e.getInventory());
                Sentinel.mainConfig.save();
            }

            case 10 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.lowScore = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.lowScore);
            case 19 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.mediumLowScore = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.mediumLowScore);
            case 28 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.mediumScore = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.mediumScore);
            case 37 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.mediumHighScore = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.mediumHighScore);
            case 46 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.highScore = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.highScore);
            case 29 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.regexScore = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.regexScore);
            case 22 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.punishScore = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.punishScore);
            case 33 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.swearFilter.scoreDecay = args.getAll().toInt(),"" + Sentinel.mainConfig.chat.swearFilter.scoreDecay);

            case 31 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.swearFilter.swearPunishCommands.add(args.getAll().toString());
                    },"" + Sentinel.mainConfig.chat.swearFilter.swearPunishCommands);
                    return;
                }
                Sentinel.mainConfig.chat.swearFilter.swearPunishCommands.clear();
                blankPage(e.getInventory());
                Sentinel.mainConfig.save();

            }
            case 40 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.swearFilter.strictPunishCommands.add(args.getAll().toString());
                    },"" + Sentinel.mainConfig.chat.swearFilter.strictPunishCommands);
                    return;
                }
                Sentinel.mainConfig.chat.swearFilter.strictPunishCommands.clear();
                blankPage(e.getInventory());
                Sentinel.mainConfig.save();
            }
        }
    }

    public static ConfigUpdater<AsyncPlayerChatEvent, MainConfig> updater = new ConfigUpdater<>(Sentinel.mainConfig);

    private void queuePlayer(Player player, BiConsumer<MainConfig, Args> action, String currentValue) {
        MainGUI.awaitingCallback.add(player.getUniqueId());
        player.closeInventory();
        updater.queuePlayer(player, 20*60, (e)->{
            e.setCancelled(true);
            ServerUtils.verbose("Supplying the message: \"%s\". Canceled? %s".formatted(e.getMessage(),e.isCancelled()));
            return e.getMessage();
        }, (cfg, newValue) -> {
            action.accept(cfg,new Args(newValue.split("\\s+")));
            cfg.save();
            player.sendMessage(Text.prefix("Value updated successfully"));
            player.openInventory(home.getInventory());
        });
        player.sendMessage(Component.text(Text.prefix("Enter the new value in chat. The value is currently set to &b%s&7. (Click to insert)".formatted(currentValue))).clickEvent(ClickEvent.suggestCommand(currentValue)));
    }
}
