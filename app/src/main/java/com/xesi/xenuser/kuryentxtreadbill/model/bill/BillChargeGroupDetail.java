package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by xenuser on 2/17/2017.
 */
public class BillChargeGroupDetail {

    public BillChargeGroupDetail() {
    }

    @SerializedName("billNo")
    @Expose
    private String billNo;
    @SerializedName("printOrderMaster")
    @Expose
    private int printOrderMaster;
    @SerializedName("printOrder")
    @Expose
    private int printOrder;
    @SerializedName("chargeName")
    @Expose
    private String chargeName;
    @SerializedName("chargeAmount")
    @Expose
    private BigDecimal chargeAmount;
    @SerializedName("chargeTotal")
    @Expose
    private BigDecimal chargeTotal;
    @SerializedName("isUploaded")
    @Expose
    private int isUploaded;

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public int getPrintOrderMaster() {
        return printOrderMaster;
    }

    public void setPrintOrderMaster(int printOrderMaster) {
        this.printOrderMaster = printOrderMaster;
    }

    public int getPrintOrder() {
        return printOrder;
    }

    public void setPrintOrder(int printOrder) {
        this.printOrder = printOrder;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getChargeTotal() {
        return chargeTotal;
    }

    public void setChargeTotal(BigDecimal chargeTotal) {
        this.chargeTotal = chargeTotal;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }

    public static Comparator<BillChargeGroupDetail> SORT_BY_PRINT_ORDER = (one, other) -> one.printOrder > other.printOrder ? 1 : one.printOrder < other.printOrder ? -1 : 0;
}
