package me.trouper.sentinel.server.events.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.utils.SchedulerUtils;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.events.QuickListener;
import me.trouper.sentinel.utils.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class ShadowRealmEvents implements QuickListener, PacketListener {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!main.dir().io.extraStorage.shadowRealm.containsKey(p.getUniqueId())) return;
        SchedulerUtils.later(20,()->{
            enforce(p);
        });
    }
    
    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) return;
        if (e.getPacketType() == PacketType.Play.Client.TELEPORT_CONFIRM) return;
        if (e.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) return;
        if (e.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) return;
        Player player = e.getPlayer();
        if (player == null) return;
        if (!main.dir().io.extraStorage.shadowRealm.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);
    }

    @Override
    public void onPacketSend(PacketSendEvent e) {
        if (e.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) return;
        if (e.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) return;
        if (e.getPacketType() == PacketType.Play.Server.RESPAWN) return;
        if (e.getPacketType() == PacketType.Play.Server.DISCONNECT) return;
        if (e.getPacketType() == PacketType.Play.Server.CLOSE_WINDOW) return;
        if (e.getPacketType() == PacketType.Play.Server.CHUNK_DATA) return;
        if (e.getPacketType() == PacketType.Play.Server.CHUNK_BATCH_BEGIN) return;
        if (e.getPacketType() == PacketType.Play.Server.CHUNK_BATCH_END) return;
        if (e.getPacketType() == PacketType.Play.Server.CHUNK_BIOMES) return;
        if (e.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) return;
        if (e.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) return;
        
        Player player = e.getPlayer();
        if (player == null) return;
        if (!main.dir().io.extraStorage.shadowRealm.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);
    }

    public static void enforce(Player p) {
        if (p == null || !main.dir().io.extraStorage.shadowRealm.containsKey(p.getUniqueId())) return;
        PacketUtils.sendFakeRespawn(p,"minecraft:the_end",Difficulty.PEACEFUL, GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(),(t)->{
            if (p == null || !p.isOnline() || !main.dir().io.extraStorage.shadowRealm.containsKey(p.getUniqueId())) t.cancel();
            PacketUtils.sendFakePosition(p,0,32767,0);
            PacketUtils.sendCloseScreen(p);
        },1,1);
    }
}
