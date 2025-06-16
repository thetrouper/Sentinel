package me.trouper.sentinel.server.functions.chatfilter.profanity;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.types.Emojis;
import me.trouper.sentinel.server.functions.chatfilter.FilterResponse;
import me.trouper.sentinel.server.functions.helpers.Report;
import me.trouper.sentinel.utils.FormatUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ProfanityResponse implements FilterResponse {

    private final AsyncChatEvent event;
    private String originalMessage;
    private String processedMessage;
    private final Report report;
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

    public String getOriginalMessage() {
        return originalMessage;
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
            ServerUtils.verbose("Profanity response: Event is already cancelled.");
        }

        String message = PlainTextComponentSerializer.plainText().serialize(e.message());
        Report report = main.dir().reportHandler.initializeReport(message);
        ProfanityResponse response = new ProfanityResponse(e, message, message, report, Severity.SAFE, false, false);
        Severity currentSeverity;

        // Stage 1: Basic check on lowercased text
        String processedText = Text.removeColors(message).toLowerCase();
        report.getStepsTaken().put("Lowercased", processedText);
        ServerUtils.verbose("ProfanityFilter: Lowercased: " + processedText);
        currentSeverity = checkProfanity(processedText, Severity.LOW);
        if (currentSeverity != Severity.SAFE) return finalizeResponse(response, processedText, "Lowercased", currentSeverity);

        // Stage 2: Mask allowed words
        processedText = maskValidWords(processedText);
        report.getStepsTaken().put("Masked Valid Words", processedText);
        ServerUtils.verbose("ProfanityFilter: Masked Valid Words: " + processedText);


        // Stage 3: Convert LeetSpeak (e.g., @ -> a, 3 -> e)
        processedText = convertLeetSpeakCharacters(processedText);
        report.getStepsTaken().put("Convert LeetSpeak", processedText);
        ServerUtils.verbose("ProfanityFilter: Leet Converted: " + processedText);
        currentSeverity = checkProfanity(processedText, Severity.MEDIUM_LOW);
        if (currentSeverity != Severity.SAFE) return finalizeResponse(response, processedText, "Convert LeetSpeak", currentSeverity);

        // Stage 4: Strip special characters
        processedText = stripSpecialCharacters(processedText);
        report.getStepsTaken().put("Remove Special Characters", processedText);
        ServerUtils.verbose("ProfanityFilter: Specials Removed: " + processedText);
        currentSeverity = checkProfanity(processedText, Severity.MEDIUM);
        if (currentSeverity != Severity.SAFE) return finalizeResponse(response, processedText, "Remove Special Characters", currentSeverity);

        // Stage 5: Simplify repeating letters (e.g., heeeello -> helo)
        processedText = simplifyRepeatingLetters(processedText);
        report.getStepsTaken().put("Remove Repeats", processedText);
        ServerUtils.verbose("ProfanityFilter: Removed Repeating: " + processedText);
        currentSeverity = checkProfanity(processedText, Severity.MEDIUM_HIGH);
        if (currentSeverity != Severity.SAFE) return finalizeResponse(response, processedText, "Remove Repeats", currentSeverity);

        // Stage 6: Remove all spaces and remaining punctuation
        processedText = removePunctuation(processedText);
        report.getStepsTaken().put("Remove Punctuation", processedText);
        ServerUtils.verbose("ProfanityFilter: Remove Punctuation: " + processedText);
        currentSeverity = checkProfanity(processedText, Severity.HIGH);
        if (currentSeverity != Severity.SAFE) return finalizeResponse(response, processedText, "Remove Punctuation", currentSeverity);

        ServerUtils.verbose("ProfanityFilter: Finished. No profanity detected.");
        return response;
    }
    
    private static ProfanityResponse finalizeResponse(ProfanityResponse response, String text, String stage, Severity severity) {
        response.setSeverity(severity);
        String highlightedText = highlightAllProfanity(text, "||", "||");
        response.getReport().getStepsTaken().replace(stage, "%s %s".formatted(highlightedText, Emojis.alarm));
        response.setProcessedMessage(highlightAllProfanity(text,"█HS█","█HE█"));
        ServerUtils.verbose("ProfanityFilter: Flagged at stage '%s' with severity '%s'", stage, severity)
;
        return response;
    }
    
    private static Severity checkProfanity(String text, Severity current) {
        if (containsSlurs(text)) return Severity.SLUR;
        if (containsSwears(text)) return current;
        return Severity.SAFE;
    }

    private static boolean containsSwears(String text) {
        ServerUtils.verbose("ProfanityFilter: Checking for swears in: " + text);
        for (String swear : main.dir().io.swearList.swears) {
            if (text.contains(swear)) return true;
        }
        if (main.dir().io.swearList.useRegex) {
            Pattern pattern = Pattern.compile(main.dir().io.swearList.regexSwears, Pattern.CASE_INSENSITIVE);
            return pattern.matcher(text).find();
        }
        return false;
    }

    private static boolean containsSlurs(String text) {
        ServerUtils.verbose("ProfanityFilter: Checking for slurs in: " + text);
        for (String slur : main.dir().io.strictList.strict) {
            if (text.contains(slur)) return true;
        }
        if (main.dir().io.strictList.useRegex) {
            Pattern pattern = Pattern.compile(main.dir().io.strictList.regexStrict, Pattern.CASE_INSENSITIVE);
            return pattern.matcher(text).find();
        }
        return false;
    }
    
    private static String maskValidWords(String text) {
        String result = text;
        for (String falsePositive : main.dir().io.falsePositiveList.swearWhitelist) {
            result = result.replace(falsePositive, "█SW█");
        }
        if (main.dir().io.falsePositiveList.useRegex) {
            result = result.replaceAll(main.dir().io.falsePositiveList.regexWhitelist, "█RW█");
        }
        for (String falsePositive : main.dir().io.falsePositiveList.cleanWords) {
            result = result.replace(falsePositive, "█CW█");
        }
        return result;
    }

    private static String convertLeetSpeakCharacters(String text) {
        Map<String, String> dictionary = Sentinel.getInstance().getDirector().io.advConfig.leetPatterns;

        for (String key : dictionary.keySet()) {
            if (!text.contains(key)) continue;
            try {
                if (key.equals("$")) {
                    text = text.replaceAll("\\$", "s");
                }
                else {
                    text = text.replaceAll(key, dictionary.get(key));
                }
            } catch (PatternSyntaxException ex) {
                String regex = "[" + key + "]";
                text = text.replaceAll(regex, dictionary.get(key));
            }
        }
        return text;
    }

    private static String stripSpecialCharacters(String text) {
        return text.replaceAll("(?!█[A-Z]{2}█)[^a-zA-Z0-9\\s,.?!█]", "");
    }

    private static String simplifyRepeatingLetters(String text) {
        return FormatUtils.replaceRepeatingLetters(text);
    }

    private static String removePunctuation(String text) {
        return text.replaceAll("[.,!?\\s]", "");
    }
    
    private static String highlightAllProfanity(String text, String start, String end) {
        String result = text;
        for (String slur : main.dir().io.strictList.strict) {
            result = result.replace(slur, start + slur + end);
        }

        for (String swear : main.dir().io.swearList.swears) {
            result = result.replace(swear, start + swear + end);
        }
        return Text.legacyColor(result);
    }
}
