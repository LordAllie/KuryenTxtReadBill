package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xenuser on 4/10/2017.
 */
public class RateMaster {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("rateMasterID")
    @Expose
    private int rateMasterID;
    @SerializedName("effectivityDate")
    @Expose
    private String effectivityDate;
    @SerializedName("rateName")
    @Expose
    private String rateName;
    @SerializedName("totalFixedCharge")
    @Expose
    private double totalFixedCharge;
    @SerializedName("totalPerKwCharge")
    @Expose
    private double totalPerKwCharge;
    @SerializedName("totalPerKwChargeSTL")
    @Expose
    private double totalPerKwChargeSTL;
    @SerializedName("isSTL")
    @Expose
    private String isSTL;
    @SerializedName("isSTSCS")
    @Expose
    private String isSTSCS;
    @SerializedName("isSTSCD")
    @Expose
    private String isSTSCD;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRateMasterID() {
        return rateMasterID;
    }

    public void setRateMasterID(int rateMasterID) {
        this.rateMasterID = rateMasterID;
    }

    public String getEffectivityDate() {
        return effectivityDate;
    }

    public void setEffectivityDate(String effectivityDate) {
        this.effectivityDate = effectivityDate;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public double getTotalFixedCharge() {
        return totalFixedCharge;
    }

    public void setTotalFixedCharge(double totalFixedCharge) {
        this.totalFixedCharge = totalFixedCharge;
    }

    public double getTotalPerKwCharge() {
        return totalPerKwCharge;
    }

    public void setTotalPerKwCharge(double totalPerKwCharge) {
        this.totalPerKwCharge = totalPerKwCharge;
    }

    public double getTotalPerKwChargeSTL() {
        return totalPerKwChargeSTL;
    }

    public void setTotalPerKwChargeSTL(double totalPerKwChargeSTL) {
        this.totalPerKwChargeSTL = totalPerKwChargeSTL;
    }

    public String getIsSTL() {
        return isSTL;
    }

    public void setIsSTL(String isSTL) {
        this.isSTL = isSTL;
    }

    public String getIsSTSCS() {
        return isSTSCS;
    }

    public void setIsSTSCS(String isSTSCS) {
        this.isSTSCS = isSTSCS;
    }

    public String getIsSTSCD() {
        return isSTSCD;
    }

    public void setIsSTSCD(String isSTSCD) {
        this.isSTSCD = isSTSCD;
    }
}
