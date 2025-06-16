package me.trouper.sentinel.server.functions.chatfilter.unicode;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.data.types.Emojis;
import me.trouper.sentinel.server.functions.chatfilter.FilterResponse;
import me.trouper.sentinel.server.functions.helpers.Report;
import me.trouper.sentinel.utils.FormatUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeResponse implements FilterResponse {

    private AsyncChatEvent event;
    private String originalMessage;
    private String highlightedMessage;
    private Report report;
    private boolean blocked;
    private boolean punished;

    public UnicodeResponse(AsyncChatEvent event, String originalMessage, String highlightedMessage, Report report, boolean blocked, boolean punished) {
        this.event = event;
        this.report = report;
        this.originalMessage = originalMessage;
        this.highlightedMessage = highlightedMessage;
        this.blocked = blocked;
        this.punished = punished;
    }

    public static UnicodeResponse generate(AsyncChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Unicode response opening: Event is canceled.");
        }

        String message = LegacyComponentSerializer.legacySection().serialize(e.message());
        message = Text.removeColors(message);
        Report report = main.dir().reportHandler.initializeReport(message);

        UnicodeResponse response = new UnicodeResponse(e, message, message, report, false, false);

        String disallowedRegex = main.dir().io.mainConfig.chat.unicodeFilter.regex;
        Pattern pattern = Pattern.compile(disallowedRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        
        boolean found = matcher.find();

        ServerUtils.verbose("Matcher result: %s",found);
        if (found) {
            ServerUtils.verbose("Group 1: %s",matcher.group());
        }

        response.getReport().getStepsTaken().put("Anti-Unicode", "`" + message + "`");
        
        if (found) {
            ServerUtils.verbose("Unicode Filter: Caught Unicode using regex: " + disallowedRegex);
            response.getReport().getStepsTaken().replace("Anti-Unicode", "`%s` %s".formatted(message, Emojis.alarm));

            response.setBlocked(true);
            response.setPunished(main.dir().io.mainConfig.chat.unicodeFilter.punished);
            response.setHighlightedMessage(FormatUtils.regexHighlighter(message, disallowedRegex, "█HS█", "█HE█"));
        }

        return response;
    }

    @Override
    public Player getPlayer() {
        return event.getPlayer();
    }

    public Report getReport() {
        return this.report;
    }

    public boolean isPunished() {
        return punished;
    }

    public AsyncChatEvent getEvent() {
        return event;
    }

    public void setEvent(AsyncChatEvent event) {
        this.event = event;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getHighlightedMessage() {
        return highlightedMessage;
    }

    public void setHighlightedMessage(String highlightedMessage) {
        this.highlightedMessage = highlightedMessage;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setPunished(boolean punished) {
        this.punished = punished;
    }
}