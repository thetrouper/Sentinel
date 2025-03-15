package me.trouper.sentinel.server.functions.helpers;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.SerialLocation;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.trees.Node;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ActionConfiguration {
    private Player player;
    private boolean deop;
    private Cancellable event;
    private boolean cancel;
    private Block block;
    private Entity entity;
    private boolean destroyBlock;
    private boolean removeEntity;
    private boolean restoreBlock;
    private boolean punish;
    private List<String> punishmentCommands;
    private boolean logToDiscord;
    private Node actionNode;

    public ActionConfiguration(Builder builder) {
        this.player = builder.player;
        this.deop = builder.deop;
        this.event = builder.event;
        this.cancel = builder.cancel;
        this.block = builder.block;
        this.entity = builder.entity;
        this.destroyBlock = builder.destroyBlock;
        this.removeEntity = builder.removeEntity;
        this.restoreBlock = builder.restoreBlock;
        this.punish = builder.punish;
        this.punishmentCommands = builder.punishmentCommands;
        this.logToDiscord = builder.logToDiscord;
        this.actionNode = builder.actionNode;
        // Removed the actions being run here to prevent double execution
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isDeop() {
        return deop;
    }

    public void setDeop(boolean deop) {
        this.deop = deop;
    }

    public Cancellable getEvent() {
        return event;
    }

    public void setEvent(Cancellable event) {
        this.event = event;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isDestroyBlock() {
        return destroyBlock;
    }

    public void setDestroyBlock(boolean destroyBlock) {
        this.destroyBlock = destroyBlock;
    }

    public void setRemoveEntity(boolean removeEntity) {
        this.removeEntity = removeEntity;
    }

    public boolean getRemoveEntity() {
        return removeEntity;
    }

    public boolean isRestoreBlock() {
        return restoreBlock;
    }

    public void setRestoreBlock(boolean restoreBlock) {
        this.restoreBlock = restoreBlock;
    }

    public boolean isPunish() {
        return punish;
    }

    public void setPunish(boolean punish) {
        this.punish = punish;
    }

    public List<String> getPunishmentCommands() {
        return punishmentCommands;
    }

    public void setPunishmentCommands(List<String> punishmentCommands) {
        this.punishmentCommands = punishmentCommands;
    }

    public boolean isLogToDiscord() {
        return logToDiscord;
    }

    public void setLogToDiscord(boolean logToDiscord) {
        this.logToDiscord = logToDiscord;
    }

    public Node getActionNode() {
        return actionNode;
    }

    public void setActionNode(Node actionNode) {
        this.actionNode = actionNode;
    }

    public static class Builder {
        private Player player;
        private boolean deop;
        private Cancellable event;
        private boolean cancel;
        private Block block;
        private Entity entity;
        private boolean destroyBlock;
        private boolean removeEntity;
        private boolean restoreBlock;
        private boolean punish;
        private List<String> punishmentCommands = new ArrayList<>();
        private boolean logToDiscord;
        private Node actionNode = new Node(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.actionNodeTitle);

        private List<Consumer<ActionConfiguration>> actions = new ArrayList<>();

        public Builder setPlayer(Player player) {
            this.player = player;
            actions.add(config -> config.player = player);
            return this;
        }

        public Builder deop(boolean deop) {
            this.deop = deop;
            actions.add(config -> {
                config.deop = deop;
                if (config.player != null) {
                    config.player.setOp(false);
                }
                config.actionNode.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.userDeoped);
            });
            return this;
        }

        public Builder setEvent(Cancellable event) {
            this.event = event;
            actions.add(config -> config.event = event);
            return this;
        }

        public Builder cancel(boolean cancel) {
            this.cancel = cancel;
            actions.add(config -> {
                config.cancel = cancel;
                if (config.event != null) {
                    config.event.setCancelled(true);
                }
                config.actionNode.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.eventCancelled);
            });
            return this;
        }

        public Builder setBlock(Block block) {
            this.block = block;
            actions.add(config -> config.block = block);
            return this;
        }

        public Builder destroyBlock(boolean destroyBlock) {
            this.destroyBlock = destroyBlock;
            actions.add(config -> {
                config.destroyBlock = destroyBlock;
                if (config.block != null) {
                    config.block.setType(Material.AIR);
                    config.actionNode.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.destroyedBlock);
                }
            });
            return this;
        }

        public Builder restoreBlock(boolean restoreBlock) {
            this.restoreBlock = restoreBlock;
            actions.add(config -> {
                config.restoreBlock = restoreBlock;
                if (config.block != null) {
                    if (Sentinel.getInstance().getDirector().whitelistManager.getFromWhitelist(config.block.getLocation()) != null && Sentinel.getInstance().getDirector().whitelistManager.getFromWhitelist(config.block.getLocation()).restore())  {
                        config.actionNode.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.restore);
                    } else {
                        config.actionNode.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.restoreFailed);
                    }
                }
            });
            return this;
        }

        public Builder setEntity(Entity entity) {
            this.entity = entity;
            actions.add(config -> config.entity = entity);
            return this;
        }

        public Builder removeEntity(boolean removeEntity) {
            this.removeEntity = removeEntity;
            actions.add(config -> {
                config.removeEntity = removeEntity;
                if (config.entity != null) {
                    entity.remove();
                }
            });
            return this;
        }

        public Builder punish(boolean punish) {
            this.punish = punish;
            actions.add(config -> config.punish = punish);
            return this;
        }

        public Builder setPunishmentCommands(List<String> punishmentCommands) {
            this.punishmentCommands = punishmentCommands;
            actions.add(config -> {
                config.punishmentCommands = punishmentCommands;
                if (config.punish && config.player != null) {
                    for (String cmd : punishmentCommands) {
                        ServerUtils.sendCommand(cmd.replaceAll("%player%", config.player.getName()));
                    }
                    config.actionNode.addTextLine(Sentinel.getInstance().getDirector().io.lang.violations.protections.actionNode.punishmentCommandsExecuted);
                }
            });
            return this;
        }

        public Builder logToDiscord(boolean logToDiscord) {
            this.logToDiscord = logToDiscord;
            actions.add(config -> config.logToDiscord = logToDiscord);
            return this;
        }

        public boolean isLoggedToDiscord() {
            return this.logToDiscord;
        }

        public Node getActionNode() {
            return this.actionNode;
        }

        public ActionConfiguration build() {
            ActionConfiguration config = new ActionConfiguration(this);
            actions.forEach(action -> action.accept(config));
            return config;
        }
    }
}