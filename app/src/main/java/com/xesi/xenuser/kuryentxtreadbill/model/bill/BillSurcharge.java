package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by Daryll-POGI on 22/05/2019.
 */

public class BillSurcharge {

    @SerializedName("days")
    @Expose
    private int days;
    @SerializedName("surcharge")
    @Expose
    private BigDecimal surcharge;
    @SerializedName("billNo")
    @Expose
    private String billNumber;
    @SerializedName("surchargeRate")
    @Expose
    private BigDecimal surchargeRate;

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

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public BigDecimal getSurchargeRate() {
        return surchargeRate;
    }

    public void setSurchargeRate(BigDecimal surchargeRate) {
        this.surchargeRate = surchargeRate;
    }
}
