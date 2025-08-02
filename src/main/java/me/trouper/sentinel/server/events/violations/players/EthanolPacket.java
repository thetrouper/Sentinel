package me.trouper.sentinel.server.events.violations.players;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import me.trouper.sentinel.server.events.violations.AbstractViolation;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class EthanolPacket extends AbstractViolation implements PacketListener {

    
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.PLUGIN_MESSAGE)) return;
        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
        String channel = packet.getChannelName();
        if (channel.equals("auth_res")) {
            // TODO: Finish Ethanol Detection
            System.out.println("Detected ethanol, but I haven't coded the way to stop it yet.");
        }
    }

    @Override
    public CustomGui getConfigGui() {
        return null;
    }

    @Override
    public void getMainPage(Inventory inv) {
        
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        
    }
}
