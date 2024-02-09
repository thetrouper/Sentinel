package io.github.thetrouper.sentinel.data.cmdblocks;

import io.papermc.paper.command.CommandBlockHolder;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;

import java.util.UUID;

public record WhitelistedBlock(Location loc, UUID owner, CMDBlockType type, boolean conditional, boolean active ,String command) {

}
