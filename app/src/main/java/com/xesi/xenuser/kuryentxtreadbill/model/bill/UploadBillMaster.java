package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daryll Sabate on 6/22/2017.
 */
public class UploadBillMaster {


    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("runDate")
    @Expose
    private String runDate;
    @SerializedName("devId")
    @Expose
    private int devId;
    @SerializedName("idReader")
    @Expose
    private int idReader;
    @SerializedName("idRdm")
    @Expose
    private int idRdm;

    public UploadBillMaster() {
    }

    public UploadBillMaster(int id, String runDate, int devId, int idReader, int idRdm) {
        this.id = id;
        this.runDate = runDate;
        this.devId = devId;
        this.idReader = idReader;
        this.idRdm = idRdm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public int getIdReader() {
        return idReader;
    }

    public void setIdReader(int idReader) {
        this.idReader = idReader;
    }

    public int getIdRdm() {
        return idRdm;
    }

    public void setIdRdm(int idRdm) {
        this.idRdm = idRdm;
    }
}
