package me.trouper.sentinel.data.config.lang;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;

public class LanguageFile implements JsonSerializable<LanguageFile> {
    public static final File PATH = new File(Sentinel.dataFolder(), "/lang/" + Sentinel.mainConfig.plugin.lang);
    public LanguageFile() {}

    @Override
    public File getFile() {
        return PATH;
    }

    public String brokenLang = "Sentinel language is working!";

    public Permissions permissions = new Permissions();
    public class Permissions {
        public String noPermission = "§cInsufficient Permissions!";
        public String elevatingPerms = "Elevating your permissions...";
        public String logElevatingPerms = "Elevating the permissions of %s";
        public String alreadyOp = "You are already a server operator!";
        public String logAlreadyOp = "The permissions of %s are already elevated! Retrying...";
        public String noTrust = "You are not a trusted user!";
        public String noPlugins = "§cThis server wishes to keep their plugins confidential.";
    }

    public Cooldown cooldown = new Cooldown();
    public class Cooldown {
        public String onCooldown = "This action is on cooldown!";
    }

    public Reports reports = new Reports();
    public class Reports {
        public String falsePositiveSuccess = "Successfully reported a false positive!";
        public String reportingFalsePositive = "Sending report to staff...";
        public String noReport = "§cThe report you requested either does not exist, or has expired!";
    }

    public PlayerInteraction playerInteraction = new PlayerInteraction();
    public class PlayerInteraction {
        public String noOnlinePlayer = "§cYou must provide an online player to send a message to!";
        public String noMessageProvided = "§cYou must provide a message to send!";
        public String noReply = "§cYou have nobody to reply to!";
        public String messageSent = "§d§lMessage §8» §b[§fYou §e>§f %1$s§b] §7%2$s";
        public String messageReceived = "§d§lMessage §8» §b[§f%1$s §e>§f You§b] §7%2$s";
    }

    public SocialSpy socialSpy = new SocialSpy();
    public class SocialSpy {
        public String enabled = "SocialSpy is now enabled.";
        public String disabled = "SocialSpy is now disabled.";
        public String spyMessage = "§d§lSpy §8» §b§n%1$s§7 has messaged §b§n%2$s§7.";
        public String spyMessageHover = "§8]==-- §d§lSocialSpy §8--==[\n§bSender: §f%1$S\n§bReceiver: §f%2$S\n§bMessage: §f%3$S";
    }

    public AutomatedActions automatedActions = new AutomatedActions();
    public class AutomatedActions {
        public String actionAutomaticReportable = "§7This action was preformed automatically \n§7by the §bSentinel Chat Filter§7 algorithm!\n§8§o(Click to report false positive)";
    }

    public Violations violations = new Violations();
    public class Violations {
        public Chat chat = new Chat();
        public class Chat {
            public Profanity profanity = new Profanity();
            public Spam spam = new Spam();
            public Unicode unicode = new Unicode();
            public URL url = new URL();

            public class Profanity {
                public String preventNotification = "has been prevented from swearing.";
                public String autoPunishNotification = "has been auto-punished for swearing.";
                public String preventWarning = "Do not use profanity in chat. Any attempt to bypass this filter will be detected, and you will be punished.";
                public String autoPunishWarning = "&cYou have been auto-punished for attempting to bypass the profanity filter!";

                public String treeTitle = "The Profanity Filter has been triggered.";
                public String playerInfoTitle = "Player: %s";
                public String uuid = "UUID";
                public String score = "Score";

                public String reportInfoTitle = "Profanity Filter Detection";
                public String originalMessage = "Original Message";
                public String processedMessage =  "Processed Message";
                public String severity = "Severity";

                public String actionTitle = "Actions";
                public String blockAction = "Blocked the message";
                public String commandAction = "Executed Punishment Commands";
            }

            public class Spam {
                public String autoPunishNotification = "has been auto-punished for spamming.";
                public String preventNotification = "might be spamming!";
                public String preventWarning = "Do not spam in chat! Please wait before sending another message.";
                public String autoPunishWarning = "&cYou have been auto-punished for violating the anti-spam repetitively!";

                public String treeTitle = "The Anti-Spam has been triggered.";
                public String playerInfoTitle = "Player: %s";
                public String uuid = "UUID";
                public String heat = "Heat";

                public String reportInfoTitle = "Spam Filter Detection";
                public String previousMessage = "Previous Message";
                public String currentMessage = "Current Message";
                public String similarity = "Similarity";

                public String actionTitle = "Actions";
                public String blockAction = "Blocked the message";
                public String commandAction = "Executed Punishment Commands";
            }

            public class Unicode {
                public String autoPunishNotification = "has been punished for triggering the Unicode filter.";
                public String preventNotification = "has been prevented from using invalid Unicode characters.";
                public String autoPunishWarning = "You have been punished for triggered the Unicode filter.";
                public String preventWarning = "You may only use unicode from the QWERTY keyboard.";

                public String treeTitle = "The Unicode Filter has been triggered.";
                public String playerInfoTitle = "Player: %s";
                public String uuid = "UUID";

                public String reportInfoTitle = "Unicode Filter Detection";
                public String originalMessage = "Original Message";
                public String highlightedMessage =  "Highlighted Message";

                public String actionTitle = "Actions";
                public String blockAction = "Blocked the message";
                public String commandAction = "Executed Punishment Commands";
            }

            public class URL {
                public String autoPunishNotification = "has been punished for triggering the URL filter.";
                public String preventNotification = "has been prevented from sending a URL.";
                public String autoPunishWarning = "You have been punished for triggered the URL filter.";
                public String preventWarning = "You may not send links in chat.";

                public String treeTitle = "The URL Filter has been triggered.";
                public String playerInfoTitle = "Player: %s";
                public String uuid = "UUID";

                public String reportInfoTitle = "URL Filter Detection";
                public String originalMessage = "Original Message";
                public String highlightedMessage =  "Highlighted Message";

                public String actionTitle = "Actions";
                public String blockAction = "Blocked the message";
                public String commandAction = "Executed Punishment Commands";
            }
        }
        public CommandBlockEdit commandBlockEdit = new CommandBlockEdit();
        public class CommandBlockEdit {
            public String playerAttemptEdit = "A player has attempted to edit a command block!";
            public String playerInfoTitle = "Player: %s";
            public String uuid = "UUID";
            public String location = "Location";
            public String violationInfoTitle = "Command Block Edit Info";
            public String blockLocation = "Block Location";
            public String insertedCommand = "Inserted Command";
        }

        public CommandBlockExecute commandBlockExecute = new CommandBlockExecute();
        public class CommandBlockExecute {
            public String commandBlockWhitelistTripped = "Command block whitelist has been tripped.";
            public String actionsTitle = "Actions";
            public String commandBlockInfoTitle = "Command Block Info";
            public String blockLocation = "Block Location";
            public String executedCommand = "Executed Command";
            public String destroyedBlock = "Destroyed block";
            public String preventExecution = "Prevented Execution";
            public String restore = "Restore";
            public String restoreSuccess = "Success";
            public String restoreFailure = "Failure";
            public String loggedToDiscord = "Logged to Discord";
        }

        public CommandBlockMinecartPlace commandBlockMinecartPlace = new CommandBlockMinecartPlace();
        public class CommandBlockMinecartPlace {
            public String detectionChat = "&b&n%s&r &7has attempted to place a command block minecart.";
            public String detectionTree = "A player has attempted to place a command block minecart!";
            public String playerInfoTitle = "Player: %s";
            public String uuid = "UUID";
            public String location = "Location";
            public String blockLocation = "Block Location";
            public String minecartPlaceInfoTitle = "Minecart Place Info";
            public String locationFormat = "X: %s Y: %s Z: %s";
            public String blockLocationFormat = "World: %s X: %s Y: %s Z: %s";
        }

        public CommandBlockMinecartUse commandBlockMinecartUse = new CommandBlockMinecartUse();
        public class CommandBlockMinecartUse {
            public String detectionChat = "&b&n%s&r &7has attempted to use a command block minecart.";
            public String detectionTree = "A player has attempted to use a command block minecart!";
            public String playerInfoTitle = "Player: %s";
            public String uuid = "UUID";
            public String location = "Location";
            public String cartLocation = "Cart Location";
            public String minecartUseInfoTitle = "Minecart Use Info";
            public String locationFormat = "X: %s Y: %s Z: %s";
            public String cartLocationFormat = "World: %s X: %s Y: %s Z: %s";
        }

        public CommandBlockPlace commandBlockPlace = new CommandBlockPlace();
        public class CommandBlockPlace {
            public String detectionChat = "&b&n%s&r &7has attempted to place a command block.";
            public String detectionTree = "A player has attempted to place a command block!";
            public String playerInfoTitle = "Player: %s";
            public String uuid = "UUID";
            public String location = "Location";
            public String blockLocation = "Block Location";
            public String commandBlockEditInfoTitle = "Command Block Edit Info";
            public String locationFormat = "X: %s Y: %s Z: %s";
            public String blockLocationFormat = "World: %s X: %s Y: %s Z: %s";
            public String insertedCommand = "Inserted Command";
            public String insertedCommandUploadedTo = "Inserted Command Uploaded to";
        }

        public CommandBlockUse commandBlockUse = new CommandBlockUse();
        public class CommandBlockUse {
            public String detectionChat =  "&b&n%s&r &7has attempted to use a command block.";
            public String detectionTree = "A player has attempted to use a command block!";
            public String playerInfoTitle = "Player: %s";
            public String uuid = "UUID";
            public String location = "Location";
            public String blockLocation = "Block Location";
            public String commandBlockUseInfoTitle = "Command Block Use Info";
            public String locationFormat = "X: %s Y: %s Z: %s";
            public String blockLocationFormat = "World: %s X: %s Y: %s Z: %s";
            public String commandInside = "Command Inside";
            public String commandUploadedTo = "Command Uploaded to";
        }

        public CommandExecute commandExecute = new CommandExecute();
        public class CommandExecute {
            public String specificCommandDetection = "A player has attempted to run a %s command.";
            public String playerInfoTitle = "Player: %s";
            public String uuid = "UUID";
            public String location = "Location";
            public String commandField = "Command";
            public String commandUploadedTo = "Command Uploaded to";
            public String violationInfoTitle = "Violation Info";
            public String locationFormat = "X: %s Y: %s Z: %s";
            public String specificCommandViolation = "&b&n%s&r &7has attempted to run a specific command.";
            public String dangerousCommandViolation = "&b&n%s&r &7has attempted to run a dangerous command.";
            public String loggedCommandViolation = "&b&n%s&r &7has ran a logged command.";
        }

        public CreativeHotbar creativeHotbar = new CreativeHotbar();
        public class CreativeHotbar {
            public String nbtAttemptDetection = "A player has attempted to grab an NBT item!";
            public String playerInfoTitle = "Player: %s";
            public String uuid = "UUID";
            public String location = "Location";
            public String locationFormat = "X: %s Y: %s Z: %s";
            public String itemType = "Type";
            public String nbtUpload = "NBT Upload";
            public String itemInfoTitle = "Item Info";
            public String nbtAttemptViolation = "&b&n%s&r &7has attempted to grab an NBT item.";
        }

        public ViolationMessages violationMessages = new ViolationMessages();
        public class ViolationMessages {
            public String actions = "Actions";
            public String eventCancelled = "Canceled Event";
            public String punishmentCommandsExecuted = "Executed Punishment Commands";
            public String userOpStripped = "Stripped user's OP";
            public String loggedToDiscord = "Logged to Discord";
        }

    }
}