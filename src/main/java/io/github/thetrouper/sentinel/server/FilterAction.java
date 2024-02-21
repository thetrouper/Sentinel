package io.github.thetrouper.sentinel.server;

import io.github.itzispyder.pdk.utils.discord.DiscordEmbed;
import io.github.itzispyder.pdk.utils.discord.DiscordWebhook;
import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.data.Emojis;
import io.github.thetrouper.sentinel.data.FAT;
import io.github.thetrouper.sentinel.data.FilterSeverity;
import io.github.thetrouper.sentinel.server.functions.AdvancedBlockers;
import io.github.thetrouper.sentinel.server.functions.ReportFalsePositives;
import io.github.thetrouper.sentinel.server.util.ServerUtils;
import io.github.thetrouper.sentinel.server.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.CompletableFuture;

import static io.github.thetrouper.sentinel.server.functions.AntiSpam.heatMap;
import static io.github.thetrouper.sentinel.server.functions.AntiSpam.lastMessageMap;
import static io.github.thetrouper.sentinel.server.functions.ProfanityFilter.*;

public class FilterAction {

    public static void filterPunish(AsyncPlayerChatEvent e, FAT type, Double similarity, FilterSeverity severity) {
        String report = "This action is not reportable.";
        TextComponent staffNotif = Component.text("");
        TextComponent playerWarning = Component.text("");
        Player offender = e.getPlayer();
        switch (type) {
            case BLOCK_UNICODE -> {
                staffNotif = Component
                        .text(Text.prefix(Sentinel.language.get("unicode-notification")
                                .formatted(offender.getName())))
                        .hoverEvent(Component.text(Sentinel.language.get("unicode-notification-hover")
                                .formatted(e.getMessage())));
                playerWarning = Component
                        .text(Text.prefix(Sentinel.language.get("unicode-warn")));
            }
            case BLOCK_URL -> {
                staffNotif = Component
                        .text(Text.prefix(Sentinel.language.get("url-notification")
                                .formatted(offender.getName())))
                        .hoverEvent(Component.text(Sentinel.language.get("url-notification-hover")
                                .formatted(Text.color(Text.regexHighlighter(e.getMessage(),Sentinel.advConfig.urlRegex," &e> &n","&r &e<&f ")))));
                playerWarning = Component
                        .text(Text.prefix(Sentinel.language.get("url-warn")));
            }
            case BLOCK_SPAM -> {
                if (Sentinel.mainConfig.chat.antiSpam.clearChat) ServerUtils.sendCommand(Sentinel.mainConfig.chat.antiSpam.chatClearCommand);

                staffNotif = Component
                        .text(Text.prefix(String.format(Sentinel.language.get("spam-notification"),
                            offender.getName(),
                            heatMap.get(offender),
                            Sentinel.mainConfig.chat.antiSpam.punishHeat
                        )))
                        .hoverEvent(Component.text(String.format(Sentinel.language.get("spam-notification-hover"),
                            lastMessageMap.get(offender),
                            e.getMessage(),
                            similarity
                        )));

                playerWarning = Component.text(Text.prefix(Sentinel.language.get("spam-block-warn")));
            }
            case SPAM_PUNISH -> {
                if (Sentinel.mainConfig.chat.antiSpam.clearChat) ServerUtils.sendCommand(Sentinel.mainConfig.chat.antiSpam.chatClearCommand);
                staffNotif = Component.text(Text.prefix(String.format(Sentinel.language.get("spam-mute-notification"),
                            offender.getName(),
                            heatMap.get(offender),
                            Sentinel.mainConfig.chat.antiSpam.punishHeat
                        )))
                        .hoverEvent(Component.text(String.format(Sentinel.language.get("spam-notification-hover"),
                                lastMessageMap.get(offender),
                                e.getMessage(),
                                similarity
                        )));

                playerWarning = Component.text(Sentinel.language.get("spam-mute-warn"));
                sendConsoleLog(offender,e,type);
                if (Sentinel.mainConfig.chat.antiSpam.logSpam) sendDiscordLog(offender,e,type);
            }
            case BLOCK_SWEAR -> {
                report = ReportFalsePositives.generateReport(e);
                staffNotif = Component.text(Text.prefix(String.format(Sentinel.language.get("profanity-block-notification"),
                                offender.getName(),
                                scoreMap.get(offender),
                                Sentinel.mainConfig.chat.antiSwear.punishScore
                            )))
                        .hoverEvent(Component.text(String.format(Sentinel.language.get("severity-notification-hover"),
                                e.getMessage(),
                                highlightProfanity(fullSimplify(e.getMessage())),
                                severity.name()
                            )));

                playerWarning = Component.text(Text.prefix(Sentinel.language.get("profanity-block-warn")))
                        .hoverEvent(Component.text(Sentinel.language.get("action-automatic-reportable")))
                        .clickEvent(ClickEvent.runCommand("sentinelcallback fpreport " + report));
            }
            case SWEAR_PUNISH -> {
                report = ReportFalsePositives.generateReport(e);
                staffNotif = Component.text(Text.prefix(String.format(Sentinel.language.get("profanity-mute-notification"),
                                offender.getName(),
                                scoreMap.get(offender),
                                Sentinel.mainConfig.chat.antiSwear.punishScore
                            )))
                        .hoverEvent(Component.text(String.format(Sentinel.language.get("severity-notification-hover"),
                                e.getMessage(),
                                highlightProfanity(fullSimplify(e.getMessage())),
                                severity.name()
                            )));

                playerWarning = Component.text(Text.prefix(Sentinel.language.get("profanity-mute-warn")))
                        .hoverEvent(Component.text(Sentinel.language.get("action-automatic-reportable")));
                sendConsoleLog(offender,e,type);
                if (Sentinel.mainConfig.chat.antiSwear.logSwears) sendDiscordLog(offender,e,type);
            }
            case SLUR_PUNISH -> {
                report = ReportFalsePositives.generateReport(e);
                staffNotif = Component.text(Text.prefix(String.format(Sentinel.language.get("slur-mute-notification"),
                                offender.getName(),
                                scoreMap.get(offender),
                                Sentinel.mainConfig.chat.antiSwear.punishScore
                            )))
                        .hoverEvent(Component.text(String.format(Sentinel.language.get("severity-notification-hover"),
                                e.getMessage(),
                                highlightProfanity(fullSimplify(e.getMessage())),
                                severity.name()
                            )));

                playerWarning = Component.text(Text.prefix(Sentinel.language.get("slur-mute-warn")))
                        .hoverEvent(Component.text(Sentinel.language.get("action-automatic-reportable")));
                sendConsoleLog(offender,e,type);
                if (Sentinel.mainConfig.chat.antiSwear.logSwears) sendDiscordLog(offender,e,type);
            }
        }
        if (type.getExecutedCommand() != null) {
            ServerUtils.sendCommand(type.getExecutedCommand().replace("%player%", offender.getName()));
        }
        staffNotif = staffNotif.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/sentinelcallback fpreport " + report));
        playerWarning = playerWarning.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/sentinelcallback fpreport " + report));

        for (Player staff : ServerUtils.getStaff()) {
            staff.sendMessage(staffNotif);
        }
        e.getPlayer().sendMessage(playerWarning);
    }

    public static void sendConsoleLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        StringBuilder log = new StringBuilder().append(String.format("]=- %s -=[",type.getTitle()));
        log.append(String.format("\nPlayer: %s",offender.getName()));
        log.append(String.format("\n> UUID: %s",offender.getUniqueId()));
        switch (type) {
            case SPAM_PUNISH -> {
                log.append(String.format("\n> Heat: %1$s/%2$s",heatMap.get(offender),Sentinel.mainConfig.chat.antiSpam.punishHeat));
                log.append(String.format("\nMessage: %s", e.getMessage()));
                log.append(String.format("\nReduced: %s",fullSimplify(e.getMessage())));
            }
            case SWEAR_PUNISH, SLUR_PUNISH -> {
                log.append(String.format("\n> Score: %1$s/%2$s",heatMap.get(offender),Sentinel.mainConfig.chat.antiSwear.punishScore));
                log.append(String.format("\nPrevious: %s",lastMessageMap.get(offender)));
                log.append(String.format("\nCurrent: %s",e.getMessage()));
            }
            default -> {
                log.append("\nYou shouldn't be seeing this! Please report this message, and the context surrounding it!");
                log.append(String.format("\n> Heat: %1$s/%2$s",heatMap.get(offender),Sentinel.mainConfig.chat.antiSpam.punishHeat));
                log.append(String.format("\nMessage: %s",e.getMessage()));
                log.append(String.format("\nReduced: %s",fullSimplify(e.getMessage())));
                log.append(String.format("\n> Score: %1$s/%2$s",heatMap.get(offender),Sentinel.mainConfig.chat.antiSwear.punishScore));
                log.append(String.format("\nPrevious: %s",lastMessageMap.get(offender)));
                log.append(String.format("\nCurrent: %s",e.getMessage()));
            }
        }
        log.append(String.format("\nExecuted: %s",type.getExecutedCommand()));
        Sentinel.log.info(String.valueOf(log));
    }

    private static void sendDiscordLog(Player offender, AsyncPlayerChatEvent e, FAT type) {
        CompletableFuture.runAsync(()->{
            String supertitle = type.getTitle();
            String title = offender.getName() + " has triggered the " + type.getName() + "!";

            String executed = type.getExecutedCommand() != null ? type.getExecutedCommand() : "Nothing, its a standard flag. You shouldn't be seeing this, please report it.";
            StringBuilder description = new StringBuilder();

            String historyTitle = "You found a bug! :D";
            String historyValue = "Congratulations.";

            String currentTitle = "Now go report it!";
            String currentValue = ">:(";

            description.append(String.format("%1$sPlayer: `%2$s` %3$s",Emojis.rightSort,offender.getName(),Emojis.target));
            switch (type) {
                case SPAM_PUNISH -> {
                    description.append(String.format("\n%1$s%2$sHeat: `%3$s/%4$s`",
                            Emojis.space,
                            Emojis.arrowRight,
                            heatMap.get(offender),
                            Sentinel.mainConfig.chat.antiSpam.punishHeat
                    ));
                    historyTitle = "Previous: ";
                    historyValue = lastMessageMap.get(offender);

                    currentTitle = "Current: ";
                    currentValue = e.getMessage();
                }
                case SWEAR_PUNISH, SLUR_PUNISH -> {
                    description.append(String.format("\n%1$s%2$sScore: `%3$s/%4$s`",
                            Emojis.space,
                            Emojis.arrowRight,
                            scoreMap.get(offender),
                            Sentinel.mainConfig.chat.antiSwear.punishScore
                    ));
                    historyTitle = "Message: ";
                    historyValue = e.getMessage();

                    currentTitle = "Reduced: ";
                    currentValue = highlightProfanity(e.getMessage(),"||", "||");
                }
            }

            try {
                String finalHistoryTitle = historyTitle;
                String finalHistoryValue = historyValue;
                String finalCurrentTitle = currentTitle;
                String finalCurrentValue = currentValue;
                CompletableFuture.runAsync(()->{
                    ServerUtils.sendDebugMessage("Executing webhook...");
                    DiscordWebhook.create()
                            .username("Sentinel Anti-Nuke | Logs")
                            .avatar("https://r2.e-z.host/d440b58a-ba90-4839-8df6-8bba298cf817/3lwit5nt.png")
                            .addEmbed(DiscordEmbed.create()
                                    .author(new DiscordEmbed.Author(supertitle,"https://builtbybit.com/resources/sentinel-anti-nuke.30130/",null))
                                    .title(title)
                                    .desc(String.valueOf(description))
                                    .addField(new DiscordEmbed.Field(finalHistoryTitle, finalHistoryValue,true))
                                    .addField(new DiscordEmbed.Field(finalCurrentTitle, finalCurrentValue,true))
                                    .addField(new DiscordEmbed.Field("Executed: ", executed.replaceAll("%player%",offender.getName()),false))
                                    .thumbnail("https://crafatar.com/avatars/" + offender.getUniqueId() + "?size=64&&overlay")
                                    .color(type.getColor())
                                    .build()).send(Sentinel.mainConfig.plugin.webhook);
                });
            } catch (Exception ex) {
                ServerUtils.sendDebugMessage("Filter Actions: Epic webhook failure!!!");
                Sentinel.log.info(ex.toString());
            }
        });
    }
}
