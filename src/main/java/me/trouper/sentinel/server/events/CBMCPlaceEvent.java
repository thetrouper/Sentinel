package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.trees.Node;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.server.functions.ViolationController;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class CBMCPlaceEvent implements CustomListener {

    @EventHandler
    private void onCMDMinecartPlace(PlayerInteractEvent e) {
        //ServerUtils.verbose("MinecartCommandPlace: Detected interaction");
        if (!Sentinel.violationConfig.commandBlockMinecartPlace.enabled) return;
        ServerUtils.verbose("MinecartCommandPlace: Check is enabled");
        if (!e.getPlayer().isOp()) return;
        ServerUtils.verbose("MinecartCommandPlace: Player is op");
        if (e.getItem() == null) return;
        ServerUtils.verbose("MinecartCommandPlace: Item isn't null");
        if (e.getClickedBlock() == null) return;
        ServerUtils.verbose("MinecartCommandPlace: Clicked block isn't null");
        if (!e.getItem().getType().equals(Material.COMMAND_BLOCK_MINECART)) return;
        ServerUtils.verbose("MinecartCommandPlace: Item is a minecart command");
        if (!(e.getClickedBlock().getType() == Material.RAIL || e.getClickedBlock().getType() == Material.POWERED_RAIL || e.getClickedBlock().getType() == Material.ACTIVATOR_RAIL || e.getClickedBlock().getType() == Material.DETECTOR_RAIL)) return;
        ServerUtils.verbose("MinecartCommandPlace: Clicked block is a rail");
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;
        ServerUtils.verbose("MinecartCommandPlace: Not trusted, preforming action");

        e.setCancelled(true);
        p.getInventory().remove(Material.COMMAND_BLOCK_MINECART);

        Node log = getLog(p, e.getClickedBlock());

        ViolationController.handleViolation(
                Sentinel.lang.violations.commandBlockMinecartPlace.detectionChat.formatted(p.getName()),
                Sentinel.violationConfig.commandBlockMinecartPlace.punish,
                Sentinel.violationConfig.commandBlockMinecartPlace.deop,
                Sentinel.violationConfig.commandBlockMinecartPlace.logToDiscord,
                p,
                Sentinel.violationConfig.commandBlockMinecartPlace.punishmentCommands,
                log
        );
    }

    private static Node getLog(Player p, Block cb) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.commandBlockMinecartPlace.detectionTree);

        Node playerInfo = new Node(Sentinel.lang.violations.commandBlockMinecartPlace.playerInfoTitle.formatted(p.getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.commandBlockMinecartPlace.uuid, p.getUniqueId().toString());
        playerInfo.addField(Sentinel.lang.violations.commandBlockMinecartPlace.location, Sentinel.lang.violations.commandBlockMinecartPlace.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));
        root.addChild(playerInfo);

        Node violationInfo = new Node(Sentinel.lang.violations.commandBlockMinecartPlace.minecartPlaceInfoTitle);
        violationInfo.addField(Sentinel.lang.violations.commandBlockMinecartPlace.blockLocation, Sentinel.lang.violations.commandBlockMinecartPlace.blockLocationFormat.formatted(cb.getWorld().getName(), cb.getX(), cb.getY(), cb.getZ()));
        root.addChild(violationInfo);
        return root;
    }
}
