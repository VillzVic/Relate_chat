package com.example.appzonepc2.relate.model;

/**
 * Created by appzonepc2 on 28/03/2018.
 */

public class    message_model {

    private String message;
    private String type;
    private Boolean seen;
    private long time;
    private String from;

    public message_model() {
    }

    public message_model(String message, String type, Boolean seen, long time, String from) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
