package me.trouper.sentinel.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import org.bukkit.entity.Player;

public class PacketUtils {
    public static void sendFakeRespawn(Player target, String worldName, Difficulty difficulty, GameMode gameMode) {
        if (target == null || !target.isOnline()) return;
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        WrapperPlayServerRespawn packet = new WrapperPlayServerRespawn(DimensionTypes.THE_END,worldName, difficulty,1L, gameMode,null,false,false,false,null,null,null);
        player.sendPacket(packet);
    }

    public static void sendCloseScreen(Player target) {
        if (target == null || !target.isOnline()) return;
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        if (player == null) return;
        WrapperPlayServerCloseWindow packet = new WrapperPlayServerCloseWindow();
        player.sendPacket(packet);
    }

    public static boolean sendFakePosition(Player target, double x, double y, double z) {
        if (target == null || !target.isOnline()) return false;
        var player = PacketEvents.getAPI().getPlayerManager().getUser(target);
        if (player == null) return false;
        WrapperPlayServerPlayerPositionAndLook packet = new WrapperPlayServerPlayerPositionAndLook(x,y,z,0,90, RelativeFlag.NONE.getMask(),0,false);
        player.sendPacket(packet);
        return true;
    }
}
