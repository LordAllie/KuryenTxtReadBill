package com.xesi.xenuser.kuryentxtreadbill.model;

/**
 * Created by Daryll Sabate on 10/23/2017.
 */
public class LogModel {
    private String dateTime;
    private String message;

    public LogModel() {
    }

    public LogModel(String dateTime, String message) {
        this.dateTime = dateTime;
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
