package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.cmdblocks.CMDBlockType;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistStorage;
import io.github.thetrouper.sentinel.data.cmdblocks.WhitelistedBlock;
import org.bukkit.block.CommandBlock;

import java.util.UUID;

public class CMDBlockWhitelist {
    public static void addWhitelist(CommandBlock cb, UUID owner) {
        //WhitelistedBlock commandblock = new WhitelistedBlock(cb.getLocation(),owner,getType(cb),)
        //Sentinel.whitelist.whitelistedCMDBlocks.add(new WhitelistedBlock(cb.getLocation(),))
    }

    public static CMDBlockType getType(CommandBlock cb) {
        switch (cb.getType()) {
            case COMMAND_BLOCK -> {
                return CMDBlockType.IMPULSE;
            }
            case REPEATING_COMMAND_BLOCK -> {
                return CMDBlockType.REPEAT;
            }
            case CHAIN_COMMAND_BLOCK -> {
                return CMDBlockType.CHAIN;
            }
        }
        return null;
    }
}
