package io.github.thetrouper.sentinel.data.cmdblocks;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WhitelistStorage implements JsonSerializable<WhitelistStorage> {
    @Override
    public File getFile() {
        File file = new File("plugins/Sentinel/storage/whitelist.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public List<WhitelistedBlock> whitelistedCMDBlocks = new ArrayList<>();

}
