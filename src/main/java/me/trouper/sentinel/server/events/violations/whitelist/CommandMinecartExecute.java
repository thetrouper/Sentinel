package me.trouper.sentinel.server.events.violations.whitelist;

import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import me.trouper.sentinel.server.functions.helpers.ActionConfiguration;
import me.trouper.sentinel.utils.PlayerUtils;
import me.trouper.sentinel.utils.ServerUtils;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class CommandMinecartExecute extends AbstractViolation {

    @EventHandler
    public void onExecute(ServerCommandEvent e) {
        //ServerUtils.verbose("Handling command block event: " + e.getCommand());
        if (!Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.enabled) return;
        //ServerUtils.verbose("Whitelist not disabled");
        if (!(e.getSender() instanceof CommandMinecart s)) return;
        CommandBlockHolder holder = Sentinel.getInstance().getDirector().whitelistManager.getFromList(s.getUniqueId());

        String label = s.getCommand();
        ServerUtils.verbose("Command block is set to %s.".formatted(label));
        label = label.split(" ")[0];
        if (label.startsWith("/")) label = label.substring(1);
        ServerUtils.verbose("It's label is %s.".formatted(label));

        ActionConfiguration.Builder config = new ActionConfiguration.Builder()
                .setEvent(e)
                .setEntity(s)
                .cancel(true)
                .removeEntity(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.destroyCart)
                .logToDiscord(Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.logToDiscord);

        if (Sentinel.getInstance().getDirector().io.violationConfig.commandBlockWhitelist.disabledCommands.contains(label)) {
            ServerUtils.verbose("Command cart is using a restricted command.");

            runActions(
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted(Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockRestriction),
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted( Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockRestriction),
                    generateMinecartInfo(s),
                    config
            );
        } else if (holder == null || !holder.isWhitelisted() || !holder.present() || !PlayerUtils.isTrusted(UUID.fromString(holder.owner()))) {
            ServerUtils.verbose("Command cart can't run. Block is not whitelisted, and/or not trusted.");

            runActions(
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted(Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockWhitelist),
                    Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.rootNameFormat.formatted(Sentinel.getInstance().getDirector().io.lang.violations.protections.rootName.commandBlockWhitelist),
                    generateMinecartInfo(s),
                    config
            );
        }
    }

    @Override
    public CustomGui getConfigGui() {
        return new CommandBlockExecute().getConfigGui();
    }

    @Override
    public void getMainPage(Inventory inv) {
        new CommandBlockExecute().getMainPage(inv);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        new CommandBlockExecute().onClick(e);
    }
}
