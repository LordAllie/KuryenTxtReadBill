package com.xesi.xenuser.kuryentxtreadbill.model.download;

/**
 * Created by xenuser on 11/22/2016.
 */
public class RemarksDetails {

    public String id = "";
    public String remarks = "";

    public RemarksDetails() {
    }

    public RemarksDetails(String id, String remarks) {
        this.id = id;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
