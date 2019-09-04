package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daryll-POGI on 12/04/2019.
 */

public class OtherCharges {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("chargeName")
    @Expose
    private String chargeName;
    @SerializedName("amountPerKw")
    @Expose
    private double amountPerKw;
    @SerializedName("amountFixed")
    @Expose
    private double amountFixed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public double getAmountPerKw() {
        return amountPerKw;
    }

    public void setAmountPerKw(double amountPerKw) {
        this.amountPerKw = amountPerKw;
    }

    public double getAmountFixed() {
        return amountFixed;
    }

    public void setAmountFixed(double amountFixed) {
        this.amountFixed = amountFixed;
    }
}
