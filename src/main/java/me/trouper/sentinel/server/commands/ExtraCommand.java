package me.trouper.sentinel.server.commands;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
//import com.xxmicloxx.NoteBlockAPI.model.Song;
//import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;
//import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
//import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
//import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.IPLocation;
import me.trouper.sentinel.data.types.SerialLocation;
import me.trouper.sentinel.server.commands.extras.AbstractExtra;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@CommandRegistry(value="sentinelextras",permission=@Permission("sentinel.extras"),printStackTrace = true)
public class ExtraCommand implements QuickCommand {
    
    public final List<AbstractExtra> extraRegistry = new ArrayList<>();
    
    public ExtraCommand(List<AbstractExtra> enabledExtras) {
        extraRegistry.addAll(enabledExtras);
    }
    
    @Override
    public void dispatchCommand(CommandSender sender, Command command, String s, Args args) {
        if (!PlayerUtils.isTrusted(sender)) {
            warningAny(sender,main.dir().io.lang.permissions.noTrust);
            return;
        }
        if (args.getSize() < 2) {
            Component helpMessage = Component.empty()
                    .append(Component.text("Extra's Guide",NamedTextColor.GOLD)).appendNewline()
                    .append(Component.text("All Features are packet based. \nThey do not effect other players.",NamedTextColor.GRAY)).appendNewline()
                    .append(Component.text("Syntax",NamedTextColor.AQUA)
                        .append(Component.text(": ",NamedTextColor.WHITE)
                        .append(Component.text("/sentinelextras <feature> <player> [free]",NamedTextColor.GRAY)))).appendNewline()
                    .append(Component.text("Features",NamedTextColor.AQUA)
                        .append(Component.text(":",NamedTextColor.WHITE))).appendNewline();

            for (AbstractExtra extra : extraRegistry) {
                helpMessage = helpMessage.append(Text.format(Text.Pallet.NEUTRAL," - {0}: {1}",extra.getName(),extra.getDescription())).appendNewline();
            }
            
            message(sender,helpMessage);
            return;
        }

        String target = args.get(1).toString();
        Player victim = Bukkit.getPlayer(target);
        if (victim == null || !victim.isOnline()) {
            errorAny(sender,"You must pick an online player. {0} is not online!",target);
            return;
        }
        
        String choice = args.get(0).toString();
        AbstractExtra extra = null;

        for (AbstractExtra abstractExtra : extraRegistry) {
            if (!abstractExtra.getName().equals(choice)) continue;
            extra = abstractExtra;
            break;
        }
        
        if (extra == null) {
            errorAny(sender,"You must pick a valid extra. {0} does not exist!",choice);
            return;
        }
        
        if (args.getSize() >= 3 && Objects.equals("free",args.get(2).toString())) {
            extra.stop(sender,victim);
            return;
        }
        
        extra.execute(sender,victim);
    }

    @Override
    public void dispatchCompletions(CommandSender commandSender, Command command, String s, CompletionBuilder b) {
        b.then(b.arg("info"));
        List<String> extras = new ArrayList<>();
        extraRegistry.forEach(extra -> extras.add(extra.getName()));
        b.then(b.arg(extras).then(
                b.argOnlinePlayers().then(b.arg("free"))
        ));
    }
}