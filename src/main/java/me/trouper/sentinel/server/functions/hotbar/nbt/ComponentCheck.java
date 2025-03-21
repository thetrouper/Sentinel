package me.trouper.sentinel.server.functions.hotbar.nbt;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.trouper.sentinel.server.functions.hotbar.AbstractCheck;
import me.trouper.sentinel.utils.ServerUtils;

public class ComponentCheck extends AbstractCheck<ReadWriteNBT> {

    @Override
    public boolean passes(ReadWriteNBT components) {
        ServerUtils.verbose("Checking Consumable & tool");
        if (!config.allowCustomConsumables && components.getCompound("minecraft:consumable") != null) {
            ServerUtils.verbose("Item is consumable and not allowed.");
            return false;
        }
        if (!config.allowCustomTools && components.getCompound("minecraft:tool") != null) {
            ServerUtils.verbose("Item is custom tool and not allowed.");
            return false;
        }

        ServerUtils.verbose("Checking Entity data");
        
        ReadWriteNBT entityData = components.getCompound("minecraft:entity_data");
        if (!new EntityDataCheck().passes(entityData)) {
            ServerUtils.verbose("Entity Data Check Failed.");
            return false;
        }
        
        return true;
    }
    
}
