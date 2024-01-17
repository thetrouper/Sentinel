package io.github.thetrouper.sentinel.data;

import java.awt.*;

public enum ActionType {
    SPECIFIC_COMMAND("Anti-Specific has been triggered","The use of a specific command has been detected!", Color.green),
    LOGGED_COMMAND("General command log","A logged command has been executed.", Color.green),
    DANGEROUS_COMMAND("Anti-Nuke has been triggered","The use of a dangerous command has been detected!", Color.red),
    NBT("Anti-NBT has been triggered", "An NBT item has been caught!", Color.orange),
    PLACE_COMMAND_BLOCK("Anti-Nuke has been triggered","The placing of a command block has been detected!", Color.orange),
    USE_COMMAND_BLOCK("Anti-Nuke has been triggered","The use of a command block has been detected!", Color.red),
    UPDATE_COMMAND_BLOCK("HoneyPot log","Caught a command block command!", Color.yellow),
    PLACE_MINECART_COMMAND("Anti-Nuke has been triggered","The placing of a minecart command has been detected!", Color.red),
    USE_MINECART_COMMAND("Anti-Nuke has been triggered", "The use of a command block has been detected!", Color.red),
    UPDATE_MINECART_COMMAND("HoneyPot log","Caught a command block command!", Color.orange);
    private final String messageTop;
    private final String messageTitle;
    private final Color embedColor;

    ActionType(String messageTop, String messageTitle, Color embedColor) {
        this.messageTop = messageTop;
        this.messageTitle = messageTitle;
        this.embedColor = embedColor;
    }
    public String getMessageTop() {
        return messageTop;
    }
    public String getMessageTitle() {
        return messageTitle;
    }
    public Color getEmbedColor() {
        return embedColor;
    }
}
