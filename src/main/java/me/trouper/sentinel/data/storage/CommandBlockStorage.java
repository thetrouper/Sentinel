package me.trouper.sentinel.data.storage;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.CommandBlockHolder;
import me.trouper.sentinel.data.types.SerialLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBlockStorage implements JsonSerializable<CommandBlockStorage> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/storage/whitelist.json");
        file.getParentFile().mkdirs();
        return file;
    }


    public List<CommandBlockHolder> holders = new ArrayList<>();

    public synchronized boolean remove(CommandBlockHolder holder) {
        return holders.removeIf(existing -> existing.loc().isSameLocation(holder.loc()));
    }
    public synchronized boolean add(CommandBlockHolder holder) {
        holders.removeIf(existing -> existing.loc().isSameLocation(holder.loc()));
        return holders.add(holder);
    }
    
}
