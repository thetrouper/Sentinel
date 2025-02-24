package me.trouper.sentinel.server.events;

import io.github.itzispyder.pdk.events.CustomListener;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.helpers.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CommandBlockMinecartUse extends AbstractViolation {

    @EventHandler
    private void onCMDBlockMinecartUse(PlayerInteractEntityEvent e) {
        //ServerUtils.verbose("MinecartCommandUse: Detected Interaction with entity");
        if (!Sentinel.violationConfig.commandBlockMinecartUse.enabled) return;
        //ServerUtils.verbose("MinecartCommandUse: Enabled");
        Player p = e.getPlayer();
        if (!p.isOp()) return;
        ServerUtils.verbose("MinecartCommandUse: Player op");
        if (e.getRightClicked().getType() != EntityType.COMMAND_BLOCK_MINECART) return;
        ServerUtils.verbose("MinecartCommandUse: Entity is minecart command");
        if (PlayerUtils.isTrusted(p)) return;
        ServerUtils.verbose("MinecartCommandUse: Not trusted, performing action");

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setPlayer(p)
                .cancel(true)
                .punish(Sentinel.violationConfig.commandBlockMinecartUse.punish)
                .deop(Sentinel.violationConfig.commandBlockMinecartUse.deop)
                .setPunishmentCommands(Sentinel.violationConfig.commandBlockMinecartUse.punishmentCommands)
                .logToDiscord(Sentinel.violationConfig.commandBlockMinecartUse.logToDiscord);

        runActions(
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.use, Sentinel.lang.violations.protections.rootName.minecartCommandBlock),
                Sentinel.lang.violations.protections.rootName.rootNameFormatPlayer.formatted(p.getName(), Sentinel.lang.violations.protections.rootName.use, Sentinel.lang.violations.protections.rootName.minecartCommandBlock),
                generateMinecartInfo(e.getRightClicked()),
                config
        );
    }
}