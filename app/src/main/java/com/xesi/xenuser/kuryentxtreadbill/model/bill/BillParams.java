package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xenuser on 2/14/2017.
 */
public class BillParams {

    private String billNumber;
    private Date tranDate;
    private String sqNo;
    private String dueDate;
    private double multiplier;
    private double coreloss;
    private String consumerType;
    private double currentReading;
    private double prevRdg;
    private double kwhConsumption;
    private double addonKwhTotal;
    private double totalKwhConsumption;
    private BigDecimal currentBill;
    private BigDecimal bdTotalAmountBill;
    private BigDecimal bdTotalBillAfterDueDate;
    private String remarks;


    public BillParams() {
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getSqNo() {
        return sqNo;
    }

    public void setSqNo(String sqNo) {
        this.sqNo = sqNo;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getCoreloss() {
        return coreloss;
    }

    public void setCoreloss(double coreloss) {
        this.coreloss = coreloss;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public double getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(double currentReading) {
        this.currentReading = currentReading;
    }

    public double getPrevRdg() {
        return prevRdg;
    }

    public void setPrevRdg(double prevRdg) {
        this.prevRdg = prevRdg;
    }

    public double getKwhConsumption() {
        return kwhConsumption;
    }

    public void setKwhConsumption(double kwhConsumption) {
        this.kwhConsumption = kwhConsumption;
    }

    public BigDecimal getBdTotalAmountBill() {
        return bdTotalAmountBill;
    }

    public void setBdTotalAmountBill(BigDecimal bdTotalAmountBill) {
        this.bdTotalAmountBill = bdTotalAmountBill;
    }

    public double getTotalKwhConsumption() {
        return totalKwhConsumption;
    }

    public void setTotalKwhConsumption(double totalKwhConsumption) {
        this.totalKwhConsumption = totalKwhConsumption;
    }

    public BigDecimal getCurrentBill() {
        return currentBill;
    }

    public void setCurrentBill(BigDecimal currentBill) {
        this.currentBill = currentBill;
    }

    public double getAddonKwhTotal() {
        return addonKwhTotal;
    }

    public void setAddonKwhTotal(double addonKwhTotal) {
        this.addonKwhTotal = addonKwhTotal;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getBdTotalBillAfterDueDate() {
        return bdTotalBillAfterDueDate;
    }

    public void setBdTotalBillAfterDueDate(BigDecimal bdTotalBillAfterDueDate) {
        this.bdTotalBillAfterDueDate = bdTotalBillAfterDueDate;
    }

}
