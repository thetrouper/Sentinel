package me.trouper.sentinel.server.gui.config.chat;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.MainConfig;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
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

public class SpamFilterGUI {

    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Editing Spam Filter"))
            .size(54)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(53, Items.BACK, e->{
                e.getWhoClicked().openInventory(new ChatGUI().home.getInventory());
            })
            .build();



    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }

        ItemStack top = Items.RED;
        if (Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.enabled) {
            top = Items.GREEN;
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i,top);
        }

        inv.setItem(53,Items.BACK);
        inv.setItem(3,Items.booleanItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.enabled, Items.configItem("Spam Filter Toggle", Material.CLOCK, "Enable or disable the whole Spam Filter")));
        inv.setItem(5,Items.booleanItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.silent, Items.configItem("Silent Toggle", Material.FEATHER, "Whether to notify players that their messages \nwere blocked. Enabling could help deter bypassing.")));
        inv.setItem(10,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.defaultGain, Items.configItem("Default Heat Gain", Material.BUCKET, "How much heat will be added to each message.")));
        inv.setItem(19,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.lowGain, Items.configItem("Low Heat Gain", Material.WATER_BUCKET, "Extra heat to be added if the \nmessage is greater than 25% similar \nto their previous message.")));
        inv.setItem(28,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.mediumGain, Items.configItem("Medium Heat Gain", Material.COD_BUCKET, "Extra heat to be added if the \nmessage is greater than 50% similar \nto their previous message.")));
        inv.setItem(37,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.highGain, Items.configItem("High Heat Gain", Material.PUFFERFISH_BUCKET, "Extra heat to be added if the \nmessage is greater than 90% similar \nto their previous message.")));
        inv.setItem(46,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.blockHeat, Items.configItem("Block Heat", Material.BARRIER, "If the player's heat is above this \nthen their message will be blocked and \nflagged as spam.")));
        inv.setItem(21,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.blockSimilarity, Items.configItem("Block Similarity", Material.BARRIER, "If the message's similarity is above \nthis, it will get automatically blocked \nand flagged as spam.")));
        inv.setItem(23,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.punishHeat, Items.configItem("Punish Heat", Material.IRON_BARS, "If the player's heat is above this \nthe punishment commands will be ran.")));
        inv.setItem(25,Items.intItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.heatDecay, Items.configItem("Heat Decay", Material.DEAD_BUBBLE_CORAL_BLOCK, "How much heat players will loose each second.")));
        inv.setItem(32,Items.stringListItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.punishCommands,Material.DIAMOND_AXE, "Punishment Commands", "%player% will be replaced with the offender's name"));
        inv.setItem(34,Items.stringListItem(Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.whitelist,Material.PAPER, "Message Whitelist", "Messages which will be ignored by the spam filter"));
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        blankPage(e.getInventory());

        switch (e.getSlot()) {
            case 3 -> {
                Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.enabled = !Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.enabled;
                Sentinel.getInstance().getDirector().io.mainConfig.save();
                blankPage(e.getInventory());
            }
            case 5 -> {
                Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.silent = !Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.silent;
                Sentinel.getInstance().getDirector().io.mainConfig.save();
                blankPage(e.getInventory());
            }

            case 10 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.defaultGain = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.defaultGain);
            case 19 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.lowGain = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.lowGain);
            case 28 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.mediumGain = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.mediumGain);
            case 37 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.highGain = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.highGain);
            case 46 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.blockHeat = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.blockHeat);
            case 21 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.blockSimilarity = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.blockSimilarity);
            case 23 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.punishHeat = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.punishHeat);
            case 25 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.chat.spamFilter.heatDecay = args.getAll().toInt(),"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.heatDecay);

            case 32 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.chat.spamFilter.punishCommands.add(args.getAll().toString());
                    },"" + Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.punishCommands);
                    return;
                }
                Sentinel.getInstance().getDirector().io.mainConfig.chat.spamFilter.punishCommands.clear();
                blankPage(e.getInventory());
                Sentinel.getInstance().getDirector().io.mainConfig.save();
            }
        }
    }

    public static ConfigUpdater<AsyncChatEvent, MainConfig> updater = new ConfigUpdater<>(Sentinel.getInstance().getDirector().io.mainConfig);

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
