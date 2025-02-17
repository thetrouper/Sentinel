package me.trouper.sentinel.startup;

import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.utils.ServerUtils;

public class IndirectLaunch {
    public static void launch() {
        Sentinel.getInstance().ip = ServerUtils.getPublicIPAddress();
        Sentinel.getInstance().port = ServerUtils.getPort();
        Sentinel.getInstance().nonce = Auth.getNonce();

        Sentinel.log.info("Getting plugin file");

        Sentinel.log.info("Reading Persistent files...");

        Sentinel.getInstance().loadConfig();

        Sentinel.log.info("Language Status: (%s)".formatted(Sentinel.lang.brokenLang));

        Sentinel.log.info("Initializing Auth Identifier");

        Sentinel.getInstance().identifier = Auth.getServerID();

        Load.load(Sentinel.getInstance().license,Sentinel.getInstance().identifier, true);
    }
}
