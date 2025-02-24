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
        public String playersOnly = "Only players can preform this operation.";
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
        public String spyMessageHover = "§8]==-- §d§lSocialSpy §8--==[\n§bSender: §f%1$s\n§bReceiver: §f%2$s\n§bMessage: §f%3$s";
    }

    public AutomatedActions automatedActions = new AutomatedActions();
    public class AutomatedActions {
        public String reportable = "§7This action was preformed automatically \n§7by the §bSentinel Chat Filter§7 algorithm!\n§8§o(Click to report false positive)";
    }

    public Plugin plugin = new Plugin();
    public class Plugin {
        public String invalidArgs = "Invalid arguments, please check usage.";
        public String invalidSubCommand = "Invalid %1$s sub-command.";
        public String reloadingConfig = "Reloading the config.";
        public String reloadingConfigLite = "Reloading the config in lite mode.";
    }

    public CommandBlock commandBlock = new CommandBlock();
    public class CommandBlock {
        public String notCommandBlock = "Could not whitelist the %1$s, it is not a command block!";
        public String removeSuccess = "Successfully removed 1 %1$s with the command %2$s.";
        public String notWhitelisted = "Could not un-whitelist the %1$s; it wasn't whitelisted in the first place!";
        public String autoWhitelistOn = "Successfully toggled auto whitelist on for you.";
        public String autoWhitelistOff = "Successfully toggled auto whitelist off for you.";
        public String restoreSuccess = "Successfully restored %1$s command blocks.";
        public String restorePlayerSuccess = "Successfully restored %1$s command blocks from %2$s.";
        public String clearSuccess = "Successfully cleared %1$s command blocks.";
        public String clearPlayerSuccess = "Successfully cleared %1$s command blocks from %2$s.";
    }

    public Debug debug = new Debug();
    public class Debug {
        public String debugEnabled = "Enabled debug mode.";
        public String debugDisabled = "Disabled debug mode."; 
        public String notFlagged = "Message did not get flagged.";
    }

    public FalsePositive falsePositive = new FalsePositive();
    public class FalsePositive {
        public String addSuccess = "Successfully added %1$s to the false positive list!";
        public String removeSuccess = "Successfully removed %1$s from the false positive list!";
    }

    public Generic generic = new Generic();
    public class Generic {
        public String yes = "Yes";
        public String no = "No";
        public String success = "Success";
        public String failure = "Failure";
        public String t = "True";
        public String f = "False";
    }

    public Violations violations = new Violations();
    public class Violations {
        public Chat chat = new Chat();
        public class Chat {
            public String denyMessage = "Blocked the message";
            public String originalMessage = "Original Message";
            public String highlightedMessage = "Highlighted Message";

            public Profanity profanity = new Profanity();
            public class Profanity {
                public String preventNotification = "§b§n%1$s§r §7has been prevented from swearing.";
                public String autoPunishNotification = "§b§n%1$s§r §7has been auto-punished for swearing.";
                public String preventWarning = "Do not use profanity in chat. Any attempt to bypass this filter will be detected, and you will be punished.";
                public String autoPunishWarning = "§cYou have been auto-punished for attempting to bypass the profanity filter!";

                public String treeTitle = "The Profanity Filter has been triggered.";
                public String score = "Score";

                public String reportInfoTitle = "Profanity Filter Detection";
                public String processedMessage = "Processed Message";
                public String severity = "Severity";
            }

            public Spam spam = new Spam();
            public class Spam {
                public String autoPunishNotification = "§b§n%1$s§r §7has been auto-punished for spamming.";
                public String preventNotification = "§b§n%1$s§r §7might be spamming!";
                public String preventWarning = "Do not spam in chat! Please wait before sending another message.";
                public String autoPunishWarning = "§cYou have been auto-punished for violating the anti-spam repetitively!";

                public String treeTitle = "The Anti-Spam has been triggered.";
                public String heat = "Heat";

                public String reportInfoTitle = "Spam Filter Detection";
                public String previousMessage = "Previous Message";
                public String currentMessage = "Current Message";
                public String similarity = "Similarity";
            }

            public Unicode unicode = new Unicode();
            public class Unicode {
                public String autoPunishNotification = "§b§n%1$s§r §7has been punished for triggering the Unicode filter.";
                public String preventNotification = "§b§n%1$s§r §7has been prevented from using invalid Unicode characters.";
                public String autoPunishWarning = "You have been punished for triggered the Unicode filter.";
                public String preventWarning = "You may only use unicode from the QWERTY keyboard.";

                public String treeTitle = "The Unicode Filter has been triggered.";
                public String reportInfoTitle = "Unicode Filter Detection";
            }

            public URL url = new URL();
            public class URL {
                public String autoPunishNotification = "§b§n%1$s§r §7has been punished for triggering the URL filter.";
                public String preventNotification = "§b§n%1$s§r §7has been prevented from sending a URL.";
                public String autoPunishWarning = "You have been punished for triggered the URL filter.";
                public String preventWarning = "You may not send links in chat.";

                public String treeTitle = "The URL Filter has been triggered.";
                public String reportInfoTitle = "URL Filter Detection";
            }
        }

        public Protections protections = new Protections();
        public class Protections {
            public RootName rootName = new RootName();
            public class RootName {
                // Headers
                public String rootNameFormat = "The §e§n%1$s§r §7has been triggered!";
                public String rootNameFormatPlayer = "§b§n%1$s§r §7has attempted to §e%2$s§r §7a §b%3$s§r§7!";

                // Triggers
                public String use = "use";
                public String edit = "edit";
                public String place = "place";
                public String run = "run";
                public String grab = "grab";

                // Types
                public String commandBlock = "Command Block";
                public String minecartCommandBlock = "Minecart Command Block";
                public String commandBlockWhitelist = "Command Block Whitelist";
                public String specificCommand = "Specific Command";
                public String loggedCommand = "Logged Command";
                public String dangerousCommand = "Dangerous Command";
                public String nbtItem = "NBT item";
            }

            public InfoNode infoNode = new InfoNode();
            public class InfoNode {
                public String playerInfo = "Player Info";
                public String commandInfo = "Command Info";
                public String blockInfo = "Block Info";
                public String itemInfo = "Item Info";
                public String minecartInfo = "Minecart Info";

                public String uuid = "UUID";
                public String name = "Name";
                public String permissionRequired = "Permission Required";
                public String permissionSatisfied = "Permission Satisfied";
                public String operator = "Operator";
                public String hasMeta = "Has Meta";
                public String hasName = "Has Name";
                public String hasLore = "Has Lore";
                public String hasEnchants = "Has Enchants";
                public String hasAttributes = "Has Attributes";

                public String locationField = "Location";
                public String worldField = "World";
                public String commandField = "Command";
                public String commandTooLargeField = "Command Too Large (Uploaded)";
                public String nbtStored = "NBT Stored";
                public String blockLocationField = "Block Location";
                public String cartLocationField = "Cart Location";
                public String locationFormat = "X: %s Y: %s Z: %s";
            }

            public ActionNode actionNode = new ActionNode();
            public class ActionNode {
                public String actionNodeTitle = "Actions";
                public String eventCancelled = "Canceled Event";
                public String destroyedBlock = "Destroyed Block";
                public String restore = "Restored Original Block";
                public String restoreFailed = "Failed to Restore Original Block";
                public String punishmentCommandsExecuted = "Executed Punishment Commands";
                public String userDeoped = "De-OP'd Player";
                public String loggedToDiscord = "Logged to Discord";
            }
        }
    }
}