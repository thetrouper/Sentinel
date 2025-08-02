package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import me.trouper.sentinel.data.types.IPLocation;
import me.trouper.sentinel.utils.IPUtils;
import me.trouper.sentinel.utils.ImageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;

public class KickTroll extends AbstractExtra {
    
    public KickTroll() {
        super("kick","Kick the player with no back button");
    }
    
    @Override
    public void execute(CommandSender sender, Player target) {
        String beforeLines = "\n".repeat(15 * 100 + 3);
        String afterLines = "\n".repeat(15 * 100);

        Component image = Component.text("\n");
        for (Component component : ImageUtils.makeImage("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/x1ksxaas.png")) {
            image = image.appendNewline().append(component);
        }
        String header = "Sorry %1$s!\nLooks like you're a griefer... \n...and this is a decoy server\nYour presence has been recorded. \n\nHow's the weather in %2$s, %3$s? \n";
        String footer = "\n\nWant to try again?\n Nope. No back to server list for you.\n\nCopyright © 2025 Sentinel Anti Nuke. All rights reserved.\n";
        String name = target.getName();
        String ip = IPUtils.extractIp(target.getAddress().getAddress());
        IPLocation location = IPUtils.getLocation(ip);
        String region = location.getRegion();
        String city = location.getCity();
        target.kick(Component.text(beforeLines)
                        .append(Component.text(
                                header.formatted(
                                        name,
                                        city,
                                        region
                                )))
                        .append(image)
                        .append(Component.text(
                                        footer + afterLines
                                )
                        ),
                PlayerKickEvent.Cause.ILLEGAL_ACTION);
        successAny(sender,"Kicked {0} and removed the back to server list button. Their IP was {1} ({2}, {3})",target.getName(), ip, city, region);
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        errorAny(sender,"You cannot un-kick someone!");
    }
}
