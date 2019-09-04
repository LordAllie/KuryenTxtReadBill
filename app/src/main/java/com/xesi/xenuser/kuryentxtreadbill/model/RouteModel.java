package com.xesi.xenuser.kuryentxtreadbill.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daryll Sabate on 12/14/2017.
 */

public class RouteModel {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("routeCode")
    @Expose
    private String routeCode;
    @SerializedName("billingDayStart")
    @Expose
    private int billingDayStart;
    @SerializedName("billingDayEnd")
    @Expose
    private int billingDayEnd;
    @SerializedName("dueDay")
    @Expose
    private int dueDay;

    public RouteModel() {
    }

    public RouteModel(int id, String routeCode, int billingDayStart, int billingDayEnd, int dueDay) {
        this.id = id;
        this.routeCode = routeCode;
        this.billingDayStart = billingDayStart;
        this.billingDayEnd = billingDayEnd;
        this.dueDay = dueDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public int getDueDay() {
        return dueDay;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }

    public int getBillingDayStart() {
        return billingDayStart;
    }

    public void setBillingDayStart(int billingDayStart) {
        this.billingDayStart = billingDayStart;
    }

    public int getBillingDayEnd() {
        return billingDayEnd;
    }

    public void setBillingDayEnd(int billingDayEnd) {
        this.billingDayEnd = billingDayEnd;
    }
}
