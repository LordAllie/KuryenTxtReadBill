package com.xesi.xenuser.kuryentxtreadbill.model;

import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;

import okhttp3.ResponseBody;

/**
 * Created by Daryll Sabate on 1/3/2018.
 */

public class DuPropertyZip {

    private RetClassGen duProperties;
    private ResponseBody logos;

    public DuPropertyZip() {
    }

    public DuPropertyZip(RetClassGen duProperties, ResponseBody logos) {
        this.duProperties = duProperties;
        this.logos = logos;
    }

    public RetClassGen getDuProperties() {
        return duProperties;
    }

    public void setDuProperties(RetClassGen duProperties) {
        this.duProperties = duProperties;
    }

    public ResponseBody getLogos() {
        return logos;
    }

    public void setLogos(ResponseBody logos) {
        this.logos = logos;
    }
}
