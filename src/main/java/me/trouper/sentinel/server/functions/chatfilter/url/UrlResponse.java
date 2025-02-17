package me.trouper.sentinel.server.functions.chatfilter.url;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.Emojis;
import me.trouper.sentinel.server.functions.chatfilter.FalsePositiveReporting;
import me.trouper.sentinel.server.functions.chatfilter.FilterResponse;
import me.trouper.sentinel.server.functions.chatfilter.Report;
import me.trouper.sentinel.server.functions.chatfilter.unicode.UnicodeResponse;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlResponse implements FilterResponse {
    private AsyncChatEvent event;
    private String originalMessage;
    private String highlightedMessage;
    private Report report;
    private boolean blocked;
    private boolean punished;

    public UrlResponse(AsyncChatEvent event, String originalMessage, String highlightedMessage, Report report, boolean blocked, boolean punished) {
        this.event = event;
        this.report = report;
        this.originalMessage = originalMessage;
        this.highlightedMessage = highlightedMessage;
        this.punished = punished;
    }

    public static UrlResponse generate(AsyncChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Unicode response opening: Event is canceled.");
        }

        String message = LegacyComponentSerializer.legacySection().serialize(e.message());
        Report report = FalsePositiveReporting.initializeReport(message);
        UrlResponse response = new UrlResponse(e,message,message,report,false,false);
        for (String allowed : Sentinel.mainConfig.chat.urlFilter.whitelist) {
            message = message.replaceAll(allowed,"");
        }

        response.getReport().getStepsTaken().put("Anti-URL","Removed allowed urls: %s".formatted(
                message
        ));

        String urlRegex = Sentinel.mainConfig.chat.urlFilter.regex;

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);

        response.getReport().getStepsTaken().put("Anti-URL", "`%s`".formatted(
                message
        ));

        if (matcher.find()) {
            String highlighted = Text.regexHighlighter(message,Sentinel.mainConfig.chat.urlFilter.regex," > "," < ");
            ServerUtils.verbose("Caught URL: " + highlighted);
            response.getReport().getStepsTaken().replace("Anti-URL", "`%s` %s".formatted(highlighted, Emojis.alarm));

            response.setBlocked(true);
            response.setPunished(Sentinel.mainConfig.chat.urlFilter.punished);
            response.setHighlightedMessage(highlighted);
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
