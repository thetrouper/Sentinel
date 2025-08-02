package me.trouper.sentinel.data.config.lang;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;

import java.io.File;

public class LanguageFile implements JsonSerializable<LanguageFile> {
    public static final File PATH = new File(Sentinel.getInstance().getDirector().io.getDataFolder(), "/lang/" + Sentinel.getInstance().getDirector().io.mainConfig.plugin.lang);
    public LanguageFile() {}

    @Override
    public File getFile() {
        return PATH;
    }

    public String brokenLang = "Sentinel language is working!";

    public Permissions permissions = new Permissions();
    public class Permissions {
        public String noPermission = "Insufficient Permissions!";
        public String elevatingPerms = "Elevating your permissions...";
        public String logElevatingPerms = "Elevating the permissions of {0}";
        public String alreadyOp = "You are already a server operator!";
        public String logAlreadyOp = "The permissions of {0} are already elevated! Retrying...";
        public String noTrust = "You are not a trusted user!";
        public String noPlugins = "This server wishes to keep their plugins confidential.";
        public String playersOnly = "Only players can preform this operation.";
    }

    public Cooldown cooldown = new Cooldown();
    public class Cooldown {
        public String onCooldown = "This action is on cooldown for {0}s!";
    }

    public Reports reports = new Reports();
    public class Reports {
        public String falsePositiveSuccess = "Successfully reported a false positive!";
        public String reportingFalsePositive = "Sending report to staff...";
        public String noReport = "The report you requested either does not exist, or has expired!";
    }

    public PlayerInteraction playerInteraction = new PlayerInteraction();
    public class PlayerInteraction {
        public String noOnlinePlayer = "§cYou must provide an online player to send a message to!";
        public String noMessageProvided = "§cYou must provide a message to send!";
        public String noReply = "§cYou have nobody to reply to!";
        public String messageSent = "§d§lMessage §8» §b[§fYou §e>§f {0}§b] §7{1}";
        public String messageReceived = "§d§lMessage §8» §b[§f{0} §e>§f You§b] §7{1}";
    }

    public SocialSpy socialSpy = new SocialSpy();
    public class SocialSpy {
        public String enabled = "SocialSpy is now enabled.";
        public String disabled = "SocialSpy is now disabled.";
        public String spyMessage = "§d§lSpy §8» §b§n{0}§7 has messaged §b§n{1}§7.";
        public String spyMessageHover = "§8]==-- §d§lSocialSpy §8--==[\n§bSender: §f{0}\n§bReceiver: §f{1}\n§bMessage: §f{2}";
    }

    public AutomatedActions automatedActions = new AutomatedActions();
    public class AutomatedActions {
        public String reportable = "§7This action was preformed automatically \n§7by the §b§lSentinel Chat Filter§7 algorithm!\n§8(Click to report false positive)";
    }

    public Plugin plugin = new Plugin();
    public class Plugin {
        public String invalidArgs = "Invalid arguments, please check usage.";
        public String invalidSubCommand = "Invalid {0} sub-command.";
        public String reloadingConfig = "Reloading the config.";
        public String reloadingConfigLite = "Reloading the config in lite mode.";
    }

    public CommandBlock commandBlock = new CommandBlock();
    public class CommandBlock {
        public String notCommandBlock = "Could not whitelist the {0}, it is not a command block!";
        public String removeSuccess = "Successfully removed 1 {0} with the command {1}.";
        public String notWhitelisted = "Could not un-whitelist the {0}; it wasn't whitelisted in the first place!";
        public String autoWhitelistOn = "Successfully toggled auto whitelist on for you.";
        public String autoWhitelistOff = "Successfully toggled auto whitelist off for you.";
        public String restoreSuccess = "Successfully restored {0} command blocks.";
        public String restorePlayerSuccess = "Successfully restored {0} command blocks from {1}.";
        public String clearSuccess = "Successfully cleared {0} command blocks.";
        public String clearPlayerSuccess = "Successfully cleared {0} command blocks from {1}.";
    }

    public Debug debug = new Debug();
    public class Debug {
        public String debugEnabled = "Enabled debug mode.";
        public String debugDisabled = "Disabled debug mode.";
        public String notFlagged = "Message did not get flagged.";
    }

    public FalsePositive falsePositive = new FalsePositive();
    public class FalsePositive {
        public String addSuccess = "Successfully added {0} to the false positive list!";
        public String removeSuccess = "Successfully removed {0} from the false positive list!";
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
                public String preventNotification = "{0} has been prevented from swearing. ({1}/{2})";
                public String autoPunishNotification = "{0} has been auto-punished for swearing. ({1}/{2})";
                public String preventWarning = "Do not use profanity in chat. Any attempt to bypass this filter will be detected, and you will be punished.";
                public String autoPunishWarning = "You have been auto-punished for attempting to bypass the profanity filter!";

                public String treeTitle = "The Profanity Filter has been triggered by {0}.";
                public String score = "Score";

                public String reportInfoTitle = "Profanity Filter Detection";
                public String processedMessage = "Processed Message";
                public String severity = "Severity";
            }

            public Spam spam = new Spam();
            public class Spam {
                public String autoPunishNotification = "{0} has been auto-punished for spamming. ({1}/{2})";
                public String preventNotification = "{0} might be spamming! ({1}/{2})";
                public String preventWarning = "Do not spam in chat! Please wait before sending another message.";
                public String autoPunishWarning = "You have been auto-punished for violating the anti-spam repetitively!";

                public String treeTitle = "The Anti-Spam has been triggered by {0}.";
                public String heat = "Heat";

                public String reportInfoTitle = "Spam Filter Detection";
                public String previousMessage = "Previous Message";
                public String currentMessage = "Current Message";
                public String similarity = "Similarity";
            }

            public Unicode unicode = new Unicode();
            public class Unicode {
                public String autoPunishNotification = "{0} has been punished for triggering the Unicode filter.";
                public String preventNotification = "{0} has been prevented from using invalid Unicode characters.";
                public String autoPunishWarning = "You have been punished for triggered the Unicode filter.";
                public String preventWarning = "You may only use unicode from the QWERTY keyboard.";

                public String treeTitle = "The Unicode Filter has been triggered by {0}.";
                public String reportInfoTitle = "Unicode Filter Detection";
            }

            public URL url = new URL();
            public class URL {
                public String autoPunishNotification = "{0} has been punished for triggering the URL filter.";
                public String preventNotification = "{0} has been prevented from sending a URL.";
                public String autoPunishWarning = "You have been punished for triggered the URL filter.";
                public String preventWarning = "You may not send links in chat.";

                public String treeTitle = "The URL Filter has been triggered by {0}.";
                public String reportInfoTitle = "URL Filter Detection";
            }
        }

        public Protections protections = new Protections();
        public class Protections {
            public RootName rootName = new RootName();
            public class RootName {
                // Headers
                public String rootNameFormat = "The {0} has been triggered!";
                public String rootNameFormatPlayer = "{0} has attempted to {1} a {2}!";

                // Triggers
                public String use = "use";
                public String edit = "edit";
                public String place = "place";
                public String brake = "break";
                public String run = "run";
                public String grab = "grab";

                // Types
                public String commandBlock = "Command Block";
                public String structureBlock = "Structure Block";
                public String jigsawBlock = "Jigsaw Block";
                public String commandMinecart = "Command Minecart";
                public String commandBlockWhitelist = "Command Block Whitelist";
                public String commandBlockRestriction = "Command Block Restriction";
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