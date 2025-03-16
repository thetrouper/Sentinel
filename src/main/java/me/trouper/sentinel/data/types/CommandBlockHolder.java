package me.trouper.sentinel.data.types;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.persistence.PersistentDataType;

public class CommandBlockHolder {

    private final String owner;
    private final SerialLocation loc;
    private final String facing;
    private final String type;
    private final boolean auto;
    private final boolean conditional;
    private final String command;
    private boolean whitelisted;
    
    public CommandBlockHolder(String owner, SerialLocation loc, String facing, String type, boolean auto, boolean conditional, String command) {
        this.owner = owner;
        this.loc = loc;
        this.facing = facing;
        this.type = type;
        this.auto = auto;
        this.conditional = conditional;
        this.command = command;
        this.whitelisted = false;
    }

    public String owner() {
        return owner;
    }

    public SerialLocation loc() {
        return loc;
    }

    public String facing() {
        return facing;
    }

    public String type() {
        return type;
    }

    public boolean auto() {
        return auto;
    }

    public boolean conditional() {
        return conditional;
    }

    public String command() {
        return command;
    }
    
    public boolean present() {
        if (this.loc.isUUID()) {
            Entity cart = Bukkit.getEntity(this.loc.toUIID());
            if (!(cart instanceof CommandMinecart cm)) return false;
            return this.command.equals(cm.getCommand());
        } else {
            Location where = loc.translate();
            boolean preLoaded = where.isChunkLoaded();
            where.getChunk().load(false);
            Block b = where.getBlock();
            if (!(b.getState() instanceof CommandBlock c) || !(b.getBlockData() instanceof org.bukkit.block.data.type.CommandBlock cb)) {
                ServerUtils.verbose("Block is not present due to not being a command block.");
                if (!this.whitelisted) this.delete();
                return false;
            }
            if (!this.command.equals(c.getCommand())) {
                ServerUtils.verbose("Block is not present due to command mismatch. Should be '%s', is '%s'",this.command,c.getCommand());
                if (!this.whitelisted) this.delete();
                return false;
            }
            if (this.conditional != cb.isConditional()) {
                ServerUtils.verbose("Block is not present due to conditional mismatch.");
                if (!this.whitelisted) this.delete();
                return false;
            }
            if (!this.getType().equals(c.getType())) {
                ServerUtils.verbose("Block is not present due to type mismatch. Should be '%s', is '%s'",this.type,c.getType());
                if (!this.whitelisted) this.delete();
                return false;
            }
            if (this.auto != (c.getPersistentDataContainer().getOrDefault(Sentinel.getInstance().getNamespace("auto"), PersistentDataType.BYTE,(byte) 0) == (byte) 1)) {
                ServerUtils.verbose("Block is not present due to auto mismatch.");
                if (!this.whitelisted) this.delete();
                return false;
            }
            if (!preLoaded) where.getChunk().unload();
            return true;
        }
    }
    
    public boolean whitelisted() {
        return whitelisted;
    }

    public CommandBlockHolder setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
        return this;
    }
    
    public CommandBlockHolder addAndWhitelist() {
        return setWhitelisted(true).add();
    }
    
    public BlockFace getDirection() {
        try {
            return BlockFace.valueOf(facing.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BlockFace.NORTH;
        }
    }
    
    public Material getType() {
        return switch (this.type) {
            case "COMMAND_BLOCK" -> Material.COMMAND_BLOCK;
            case "REPEATING_COMMAND_BLOCK" -> Material.REPEATING_COMMAND_BLOCK;
            case "CHAIN_COMMAND_BLOCK" -> Material.CHAIN_COMMAND_BLOCK;
            case "COMMAND_BLOCK_MINECART" -> Material.COMMAND_BLOCK_MINECART;
            default -> throw new IllegalArgumentException("Unknown command block type: " + type);
        };
    }
    
    public void destroy() {
        SerialLocation.translate(this.loc).getBlock().setType(Material.AIR);
        if (!whitelisted) delete();
    }

    public boolean restore() {
        if (Material.COMMAND_BLOCK_MINECART.equals(this.getType())) {
            ServerUtils.verbose("Cannot restore minecarts yet.");
            return false;
        }
        
        if (this.present()) return false;
        
        Block block = SerialLocation.translate(this.loc).getBlock();
        block.setType(this.getType());
        if (!ServerUtils.isCommandBlock(block)) {
            ServerUtils.verbose("Block at the location was not a command block (You shouldn't be seeing this. Report it).");
            return false;
        }
        
        CommandBlock cb = (CommandBlock) block.getState();

        cb.setCommand(this.command());
        block.setType(this.getType());
        block.getState().update(true, false);

        org.bukkit.block.data.type.CommandBlock conditional = (org.bukkit.block.data.type.CommandBlock) cb.getBlock().getBlockData();
        ServerUtils.verbose("Direction is " + this.getDirection());
        ServerUtils.verbose("Conditional is " + this.conditional);

        conditional.setFacing(this.getDirection());
        conditional.setConditional(this.conditional);

        cb.setBlockData(conditional);
        
        cb.getPersistentDataContainer().set(
                Sentinel.getInstance().getNamespace("auto"),
                PersistentDataType.BYTE,
                this.auto ? (byte) 1 : (byte) 0
        );
        
        cb.update(true,false);
        ServerUtils.verbose("Command block at " + this.loc.toString() + " has been restored.");
        return true;
    }
    
    public boolean isCart() {
        return loc.isUUID();
    }
    
    public CommandBlockHolder add() {
        Sentinel.getInstance().getDirector().io.commandBlocks.holders.add(this);
        Sentinel.getInstance().getDirector().io.commandBlocks.save();
        return this;
    }

    public void delete() {
        SerialLocation.translate(this.loc).getBlock().setType(Material.AIR);
        Sentinel.getInstance().getDirector().io.commandBlocks.holders.removeIf(h->h.loc.isSameLocation(this.loc));
        Sentinel.getInstance().getDirector().io.commandBlocks.save();
    }
}
