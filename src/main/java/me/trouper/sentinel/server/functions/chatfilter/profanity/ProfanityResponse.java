package me.trouper.sentinel.server.functions.chatfilter.profanity;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.misc.Emojis;
import me.trouper.sentinel.server.functions.chatfilter.FilterResponse;
import me.trouper.sentinel.server.functions.helpers.Report;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Report report = Sentinel.getInstance().getDirector().reportHandler.initializeReport(message);
        Severity severity = Severity.SAFE;

        ProfanityResponse response = new ProfanityResponse(e,message,null,report,severity,false,false);

        String text = Text.removeFirstColor(message);
        response.setOriginalMessage(text);

        // 1:
        String lowercasedText = text.toLowerCase();
        response.getReport().getStepsTaken().put("Lowercased", lowercasedText);
        response.setProcessedMessage(highlightProfanity(lowercasedText,"<hs>", "<he>"));
        ServerUtils.verbose("ProfanityFilter:  Lowercased: " + lowercasedText);


        // 2:
        String cleanedText = removeFalsePositives(lowercasedText);
        response.getReport().getStepsTaken().put("Remove False Positives", cleanedText);
        response.setProcessedMessage(highlightProfanity(cleanedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Removed False positives: " + cleanedText));

        response.setSeverity(checkProfanity(cleanedText, Severity.LOW));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove False Positives", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 4:
        String convertedText = convertLeetSpeakCharacters(cleanedText);
        response.getReport().getStepsTaken().put("Convert LeetSpeak", convertedText);
        response.setProcessedMessage(highlightProfanity(convertedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Leet Converted: " + convertedText));

        response.setSeverity(checkProfanity(convertedText, Severity.MEDIUM_LOW));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Convert LeetSpeak", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 6:
        String strippedText = stripSpecialCharacters(convertedText);
        response.getReport().getStepsTaken().put("Remove Special Characters", strippedText);
        response.setProcessedMessage(highlightProfanity(strippedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Specials Removed: " + strippedText));

        response.setSeverity(checkProfanity(strippedText, Severity.MEDIUM));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove Special Characters", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 8:
        String simplifiedText = simplifyRepeatingLetters(strippedText);
        response.getReport().getStepsTaken().put("Remove Repeats", simplifiedText);
        response.setProcessedMessage(highlightProfanity(simplifiedText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Removed Repeating: " + simplifiedText));

        response.setSeverity(checkProfanity(simplifiedText, Severity.MEDIUM_HIGH));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove Repeats", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        // 10:
        String finalText = removePeriodsAndSpaces(simplifiedText);
        response.getReport().getStepsTaken().put("Remove Punctuation", finalText);
        response.setProcessedMessage(highlightProfanity(finalText,"<hs>", "<he>"));
        ServerUtils.verbose(("ProfanityFilter: Remove Punctuation: " + finalText));

        response.setSeverity(checkProfanity(finalText, Severity.HIGH));
        if (response.getSeverity() != Severity.SAFE) {
            response.getReport().getStepsTaken().replace("Remove Punctuation", "%s %s".formatted(
                    highlightProfanity(cleanedText,"||","||"),
                    Emojis.alarm));
            return response;
        }

        ServerUtils.verbose(("ProfanityFilter: Finished " + finalText));
        if (e.isCancelled()) {
            ServerUtils.verbose("Profanity response closing: Event is canceled.");
        }
        return response;
    }

    private static Severity checkProfanity(String text, Severity backup) {
        if (containsSlurs(text)) return Severity.SLUR;
        if (containsSwears(text)) return backup;
        return Severity.SAFE;
    }

    private static boolean containsSwears(String text) {
        ServerUtils.verbose("ProfanityFilter: Checking for swears");
        for (String swear : Sentinel.getInstance().getDirector().io.swearConfig.swears) {
            if (text.contains(swear)) return true;
        }

        Pattern pattern = Pattern.compile(Sentinel.getInstance().getDirector().io.swearConfig.regexSwears, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        return matcher.find() && Sentinel.getInstance().getDirector().io.swearConfig.useRegex;
    }

    private static boolean containsSlurs(String text) {
        ServerUtils.verbose("ProfanityFilter: Checking for slurs");
        for (String slur : Sentinel.getInstance().getDirector().io.strictConfig.strict) {
            if (text.contains(slur)) return true;
        }

        Pattern pattern = Pattern.compile(Sentinel.getInstance().getDirector().io.strictConfig.regexStrict, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        return matcher.find() && Sentinel.getInstance().getDirector().io.strictConfig.useRegex;
    }

    private static String removeFalsePositives(String text) {
        for (String falsePositive : Sentinel.getInstance().getDirector().io.fpConfig.swearWhitelist) {
            text = text.replace(falsePositive, "");
        }
        if (Sentinel.getInstance().getDirector().io.fpConfig.useRegex) text = text.replaceAll(Sentinel.getInstance().getDirector().io.fpConfig.regexWhitelist,"");
        return text;
    }

    private static String convertLeetSpeakCharacters(String text) {
        text = Text.fromLeetString(text);
        return text;
    }

    private static String stripSpecialCharacters(String text) {
        text = text.replaceAll("[^A-Za-z0-9.,!?;:'\"()\\[\\]{}]", "").trim();
        return text;
    }

    private static String simplifyRepeatingLetters(String text) {
        text = Text.replaceRepeatingLetters(text);
        return text;
    }

    private static String removePeriodsAndSpaces(String text) {
        return text.replaceAll("[^A-Za-z0-9]", "").replace(" ", "");
    }

    private static String highlightProfanity(String text, String start, String end) {
        String highlightedSwears = highlightSwears(fullSimplify(text), start, end);
        return Text.color(highlightSlurs(highlightedSwears, start, end));
    }

    private static String highlightSwears(String text, String start, String end) {
        for (String swear : Sentinel.getInstance().getDirector().io.swearConfig.swears) {
            text = text.replace(swear, start + swear + end);
        }
        return text;
    }

    private static String highlightSlurs(String text, String start, String end) {
        for (String slur : Sentinel.getInstance().getDirector().io.strictConfig.strict) {
            text = text.replace(slur, start + slur + end);
        }
        return text;
    }

    private static String fullSimplify(String text) {
        String lowercasedText = text.toLowerCase();
        String cleanedText = removeFalsePositives(lowercasedText);
        String convertedText =convertLeetSpeakCharacters(cleanedText);
        String strippedText = stripSpecialCharacters(convertedText);
        String simplifiedText = simplifyRepeatingLetters(strippedText);
        return removePeriodsAndSpaces(simplifiedText);
    }

    
}
