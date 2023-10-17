package io.github.thetrouper.sentinel.server;


import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Config;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class Action {
    private Cancellable event;
    private ActionType action;
    private Player player;
    private String command;
    private String loggedCommand;
    private ItemStack item;
    private Block block;
    private boolean denied;
    private boolean deoped;
    private boolean punished;
    private boolean revertGM;
    private boolean notifyDiscord;
    private boolean notifyTrusted;
    private boolean notifyConsole;

    private Action(Cancellable event, ActionType action, Player player, String command, String loggedCommand, ItemStack item, Block block,boolean denied, boolean deoped, boolean punished, boolean revertedGM, boolean notifyDiscord, boolean notifyTrusted, boolean notifyConsole) {
        this.event = event;
        this.action = action;
        this.player = player;
        this.command = command;
        this.loggedCommand = loggedCommand;
        this.item = item;
        this.block = block;
        this.denied = denied;
        this.deoped = deoped;
        this.punished = punished;
        this.revertGM = revertedGM;
        this.notifyDiscord = notifyDiscord;
        this.notifyTrusted = notifyTrusted;
        this.notifyConsole = notifyConsole;
    }

    public static class Builder {
        Cancellable event;
        ActionType action;
        private Player player;
        private String command;
        private String loggedCommand;
        private ItemStack item;
        private Block block;
        private boolean denied;
        private boolean deoped;
        private boolean punished;
        private boolean revertGM;
        private boolean notifyDiscord;
        private boolean notifyTrusted;
        private boolean notifyConsole;
        public Builder setEvent(Cancellable event) {
            this.event = event;
            return this;
        }
        public Builder setAction(ActionType action) {
            this.action = action;
            return this;
        }
        public Builder setPlayer(Player player) {
            this.player = player;
            return this;
        }
        public Builder setCommand(String command) {
            this.command = command;
            return this;
        }
        public Builder setLoggedCommand(String loggedCommand) {
            this.loggedCommand = loggedCommand;
            return this;
        }
        public Builder setItem(ItemStack item) {
            this.item = item;
            return this;
        }
        public Builder setBlock(Block block){
            this.block = block;
            return this;
        }
        public Builder setDenied(boolean denied) {
            this.denied = denied;
            return this;
        }
        public Builder setDeoped(boolean deoped) {
            this.deoped = deoped;
            return this;
        }
        public Builder setPunished(boolean punished) {
            this.punished = punished;
            return this;
        }
        public Builder setRevertGM(boolean revertGM) {
            this.revertGM = revertGM;
            return this;
        }
        public Builder setnotifyDiscord(boolean notifyDiscord) {
            this.notifyDiscord= notifyDiscord;
            return this;
        }
        public Builder setNotifyTrusted(boolean notifyTrusted) {
            this.notifyTrusted = notifyTrusted;
            return this;
        }
        public Builder setNotifyConsole(boolean notifyConsole) {
            this.notifyConsole = notifyConsole;
            return this;
        }
        public Action execute() {
            String actionTop = "Generic Anti-Action has been triggered";
            String actionTitle = "A generic action has been detected!";
            String itemLog = "";
            String commandLog = "";
            actionTop = action.getMessageTop();
            actionTitle = action.getMessageTitle();
            final List<String> punishCommands = Config.getPunishCommands();
            if (denied) event.setCancelled(true);
            if (deoped) player.setOp(false);
            if (punished) for (String command : punishCommands) {
                ServerUtils.sendCommand(command);
            }
            if (revertGM) player.setGameMode(GameMode.SURVIVAL);
            if (item != null) itemLog = FileUtils.createNBTLog(item.getItemMeta().getAsString());
            if (loggedCommand != null) commandLog = FileUtils.createCommandLog(loggedCommand);
            if (notifyConsole) {
                String conNotif = "]=- Sentinel -=[";
                conNotif += " " + actionTop;
                conNotif += "\n" + actionTitle + "\n";
                if (player != null) {
                    conNotif += "Player: " + player.getName() + "\n";
                }
                if (command != null) {
                    if (loggedCommand != null) {
                        if (loggedCommand.length() > 128) {
                            conNotif += "Command: Too long to show here!" + "\n";
                            conNotif += " | Saved to file: " + commandLog + "\n";
                        } else {
                            conNotif += "Command: " + command + "\n";
                        }
                    } else {
                        conNotif += "Command: " + command + "\n";
                    }
                }

                if (item != null) {
                    conNotif += "Item: /Sentinel/LoggedNBT/" + itemLog + "\n";
                }
                if (block != null) {
                    Location loc = block.getLocation();
                    conNotif += "Block: " + block.getType().toString().toLowerCase().replace("_", " ") + "\n";
                    conNotif += "Location: " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + "\n";
                }
                conNotif += "Denied: " + (denied ? "\u2714" : "\u2718") + "\n";
                if (deoped) {
                    player.setOp(false);
                }
                conNotif += "Deoped: " + (deoped ? "\u2714" : "\u2718") + "\n";

                conNotif += "Punished: " + (punished ? "\u2714" : "\u2718") + "\n";
                if (revertGM) conNotif += "RevertGM: " + "\u2714" + "\n";
                conNotif += "Logged: " + (notifyDiscord ? "\u2714" : "\u2718");
                Sentinel.log.info(conNotif);
            }
            if (notifyTrusted) {
                TextComponent notification = new TextComponent();
                notification.setText(Text.prefix(" " + actionTop));
                String body = "]=- Sentinel -=[ ";
                body += "\n" + actionTitle + "\n";
                if (player != null) {
                    body += "Player: " + player.getName() + "\n";
                }
                if (command != null) {
                    if (loggedCommand != null) {
                        if (loggedCommand.length() > 64) {
                            body += "Command: Too long to show here!" + "\n";
                            body += " | Saved to file: " + commandLog + "\n";
                        } else {
                            body += "Command: " + command + "\n";
                        }
                    } else {
                        body += "Command: " + command + "\n";
                    }
                }
                if (item != null) {
                    body += "Item: /Sentinel/LoggedNBT/" + itemLog + "\n";
                }
                if (block != null) {
                    Location loc = block.getLocation();
                    body += "Block: " + block.getType().toString().toLowerCase().replace("_", " ") + "\n";
                    body += "Location: " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + "\n";
                }
                body += "Denied: " + (denied ? "\u00a7a\u2714" : "\u00a7c\u2718") + "\n";
                if (deoped) {
                    player.setOp(false);
                }
                body += "Deoped: " + (deoped ? "\u00a7a\u2714" : "\u00a7c\u2718") + "\n";

                body += "Punished: " + (punished ? "\u00a7a\u2714" : "\u00a7c\u2718") + "\n";
                if (revertGM) body += "RevertGM: " + "\u00a7a\u2714" + "\n";
                body += "Logged: " + (notifyDiscord ? "\u00a7a\u2714" : "\u00a7c\u2718");
                notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(body)));
                ServerUtils.forEachPlayer(trusted -> {
                    if (Sentinel.isTrusted(trusted)) {
                        trusted.spigot().sendMessage(notification);
                    }
                });
            }
            if (notifyDiscord) {
                DiscordWebhook webhook = new DiscordWebhook(Config.webhook);
                webhook.setAvatarUrl("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png");
                webhook.setUsername("Sentinel Anti-Nuke | Logs");
                String description = "";
                if (player != null) description += Emojis.rightSort + " **Player:** " + player.getName() + " " + Emojis.member + "\\n";
                if (command != null) {
                    if (loggedCommand != null) {
                        if (loggedCommand.length() > 128) {
                            description += Emojis.rightSort + " **Command:** Too long to show here!" + " " + Emojis.nuke + "\\n";
                            description += Emojis.space + Emojis.rightDoubleArrow + " | Saved to file: " + commandLog + "\n";
                        } else {
                            description += Emojis.rightSort + " **Command:** " + command + " " + Emojis.nuke + "\\n";
                        }
                    } else {
                        description += Emojis.rightSort + " **Command:** " + command + " " + Emojis.nuke + "\\n";
                    }

                }
                if (item != null) description += Emojis.rightSort + " **Item:** " + item.getType().toString().toLowerCase() + " " + Emojis.nuke + "\\n" +
                                                 Emojis.space + Emojis.rightDoubleArrow + "**NBT:** Uploaded to /Sentinel/LoggedNBT/" + itemLog;
                if (block != null) {
                    description += Emojis.rightSort + " **Block:** " + block.getType().toString().toLowerCase() + " " + Emojis.nuke + "\\n" +
                                   Emojis.space + Emojis.rightDoubleArrow + " **Location:** X: " + block.getX() + " Y: " + block.getY() + " Z: " + block.getZ() + "\\n";
                }
                String actions = "";
                actions += Emojis.rightSort + " **Denied:** " + (denied ? Emojis.success : Emojis.failure) + "\\n";
                actions += Emojis.rightSort + " **De-oped:** " + (deoped ? Emojis.success : Emojis.failure) + "\\n";
                actions += Emojis.rightSort + " **Punished:** " + (punished ? Emojis.success : Emojis.failure) + "\\n";
                if (revertGM) actions += Emojis.rightSort + " **GM Reverted:** " + Emojis.success + "\\n";
                actions += Emojis.rightSort + " **Logged:** "  + Emojis.success;
                DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                        .setAuthor(actionTop,"","")
                        .setTitle(actionTitle)
                        .setDescription(description)
                        .addField("Actions:",actions, false)
                        .setThumbnail("https://crafatar.com/avatars/" + player.getUniqueId() + "?size=64&&overlay")
                        .setColor(action.getEmbedColor());
                webhook.addEmbed(embed);
                try {
                    ServerUtils.sendDebugMessage("Executing webhook...");
                    webhook.execute();
                } catch (IOException e) {
                    ServerUtils.sendDebugMessage(Text.prefix("Epic webhook failure!!!"));
                    Sentinel.log.info(e.toString());
                }
            }
            return new Action(event, action, player, command, loggedCommand, item, block, denied, deoped, punished, revertGM, notifyDiscord, notifyTrusted, notifyConsole);
        }
    }
}
