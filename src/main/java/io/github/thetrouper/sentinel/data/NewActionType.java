package io.github.thetrouper.sentinel.data;

public record NewActionType(String superTitle, String title, int embedColor, String chatNotification) {
    public static final NewActionType CMD_BLOCK_EXECUTE = new NewActionType("Command Block Whitelist Log", "An unauthorized command block has been detected",0xFF0000,"The Command Block Whitelist has been triggered!");
    public static final NewActionType CMD_BLOCK_PLACE = new NewActionType("Anti-Nuke Log", "A player attempted to place a command block",0xFF0000,"§e%s§7 has triggered the Anti-Nuke!");
    public static final NewActionType CMD_BLOCK_USE = new NewActionType("Anti-Nuke Log", "A player attempted to use a command block",0xFF0000,"§e%s§7 has triggered the Anti-Nuke!");
    public static final NewActionType CMD_BLOCK_CHANGE = new NewActionType("Anti-Nuke Log", "A player attempted to change a command block",0xFF0000,"§e%s§7 has triggered the Anti-Nuke!");
    public static final NewActionType CMD_MINECART_USE = new NewActionType("Anti-Nuke Log", "A player attempted to use a command minecart",0xFF0000,"§e%s§7 has triggered the Anti-Nuke!");
    public static final NewActionType CMD_MINECART_PLACE = new NewActionType("Anti-Nuke Log", "A player attempted to place a command minecart",0xFF0000,"§e%s§7 has triggered the Anti-Nuke!");
    public static final NewActionType CMD_MINECART_BREAK = new NewActionType("Anti-Nuke Log", "A player attempted to break a command minecart",0xFF0000,"§e%s§7 has triggered the Anti-Nuke!");
    public static final NewActionType CMD_EXECUTE = new NewActionType("Anti-Nuke Log", "A player attempted to run a dangerous command",0xFF0000,"§e%s§7 has triggered the Anti-Nuke!");
    public static final NewActionType CMD_SPECIFIC = new NewActionType("Anti-Specific Log", "A player attempted to run a plugin specific command",0xFF0000,"§e%s§7 has triggered the Anti-Specific.");
    public static final NewActionType CMD_LOGGED = new NewActionType("Command Log", "A player has ran a logged command",0xFF0000,"§e%s§7 has ran a logged command.");
    public static final NewActionType NBT_PULL = new NewActionType("Anti-NBT Log", "A player attempted to pull out an NBT item",0xFF0000,"§e%s§7 has triggered the Anti-NBT!");
}
