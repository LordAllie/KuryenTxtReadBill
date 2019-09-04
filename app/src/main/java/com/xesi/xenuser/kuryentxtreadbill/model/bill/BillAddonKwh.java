package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xenuser on 2/16/2017.
 */
public class BillAddonKwh {
    @SerializedName("billNo")
    @Expose
    private String billNo;
    @SerializedName("addonKwh")
    @Expose
    private String addonKwh;
    @SerializedName("value")
    @Expose
    private double value;
    @SerializedName("isUploaded")
    @Expose
    private String isUploaded;

    public BillAddonKwh() {
    }

    public String getBillNo() {

        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getAddonKwh() {
        return addonKwh;
    }

    public void setAddonKwh(String addonKwh) {
        this.addonKwh = addonKwh;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }
}
