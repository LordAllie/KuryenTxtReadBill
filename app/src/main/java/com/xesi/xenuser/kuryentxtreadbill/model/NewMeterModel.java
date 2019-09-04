package com.xesi.xenuser.kuryentxtreadbill.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daryll Sabate on 7/21/2017.
 */
public class NewMeterModel {

    @SerializedName("idRoute")
    @Expose
    private int idRoute;
    @SerializedName("msn")
    @Expose
    private String msn;
    @SerializedName("dateRead")
    @Expose
    private String dateRead;
    @SerializedName("timeRead")
    @Expose
    private String timeRead;
    @SerializedName("reading")
    @Expose
    private int reading;
    @SerializedName("isUploaded")
    @Expose
    private int isUploaded;

    public NewMeterModel() {
    }

    public NewMeterModel(int idRoute, String msn, String dateRead, String timeRead, int reading, int isUploaded) {
        this.idRoute = idRoute;
        this.msn = msn;
        this.dateRead = dateRead;
        this.timeRead = timeRead;
        this.reading = reading;
        this.isUploaded = isUploaded;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getDateRead() {
        return dateRead;
    }

    public void setDateRead(String dateRead) {
        this.dateRead = dateRead;
    }

    public String getTimeRead() {
        return timeRead;
    }

    public void setTimeRead(String timeRead) {
        this.timeRead = timeRead;
    }

    public int getReading() {
        return reading;
    }

    public void setReading(int reading) {
        this.reading = reading;
    }
}
