package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.ViolationController;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.trees.Node;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CBMCUseEvent implements CustomListener {

    @EventHandler
    private void onCMDBlockMinecartUse(PlayerInteractEntityEvent e) {
        //ServerUtils.verbose("MinecartCommandUse: Detected Interaction with entity");
        if (!Sentinel.violationConfig.commandBlockMinecartUse.enabled) return;
        //ServerUtils.verbose("MinecartCommandUse: Enabled");
        if (!e.getPlayer().isOp()) return;
        ServerUtils.verbose("MinecartCommandUse: Player op");
        if (e.getRightClicked().getType() != EntityType.COMMAND_BLOCK_MINECART) return;
        ServerUtils.verbose("MinecartCommandUse: Entity is minecart command");
        Player p = e.getPlayer();
        if (PlayerUtils.isTrusted(p)) return;
        ServerUtils.verbose("MinecartCommandUse: Not trusted, performing action");
        e.setCancelled(true);

        Node log = getLog(p, e.getRightClicked());

        ViolationController.handleViolation(
                Sentinel.lang.violations.commandBlockMinecartUse.detectionChat.formatted(p.getName()),
                Sentinel.violationConfig.commandBlockMinecartUse.punish,
                Sentinel.violationConfig.commandBlockMinecartUse.deop,
                Sentinel.violationConfig.commandBlockMinecartUse.logToDiscord,
                p,
                Sentinel.violationConfig.commandBlockMinecartUse.punishmentCommands,
                log
        );
    }

    private static Node getLog(Player p, Entity e) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.commandBlockMinecartUse.detectionTree);

        Node playerInfo = new Node(Sentinel.lang.violations.commandBlockMinecartUse.playerInfoTitle.formatted(p.getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.commandBlockMinecartUse.uuid, p.getUniqueId().toString());
        playerInfo.addField(Sentinel.lang.violations.commandBlockMinecartUse.location, Sentinel.lang.violations.commandBlockMinecartUse.locationFormat.formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));
        root.addChild(playerInfo);

        Node violationInfo = new Node(Sentinel.lang.violations.commandBlockMinecartUse.minecartUseInfoTitle);
        violationInfo.addField(Sentinel.lang.violations.commandBlockMinecartUse.cartLocation, Sentinel.lang.violations.commandBlockMinecartUse.cartLocationFormat.formatted(e.getWorld().getName(), e.getX(), e.getY(), e.getZ()));
        root.addChild(violationInfo);

        return root;
    }
}
