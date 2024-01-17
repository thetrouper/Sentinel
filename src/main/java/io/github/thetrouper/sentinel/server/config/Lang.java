package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;

public class Lang implements JsonSerializable<Lang> {

    @Override
    public File getFile() {
        return new File("plugins/Sentinel/lang/" + Config.lang);
    }


}
