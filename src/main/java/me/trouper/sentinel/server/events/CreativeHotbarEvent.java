package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.ViolationController;
import me.trouper.sentinel.utils.trees.Node;
import me.trouper.sentinel.utils.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class CreativeHotbarEvent implements CustomListener {

    @EventHandler
    private void onNBTPull(InventoryCreativeEvent e) {
        //ServerUtils.verbose("NBT: Detected creative mode action");
        if (!Sentinel.violationConfig.creativeHotbarAction.enabled) return;
        ServerUtils.verbose("NBT: Enabled");
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ServerUtils.verbose("NBT: Clicker is a player");
        if (e.getCursor() == null) return; // Well it through an exception during testing, so it isn't always false!
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
        e.setCancelled(true);
        Node root = getLog(p, i);

        ViolationController.handleViolation(
                Sentinel.lang.violations.creativeHotbar.nbtAttemptViolation.formatted(p.getName()),
                Sentinel.violationConfig.creativeHotbarAction.punish,
                Sentinel.violationConfig.creativeHotbarAction.deop,
                Sentinel.violationConfig.creativeHotbarAction.logToDiscord,
                p,
                Sentinel.violationConfig.creativeHotbarAction.punishmentCommands,
                root
        );
    }

    private static Node getLog(Player p, ItemStack item) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.creativeHotbar.nbtAttemptDetection);

        Node playerInfo = new Node(Sentinel.lang.violations.creativeHotbar.playerInfoTitle.formatted(p.getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.creativeHotbar.uuid, p.getUniqueId().toString());
        playerInfo.addField(Sentinel.lang.violations.creativeHotbar.location, Sentinel.lang.violations.creativeHotbar.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));
        root.addChild(playerInfo);

        Node violationInfo = new Node(Sentinel.lang.violations.creativeHotbar.itemInfoTitle);
        violationInfo.addKeyValue(Sentinel.lang.violations.creativeHotbar.itemType, item.getType().toString());
        violationInfo.addField(Sentinel.lang.violations.creativeHotbar.nbtUpload, FileUtils.createNBTLog(item));
        root.addChild(violationInfo);
        return root;
    }
}
