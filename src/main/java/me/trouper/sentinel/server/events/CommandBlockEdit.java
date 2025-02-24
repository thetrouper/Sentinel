package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.server.functions.helpers.CBWhitelistManager;
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

public class CommandBlockEdit extends AbstractViolation {

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
        CommandBlock cb = (CommandBlock) b.getState();
        if (PlayerUtils.isTrusted(p)) {
            if (!CBWhitelistManager.autoWhitelist.contains(p.getUniqueId())) return;
            CBWhitelistManager.add(cb, p.getUniqueId());
            return;
        }
        ServerUtils.verbose("CommandBlockChange: Not trusted, performing action");

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .deop(Sentinel.violationConfig.commandBlockEdit.deop)
                .cancel(true)
                .punish(Sentinel.violationConfig.commandBlockEdit.punish)
                .setPunishmentCommands(Sentinel.violationConfig.commandBlockEdit.punishmentCommands)
                .logToDiscord(Sentinel.violationConfig.commandBlockEdit.logToDiscord);

        runActions(
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.edit, Sentinel.lang.violations.protections.rootName.commandBlock),
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.edit, Sentinel.lang.violations.protections.rootName.commandBlock),
                generateCommandBlockInfo(cb),
                config
        );
    }
}
