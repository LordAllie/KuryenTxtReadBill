package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by Daryll Sabate on 8/13/2018.
 */

public class DuAreaRate {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("idArea")
    @Expose
    private long idArea;
    @SerializedName("idRateMaster")
    @Expose
    private long idRateMaster;
    @SerializedName("localTax")
    @Expose
    private BigDecimal localTax;
    @SerializedName("franchiseTax")
    @Expose
    private BigDecimal franchiseTax;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdArea() {
        return idArea;
    }

    public void setIdArea(long idArea) {
        this.idArea = idArea;
    }

    public long getIdRateMaster() {
        return idRateMaster;
    }

    public void setIdRateMaster(long idRateMaster) {
        this.idRateMaster = idRateMaster;
    }

    public BigDecimal getLocalTax() {
        return localTax;
    }

    public void setLocalTax(BigDecimal localTax) {
        this.localTax = localTax;
    }

    public BigDecimal getFranchiseTax() {
        return franchiseTax;
    }

    public void setFranchiseTax(BigDecimal franchiseTax) {
        this.franchiseTax = franchiseTax;
    }
}
