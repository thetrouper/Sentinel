package me.trouper.sentinel.server.functions.chatfilter.regex;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.FalsePositiveReporting;
import me.trouper.sentinel.server.functions.chatfilter.Report;
import me.trouper.sentinel.data.Emojis;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexResponse {
    private AsyncPlayerChatEvent event;
    private String originalMessage;
    private String highlightedMessage;
    private RegexFlagType flagType;
    private Report report;
    private boolean isPunished;

    public RegexResponse(AsyncPlayerChatEvent event, String originalMessage, String highlightedMessage, RegexFlagType flagType, Report report, boolean isPunished) {
        this.event = event;
        this.originalMessage = originalMessage;
        this.highlightedMessage = highlightedMessage;
        this.flagType = flagType;
        this.report = report;
        this.isPunished = isPunished;
    }

    public AsyncPlayerChatEvent getEvent() {
        return event;
    }

    public void setEvent(AsyncPlayerChatEvent event) {
        this.event = event;
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

    public RegexFlagType getFlagType() {
        return flagType;
    }

    public void setFlagType(RegexFlagType flagType) {
        this.flagType = flagType;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public boolean isPunished() {
        return isPunished;
    }

    public void setPunished(boolean punished) {
        isPunished = punished;
    }


    public static RegexResponse generate(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Regex response opening Event is canceled.");
        }
        Report report = FalsePositiveReporting.initializeReport(e.getMessage());
        RegexResponse response = new RegexResponse(e,e.getMessage(),null,null,report,false);

        String unicode = handleAntiUnicode(e,response.getReport());
        String url = handleAntiURL(e,response.getReport());
        String strict = handleStrictRegex(e,response.getReport());
        String swear = handleSwearRegex(e,response.getReport());
        ServerUtils.verbose("All filters have ran without thread error");
        if (Sentinel.mainConfig.chat.useAntiUnicode && handleAntiUnicode(e,report) != null) {
            response.setHighlightedMessage(unicode);
            response.setFlagType(RegexFlagType.UNICODE_BLOCK);
            return response;
        }
        if (Sentinel.mainConfig.chat.useAntiURL && handleAntiURL(e,report) != null) {
            response.setHighlightedMessage(url);
            response.setFlagType(RegexFlagType.URL_BLOCK);
            return response;
        }
        if (Sentinel.mainConfig.chat.useStrictRegex && handleStrictRegex(e,report) != null) {
            response.setHighlightedMessage(strict);
            response.setFlagType(RegexFlagType.STRICT_BLOCK);
            return response;
        }
        if (Sentinel.mainConfig.chat.useSwearRegex && handleSwearRegex(e,report) != null) {
            response.setPunished(true);
            response.setHighlightedMessage(swear);
            response.setFlagType(RegexFlagType.SWEAR_BLOCK);
            return response;
        }
        ServerUtils.verbose("Nothing caught, returning the blank response");
        return response;
    }


    public static String handleAntiUnicode(AsyncPlayerChatEvent e, Report report) {
        String message = Text.removeFirstColor(e.getMessage());
        report.getStepsTaken().put("Anti-Unicode", "`%s`".formatted(message));
        ServerUtils.verbose("AdvBlocker: Checking for unicode: " + message);
        String nonAllowed = message.replaceAll(Sentinel.advConfig.allowedCharRegex, "").trim();

        if (!nonAllowed.isEmpty()) {
            ServerUtils.verbose("AdvBlocker: Caught Unicode: " + nonAllowed);
            report.getStepsTaken().replace("Anti-Unicode", "`%s` %s".formatted(message, Emojis.alarm));
            return Text.regexHighlighter(message,Sentinel.advConfig.allowedCharRegex," > ", " < ");
        }
        ServerUtils.verbose("Nothing caught, returning null");
        return null;
    }

    public static String handleSwearRegex(AsyncPlayerChatEvent e, Report report) {
        String swearRegex = Sentinel.advConfig.swearRegex;

        Pattern pattern = Pattern.compile(swearRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());

        report.getStepsTaken().put("Anti-Swear Regex", "`%s`".formatted(e.getMessage()));

        if (matcher.find()) {
            String highlighted = Text.regexHighlighter(swearRegex,e.getMessage()," > "," < ");
            report.getStepsTaken().replace("Anti-Swear Regex", "`%s` %s".formatted(highlighted, Emojis.alarm));
            return highlighted;
        }
        ServerUtils.verbose("Nothing caught, returning null");
        return null;
    }

    public static String handleStrictRegex(AsyncPlayerChatEvent e, Report report) {
        String strictRegex = Sentinel.advConfig.strictRegex;

        Pattern pattern = Pattern.compile(strictRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());

        report.getStepsTaken().put("Strict Regex", "`%s`".formatted(e.getMessage()));



        if (matcher.find()) {
            String highlighted = Text.regexHighlighter(strictRegex,e.getMessage()," > "," < ");
            report.getStepsTaken().replace("Strict Regex", "`%s` %s".formatted(highlighted, Emojis.alarm));
            return highlighted;
        }
        ServerUtils.verbose("Nothing caught, returning null");
        return null;
    }

    public static String handleAntiURL(AsyncPlayerChatEvent e, Report report) {
        String urlRegex = Sentinel.advConfig.urlRegex;

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(e.getMessage());
        //ServerUtils.sendDebugMessage("AdvBlocker: Checking for URLs against regex `%1$s`:%2$s".formatted(urlRegex, e.getMessage()));

        report.getStepsTaken().put("Anti-URL", "`%s`".formatted(
                e.getMessage()
        ));

        if (matcher.find()) {
            String highlighted = Text.regexHighlighter(e.getMessage(),Sentinel.advConfig.urlRegex," > "," < ");
            ServerUtils.verbose("AdvBlocker: Caught URL: " + highlighted);
            report.getStepsTaken().replace("Anti-URL", "`%s` %s".formatted(highlighted, Emojis.alarm));
            return highlighted;
        }
        ServerUtils.verbose("Nothing caught, returning null");
        return null;
    }
}
