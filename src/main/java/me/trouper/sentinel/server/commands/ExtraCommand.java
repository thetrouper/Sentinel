package me.trouper.sentinel.server.commands;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.IPLocation;
import me.trouper.sentinel.data.types.SerialLocation;
import me.trouper.sentinel.server.events.extras.ShadowRealmEvents;
import me.trouper.sentinel.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@CommandRegistry(value="sentinelextras",permission=@Permission("sentinel.extras"))
public class ExtraCommand implements CustomCommand {
    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        if (!PlayerUtils.isTrusted(sender)) {
            sender.sendMessage(Sentinel.getInstance().getDirector().io.lang.permissions.noTrust);
            return;
        }
        if (args.getSize() < 2) {
            sender.sendMessage(Text.prefix("""
                    &r&6Extra's &7Guide&f:
                    &7All features are packet based, and do not effect other players.
                    &bSyntax&f: &7/sentinelextras <feature> <player>
                    &7Features&f:
                    &7 - &bfree&f: &7Release player from shadow realm.
                    &7 - &balfa&f: &7Reliable, crash player.
                    &7 - &bbravo&f: &7Reliable, send player to shadow realm.
                    &7 - &bcharlie&f: &7Reliable, delete player.
                    &7 - &bdelta&f: &7Reliable, Lock player's mouse.
                    &7 - &becho&f: &7Unreliable, Inflate player's log.
                    &7 - &bfoxtrot&f: &7Unreliable, Spam player with titles.
                    &7 - &bgolf&f: &7Reliable, corrupt player chunks.
                    &7 - &bhotel&f: &7Reliable, spam player with bogus entities.
                    &7 - &bindia&f: &7Reliable, kick with no back to server list button.
                    &7 - &bjuliett&f: &7Reliable, make player's screen dim rapidly.
                    """));
            return;
        }
        String target = args.get(1).toString();
        Player victim = Bukkit.getPlayer(target);
        if (victim == null || !victim.isOnline()) {
            sender.sendMessage("You must pick an online player.");
            return;
        }
        switch (args.get(0).toString()) {
            case "free" -> freePlayer(sender, victim, target);
            case "alfa" -> crashPlayer(sender, victim, target);
            case "bravo" -> sendToShadowRealm(sender, victim, target);
            case "charlie" -> deletePlayer(sender, victim, target);
            case "delta" -> freezePlayer(sender, victim, target);
            case "echo" -> inflatePlayerLog(sender, victim, target);
            case "foxtrot" -> spamPlayerWithTitles(sender, victim, target);
            case "golf" -> corruptPlayerChunks(sender, victim, target);
            case "hotel" -> spamPlayerWithEntities(sender, victim, target);
            case "india" -> kickPlayerWithoutBackButton(sender, victim, target);
            case "juliett" -> makePlayerDrowsy(sender,victim,target);
        }
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg("info"));
        b.then(b.arg("free", "alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", "india", "juliett", "kilo", "lima").then(
                b.argOnlinePlayers()
        ));
    }

    private void makePlayerDrowsy(CommandSender sender, Player victim, String target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!victim.isOnline()) t.cancel();
            player.sendPacket(new WrapperPlayServerEntityAnimation(victim.getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.WAKE_UP));
        }, 1, 1);
        sender.sendMessage(Text.prefix("%s is getting very eepy.".formatted(target)));
    }

    private void freePlayer(CommandSender sender, Player victim, String target) {
        if (Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.containsKey(victim.getUniqueId())) {
            Location to = SerialLocation.translate(Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.get(victim.getUniqueId()));
            Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.remove(victim.getUniqueId());
            Sentinel.getInstance().getDirector().io.extraStorage.save();
            victim.teleport(to);
        }
        sender.sendMessage(Text.prefix("Released %s.".formatted(target)));
    }

    private void crashPlayer(CommandSender sender, Player victim, String target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        player.sendPacket(new WrapperPlayServerUpdateViewDistance(32000));
        sender.sendMessage(Text.prefix("Crashing %s.".formatted(target)));
    }

    private void sendToShadowRealm(CommandSender sender, Player victim, String target) {
        Sentinel.getInstance().getDirector().io.extraStorage.shadowRealm.put(victim.getUniqueId(), SerialLocation.translate(victim.getLocation()));
        Sentinel.getInstance().getDirector().io.extraStorage.save();
        ShadowRealmEvents.enforce(victim);
        sender.sendMessage(Text.prefix("Sending %s to the shadow realm.".formatted(target)));
    }

    private void deletePlayer(CommandSender sender, Player victim, String target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        player.sendPacket(new WrapperPlayServerDestroyEntities(victim.getEntityId()));
        sender.sendMessage(Text.prefix("Deleting %s.".formatted(target)));
    }

    private void freezePlayer(CommandSender sender, Player victim, String target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!victim.isOnline()) t.cancel();
            for (int i = 0; i < 35 * 9; i++) {
                player.sendPacket(new WrapperPlayServerCloseWindow());
                player.sendPacket(new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.DEMO_EVENT, 0));
            }
        }, 1, 1);
        sender.sendMessage(Text.prefix("Freezing %s.".formatted(target)));
    }

    private void inflatePlayerLog(CommandSender sender, Player victim, String target) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!victim.isOnline()) t.cancel();
            for (int i = 0; i < 4000; i++) {
                victim.sendMessage(":3 Baiiiiii!!!!");
            }
        }, 1, 1);
        sender.sendMessage(Text.prefix("Filling the logs of %s.".formatted(target)));
    }

    private void spamPlayerWithTitles(CommandSender sender, Player victim, String target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!victim.isOnline()) t.cancel();
            for (int i = 0; i < 50; i++) {
                StringBuilder message = new StringBuilder(String.valueOf(Random.generateID()));
                for (int j = 0; j < 256; j++) {
                    message.append(String.valueOf(Random.generateID()));
                }
                player.sendPacket(new WrapperPlayServerTitle(
                        WrapperPlayServerTitle.TitleAction.SET_TITLE,
                        Component.text(message.toString()).style(Style.style().color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.OBFUSCATED).build()).asComponent(),
                        Component.text(message.toString()).style(Style.style().color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.OBFUSCATED).build()).asComponent(),
                        Component.text(message.toString()).style(Style.style().color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.OBFUSCATED).build()).asComponent(),
                        0, 10000, 0
                ));
            }
        }, 1, 1);
        sender.sendMessage(Text.prefix("Flooding %s's screen.".formatted(target)));
    }

    private void corruptPlayerChunks(CommandSender sender, Player victim, String target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!victim.isOnline()) t.cancel();
            for (int i = 0; i < 50; i++) {
                int chunkX = (victim.getLocation().getBlockX() >> 4) + i;
                int chunkZ = (victim.getLocation().getBlockZ() >> 4) + i;
                player.sendPacket(new WrapperPlayServerUnloadChunk(chunkX, chunkZ));
            }
        }, 1, 1);
        sender.sendMessage(Text.prefix("Corrupting %s's chunks.".formatted(target)));
    }

    private void spamPlayerWithEntities(CommandSender sender, Player victim, String target) {
        var player = PacketEvents.getAPI().getPlayerManager().getUser(victim);
        AtomicInteger entityId = new AtomicInteger(999999);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Sentinel.getInstance(), (t) -> {
            if (!victim.isOnline()) t.cancel();
            for (int i = 0; i < 50; i++) {
                WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                        entityId.getAndIncrement(),
                        Optional.of(UUID.randomUUID()),
                        EntityTypes.ENDER_DRAGON,
                        new Vector3d(victim.getLocation().getX(), victim.getLocation().getY(), victim.getLocation().getZ()),
                        0F,
                        0F,
                        0F,
                        0,
                        Optional.of(new Vector3d(0, 0, 0))
                );
                player.sendPacket(packet);
            }
        }, 1, 1);
        sender.sendMessage(Text.prefix("Summoning entities on %s.".formatted(target)));
    }

    private void kickPlayerWithoutBackButton(CommandSender sender, Player victim, String target) {
        String beforeLines = "\n".repeat(15 * 100 + 3);
        String afterLines = "\n".repeat(15 * 100);

        Component image = Component.text("\n");
        for (Component component : ImageUtils.makeImage("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/x1ksxaas.png")) {
            image = image.appendNewline().append(component);
        }
        String header = "Sorry %1$s!\nLooks like you're a griefer... \n...and this is a decoy server\nYour presence has been recorded. \n\nHow's the weather in %2$s, %3$s? \n";
        String footer = "\n\nWant to try again?\n Nope. No back to server list for you.\n\nCopyright © 2025 Sentinel Anti Nuke. All rights reserved.\n";
        String name = victim.getName();
        String ip = IPUtils.extractIp(victim.getAddress().getAddress());
        IPLocation location = IPUtils.getLocation(ip);
        String region = location.getRegion();
        String city = location.getCity();
        victim.kick(Component.text(beforeLines)
                        .append(Component.text(
                                header.formatted(
                                        name,
                                        city,
                                        region
                                )))
                        .append(image)
                        .append(Component.text(
                                        footer + afterLines
                                )
                        ),
                PlayerKickEvent.Cause.ILLEGAL_ACTION);
        sender.sendMessage(Text.prefix("Kicked %1$s and removed the back to server list button. Their IP was %2$s (%3$s %4$s)".formatted(target, ip, city, region)));
    }

    
}