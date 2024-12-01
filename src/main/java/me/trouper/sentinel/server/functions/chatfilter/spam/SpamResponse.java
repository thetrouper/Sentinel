package me.trouper.sentinel.server.functions.chatfilter.spam;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.server.functions.chatfilter.FalsePositiveReporting;
import me.trouper.sentinel.server.functions.chatfilter.Report;
import me.trouper.sentinel.utils.MathUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.trouper.sentinel.server.functions.chatfilter.spam.AntiSpam.lastMessageMap;

public class SpamResponse {
    private AsyncPlayerChatEvent event;
    private String currentMessage;
    private String previousMessage;
    private double similarity;
    private int heatAdded;
    private Report report;
    private boolean punished;

    public SpamResponse(AsyncPlayerChatEvent event, String currentMessage, String previousMessage, double similarity, int heatAdded, Report report, boolean punished) {
        this.event = event;
        this.currentMessage = currentMessage;
        this.previousMessage = previousMessage;
        this.similarity = similarity;
        this.heatAdded = heatAdded;
        this.report = report;
        this.punished = punished;
    }

    public static SpamResponse generate(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Spam response opening: Event is canceled.");
        }
        Report report = FalsePositiveReporting.initializeReport(e.getMessage());

        String message = Text.removeFirstColor(e.getMessage());
        String previousMessage = lastMessageMap.getOrDefault(e.getPlayer().getUniqueId(),"/* Placeholder Message from Sentinel */");

        SpamResponse response = new SpamResponse(e,e.getMessage(),previousMessage,0,0,report,false);


        double similarity = MathUtils.calcSim(message, previousMessage);
        response.setSimilarity(similarity);
        report.getStepsTaken().put("Calculated Similarity: ","%s".formatted(similarity));

        int addHeat = Sentinel.mainConfig.chat.spamFilter.defaultGain;
        if (similarity > Sentinel.mainConfig.chat.spamFilter.blockSimilarity) {
            addHeat = Sentinel.mainConfig.chat.spamFilter.highGain;
            response.getReport().getStepsTaken().put("Similarity is greater than %s%%".formatted(Sentinel.mainConfig.chat.spamFilter.blockSimilarity), "That is %s heat. (Auto-Block due to configured value)".formatted(addHeat));
            response.setHeatAdded(addHeat);
            return response;
        } else if (similarity > 90) {
            addHeat = Sentinel.mainConfig.chat.spamFilter.highGain;
            response.getReport().getStepsTaken().put("Similarity is greater than 90%", "That is %s heat.".formatted(addHeat));
            response.setHeatAdded(addHeat);
            return response;
        } else if (similarity > 50) {
            addHeat = Sentinel.mainConfig.chat.spamFilter.mediumGain;
            response.getReport().getStepsTaken().put("Similarity is greater than 50%", "That is %s heat.".formatted(addHeat));
            response.setHeatAdded(addHeat);
            return response;
        } else if (similarity > 25) {
            response.getReport().getStepsTaken().put("Similarity is greater than 25%", "That is %s heat.".formatted(addHeat));
            addHeat = Sentinel.mainConfig.chat.spamFilter.lowGain;
            response.setHeatAdded(addHeat);
            return response;
        }

        report.getStepsTaken().put("Similarity is less than 25%", "That is %s heat.".formatted(addHeat));
        response.setHeatAdded(addHeat);
        if (e.isCancelled()) {
            ServerUtils.verbose("Spam Response Closing: Event is canceled.");
        }
        return response;
    }

    public AsyncPlayerChatEvent getEvent() {
        return event;
    }

    public void setEvent(AsyncPlayerChatEvent event) {
        this.event = event;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public void setCurrentMessage(String currentMessage) {
        this.currentMessage = currentMessage;
    }

    public String getPreviousMessage() {
        return previousMessage;
    }

    public void setPreviousMessage(String previousMessage) {
        this.previousMessage = previousMessage;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public int getHeatAdded() {
        return heatAdded;
    }

    public void setHeatAdded(int heatAdded) {
        this.heatAdded = heatAdded;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public boolean isPunished() {
        return punished;
    }

    public void setPunished(boolean punished) {
        this.punished = punished;
    }
}
