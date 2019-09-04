package com.xesi.xenuser.kuryentxtreadbill.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xenuser on 1/23/2017.
 */
public class ErrorMsg {
    public String[] errMsgArr;
    String acctMsg;
    String rateMasterMsg;
    String routeDef;
    String llMaster;
    String unbundled;
    String scDiscount;
    String coreloss;
    String kwhAddon;
    String rateAddon;
    String remarks;
    String cutoffMaster;
    String fixChargedDetails;
    String lifelineDetails;
    String chargetype;
    String duProperty;
    String rateAddonSpecial;
    String routes;

    public ErrorMsg() {
    }

    public ErrorMsg(String[] errMsgArr, String acctMsg, String rateMasterMsg, String routeDef, String llMaster,
                    String unbundled, String scDiscount, String coreloss, String kwhAddon, String rateAddon,
                    String remarks, String cutoffMaster, String fixChargedDetails, String lifelineDetails,
                    String chargetype, String duProperty, String rateAddonSpecial, String routes) {
        this.errMsgArr = errMsgArr;
        this.acctMsg = acctMsg;
        this.rateMasterMsg = rateMasterMsg;
        this.routeDef = routeDef;
        this.llMaster = llMaster;
        this.unbundled = unbundled;
        this.scDiscount = scDiscount;
        this.coreloss = coreloss;
        this.kwhAddon = kwhAddon;
        this.rateAddon = rateAddon;
        this.remarks = remarks;
        this.cutoffMaster = cutoffMaster;
        this.fixChargedDetails = fixChargedDetails;
        this.lifelineDetails = lifelineDetails;
        this.chargetype = chargetype;
        this.duProperty = duProperty;
        this.rateAddonSpecial = rateAddonSpecial;
        this.routes = routes;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public String getRateAddonSpecial() {
        return rateAddonSpecial;
    }

    public void setRateAddonSpecial(String rateAddonSpecial) {
        this.rateAddonSpecial = rateAddonSpecial;
    }

    public String getChargetype() {
        return chargetype;
    }

    public void setChargetype(String chargetype) {
        this.chargetype = chargetype;
    }

    public String[] getErrMsgArr() {

        return errMsgArr;
    }

    public void setErrMsgArr(String[] errMsgArr) {
        this.errMsgArr = errMsgArr;
    }

    public String getLifelineDetails() {
        return lifelineDetails;
    }

    public void setLifelineDetails(String lifelineDetails) {
        this.lifelineDetails = lifelineDetails;
    }

    public String getFixChargedDetails() {
        return fixChargedDetails;
    }

    public void setFixChargedDetails(String fixChargedDetails) {
        this.fixChargedDetails = fixChargedDetails;
    }

    public String getCutoffMaster() {
        return cutoffMaster;
    }

    public void setCutoffMaster(String cutoffMaster) {
        this.cutoffMaster = cutoffMaster;
    }

    public ErrorMsg(String[] errMsgArr) {
        this.errMsgArr = errMsgArr;
    }

    public List<String> getErrMsgList() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(this.errMsgArr));
        return arrayList;
    }

    public String getRouteDef() {
        return routeDef;
    }

    public void setRouteDef(String routeDef) {
        this.routeDef = routeDef;
    }

    public String getLlMaster() {
        return llMaster;
    }

    public void setLlMaster(String llMaster) {
        this.llMaster = llMaster;
    }

    public String getUnbundled() {
        return unbundled;
    }

    public void setUnbundled(String unbundled) {
        this.unbundled = unbundled;
    }

    public String getScDiscount() {
        return scDiscount;
    }

    public void setScDiscount(String scDiscount) {
        this.scDiscount = scDiscount;
    }

    public String getCoreloss() {
        return coreloss;
    }

    public void setCoreloss(String coreloss) {
        this.coreloss = coreloss;
    }

    public String getKwhAddon() {
        return kwhAddon;
    }

    public void setKwhAddon(String kwhAddon) {
        this.kwhAddon = kwhAddon;
    }

    public String getRateAddon() {
        return rateAddon;
    }

    public void setRateAddon(String rateAddon) {
        this.rateAddon = rateAddon;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRateMasterMsg() {
        return rateMasterMsg;
    }

    public void setRateMasterMsg(String rateMasterMsg) {
        this.rateMasterMsg = rateMasterMsg;
    }


    public String getAcctMsg() {
        return acctMsg;
    }

    public void setAcctMsg(String acctMsg) {
        this.acctMsg = acctMsg;
    }

    public String getDuProperty() {
        return duProperty;
    }

    public void setDuProperty(String duProperty) {
        this.duProperty = duProperty;
    }
}
