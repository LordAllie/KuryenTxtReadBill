package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xenuser on 4/10/2017.
 */
public class RatePerKwChargeDetail {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("chargeType")
    @Expose
    private String chargeType;
    @SerializedName("perKwRateName")
    @Expose
    private String perKwRateName;
    @SerializedName("printOrder")
    @Expose
    private int printOrder;
    @SerializedName("rateId")
    @Expose
    private int rateId;
    @SerializedName("totalAmount")
    @Expose
    private double totalAmount;
    @SerializedName("fixedAddtl")
    @Expose
    private double fixedAddtl;
    @SerializedName("adjToLifeline")
    @Expose
    private double adjToLifeline;
    @SerializedName("adjToSc")
    @Expose
    private double adjToSc;
    @SerializedName("isSubToSurcharge")
    @Expose
    private String isSubToSurcharge;
    @SerializedName("isSubjectToLifeLine")
    @Expose
    private String isSubjectToLifeLine;
    @SerializedName("isOffIfSenior")
    @Expose
    private String isOffIfSenior;
    @SerializedName("isOffIfLifeliner")
    @Expose
    private String isOffIfLifeliner;

    public RatePerKwChargeDetail() {
    }

    public RatePerKwChargeDetail(int id, String chargeType, String perKwRateName, int printOrder, int rateId, double totalAmount, double fixedAddtl, double adjToLifeline, double adjToSc, String isSubToSurcharge, String isSubjectToLifeLine, String isOffIfSenior, String isOffIfLifeliner) {
        this.id = id;
        this.chargeType = chargeType;
        this.perKwRateName = perKwRateName;
        this.printOrder = printOrder;
        this.rateId = rateId;
        this.totalAmount = totalAmount;
        this.fixedAddtl = fixedAddtl;
        this.adjToLifeline = adjToLifeline;
        this.adjToSc = adjToSc;
        this.isSubToSurcharge = isSubToSurcharge;
        this.isSubjectToLifeLine = isSubjectToLifeLine;
        this.isOffIfSenior = isOffIfSenior;
        this.isOffIfLifeliner = isOffIfLifeliner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getPerKwRateName() {
        return perKwRateName;
    }

    public void setPerKwRateName(String perKwRateName) {
        this.perKwRateName = perKwRateName;
    }

    public int getPrintOrder() {
        return printOrder;
    }

    public void setPrintOrder(int printOrder) {
        this.printOrder = printOrder;
    }

    public int getRateId() {
        return rateId;
    }

    public void setRateId(int rateId) {
        this.rateId = rateId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getFixedAddtl() {
        return fixedAddtl;
    }

    public void setFixedAddtl(double fixedAddtl) {
        this.fixedAddtl = fixedAddtl;
    }

    public double getAdjToLifeline() {
        return adjToLifeline;
    }

    public void setAdjToLifeline(double adjToLifeline) {
        this.adjToLifeline = adjToLifeline;
    }

    public double getAdjToSc() {
        return adjToSc;
    }

    public void setAdjToSc(double adjToSc) {
        this.adjToSc = adjToSc;
    }

    public String getIsSubToSurcharge() {
        return isSubToSurcharge;
    }

    public void setIsSubToSurcharge(String isSubToSurcharge) {
        this.isSubToSurcharge = isSubToSurcharge;
    }

    public String getIsSubjectToLifeLine() {
        return isSubjectToLifeLine;
    }

    public void setIsSubjectToLifeLine(String isSubjectToLifeLine) {
        this.isSubjectToLifeLine = isSubjectToLifeLine;
    }

    public String getIsOffIfSenior() {
        return isOffIfSenior;
    }

    public void setIsOffIfSenior(String isOffIfSenior) {
        this.isOffIfSenior = isOffIfSenior;
    }

    public String getIsOffIfLifeliner() {
        return isOffIfLifeliner;
    }

    public void setIsOffIfLifeliner(String isOffIfLifeliner) {
        this.isOffIfLifeliner = isOffIfLifeliner;
    }
}
