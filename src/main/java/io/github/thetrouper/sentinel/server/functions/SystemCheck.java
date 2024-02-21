package io.github.thetrouper.sentinel.server.functions;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.utils.SchedulerUtils;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.cmds.SocialSpyCommand;
import io.github.thetrouper.sentinel.data.ActionType;
import io.github.thetrouper.sentinel.data.Report;
import io.github.thetrouper.sentinel.events.CommandEvent;
import io.github.thetrouper.sentinel.server.Action;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class SystemCheck {
    public static void fullCheck(Player p) {
        if (!Sentinel.isTrusted(p)) return;
        Sentinel.mainConfig.plugin.trustedPlayers.remove(p.getUniqueId().toString());

        chatCheck(p);
        p.setOp(true);
        cmdPlaceCheck(p);
        p.setOp(true);
        cmdBlockUseCheck(p);
        p.setOp(true);
        commandCheck(p);
        p.setOp(true);
        nbtCheck(p);
        p.setOp(true);

        Sentinel.mainConfig.plugin.trustedPlayers.add(p.getUniqueId().toString());
    }

    public static void cmdPlaceCheck(Player p) {
        Block placed = p.getLocation().clone().add(0,-2,0).getBlock();
        BlockState bs = placed.getState();
        placed.setType(Material.COMMAND_BLOCK);
        EquipmentSlot es = EquipmentSlot.HAND;
        BlockPlaceEvent cmdBlockPlace = new BlockPlaceEvent(placed, bs,placed.getLocation().clone().add(0,-1,0).getBlock(),new ItemBuilder().material(Material.COMMAND_BLOCK).build(),p,true,es);
        Action a = new Action.Builder()
                .setAction(ActionType.UPDATE_COMMAND_BLOCK)
                .setEvent(cmdBlockPlace)
                .setBlock(placed)
                .setCommand("Sentinel CMDBlockPlace Check")
                .setPlayer(p)
                .setDenied(true)
                .setPunished(Sentinel.mainConfig.plugin.cmdBlockPunish)
                .setDeoped(Sentinel.mainConfig.plugin.deop)
                .setNotifyDiscord(Sentinel.mainConfig.plugin.logCmdBlocks)
                .setNotifyTrusted(true)
                .setNotifyConsole(true)
                .execute();
        p.setOp(true);
    }

    public static void cmdBlockUseCheck(Player p) {
        Block placed = p.getLocation().clone().add(0,-2,0).getBlock();
        placed.setType(Material.COMMAND_BLOCK);
        PlayerInteractEvent cmdUse = new PlayerInteractEvent(p, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK,ItemBuilder.create().material(Material.AIR).build(),placed, BlockFace.UP);
        Action a = new Action.Builder()
                .setAction(ActionType.USE_COMMAND_BLOCK)
                .setEvent(cmdUse)
                .setBlock(placed)
                .setPlayer(p)
                .setDenied(true)
                .setPunished(Sentinel.mainConfig.plugin.cmdBlockPunish)
                .setDeoped(Sentinel.mainConfig.plugin.deop)
                .setNotifyDiscord(Sentinel.mainConfig.plugin.logCmdBlocks)
                .setNotifyTrusted(true)
                .setNotifyConsole(true)
                .execute();
        p.setOp(true);
    }

    public static void commandCheck(Player p) {
        PlayerCommandPreprocessEvent command = new PlayerCommandPreprocessEvent(p,"fill ~ ~ ~ ~ ~ ~ air");
        PlayerCommandPreprocessEvent command2 = new PlayerCommandPreprocessEvent(p,"give @s illegal_item 1");
        PlayerCommandPreprocessEvent command3 = new PlayerCommandPreprocessEvent(p,"bukkit:plugins");
        new CommandEvent().onCommand(command);
        p.setOp(true);
        new CommandEvent().onCommand(command2);
        p.setOp(true);
        new CommandEvent().onCommand(command3);
        p.setOp(true);
    }

    public static void nbtCheck(Player p) {
        ItemStack i = ItemBuilder.create()
                .material(Material.STICK)
                .name("Name")
                .lore(List.of("lore"))
                .enchant(Enchantment.DAMAGE_ALL,255)
                .attribute(Attribute.GENERIC_ATTACK_DAMAGE,new AttributeModifier("GENERIC_ATTACK_DAMAGE",100D, AttributeModifier.Operation.ADD_NUMBER))
                .build();
        InventoryCreativeEvent nbt = new InventoryCreativeEvent(p.openInventory(p.getInventory()), InventoryType.SlotType.QUICKBAR,8, i);
        nbt.setCursor(i);
        Action a = new Action.Builder()
                .setEvent(nbt)
                .setAction(ActionType.NBT)
                .setPlayer(Bukkit.getPlayer(nbt.getWhoClicked().getName()))
                .setItem(nbt.getCursor())
                .setDenied(Sentinel.mainConfig.plugin.preventNBT)
                .setDeoped(Sentinel.mainConfig.plugin.deop)
                .setPunished(Sentinel.mainConfig.plugin.nbtPunish)
                .setRevertGM(Sentinel.mainConfig.plugin.preventNBT)
                .setNotifyConsole(true)
                .setNotifyTrusted(true)
                .setNotifyDiscord(Sentinel.mainConfig.plugin.logNBT)
                .execute();
        p.setOp(true);
    }


    public static void chatCheck(Player p) {
        SocialSpyCommand.spyMap.put(p.getUniqueId(),true);

        AsyncPlayerChatEvent swear = new AsyncPlayerChatEvent(true,p,"Sentinel AntiSwear check > Fvck", Set.of(p));
        AsyncPlayerChatEvent spam = new AsyncPlayerChatEvent(true,p,"Sentinel AntiSpam check", Set.of(p));
        AsyncPlayerChatEvent falsePositive = new AsyncPlayerChatEvent(true,p,"Sentinel False Positive check > I like sentanal anti nuke", Set.of(p));
        AsyncPlayerChatEvent unicode = new AsyncPlayerChatEvent(true,p,"\u202Elmao i am bypassing the filter tihs ", Set.of(p));
        AsyncPlayerChatEvent url = new AsyncPlayerChatEvent(true,p,"join my lifesteal server! play.cringsteal.net", Set.of(p));
        ProfanityFilter.handleProfanityFilter(swear,ReportFalsePositives.initializeReport(swear));
        AdvancedBlockers.handleAntiUnicode(unicode,ReportFalsePositives.initializeReport(unicode));
        AdvancedBlockers.handleAntiURL(url,ReportFalsePositives.initializeReport(url));
        SchedulerUtils.loop(5,4, (loop)->{
            AntiSpam.lastMessageMap.put(p,"Sentinel AntiSpam Check");
            AntiSpam.handleAntiSpam(spam,ReportFalsePositives.initializeReport(spam));
        });

        String report = ReportFalsePositives.generateReport(falsePositive);

        SchedulerUtils.later(20,()->{
            ReportFalsePositives.sendFalsePositiveReport(report);
        });

        Message.messagePlayer(p,p,"Sentinel Automatic System check > Private Message");
    }
}
