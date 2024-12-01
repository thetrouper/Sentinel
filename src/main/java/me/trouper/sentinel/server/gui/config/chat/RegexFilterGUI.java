package me.trouper.sentinel.server.gui.config.chat;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.itzispyder.pdk.utils.misc.config.ConfigUpdater;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.config.AdvancedConfig;
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

import java.util.function.BiConsumer;

public class RegexFilterGUI implements CustomListener {

    public final CustomGui home = CustomGui.create()
            .title(Text.color("&6&lSentinel &8»&0 Edit a Chat Filter"))
            .size(27)
            .onDefine(this::blankPage)
            .defineMain(this::mainClick)
            .define(26, Items.BACK, e->{
                e.getWhoClicked().openInventory(new ChatGUI().home.getInventory());
            })
            .build();

    private void blankPage(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i,Items.BLANK);
        }

        inv.setItem(26,Items.BACK);
        ServerUtils.verbose("RegexFilterGUI#blankPage Setting up page");
        inv.setItem(11, Items.stringItem(Sentinel.advConfig.allowedCharRegex, Items.configItem("Unicode Whitelist", Material.FEATHER, "The regex defining what characters \nare allowed to be used in chat.")));
        ServerUtils.verbose("RegexFilterGUI#blankPage Finished with Unicode Whitelist");
        inv.setItem(12, Items.stringItem(Sentinel.advConfig.falsePosRegex, Items.configItem("False Positive Regex", Material.EMERALD, "This regex will be replaced with \nan empty string before profanity filter \nprocessing begins.")));
        ServerUtils.verbose("RegexFilterGUI#blankPage Finished with False Positive Regex");
        inv.setItem(13, Items.stringItem(Sentinel.advConfig.swearRegex, Items.configItem("Swear Regex", Material.ROTTEN_FLESH, "If anything matches to this regex, \nthe profanity filter will immediately flag it.")));
        ServerUtils.verbose("RegexFilterGUI#blankPage Finished with Swear Regex");
        inv.setItem(14, Items.stringItem(Sentinel.advConfig.strictRegex, Items.configItem("Strict Regex", Material.LEAD, "If anything matches to this regex, the profanity \nfilter will immediately flag it as a slur.")));
        ServerUtils.verbose("RegexFilterGUI#blankPage Finished with Strict Regex");
        inv.setItem(15, Items.stringItem(Sentinel.advConfig.urlRegex, Items.configItem("URL Blocker", Material.CHAIN, "If anything matches to this regex, it will get \nflagged as a URL.")));
        ServerUtils.verbose("RegexFilterGUI#blankPage Done Setting up page (Finished URL Blocker)");
    }

    private void mainClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!MainGUI.verify((Player) e.getWhoClicked())) return;
        SchedulerUtils.later(0,()->{
            switch (e.getSlot()) {
                case 11 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.allowedCharRegex = args.getAll().toString(),Sentinel.advConfig.allowedCharRegex);
                case 12 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.falsePosRegex = args.getAll().toString(),Sentinel.advConfig.falsePosRegex);
                case 13 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.swearRegex = args.getAll().toString(),Sentinel.advConfig.swearRegex);
                case 14 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.strictRegex = args.getAll().toString(),Sentinel.advConfig.strictRegex);
                case 15 -> queuePlayer((Player) e.getWhoClicked(), (cfg,args) -> cfg.urlRegex = args.getAll().toString(),Sentinel.advConfig.urlRegex);
            }
        });
    }

    public static ConfigUpdater<AsyncPlayerChatEvent, AdvancedConfig> updater = new ConfigUpdater<>(Sentinel.advConfig);

    private void queuePlayer(Player player, BiConsumer<AdvancedConfig, Args> action, String currentValue) {
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

