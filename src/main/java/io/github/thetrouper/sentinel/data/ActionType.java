package io.github.thetrouper.sentinel.data;

public enum ActionType {
    SPECIFIC_COMMAND("Anti-Specific has been triggered","The use of a specific command has been detected!", 0x00FF00),
    LOGGED_COMMAND("General command log","A logged command has been executed.", 0x00FF00),
    DANGEROUS_COMMAND("Anti-Nuke has been triggered","The use of a dangerous command has been detected!", 0xFF0000),
    NBT("Anti-NBT has been triggered", "An NBT item has been caught!", 0xFFB000),
    PLACE_COMMAND_BLOCK("Anti-Nuke has been triggered","The placing of a command block has been detected!", 0xFFB000),
    USE_COMMAND_BLOCK("Anti-Nuke has been triggered","The use of a command block has been detected!", 0xFF0000),
    UPDATE_COMMAND_BLOCK("HoneyPot log","Caught a command block command!", 0xF8FF00),
    PLACE_MINECART_COMMAND("Anti-Nuke has been triggered","The placing of a minecart command has been detected!", 0xFF0000),
    USE_MINECART_COMMAND("Anti-Nuke has been triggered", "The use of a command block has been detected!", 0xFF0000),
    UPDATE_MINECART_COMMAND("HoneyPot has been triggered","Caught a command minecart command!", 0xFFB000),
    COMMAND_BLOCK_EXECUTE("Command block whitelist has been triggered","Caught an invalid command block.", 0xFFB000);
    private final String messageTop;
    private final String messageTitle;
    private final int embedColor;

    ActionType(String messageTop, String messageTitle, int embedColor) {
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
    public int getEmbedColor() {
        return embedColor;
    }
}
