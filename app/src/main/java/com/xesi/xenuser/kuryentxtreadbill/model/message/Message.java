package com.xesi.xenuser.kuryentxtreadbill.model.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xenuser on 2/22/2017.
 */
public class Message {
    @SerializedName("acctNo")
    @Expose
    String acctNo;
    @SerializedName("code")
    @Expose
    String code;
    @SerializedName("message")
    @Expose
    String message;
    @SerializedName("idRoute")
    @Expose
    int idRoute;

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }
}
