package me.trouper.sentinel.server.events.extras;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class ShadowRealmEvents implements CustomListener, PacketListener {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.containsKey(p.getUniqueId())) return;
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
        if (!Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.containsKey(player.getUniqueId())) return;
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
        if (!Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);
    }

    public static void enforce(Player p) {
        if (p == null || !Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.containsKey(p.getUniqueId())) return;
        sendFakeRespawn(p);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(),(t)->{
            if (p == null || !p.isOnline() || !Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.containsKey(p.getUniqueId())) t.cancel();
            sendFakePosition(p,0,666,0);
            sendCloseScreen(p);
        },1,1);
    }

    public static void sendFakeRespawn(Player victim) {
        if (victim == null || !victim.isOnline()) return;
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        WrapperPlayServerRespawn packet = new WrapperPlayServerRespawn(DimensionTypes.THE_END,"minecraft:the_end", Difficulty.PEACEFUL,1L, GameMode.SPECTATOR,null,false,false,false,null,null,null);
        player.sendPacket(packet);
    }
    
    public static void sendCloseScreen(Player victim) {
        if (victim == null || !victim.isOnline()) return;
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        if (player == null) return;
        WrapperPlayServerCloseWindow packet = new WrapperPlayServerCloseWindow();
        player.sendPacket(packet);
    }

    public static void sendFakePosition(Player victim, double x, double y, double z) {
        if (victim == null || !victim.isOnline()) return;
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        if (player == null) return;
        WrapperPlayServerPlayerPositionAndLook packet = new WrapperPlayServerPlayerPositionAndLook(x,y,z,0,90, RelativeFlag.NONE.getMask(),0,false);
        player.sendPacket(packet);
    }
}
