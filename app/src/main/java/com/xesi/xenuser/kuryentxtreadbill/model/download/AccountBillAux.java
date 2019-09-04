package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by Daryll-POGI on 30/05/2019.
 */

public class AccountBillAux {


    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("accountNo")
    @Expose
    private String accountNo;
    @SerializedName("chargeName")
    @Expose
    private String chargeName;
    @SerializedName("chargeAmount")
    @Expose
    private BigDecimal chargeAmount;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
}
