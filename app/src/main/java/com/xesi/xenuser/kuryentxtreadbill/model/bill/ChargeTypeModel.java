package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xenuser on 2/14/2017.
 */
public class ChargeTypeModel {

    @SerializedName("rateMasterId")
    @Expose
    private int rateMasterId;
    @SerializedName("printOrder")
    @Expose
    private int printOrder;
    @SerializedName("chargeTypeCode")
    @Expose
    private String chargeTypeCode;
    @SerializedName("chargeTypeName")
    @Expose
    private String chargeTypeName;
    @SerializedName("subtotalName")
    @Expose
    private String subtotalName;
    @SerializedName("idChargeType")
    @Expose
    private int idChargeType;

    public ChargeTypeModel() {
    }

    public int getIdRateMaster() {
        return rateMasterId;
    }

    public void setIdRateMaster(int idRateMaster) {
        this.rateMasterId = idRateMaster;
    }

    public int getPrintOrder() {
        return printOrder;
    }

    public void setPrintOrder(int printOrder) {
        this.printOrder = printOrder;
    }

    public String getChargeTypeCode() {
        return chargeTypeCode;
    }

    public void setChargeTypeCode(String chargeTypeCode) {
        this.chargeTypeCode = chargeTypeCode;
    }

    public String getChargeTypeName() {
        return chargeTypeName;
    }

    public void setChargeTypeName(String chargeTypeName) {
        this.chargeTypeName = chargeTypeName;
    }

    public String getSubtotalName() {
        return subtotalName;
    }

    public void setSubtotalName(String subtotalName) {
        this.subtotalName = subtotalName;
    }

    public int getIdChargeType() {
        return idChargeType;
    }

    public void setIdChargeType(int idChargeType) {
        this.idChargeType = idChargeType;
    }
}
