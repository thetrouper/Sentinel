package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class CommandBlockPlace extends AbstractViolation {

    @EventHandler
    public void listen(BlockPlaceEvent e) {
        //ServerUtils.verbose("CommandBlockPlace: Detected block place");
        if (!Sentinel.violationConfig.commandBlockPlace.enabled) return;
        //ServerUtils.verbose("CommandBlockPlace: Enabled");
        Player p = e.getPlayer();
        if (!p.isOp()) return;
        //ServerUtils.verbose("CommandBlockPlace: Player is operator");
        Block b = e.getBlockPlaced();
        if (!(b.getType().equals(Material.COMMAND_BLOCK) ||
                b.getType().equals(Material.REPEATING_COMMAND_BLOCK) ||
                b.getType().equals(Material.CHAIN_COMMAND_BLOCK))) return;
        ServerUtils.verbose("CommandBlockPlace: Block is a command block");
        CommandBlock cb = (CommandBlock) b.getState();
        if (PlayerUtils.isTrusted(p)) return;
        ServerUtils.verbose("CommandBlockPlace: Not trusted, performing action");


        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .deop(Sentinel.violationConfig.commandBlockPlace.deop)
                .cancel(true)
                .setEvent(e)
                .punish(true)
                .setPunishmentCommands(Sentinel.violationConfig.commandBlockPlace.punishmentCommands)
                .logToDiscord(Sentinel.violationConfig.commandBlockPlace.logToDiscord);

        runActions(
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.place, Sentinel.lang.violations.protections.rootName.commandBlock),
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.place, Sentinel.lang.violations.protections.rootName.commandBlock),
                generateCommandBlockInfo(cb),
                config
        );
    }
}
