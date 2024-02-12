package io.github.thetrouper.sentinel.data.cmdblocks;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class WhitelistStorage implements JsonSerializable<WhitelistStorage> {
    @Override
    public File getFile() {
        File file = new File("plugins/Sentinel/storage/whitelist.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public Set<WhitelistedBlock> whitelistedCMDBlocks = new HashSet<>();

}
