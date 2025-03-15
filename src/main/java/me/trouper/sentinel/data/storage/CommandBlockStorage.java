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

    public List<CommandBlockHolder> holders = new ArrayList<>() {
        @Override
        public boolean add(CommandBlockHolder holder) {
            for (CommandBlockHolder existing : holders) {
                if (existing.loc().isSameLocation(holder.loc())) {
                    super.remove(existing);
                }
            }
            
            return super.add(holder);
        }
    };
    
}
