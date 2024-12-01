package me.trouper.sentinel.data;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.WhitelistedBlock;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WhitelistStorage implements JsonSerializable<WhitelistStorage> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.dataFolder(), "/storage/whitelist.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public ConcurrentLinkedQueue<WhitelistedBlock> whitelistedCMDBlocks = new ConcurrentLinkedQueue<>();

}
