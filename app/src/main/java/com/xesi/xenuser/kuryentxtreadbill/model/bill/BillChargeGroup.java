package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by xenuser on 2/14/2017.
 */
public class BillChargeGroup {

    public static Comparator<BillChargeGroup> SORT_BY_PRINT_ORDER = (one, other) -> one.printOrder > other.printOrder ? 1 : one.printOrder < other.printOrder ? -1 : 0;

    @SerializedName("billNumber")
    @Expose
    private String billNumber;
    @SerializedName("chargeTypeCode")
    @Expose
    private String chargeTypeCode;
    @SerializedName("chargeTypeName")
    @Expose
    private String chargeTypeName;
    @SerializedName("printOrder")
    @Expose
    private int printOrder;
    @SerializedName("subtotal")
    @Expose
    private BigDecimal subtotalCharges;

    @SerializedName("total")
    @Expose
    private BigDecimal totalCharges;

    public BillChargeGroup() {
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getChargeTypeCode() {

        return chargeTypeCode;
    }

    public String getChargeTypeName() {
        return chargeTypeName;
    }

    public void setChargeTypeName(String chargeTypeName) {
        this.chargeTypeName = chargeTypeName;
    }

    public int getPrintOrder() {
        return printOrder;
    }

    public void setPrintOrder(int printOrder) {
        this.printOrder = printOrder;
    }

    public void setChargeTypeCode(String chargeTypeCode) {
        this.chargeTypeCode = chargeTypeCode;
    }

    public BigDecimal getSubtotalCharges() {
        return subtotalCharges;
    }

    public void setSubtotalCharges(BigDecimal subtotalCharges) {
        this.subtotalCharges = subtotalCharges;
    }

    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }
}
