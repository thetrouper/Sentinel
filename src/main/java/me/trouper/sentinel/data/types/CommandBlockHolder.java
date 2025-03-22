package me.trouper.sentinel.data.types;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateCommandBlock;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateCommandBlockMinecart;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.startup.drm.Auth;
import me.trouper.sentinel.utils.DisplayUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import me.trouper.sentinel.utils.display.BlockDisplayRaytracer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CommandBlockHolder {

    private String owner;
    private SerialLocation loc;
    private String facing;
    private String type;
    private boolean auto;
    private boolean conditional;
    private String command;
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

    public CommandBlockHolder setOwner(String owner) {
        this.owner = owner;
        Sentinel.getInstance().getDirector().io.commandBlocks.save();
        return this;
    }

    public SerialLocation loc() {
        return this.loc;
    }

    public String facing() {
        return this.facing;
    }

    public String type() {
        return this.type;
    }

    public boolean isAuto() {
        return this.auto;
    }

    public boolean isConditional() {
        return this.conditional;
    }

    public String command() {
        return this.command;
    }
    
    public boolean present() {
        try {
            if (this.loc.isUUID()) {
                Entity cart = Bukkit.getEntity(this.loc.toUIID());
                if (!(cart instanceof CommandMinecart cm)) return false;
                return this.command.equals(cm.getCommand());
            } else {
                Location where = loc.translate();
                boolean preLoaded = where.isChunkLoaded();
                
                if (!where.isChunkLoaded()) where.getChunk().load(false);
                
                
                Block b = where.getBlock();
                if (!(b.getState() instanceof CommandBlock c) || !(b.getBlockData() instanceof org.bukkit.block.data.type.CommandBlock cb)) {
                    ServerUtils.verbose(1,"Block is not present due to not being a command block. Whitelisted: %s",this.isWhitelisted());
                    if (!this.isWhitelisted()) this.delete();
                    return false;
                }
                if (!this.getDirection().equals(cb.getFacing())) {
                    ServerUtils.verbose("Block is not present due to facing mismatch. Should be '%s', is '%s'",this.facing(),cb.getFacing());
                    if (!this.isWhitelisted()) this.delete();
                    return false;
                }
                if (!this.getType().equals(c.getType())) {
                    ServerUtils.verbose("Block is not present due to type mismatch. Should be '%s', is '%s'",this.type(),c.getType());
                    if (!this.isWhitelisted()) this.delete();
                    return false;
                }
                if (!this.command().equals(c.getCommand())) {
                    ServerUtils.verbose("Block is not present due to command mismatch. Should be '%s', is '%s'",this.command(),c.getCommand());
                    if (!this.isWhitelisted()) this.delete();
                    return false;
                }
                if (this.isConditional() != cb.isConditional()) {
                    ServerUtils.verbose("Block is not present due to conditional mismatch.");
                    if (!this.isWhitelisted()) this.delete();
                    return false;
                }
                if (this.isAuto() != (c.getPersistentDataContainer().getOrDefault(Sentinel.getInstance().getNamespace("auto"), PersistentDataType.BYTE,(byte) 0) == (byte) 1)) {
                    ServerUtils.verbose("Block is not present due to auto mismatch.");
                    if (!this.isWhitelisted()) this.delete();
                    return false;
                }
                if (!preLoaded) where.getChunk().unload();
                return true;
            }
        } catch (IllegalStateException ex) {
            ServerUtils.verbose("Do not check present command blocks asynchronously. Bukkit has something to say about this.");
            ex.printStackTrace();
            ServerUtils.verbose("Not present because the command block is not loaded. I really should make this not call async, and just have a variable that I update every so often...");
            return false;
        }
    }
    
    public boolean isWhitelisted() {
        return this.whitelisted;
    }

    public CommandBlockHolder setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
        Sentinel.getInstance().getDirector().io.commandBlocks.save();
        return this;
    }
    
    public CommandBlockHolder addAndWhitelist() {
        return setWhitelisted(true).add();
    }
    
    public BlockFace getDirection() {
        return BlockFace.valueOf(facing().toUpperCase());
    }
    
    public Material getType() {
        return Material.valueOf(type().toUpperCase());
    }
    
    public void destroy() {
        ServerUtils.verbose(1,"Destroying command block...");
        SerialLocation.translate(this.loc).getBlock().setType(Material.AIR);
        if (!this.isWhitelisted()) delete();
    }

    public boolean restore() {
        if (Material.COMMAND_BLOCK_MINECART.equals(this.getType())) {
            ServerUtils.verbose(1,"Cannot restore minecarts yet.");
            return false;
        }
        
        if (this.present() || !this.isWhitelisted()) return false;
        
        Block block = SerialLocation.translate(this.loc).getBlock();
        block.setType(this.getType());
        if (!ServerUtils.isCommandBlock(block)) {
            ServerUtils.verbose(1,"Block at the location was not a command block (You shouldn't be seeing this. Report it).");
            return false;
        }
        
        CommandBlock cb = (CommandBlock) block.getState();

        cb.setCommand(this.command());
        block.setType(this.getType());
        block.getState().update(true, false);

        org.bukkit.block.data.type.CommandBlock conditional = (org.bukkit.block.data.type.CommandBlock) cb.getBlock().getBlockData();
        //ServerUtils.verbose("Direction is " + this.getDirection());
        //ServerUtils.verbose("Conditional is " + this.conditional);

        conditional.setFacing(this.getDirection());
        conditional.setConditional(this.conditional);

        cb.setBlockData(conditional);
        
        cb.getPersistentDataContainer().set(
                Sentinel.getInstance().getNamespace("auto"),
                PersistentDataType.BYTE,
                this.auto ? (byte) 1 : (byte) 0
        );
        
        cb.update(true,true);
        ServerUtils.verbose("Command block at " + this.loc.toString() + " has been restored.");
        return true;
    }
    
    public boolean isCart() {
        return loc.isUUID();
    }
    
    public CommandBlockHolder add() {
        ServerUtils.verbose(1,"Adding command block...");
        Sentinel.getInstance().getDirector().io.commandBlocks.add(this);
        Sentinel.getInstance().getDirector().io.commandBlocks.save();
        return this;
    }

    public void delete() {
        ServerUtils.verbose(1,"Deleting & Destroying command block...");
        if (this.loc.isUUID() && Bukkit.getEntity(this.loc.toUIID()) != null) Bukkit.getEntity(this.loc.toUIID()).remove();
        else SerialLocation.translate(this.loc).getBlock().setType(Material.AIR);
        Sentinel.getInstance().getDirector().io.commandBlocks.remove(this);
        Sentinel.getInstance().getDirector().io.commandBlocks.save();
    }

    public void highlight(Player viewer, Material color) {
        if (this.loc.isUUID()) {
            Color c = switch (color) {
                case RED_CONCRETE_POWDER -> Color.RED;
                case LIME_CONCRETE_POWDER -> Color.LIME;
                case MAGENTA_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER -> Color.FUCHSIA;
                default -> Color.BLACK;
            };
            Entity cart = Bukkit.getEntity(this.loc.toUIID());
            if (cart == null) return;
            for (int i = 0; i < 5; i++) {
                DisplayUtils.ring(cart.getLocation().clone().add(0, (double) i /5,0),0.6, (location) -> {
                    DisplayUtils.PLAYER_DUST_PARTICLE_FACTORY.apply(c,1F).accept(viewer,location);
                },((location, integer) -> {
                    return integer % 36 == 0;
                }));
            }
        } else {
            BlockDisplayRaytracer.outline(color, this.loc.translate(), 0.05, 2, List.of(viewer));
        }
    }

    public boolean update(Player updater) {
        ServerUtils.verbose(1,"Processing update requested by %s",updater.getName());
        if (this.isWhitelisted()) return false;
        boolean changesMade = false;
        if (!this.owner().equals(updater.getUniqueId().toString())) {
            this.owner = updater.getUniqueId().toString();
            changesMade = true;
        }
        if (this.loc.isUUID()) {
            Entity cart = Bukkit.getEntity(this.loc.toUIID());
            if (!(cart instanceof CommandMinecart cm)) return false;
            if (!cm.getCommand().equals(this.command())) {
                this.command = cm.getCommand();
                changesMade = true;
            }
        } else {
            Location where = loc.translate();
            boolean preLoaded = where.isChunkLoaded();
            where.getChunk().load(false);
            Block b = where.getBlock();
            if (!(b.getState() instanceof CommandBlock c) || !(b.getBlockData() instanceof org.bukkit.block.data.type.CommandBlock cb)) {
                ServerUtils.verbose(1,"Block cannot be updated due to not being a command block. It will be deleted if it is not whitelisted. Whitelisted: %s",this.isWhitelisted());
                if (!this.isWhitelisted()) this.delete();
                return false;
            }
            if (!this.getDirection().equals(cb.getFacing())) {
                ServerUtils.verbose("Block needs update due to facing mismatch. Should be '%s', is '%s'",this.facing(),cb.getFacing());
                this.facing = cb.getFacing().toString();
                changesMade = true;
            }
            if (!this.getType().equals(c.getType())) {
                ServerUtils.verbose("Block needs update due to type mismatch. Should be '%s', is '%s'",this.type(),c.getType());
                this.type = c.getType().toString();
                changesMade = true;

            }
            if (!this.command().equals(c.getCommand())) {
                ServerUtils.verbose("Block needs update due to command mismatch. Should be '%s', is '%s'",this.command(),c.getCommand());
                this.command = c.getCommand();
                changesMade = true;

            }
            if (this.isConditional() != cb.isConditional()) {
                ServerUtils.verbose("Block needs update due to conditional mismatch.");
                this.conditional = cb.isConditional();
                changesMade = true;

            }
            if (this.isAuto() != (c.getPersistentDataContainer().getOrDefault(Sentinel.getInstance().getNamespace("auto"), PersistentDataType.BYTE,(byte) 0) == (byte) 1)) {
                ServerUtils.verbose("Block needs update due to auto mismatch.");
                this.auto = (c.getPersistentDataContainer().getOrDefault(Sentinel.getInstance().getNamespace("auto"), PersistentDataType.BYTE,(byte) 0) == (byte) 1);
                changesMade = true;
            }
            if (!preLoaded) where.getChunk().unload();
        }
        
        if (changesMade) {
            updater.sendMessage(Text.prefix("Successfully updated a &b%s&7.".formatted(Text.cleanName(this.type()))));
            updater.playSound(updater.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1.5F);
        }
        return changesMade;
    }

    public boolean update(Player updater, WrapperPlayClientUpdateCommandBlockMinecart packet) {
        ServerUtils.verbose(1,"Processing packet update requested by %s",updater.getName());
        if (this.isWhitelisted()) return false;
        boolean changesMade = false;
        if (!this.owner().equals(updater.getUniqueId().toString())) {
            this.owner = updater.getUniqueId().toString();
            changesMade = true;
        }

        if (!this.loc.isUUID()) {
            throw new IllegalArgumentException("Cannot update block commands with this packet.");
        }

        if (!this.command().equals(packet.getCommand())) {
            ServerUtils.verbose("Block needs update due to command mismatch. Should be '%s', is '%s'",this.command(),packet.getCommand());
            this.command = packet.getCommand();
            changesMade = true;

        }

        if (changesMade) {
            updater.sendMessage(Text.prefix("Successfully updated a &b%s&7.".formatted(Text.cleanName(this.type()))));
            updater.playSound(updater.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1.5F);
        }
        return changesMade;
    }

    public boolean update(Player updater, WrapperPlayClientUpdateCommandBlock packet) {
        ServerUtils.verbose(1,"Processing packet update requested by %s",updater.getName());
        if (this.isWhitelisted()) return false;
        boolean changesMade = false;
        if (!this.owner().equals(updater.getUniqueId().toString())) {
            this.owner = updater.getUniqueId().toString();
            changesMade = true;
        }

        if (this.loc.isUUID()) {
           throw new IllegalArgumentException("Cannot update UUID command blocks with this packet.");
        }

        Material t = switch (packet.getMode()) {
            case AUTO -> Material.REPEATING_COMMAND_BLOCK;
            case REDSTONE -> Material.COMMAND_BLOCK;
            case SEQUENCE -> Material.CHAIN_COMMAND_BLOCK;
        };

        if (!this.getType().equals(t)) {
            ServerUtils.verbose("Block needs update due to type mismatch. Should be '%s', is '%s'",this.type(),t.toString());
            this.type = t.toString();
            changesMade = true;

        }
        if (!this.command().equals(packet.getCommand())) {
            ServerUtils.verbose("Block needs update due to command mismatch. Should be '%s', is '%s'",this.command(),packet.getCommand());
            this.command = packet.getCommand();
            changesMade = true;

        }
        if (this.isConditional() != packet.isConditional()) {
            ServerUtils.verbose("Block needs update due to conditional mismatch.");
            this.conditional = packet.isConditional();
            changesMade = true;

        }
        if (this.isAuto() != packet.isAutomatic()) {
            ServerUtils.verbose("Block needs update due to auto mismatch.");
            this.auto = packet.isAutomatic();
            changesMade = true;
        }

        if (changesMade) {
            updater.sendMessage(Text.prefix("Successfully updated a &b%s&7.".formatted(Text.cleanName(this.type()))));
            updater.playSound(updater.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1.5F);
        }
        return changesMade;
    }
}
