package me.trouper.sentinel.server.functions.chatfilter.profanity;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.data.Emojis;
import me.trouper.sentinel.server.functions.chatfilter.FalsePositiveReporting;
import me.trouper.sentinel.server.functions.chatfilter.FilterHelpers;
import me.trouper.sentinel.server.functions.chatfilter.FilterResponse;
import me.trouper.sentinel.server.functions.chatfilter.Report;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ProfanityResponse implements FilterResponse {

    private AsyncChatEvent event;
    private String originalMessage;
    private String processedMessage;
    private Report report;
    private Severity severity;
    private boolean blocked;
    private boolean punished;

    public ProfanityResponse(AsyncChatEvent event, String originalMessage, String processedMessage, Report report, Severity severity, boolean blocked, boolean punished) {
        this.event = event;
        this.originalMessage = originalMessage;
        this.processedMessage = processedMessage;
        this.report = report;
        this.severity = severity;
        this.blocked = blocked;
        this.punished = punished;
    }

    @Override
    public Player getPlayer() {
        return event.getPlayer();
    }
    
    public AsyncChatEvent getEvent() {
        return event;
    }

    public void setEvent(AsyncChatEvent event) {
        this.event = event;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getProcessedMessage() {
        return processedMessage;
    }

    public void setProcessedMessage(String processedMessage) {
        this.processedMessage = processedMessage;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isPunished() {
        return punished;
    }

    public void setPunished(boolean punished) {
        this.punished = punished;
    }

    public static ProfanityResponse generate(AsyncChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Profanity response opening: Event is canceled.");
        }

        String message = LegacyComponentSerializer.legacySection().serialize(e.message());
        Report report = FalsePositiveReporting.initializeReport(message);
        Severity severity = Severity.SAFE;

        ProfanityResponse response = new ProfanityResponse(e,message,null,report,severity,false,false);

        String text = Text.removeFirstColor(message);
        response.setOriginalMessage(text);

        // 1:
        String lowercasedText = text.toLowerCase();
        response.getReport().getStepsTaken().put("Lowercased", lowercasedText);
        response.setProcessedMessage(FilterHelpers.highlightProfanity(lowercasedText,"<hs>", "<he>"));
        ServerUtils.verbose("ProfanityFilter:  Lowercased: " + lowercasedText);


        // 2:
        String cleanedText = FilterHelpers.removeFalsePositives(lowercasedText);
        response.getReport().getStepsTaken().put("Remove False Positives", cleanedText);
        response.setProcessedMessage(FilterHelpers.highlightProfanity(cleanedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Removed False positives: " + cleanedText));

        response.setSeverity(FilterHelpers.checkSlur(cleanedText, Severity.LOW));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove False Positives", "%s %s".formatted(
                    FilterHelpers.highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 4:
        String convertedText = FilterHelpers.convertLeetSpeakCharacters(cleanedText);
        response.getReport().getStepsTaken().put("Convert LeetSpeak", convertedText);
        response.setProcessedMessage(FilterHelpers.highlightProfanity(convertedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Leet Converted: " + convertedText));

        response.setSeverity(FilterHelpers.checkSlur(convertedText, Severity.MEDIUM_LOW));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Convert LeetSpeak", "%s %s".formatted(
                    FilterHelpers.highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 6:
        String strippedText = FilterHelpers.stripSpecialCharacters(convertedText);
        response.getReport().getStepsTaken().put("Remove Special Characters", strippedText);
        response.setProcessedMessage(FilterHelpers.highlightProfanity(strippedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Specials Removed: " + strippedText));

        response.setSeverity(FilterHelpers.checkSlur(strippedText, Severity.MEDIUM));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove Special Characters", "%s %s".formatted(
                    FilterHelpers.highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 8:
        String simplifiedText = FilterHelpers.simplifyRepeatingLetters(strippedText);
        response.getReport().getStepsTaken().put("Remove Repeats", simplifiedText);
        response.setProcessedMessage(FilterHelpers.highlightProfanity(simplifiedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Removed Repeating: " + simplifiedText));

        response.setSeverity(FilterHelpers.checkSlur(simplifiedText, Severity.MEDIUM_HIGH));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove Repeats", "%s %s".formatted(
                    FilterHelpers.highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 10:
        String finalText = FilterHelpers.removePeriodsAndSpaces(simplifiedText);
        response.getReport().getStepsTaken().put("Remove Punctuation", finalText);
        response.setProcessedMessage(FilterHelpers.highlightProfanity(finalText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Remove Punctuation: " + finalText));

        response.setSeverity(FilterHelpers.checkSlur(finalText, Severity.HIGH));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove Punctuation", "%s %s".formatted(
                    FilterHelpers.highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
        }

        ServerUtils.verbose(("ProfanityFilter: Finished " + finalText));
        if (e.isCancelled()) {
            ServerUtils.verbose("Profanity response closing: Event is canceled.");
        }
        return response;
    }
}
