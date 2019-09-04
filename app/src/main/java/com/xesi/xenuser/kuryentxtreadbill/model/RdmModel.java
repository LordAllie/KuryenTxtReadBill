package com.xesi.xenuser.kuryentxtreadbill.model;

/**
 * Created by Daryll Sabate on 6/7/2017.
 */
public class RdmModel {

    int idrdm;
    int idReader;
    String readersName;
    int consumerCount;
    String isReadyForUse;

    public int getConsumerCount() {
        return consumerCount;
    }

    public void setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
    }

    public String getIsReadyForUse() {
        return isReadyForUse;
    }

    public void setIsReadyForUse(String isReadyForUse) {
        this.isReadyForUse = isReadyForUse;
    }

    public int getIdReader() {
        return idReader;
    }

    public void setIdReader(int idReader) {
        this.idReader = idReader;
    }

    public int getIdrdm() {
        return idrdm;
    }

    public void setIdrdm(int idrdm) {
        this.idrdm = idrdm;
    }

    public String getReadersName() {
        return readersName;
    }

    public void setReadersName(String readersName) {
        this.readersName = readersName;
    }
}
