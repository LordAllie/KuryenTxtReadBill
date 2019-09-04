package com.xesi.xenuser.kuryentxtreadbill.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Daryll Sabate on 2/1/2018.
 */

public class UploadData {

    @SerializedName("billMasterId")
    @Expose
    private int billMasterId;
    @SerializedName("bills")
    @Expose
    private List<StringBill> bills;

    public UploadData() {
    }

    public UploadData(int billMasterId, List<StringBill> bills) {
        this.billMasterId = billMasterId;
        this.bills = bills;
    }

    public int getBillMasterId() {
        return billMasterId;
    }

    public void setBillMasterId(int billMasterId) {
        this.billMasterId = billMasterId;
    }

    public List<StringBill> getBills() {
        return bills;
    }

    public void setBills(List<StringBill> bills) {
        this.bills = bills;
    }
}
