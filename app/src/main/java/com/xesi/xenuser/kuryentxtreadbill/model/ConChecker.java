package com.xesi.xenuser.kuryentxtreadbill.model;

/**
 * Created by xenuser on 4/4/2017.
 */
public class ConChecker {
    int code;
    String message;

    public ConChecker() {
    }

    public ConChecker(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
