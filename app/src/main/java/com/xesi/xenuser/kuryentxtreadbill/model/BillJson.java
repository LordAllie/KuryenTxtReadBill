package com.xesi.xenuser.kuryentxtreadbill.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daryll-POGI on 08/01/2020.
 */

public class BillJson {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("billNo")
    @Expose
    private String billNo;
    @SerializedName("json")
    @Expose
    private String json;
    @SerializedName("isUploaded")
    @Expose
    private int isUploaded;
    @SerializedName("isArchived")
    @Expose
    private int isArchived;

    public BillJson(){}

    public BillJson(int id, String billNo, String json, int isUploaded, int isArchived) {
        this.id = id;
        this.billNo = billNo;
        this.json = json;
        this.isUploaded = isUploaded;
        this.isArchived = isArchived;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }

    public int getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }
}
