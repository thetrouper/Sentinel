package io.github.thetrouper.sentinel.server;


import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
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
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemFlag;
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


            return new NewAction(event, action, player, command, loggedCommand, item, block, denied, deoped, punished, revertGM, notifyDiscord, notifyTrusted, notifyConsole);
        }
    }
}
