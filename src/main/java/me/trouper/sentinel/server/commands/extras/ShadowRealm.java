package me.trouper.sentinel.server.commands.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewDistance;
import me.trouper.sentinel.data.types.SerialLocation;
import me.trouper.sentinel.server.events.extras.ShadowRealmEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShadowRealm extends AbstractExtra {
    
    public ShadowRealm() {
        super("shadow","Send player to the shadow realm");
    }
    
    @Override
    public void execute(CommandSender sender, Player target) {
        main.dir().io.extraStorage.shadowRealm.put(target.getUniqueId(), SerialLocation.translate(target.getLocation()));
        main.dir().io.extraStorage.save();
        ShadowRealmEvents.enforce(target);
        successAny(sender,"Sending {0} to the shadow realm.",target.getName());
    }

    @Override
    public void stop(CommandSender sender, Player target) {
        if (main.dir().io.extraStorage.shadowRealm.containsKey(target.getUniqueId())) {
            Location to = SerialLocation.translate(main.dir().io.extraStorage.shadowRealm.get(target.getUniqueId()));
            main.dir().io.extraStorage.shadowRealm.remove(target.getUniqueId());
            main.dir().io.extraStorage.save();
            target.teleport(to);
        }
        successAny(sender,"Released {0}.",target.getName());
    }
}
