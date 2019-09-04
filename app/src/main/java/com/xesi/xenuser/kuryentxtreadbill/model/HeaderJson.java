package com.xesi.xenuser.kuryentxtreadbill.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroup;

import java.util.List;

/**
 * Created by Daryll-POGI on 28/08/2019.
 */

public class HeaderJson {

    private String device_id;
    private String rdm_id;
    private String reader_id;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getRdm_id() {
        return rdm_id;
    }

    public void setRdm_id(String rdm_id) {
        this.rdm_id = rdm_id;
    }

    public String getReader_id() {
        return reader_id;
    }

    public void setReader_id(String reader_id) {
        this.reader_id = reader_id;
    }
}
