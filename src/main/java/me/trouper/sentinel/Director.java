package me.trouper.sentinel;

import me.trouper.sentinel.data.IO;
import me.trouper.sentinel.server.functions.helpers.CBWhitelistManager;
import me.trouper.sentinel.server.functions.helpers.MessageHandler;
import me.trouper.sentinel.server.functions.helpers.ReportHandler;
import me.trouper.sentinel.startup.*;
import me.trouper.sentinel.startup.drm.Auth;
import me.trouper.sentinel.startup.drm.Loader;
import me.trouper.sentinel.utils.ServerUtils;

public final class Director {
    
    public Loader loader;
    public BackdoorDetection backdoorDetection;
    public Auth auth;
    public Telemetry telemetry;
    public Injection injection;
    public CBWhitelistManager whitelistManager;
    public MessageHandler messageHandler;
    public ReportHandler reportHandler;
    public IO io;

    public Director() {
        Sentinel.getInstance().getLogger().info("Instantiating Systems");
        telemetry = new Telemetry();
        auth = new Auth();
        loader = new Loader();
        backdoorDetection = new BackdoorDetection();
        injection = new Injection();
        whitelistManager = new CBWhitelistManager();
        messageHandler = new MessageHandler();
        reportHandler = new ReportHandler();
        io = new IO();
    }
    
    public void launch() {
        Sentinel.getInstance().getLogger().info("Launching Sentinel");
        Sentinel.getInstance().ip = ServerUtils.getPublicIPAddress();
        Sentinel.getInstance().port = ServerUtils.getPort();
        Sentinel.getInstance().nonce = auth.getNonce();

        Sentinel.getInstance().getLogger().info("Getting plugin file");

        Sentinel.getInstance().getLogger().info("Reading Persistent files...");
        
        io.loadConfig();

        Sentinel.getInstance().getLogger().info("Language Status: (%s)".formatted(io.lang.brokenLang));

        Sentinel.getInstance().getLogger().info("Initializing Auth Identifier");

        Sentinel.getInstance().license = auth.getLicenseKey();
        Sentinel.getInstance().identifier = auth.getServerID();

        loader.load(Sentinel.getInstance().license,Sentinel.getInstance().identifier, true);
    }
}
