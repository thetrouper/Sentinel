package me.trouper.sentinel.data.storage;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.SerialLocation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExtraStorage implements JsonSerializable<ExtraStorage> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/storage/extra.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public Map<UUID, SerialLocation> shadowRealm = new HashMap<>();
    
}
