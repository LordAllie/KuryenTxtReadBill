package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xenuser on 2/20/2017.
 */
public class BillGroupCategory {

    public BillGroupCategory() {
    }

    @SerializedName("chargeType")
    @Expose
    private String chargeType;
    @SerializedName("billChargeGroupDetailList")
    @Expose
    private List<BillChargeGroupDetail> billChargeGroupDetailList;

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public List<BillChargeGroupDetail> getBillChargeGroupDetailList() {
        return billChargeGroupDetailList;
    }

    public void setBillChargeGroupDetailList(List<BillChargeGroupDetail> billChargeGroupDetailList) {
        this.billChargeGroupDetailList = billChargeGroupDetailList;
    }
}
