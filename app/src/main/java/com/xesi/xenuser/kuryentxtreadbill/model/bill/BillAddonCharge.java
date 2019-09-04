package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by xenuser on 2/16/2017.
 */
public class BillAddonCharge {

    @SerializedName("_id")
    @Expose
    private int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    @SerializedName("billNo")
    @Expose
    private String billNo;
    @SerializedName("addonCharge")
    @Expose
    private String addonCharge;
    @SerializedName("value")
    @Expose
    private double value;
    @SerializedName("isUploaded")
    @Expose
    private String isUploaded;

    public BillAddonCharge() {
    }

    public String getBillNo() {

        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getAddonCharge() {
        return addonCharge;
    }

    public void setAddonCharge(String addonKwh) {
        this.addonCharge = addonKwh;
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
