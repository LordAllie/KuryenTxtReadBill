package com.xesi.xenuser.kuryentxtreadbill.model.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.math.BigDecimal;


/**
 * Created by xenuser on 2/10/2017.
 */
public class BillHeader {

    @SerializedName("idBh")
    @Expose
    private int idBh;
    @SerializedName("runDate")
    @Expose
    private String runDate;
    @SerializedName("billNo")
    @Expose
    private String billNo;
    @SerializedName("oldAccountNo")
    @Expose
    private String oldAccountNo;
    @SerializedName("routeCode")
    @Expose
    private String routeCode;
    @SerializedName("sequenceNo")
    @Expose
    private int sequenceNo;
    @SerializedName("acctName")
    @Expose
    private String acctName;
    @SerializedName("meterNo")
    @Expose
    private String meterNo;
    @SerializedName("acctNo")
    @Expose
    private String acctNo;
    @SerializedName("consumerType")
    @Expose
    private String consumerType;
    @SerializedName("curReading")
    @Expose
    private double curReading;
    @SerializedName("prevReading")
    @Expose
    private double prevReading;
    @SerializedName("meterMultiplier")
    @Expose
    private double meterMultiplier;
    @SerializedName("coreloss")
    @Expose
    private double coreloss;
    @SerializedName("consumption")
    @Expose
    private double consumption;
    @SerializedName("addonKwhTotal")
    @Expose
    private double addonKwhTotal;
    @SerializedName("totalConsumption")
    @Expose
    private double totalConsumption;
    @SerializedName("periodFrom")
    @Expose
    private String periodFrom;
    @SerializedName("periodTo")
    @Expose
    private String periodTo;
    @SerializedName("billingMonth")
    @Expose
    private String billingMonth;
    @SerializedName("curBill")
    @Expose
    private String curBill;
    @SerializedName("totalAmountDue")
    @Expose
    private String totalAmountDue;
    @SerializedName("totalBillAfterDueDate")
    @Expose
    private String totalBillAfterDueDate;
    @SerializedName("reader")
    @Expose
    private String reader;
    @SerializedName("deviceId")
    @Expose
    private int deviceId;
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("isUploaded")
    @Expose
    private int isUploaded;
    @SerializedName("idRoute")
    @Expose
    private int idRoute;
    @SerializedName("discoDate")
    @Expose
    private String discoDate;
    @SerializedName("isPrinted")
    @Expose
    private int isPrinted;
    @SerializedName("minimumContractedEnergy")
    @Expose
    private double minimumContractedEnergy;
    @SerializedName("minimumContractedDemand")
    @Expose
    private double minimumContractedDemand;
    @SerializedName("previousConsumption")
    @Expose
    private double previousConsumption;
    @SerializedName("accountArrears")
    @Expose
    private double accountArrears;
    @SerializedName("arrearsAsOf")
    @Expose
    private String arrearsAsOf;
    @SerializedName("editCount")
    @Expose
    private int editCount;
    @SerializedName("isVoid")
    @Expose
    private String isVoid;
    public BillHeader() {
    }

    public BillHeader(int idBh, String runDate, String billNo, String oldAccountNo, String routeCode, int sequenceNo, String acctName, String meterNo, String acctNo, String consumerType, double curReading, double prevReading, double meterMultiplier, double coreloss, double consumption, double addonKwhTotal, double totalConsumption, String periodFrom, String periodTo, String billingMonth, String curBill, String totalAmountDue, String totalBillAfterDueDate, String reader, int deviceId, String dueDate, String remarks, int isUploaded, int idRoute, String discoDate, int isPrinted, double minimumContractedEnergy, double minimumContractedDemand, double previousConsumption, double accountArrears, String arrearsAsOf, int editCount, String isVoid) {
        this.idBh = idBh;
        this.runDate = runDate;
        this.billNo = billNo;
        this.oldAccountNo = oldAccountNo;
        this.routeCode = routeCode;
        this.sequenceNo = sequenceNo;
        this.acctName = acctName;
        this.meterNo = meterNo;
        this.acctNo = acctNo;
        this.consumerType = consumerType;
        this.curReading = curReading;
        this.prevReading = prevReading;
        this.meterMultiplier = meterMultiplier;
        this.coreloss = coreloss;
        this.consumption = consumption;
        this.addonKwhTotal = addonKwhTotal;
        this.totalConsumption = totalConsumption;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.billingMonth = billingMonth;
        this.curBill = curBill;
        this.totalAmountDue = totalAmountDue;
        this.totalBillAfterDueDate = totalBillAfterDueDate;
        this.reader = reader;
        this.deviceId = deviceId;
        this.dueDate = dueDate;
        this.remarks = remarks;
        this.isUploaded = isUploaded;
        this.idRoute = idRoute;
        this.discoDate = discoDate;
        this.isPrinted = isPrinted;
        this.minimumContractedEnergy = minimumContractedEnergy;
        this.minimumContractedDemand = minimumContractedDemand;
        this.previousConsumption = previousConsumption;
        this.accountArrears = accountArrears;
        this.arrearsAsOf = arrearsAsOf;
        this.editCount = editCount;
        this.isVoid = isVoid;
    }

    public BillHeader(String runDate, String billNo, String oldAccountNo, int sequenceNo, String acctName,
                      String meterNo, String acctNo, double curReading, String remarks) {
        this.runDate = runDate;
        this.billNo = billNo;
        this.oldAccountNo = oldAccountNo;
        this.sequenceNo = sequenceNo;
        this.acctName = acctName;
        this.meterNo = meterNo;
        this.acctNo = acctNo;
        this.curReading = curReading;
        this.remarks = remarks;
    }


    public int getIsPrinted() {
        return isPrinted;
    }

    public void setIsPrinted(int isPrinted) {
        this.isPrinted = isPrinted;
    }

    public String getDiscoDate() {
        return discoDate;
    }

    public void setDiscoDate(String discoDate) {
        this.discoDate = discoDate;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getOldAccountNo() {
        return oldAccountNo;
    }

    public void setOldAccountNo(String oldAccountNo) {
        this.oldAccountNo = oldAccountNo;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public double getCurReading() {
        return curReading;
    }

    public void setCurReading(double curReading) {
        this.curReading = curReading;
    }

    public double getPrevReading() {
        return prevReading;
    }

    public void setPrevReading(double prevReading) {
        this.prevReading = prevReading;
    }

    public double getMeterMultiplier() {
        return meterMultiplier;
    }

    public void setMeterMultiplier(double meterMultiplier) {
        this.meterMultiplier = meterMultiplier;
    }

    public double getCoreloss() {
        return coreloss;
    }

    public void setCoreloss(double coreloss) {
        this.coreloss = coreloss;
    }

    public double getTotalConsumption() {
        return totalConsumption;
    }

    public void setTotalConsumption(double totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public String getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
    }

    public String getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(String periodTo) {
        this.periodTo = periodTo;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public String getCurBill() {
        return curBill;
    }

    public void setCurBill(String curBill) {
        this.curBill = curBill;
    }

    public String getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(String totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getIdBh() {
        return idBh;
    }

    public void setIdBh(int idBh) {
        this.idBh = idBh;
    }

    public double getAddonKwhTotal() {
        return addonKwhTotal;
    }

    public void setAddonKwhTotal(double addonKwhTotal) {
        this.addonKwhTotal = addonKwhTotal;
    }

    public double getConsumption() {

        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public String getTotalBillAfterDueDate() {
        return totalBillAfterDueDate;
    }

    public void setTotalBillAfterDueDate(String totalBillAfterDueDate) {
        this.totalBillAfterDueDate = totalBillAfterDueDate;
    }

    public double getMinimumContractedEnergy() {
        return minimumContractedEnergy;
    }

    public void setMinimumContractedEnergy(double minimumContractedEnergy) {
        this.minimumContractedEnergy = minimumContractedEnergy;
    }

    public double getMinimumContractedDemand() {
        return minimumContractedDemand;
    }

    public void setMinimumContractedDemand(double minimumContractedDemand) {
        this.minimumContractedDemand = minimumContractedDemand;
    }

    public double getPreviousConsumption() {
        return previousConsumption;
    }

    public void setPreviousConsumption(double previousConsumption) {
        this.previousConsumption = previousConsumption;
    }

    public double getAccountArrears() {
        return accountArrears;
    }

    public void setAccountArrears(double accountArrears) {
        this.accountArrears = accountArrears;
    }

    public String getArrearsAsOf() {
        return arrearsAsOf;
    }

    public void setArrearsAsOf(String arrearsAsOf) {
        this.arrearsAsOf = arrearsAsOf;
    }

    public int getEditCount() {
        return editCount;
    }

    public void setEditCount(int editCount) {
        this.editCount = editCount;
    }

    public String getIsVoid() {
        return isVoid;
    }

    public void setIsVoid(String isVoid) {
        this.isVoid = isVoid;
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields())
            if (f.get(this) != null)
                return false;
        return true;
    }
}
