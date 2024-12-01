package me.trouper.sentinel.server.gui.config.nuke.checks.command;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.ViolationConfig;
import me.trouper.sentinel.server.gui.Items;
import me.trouper.sentinel.server.gui.MainGUI;
import me.trouper.sentinel.server.gui.config.nuke.CommandGUI;
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

import java.util.List;
import java.util.function.BiConsumer;

public class LoggedCMDGUI {
    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Logged Command Check"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(26, Items.BACK, e->{
                e.getWhoClicked().openInventory(new CommandGUI().home.getInventory());
            })
            .build();

    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }

        ItemStack ring = Items.RED;
        if (Sentinel.violationConfig.commandExecute.logged.enabled) {
            ring = Items.GREEN;
        }

        List<Integer> ringList = List.of(3,4,5,12,14,21,22,23);

        for (Integer i : ringList) {
            inv.setItem(i,ring);
        }

        inv.setItem(26,Items.BACK);
        inv.setItem(13,Items.booleanItem(Sentinel.violationConfig.commandExecute.logged.enabled,Items.configItem("Check Toggle",Material.CLOCK,"Enable/Disable this check entirely")));
        inv.setItem(11,Items.booleanItem(Sentinel.violationConfig.commandExecute.logged.logToDiscord,Items.configItem("Log to Discord",Material.OAK_LOG,"If this check will log to discord")));
        inv.setItem(15,Items.stringListItem(Sentinel.violationConfig.commandExecute.logged.commands,Material.CRIMSON_HANGING_SIGN,"Commands","Commands that will flag this check"));
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        switch (e.getSlot()) {
            case 13 -> {
                Sentinel.violationConfig.commandExecute.logged.enabled = !Sentinel.violationConfig.commandExecute.logged.enabled;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 11 -> {
                Sentinel.violationConfig.commandExecute.logged.logToDiscord = !Sentinel.violationConfig.commandExecute.logged.logToDiscord;
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
            case 15 -> {
                if (e.isLeftClick()) {
                    queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> {
                        cfg.commandExecute.logged.commands.add(args.getAll().toString());
                    },"" + Sentinel.violationConfig.commandExecute.logged.commands);
                    return;
                }
                Sentinel.violationConfig.commandExecute.logged.commands.clear();
                blankPage(e.getInventory());
                Sentinel.violationConfig.save();
            }
        }
    }

    public static ConfigUpdater<AsyncPlayerChatEvent, ViolationConfig> updater = new ConfigUpdater<>(Sentinel.violationConfig);
    private void queuePlayer(Player player, BiConsumer<ViolationConfig, Args> action, String currentValue) {
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
