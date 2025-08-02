package me.trouper.sentinel.server.functions.chatfilter.spam;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.trouper.sentinel.server.functions.chatfilter.FilterResponse;
import me.trouper.sentinel.server.functions.helpers.Report;
import me.trouper.sentinel.utils.FormatUtils;
import me.trouper.sentinel.utils.MathUtils;
import me.trouper.sentinel.utils.ServerUtils;
import me.trouper.sentinel.utils.Text;
import org.bukkit.entity.Player;

import static me.trouper.sentinel.server.functions.chatfilter.spam.SpamFilter.lastMessageMap;

public class SpamResponse implements FilterResponse {
    private AsyncChatEvent event;
    private String currentMessage;
    private String previousMessage;
    private double similarity;
    private int heatAdded;
    private Report report;
    private boolean blocked;
    private boolean punished;

    public SpamResponse(AsyncChatEvent event, String currentMessage, String previousMessage, double similarity, int heatAdded, Report report, boolean blocked, boolean punished) {
        this.event = event;
        this.currentMessage = currentMessage;
        this.previousMessage = previousMessage;
        this.similarity = similarity;
        this.heatAdded = heatAdded;
        this.report = report;
        this.blocked = blocked;
        this.punished = punished;
    }

    public static SpamResponse generate(AsyncChatEvent e) {
        if (e.isCancelled()) {
            ServerUtils.verbose("Spam response opening: Event is canceled.");
        }

        String message = LegacyComponentSerializer.legacySection().serialize(e.message());
        Report report = main.dir().reportHandler.initializeReport(message);

        message = Text.removeColors(message);
        String previousMessage = lastMessageMap.getOrDefault(e.getPlayer().getUniqueId(),"/* Placeholder Message from Sentinel */");

        SpamResponse response = new SpamResponse(e,message,previousMessage,0,0,report,false,false);


        double similarity = MathUtils.calcSim(message, previousMessage);
        response.setSimilarity(similarity);
        report.getStepsTaken().put("Calculated Similarity: ","%s".formatted(similarity));

        int addHeat = main.dir().io.mainConfig.chat.spamFilter.defaultGain;
        if (similarity > main.dir().io.mainConfig.chat.spamFilter.blockSimilarity) {
            addHeat = main.dir().io.mainConfig.chat.spamFilter.highGain;
            response.getReport().getStepsTaken().put("Similarity is greater than %s%%".formatted(main.dir().io.mainConfig.chat.spamFilter.blockSimilarity), "That is %s heat. (Auto-Block due to configured value)".formatted(addHeat));
            response.setHeatAdded(addHeat);
            return response;
        } else if (similarity > 90) {
            addHeat = main.dir().io.mainConfig.chat.spamFilter.highGain;
            response.getReport().getStepsTaken().put("Similarity is greater than 90%", "That is %s heat.".formatted(addHeat));
            response.setHeatAdded(addHeat);
            return response;
        } else if (similarity > 50) {
            addHeat = main.dir().io.mainConfig.chat.spamFilter.mediumGain;
            response.getReport().getStepsTaken().put("Similarity is greater than 50%", "That is %s heat.".formatted(addHeat));
            response.setHeatAdded(addHeat);
            return response;
        } else if (similarity > 25) {
            response.getReport().getStepsTaken().put("Similarity is greater than 25%", "That is %s heat.".formatted(addHeat));
            addHeat = main.dir().io.mainConfig.chat.spamFilter.lowGain;
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

    public AsyncChatEvent getEvent() {
        return event;
    }

    public void setEvent(AsyncChatEvent event) {
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

    @Override
    public Player getPlayer() {
        return event.getPlayer();
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
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
}
