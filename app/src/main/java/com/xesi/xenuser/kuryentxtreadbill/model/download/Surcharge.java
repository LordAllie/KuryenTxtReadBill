package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;


/**
 * Created by Daryll-POGI on 20/05/2019.
 */
public class Surcharge {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("DAYS")
    @Expose
    private int days;
    @SerializedName("SURCHARGE")
    @Expose
    private BigDecimal surcharge;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }
}
