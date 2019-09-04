package com.xesi.xenuser.kuryentxtreadbill.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daryll Sabate on 12/27/2017.
 */

public class AuthenticateEntity {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("activationCode")
    @Expose
    private String activationCode;
    @SerializedName("assignedTo")
    @Expose
    private String assignedTo;
    @SerializedName("masterKey")
    @Expose
    private String masterKey;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("printDelay")
    @Expose
    private int printDelay;
    @SerializedName("isSuppressedPrintBuffer")
    @Expose
    private String isSuppressedPrintBuffer;

    public String getIsSuppressedPrintBuffer() {
        return isSuppressedPrintBuffer;
    }

    public void setIsSuppressedPrintBuffer(String isSuppressedPrintBuffer) {
        this.isSuppressedPrintBuffer = isSuppressedPrintBuffer;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }


    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public int getPrintDelay() {
        return printDelay;
    }

    public void setPrintDelay(int printDelay) {
        this.printDelay = printDelay;
    }
}
