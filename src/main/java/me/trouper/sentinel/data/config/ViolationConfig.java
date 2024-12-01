package me.trouper.sentinel.data.config;

import io.github.itzispyder.pdk.utils.misc.config.JsonSerializable;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.startup.Load;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViolationConfig implements JsonSerializable<SwearsConfig> {
    @Override
    public File getFile() {
        File file = new File(Sentinel.dataFolder(), "/violation-config.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public CommandBlockEdit commandBlockEdit = new CommandBlockEdit();
    public CommandBlockExecute commandBlockExecute = new CommandBlockExecute();
    public CommandBlockMinecartPlace commandBlockMinecartPlace = new CommandBlockMinecartPlace();
    public CommandBlockMinecartUse commandBlockMinecartUse = new CommandBlockMinecartUse();
    public CommandBlockPlace commandBlockPlace = new CommandBlockPlace();
    public CommandBlockUse commandBlockUse = new CommandBlockUse();
    public CommandExecute commandExecute = new CommandExecute();
    public CreativeHotbarAction creativeHotbarAction = new CreativeHotbarAction();


    public class CommandBlockEdit {
        public boolean enabled = true;
        public boolean deop = true;
        public boolean logToDiscord = true;
        public boolean punish = false;
        public List<String> punishmentCommands = List.of(
                "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
        );
    }

    public class CommandBlockMinecartPlace {
        public boolean enabled = true;
        public boolean deop = true;
        public boolean logToDiscord = true;
        public boolean punish = false;
        public List<String> punishmentCommands = List.of(
                "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
        );
    }

    public class CommandBlockMinecartUse {
        public boolean enabled = true;
        public boolean deop = true;
        public boolean logToDiscord = true;
        public boolean punish = false;
        public List<String> punishmentCommands = List.of(
                "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
        );
    }

    public class CommandBlockPlace {
        public boolean enabled = true;
        public boolean deop = true;
        public boolean logToDiscord = true;
        public boolean punish = false;
        public List<String> punishmentCommands = List.of(
                "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
        );
    }

    public class CommandBlockUse {
        public boolean enabled = true;
        public boolean deop = true;
        public boolean logToDiscord = true;
        public boolean punish = false;
        public List<String> punishmentCommands = List.of(
                "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
        );
    }

    public class CreativeHotbarAction {
        public boolean enabled = true;
        public boolean deop = true;
        public boolean logToDiscord = true;
        public boolean punish = false;
        public List<String> punishmentCommands = List.of(
                "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
        );
    }

    public class CommandExecute {
        public Dangerous dangerous = new Dangerous();
        public Logged logged = new Logged();
        public Specific specific = new Specific();
        public class Dangerous {
            public boolean enabled = true;
            public List<String> commands = List.of(
                    "op",
                    "deop",
                    "stop",
                    "restart",
                    "execute",
                    "sudo",
                    "esudo",
                    "fill",
                    "setblock",
                    "data",
                    "whitelist",
                    "lp",
                    "luckperms",
                    "perms",
                    "perm",
                    "permission",
                    "permissions",
                    "pm",
                    "pluginmanager",
                    "rl",
                    "reload",
                    "plugman"
            );
            public boolean deop = true;
            public boolean logToDiscord = true;
            public boolean punish = false;
            public List<String> punishmentCommands = List.of(
                    "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
            );
        }
        public class Logged {
            public boolean enabled = true;
            public List<String> commands = List.of(
                    "give",
                    "item"
            );
            public boolean logToDiscord = true;
        }
        public class Specific {
            public boolean enabled = true;
            public boolean logToDiscord = false;
            public boolean punish = false;
            public List<String> punishmentCommands = List.of(
                    "ban %player% ]=- Sentinel -=[ \nYou have been banned for attempting a dangerous action. \nIf you believe this to be a mistake, please contact the server owner."
            );
        }
    }

    public class CommandBlockExecute {
        public boolean enabled = true;
        public boolean destroyBlock = false;
        public boolean attemptRestore = true;
        public boolean logToDiscord = false;
    }

}
