package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.CBWhitelistManager;
import me.trouper.sentinel.server.functions.ViolationController;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.trees.Node;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class CBEditEvent implements CustomListener {

    @EventHandler
    private void onCMDBlockChange(EntityChangeBlockEvent e) {
        //ServerUtils.verbose("CommandBlockChange: Detected the event");
        if (!Sentinel.violationConfig.commandBlockEdit.enabled) return;
        //ServerUtils.verbose("CommandBlockChange: Enabled");
        if (!(e.getEntity() instanceof Player p)) return;
        //ServerUtils.verbose("CommandBlockChange: Changer is a player");
        Block b = e.getBlock();
        if (!(b.getType() == Material.COMMAND_BLOCK || b.getType() == Material.REPEATING_COMMAND_BLOCK || b.getType() == Material.CHAIN_COMMAND_BLOCK))
            return;
        ServerUtils.verbose("CommandBlockChange: Block is a command block");
        BlockState state = b.getState();
        CommandBlock cb = (CommandBlock) state;
        if (PlayerUtils.isTrusted(p)) {
            if (!CBWhitelistManager.autoWhitelist.contains(p.getUniqueId())) return;
            CBWhitelistManager.add(cb, p.getUniqueId());
            return;
        }
        ServerUtils.verbose("CommandBlockChange: Not trusted, performing action");
        e.setCancelled(true);

        Node root = getLog(p, cb);

        ViolationController.handleViolation(
                "&b&n%s&r &7%s".formatted(p.getName(), Sentinel.lang.violations.commandBlockEdit.playerAttemptEdit),
                Sentinel.violationConfig.commandBlockEdit.punish,
                Sentinel.violationConfig.commandBlockEdit.deop,
                Sentinel.violationConfig.commandBlockEdit.logToDiscord,
                p,
                Sentinel.violationConfig.commandBlockEdit.punishmentCommands,
                root
        );
    }

    private static Node getLog(Player p, CommandBlock cb) {
        Node root = new Node("Sentinel");
        root.addTextLine(Sentinel.lang.violations.commandBlockEdit.playerAttemptEdit);

        Node playerInfo = new Node(Sentinel.lang.violations.commandBlockEdit.playerInfoTitle.formatted(p.getName()));
        playerInfo.addKeyValue(Sentinel.lang.violations.commandBlockEdit.uuid, p.getUniqueId().toString());
        playerInfo.addField(Sentinel.lang.violations.commandBlockEdit.location, "X: %s Y: %s Z: %s".formatted(Math.round(p.getX()), Math.round(p.getY()), Math.round(p.getZ())));
        root.addChild(playerInfo);

        Node violationInfo = new Node(Sentinel.lang.violations.commandBlockEdit.violationInfoTitle);
        violationInfo.addField(Sentinel.lang.violations.commandBlockEdit.blockLocation,"World: %s X: %s Y: %s Z: %s".formatted(cb.getWorld().getName(), cb.getX(), cb.getY(), cb.getZ()));
        violationInfo.addField(Sentinel.lang.violations.commandBlockEdit.insertedCommand, cb.getCommand());
        root.addChild(violationInfo);
        return root;
    }
}
