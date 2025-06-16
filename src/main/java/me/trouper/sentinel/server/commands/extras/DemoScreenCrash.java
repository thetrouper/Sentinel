package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import me.trouper.sentinel.Sentinel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DemoScreenCrash extends AbstractExtra {
    
    public DemoScreenCrash() {
        super("demo","Lock player's mouse and crash them");
    }
    
    private final List<UUID> demoCrash = new ArrayList<>();

    @Override
    public void execute(CommandSender sender, Player target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        demoCrash.add(target.getUniqueId());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!target.isOnline() || !demoCrash.contains(target.getUniqueId())) {
                t.cancel();
                return;
            }
            for (int i = 0; i < 35 * 9; i++) {
                player.sendPacket(new WrapperPlayServerCloseWindow());
                player.sendPacket(new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.DEMO_EVENT, 0));
            }
        }, 1, 1);
        successAny(sender,"Freezing {0}.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        demoCrash.remove(target.getUniqueId());
        successAny(sender,"Attempting to save {0}.",target.getName());
    }
}
