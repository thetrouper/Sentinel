package me.trouper.sentinel.server.functions.chatfilter;

import java.util.LinkedHashMap;

public class Report {
    private long id;
    private String original;
    private LinkedHashMap<String,String> stepsTaken;


    public Report(long id, String original, LinkedHashMap<String, String> stepsTaken) {
        this.id = id;
        this.original = original;
        this.stepsTaken = stepsTaken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public LinkedHashMap<String, String> getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(LinkedHashMap<String, String> stepsTaken) {
        this.stepsTaken = stepsTaken;
    }
}
