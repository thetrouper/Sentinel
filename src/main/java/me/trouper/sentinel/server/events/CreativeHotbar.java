package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.utils.ItemUtils;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class CreativeHotbar extends AbstractViolation {

    @EventHandler
    private void onNBTPull(InventoryCreativeEvent e) {
        //ServerUtils.verbose("NBT: Detected creative mode action");
        if (!Sentinel.violationConfig.creativeHotbarAction.enabled) return;
        ServerUtils.verbose("NBT: Enabled");
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ServerUtils.verbose("NBT: Clicker is a player");
        if (e.getCursor() == null) return; // Well it threw an exception during testing, so it isn't always false!
        ServerUtils.verbose("NBT: Cursor isn't null");
        ItemStack i = e.getCursor();
        if (PlayerUtils.isTrusted(p)) return;
        ServerUtils.verbose("NBT: Not trusted");
        if (e.getCursor().getItemMeta() == null) return;
        ServerUtils.verbose("NBT: Cursor has meta");
        if (!(i.hasItemMeta() && i.getItemMeta() != null)) return;
        ServerUtils.verbose("NBT: Item has meta");
        if (ItemUtils.itemPasses(i)) return;
        ServerUtils.verbose("NBT: Item doesn't pass, performing action");

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .cancel(true)
                .punish(Sentinel.violationConfig.creativeHotbarAction.punish)
                .deop(Sentinel.violationConfig.creativeHotbarAction.deop)
                .setPunishmentCommands(Sentinel.violationConfig.creativeHotbarAction.punishmentCommands)
                .logToDiscord(Sentinel.violationConfig.creativeHotbarAction.logToDiscord);

        runActions(
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.grab, Sentinel.lang.violations.protections.rootName.nbtItem),
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.grab, Sentinel.lang.violations.protections.rootName.nbtItem),
                generateItemInfo(i),
                config
        );
    }
}