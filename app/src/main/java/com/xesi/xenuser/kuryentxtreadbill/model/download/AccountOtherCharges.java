package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daryll-POGI on 12/04/2019.
 */

public class AccountOtherCharges {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("idAccount")
    @Expose
    private long idAccount;
    @SerializedName("idCharge")
    @Expose
    private long idCharge;
    @SerializedName("accountNo")
    @Expose
    private String accountNo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(long idAccount) {
        this.idAccount = idAccount;
    }

    public long getIdCharge() {
        return idCharge;
    }

    public void setIdCharge(long idCharge) {
        this.idCharge = idCharge;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
