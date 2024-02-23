package io.github.thetrouper.sentinel.server;


import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.server.util.FileUtils;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NewAction {

    private NewAction(Cancellable event, ActionType action, Player player, String command, String loggedCommand, ItemStack item, Block block, boolean denied, boolean deoped, boolean punished, boolean revertedGM, boolean notifyDiscord, boolean notifyTrusted, boolean notifyConsole) {
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
        public Builder setNotifyDiscord(boolean notifyDiscord) {
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
        public NewAction execute() {
            String actionTop = action.getMessageTop();
            String actionTitle = action.getMessageTitle();
            String itemLog = (item != null && item.hasItemMeta() && item.getItemMeta().getAsString() != null) ? FileUtils.createNBTLog(item) : "";
            String commandLog = (loggedCommand != null) ? FileUtils.createCommandLog(loggedCommand) : "";

            final List<String> punishCommands = Sentinel.mainConfig.plugin.punishCommands;

            if (denied) {
                event.setCancelled(true);
            }

            if (deoped) {
                player.setOp(false);
            }

            if (punished) {
                for (String command : punishCommands) {
                    ServerUtils.sendCommand(command.replaceAll("%player%",player.getName()));
                }
            }

            if (revertGM) {
                player.setGameMode(GameMode.SURVIVAL);
            }

            if (notifyConsole) {
                String conNotif = "]=- Sentinel -=[\n";
                conNotif += actionTop + "\n";
                conNotif += (player != null) ? "Player: " + player.getName() + "\n" : "";
                conNotif += (command != null) ? ((loggedCommand != null && loggedCommand.length() > 128) ? "Command: Too long to show here!\n | Saved to file: " + commandLog + "\n" : "Command: " + command + "\n") : "";
                conNotif += (item != null) ? "Item: /Sentinel/LoggedNBT/" + itemLog + "\n" : "";
                conNotif += (block != null) ? "Block: " + block.getType().toString().toLowerCase().replace("_", " ") + "\nLocation: " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ() + "\n" : "";
                conNotif += "Denied: " + (denied ? "\u2714" : "\u2718") + "\n";
                conNotif += "Deoped: " + (deoped ? "\u2714" : "\u2718") + "\n";
                conNotif += "Punished: " + (punished ? "\u2714" : "\u2718") + "\n";
                conNotif += (revertGM) ? "RevertGM: \u2714\n" : "";
                conNotif += "Logged: " + (notifyDiscord ? "\u2714" : "\u2718");
                Sentinel.log.info(conNotif);
            }

            if (notifyTrusted) {
                TextComponent notification = new TextComponent();
                notification.setText(Text.prefix(" " + actionTop));
                String body = "&b]=- Sentinel -=[&f\n" + actionTitle + "&r\n";
                body += (player != null) ? "&fPlayer: &b" + player.getName() + "&r\n" : "";
                body += (command != null) ? ((loggedCommand != null && loggedCommand.length() > 64) ? "&fCommand: &cToo long to show here!&r\n &7&l| &fSaved to file: &b" + commandLog + "&r\n" : "&fCommand: &b" + command + "&r\n") : "";
                body += (item != null) ? "&fItem: &b/Sentinel/LoggedNBT/&b" + itemLog + "\n" : "";
                body += (block != null) ? "&fBlock: &b" + block.getType().toString().toLowerCase().replace("_", " ") + "\n&fLocation: &b" + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ() + "&r\n" : "";
                body += "&fDenied: &b" + (denied ? "&a\u2714" : "&c\u2718") + "&r\n";
                body += "&fDeoped: " + (deoped ? "&a\u2714" : "&c\u2718") + "&r\n";
                body += "&fPunished: " + (punished ? "&a\u2714" : "&c\u2718") + "&r\n";
                body += (revertGM) ? "&fRevertGM: &a\u2714\n" : "";
                body += "&fLogged: " + (notifyDiscord ? "&a\u2714" : "&c\u2718");
                notification.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(Text.color(body))));
                ServerUtils.forEachPlayer(trusted -> {
                    if (Sentinel.isTrusted(trusted)) {
                        trusted.spigot().sendMessage(notification);
                    }
                });
            }

            if (notifyDiscord) {
                String description = (player != null) ? Emojis.rightSort + " **Player:** " + player.getName() + " " + Emojis.member + "\n" : "";
                description += (command != null) ? ((loggedCommand != null && loggedCommand.length() > 128) ? Emojis.rightSort + " **Command:** Too long to show here! " + Emojis.nuke + "\n | Saved to file: " + commandLog + "\n" : Emojis.rightSort + " **Command:** " + command + " " + Emojis.nuke + "\n") : "";
                description += (item != null) ? Emojis.rightSort + " **Item:** " + item.getType().toString().toLowerCase() + " " + Emojis.nuke + "\n" + Emojis.space + Emojis.rightDoubleArrow + "**NBT:** Uploaded to /Sentinel/LoggedNBT/" + itemLog : "";
                description += (block != null) ? Emojis.rightSort + " **Block:** " + block.getType().toString().toLowerCase() + " " + Emojis.nuke + "\n" + Emojis.space + Emojis.rightDoubleArrow + " **Location:** X: " + block.getX() + " Y: " + block.getY() + " Z: " + block.getZ() + "\n" : "";
                String actions = Emojis.rightSort + " **Denied:** " + (denied ? Emojis.success : Emojis.failure) + "\n";
                actions += Emojis.rightSort + " **De-oped:** " + (deoped ? Emojis.success : Emojis.failure) + "\n";
                actions += Emojis.rightSort + " **Punished:** " + (punished ? Emojis.success : Emojis.failure) + "\n";
                actions += (revertGM) ? Emojis.rightSort + " **GM Reverted:** " + Emojis.success + "\n" : "";
                actions += Emojis.rightSort + " **Logged:** " + Emojis.success;

                try {
                    String finalDescription = description;
                    String finalActions = actions;
                    CompletableFuture.runAsync(()->{
                        ServerUtils.sendDebugMessage("Executing webhook...");
                        DiscordWebhook.create()
                                .username("Sentinel Anti-Nuke | Logs")
                                .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                                .addEmbed(DiscordEmbed.create()
                                        .author(new DiscordEmbed.Author(actionTop,"https://builtbybit.com/resources/sentinel-anti-nuke.30130/",null))
                                        .title(actionTitle)
                                        .desc(finalDescription)
                                        .addField(new DiscordEmbed.Field("Actions:", finalActions,false))
                                        .thumbnail("https://crafatar.com/avatars/" + (player == null ? "049460f7-21cb-42f5-8059-d42752bf406f" : player.getUniqueId()) + "?size=64&&overlay")
                                        .color(action.getEmbedColor())
                                        .build()).send(Sentinel.mainConfig.plugin.webhook);
                    });
                } catch (Exception e) {
                    ServerUtils.sendDebugMessage(Text.prefix("Epic webhook failure!!!"));
                    Sentinel.log.info(e.toString());
                }
            }

            return new NewAction(event, action, player, command, loggedCommand, item, block, denied, deoped, punished, revertGM, notifyDiscord, notifyTrusted, notifyConsole);
        }
    }
}
