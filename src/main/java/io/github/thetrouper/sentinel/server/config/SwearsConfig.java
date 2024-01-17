package io.github.thetrouper.sentinel.server.config;

import io.github.itzispyder.pdk.utils.misc.JsonSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SwearsConfig implements JsonSerializable<SwearsConfig> {
    @Override
    public File getFile() {
        File file = new File("plugins/Sentinel/swears.json");
        file.getParentFile().mkdirs();
        return file;
    }

    public static List<String> swears = new ArrayList<>() {{
        add("anal");
        add("anus");
        add("arse");
        add("ass");
        add("ballsack");
        add("balls");
        add("bastard");
        add("bitch");
        add("btch");
        add("biatch");
        add("blowjob");
        add("bollock");
        add("bollok");
        add("boner");
        add("boob");
        add("bugger");
        add("butt");
        add("choad");
        add("clitoris");
        add("cock");
        add("coon");
        add("crap");
        add("cum");
        add("cunt");
        add("dick");
        add("dildo");
        add("douchebag");
        add("dyke");
        add("feck");
        add("fellate");
        add("fellatio");
        add("felching");
        add("fuck");
        add("fudgepacker");
        add("flange");
        add("gtfo");
        add("hoe");
        add("horny");
        add("incest");
        add("jerk");
        add("jizz");
        add("labia");
        add("masturb");
        add("muff");
        add("nazi");
        add("nipple");
        add("nips");
        add("nude");
        add("pedophile");
        add("penis");
        add("piss");
        add("poop");
        add("porn");
        add("prick");
        add("prostit");
        add("pube");
        add("pussie");
        add("pussy");
        add("queer");
        add("rape");
        add("rapist");
        add("retard");
        add("rimjob");
        add("scrotum");
        add("sex");
        add("shit");
        add("slut");
        add("spunk");
        add("stfu");
        add("suckmy");
        add("tits");
        add("tittie");
        add("titty");
        add("turd");
        add("twat");
        add("vagina");
        add("wank");
        add("whore");
    }};
}
