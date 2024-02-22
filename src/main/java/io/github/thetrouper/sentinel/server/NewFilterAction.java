package io.github.thetrouper.sentinel.server;

import io.github.thetrouper.sentinel.data.FilterActionType;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NewFilterAction {

    public static void takeAction(AsyncPlayerChatEvent e, FilterActionType type, Report report, double similarity, FilterSeverity severity) {
        if (type.equals(FilterActionType.SAFE)) return;
        sendWarnings(e.getPlayer(),type,report.id());

    }

    public static void sendConsoleLog(AsyncPlayerChatEvent e, FilterActionType type) {

    }

    public static void sendWarnings(Player offender, FilterActionType type, long report) {
        TextComponent warning = Component.text(type.chatWarning())
                .hoverEvent(Component.text(type.chatWarningHover()))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"sentinelcallback fpreport " + report));
        TextComponent notification = Component.text(type.chatNotification())
                .hoverEvent(Component.text(type.chatNotificationHover()))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"sentinelcallback fpreport " + report));
        if (type.punishmentCommand() != null) {
            ServerUtils.forEachStaff(staff->{
                staff.sendMessage(notification);
            });
        }
        offender.sendMessage(warning);
    }
}
