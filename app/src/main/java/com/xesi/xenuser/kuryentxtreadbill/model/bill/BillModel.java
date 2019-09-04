package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xenuser on 2/15/2017.
 */
public class BillModel {


    @SerializedName("billMasterId")
    @Expose
    private int billMasterId;
    @SerializedName("billHeaders")
    @Expose
    private BillHeader billHeaders;

    @SerializedName("billChargeGroups")
    @Expose
    private List<BillChargeGroup> billChargeGroups;

    @SerializedName("billAddonKwh")
    @Expose
    private List<BillAddonKwh> billAddonKwh;

    @SerializedName("billAddonCharge")
    @Expose
    private List<BillAddonCharge> billAddonCharge;

    @SerializedName("billGroupCategories")
    @Expose
    private List<BillGroupCategory> billGroupCategories;

    public List<BillAddonKwh> getBillAddonKwh() {
        return billAddonKwh;
    }

    public void setBillAddonKwh(List<BillAddonKwh> billAddonKwh) {
        this.billAddonKwh = billAddonKwh;
    }

    public List<BillChargeGroup> getBillChargeGroups() {
        return billChargeGroups;
    }

    public void setBillChargeGroups(List<BillChargeGroup> billChargeGroups) {
        this.billChargeGroups = billChargeGroups;
    }

    public BillHeader getBillHeaders() {
        return billHeaders;
    }

    public void setBillHeaders(BillHeader billHeaders) {
        this.billHeaders = billHeaders;
    }

    public List<BillAddonCharge> getBillAddonCharge() {
        return billAddonCharge;
    }

    public void setBillAddonCharge(List<BillAddonCharge> billAddonCharge) {
        this.billAddonCharge = billAddonCharge;
    }

    public List<BillGroupCategory> getBillGroupCategories() {
        return billGroupCategories;
    }

    public void setBillGroupCategories(List<BillGroupCategory> billGroupCategories) {
        this.billGroupCategories = billGroupCategories;
    }

    public int getBillMasterId() {
        return billMasterId;
    }

    public void setBillMasterId(int billMasterId) {
        this.billMasterId = billMasterId;
    }
}
