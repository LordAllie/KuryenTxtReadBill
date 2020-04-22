package com.xesi.xenuser.kuryentxtreadbill.model.download;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by xenuser on 4/10/2017.
 */
public class AccountModelV2 implements Parcelable {

    public static final Creator<AccountModelV2> CREATOR = new Creator<AccountModelV2>() {
        @Override
        public AccountModelV2 createFromParcel(Parcel in) {
            return new AccountModelV2(in);
        }

        @Override
        public AccountModelV2[] newArray(int size) {
            return new AccountModelV2[size];
        }
    };

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("idRateMaster")
    @Expose
    private int idRateMaster;
    @SerializedName("idRoute")
    @Expose
    private int idRoute;
    @SerializedName("idRDM")
    @Expose
    private int idRDM;
    @SerializedName("idArea")
    @Expose
    private long idArea;
    @SerializedName("sequenceNumber")
    @Expose
    private int sequenceNumber;
    @SerializedName("oldSequenceNumber")
    @Expose
    private int oldSequenceNumber;
    @SerializedName("oldAccountNumber")
    @Expose
    private String oldAccountNumber;
    @SerializedName("accountNumber")
    @Expose
    private String accountNumber;
    @SerializedName("meterNumber")
    @Expose
    private String meterNumber;
    @SerializedName("accountName")
    @Expose
    private String accountName;
    @SerializedName("addressLn1")
    @Expose
    private String addressLn1;
    @SerializedName("addressLn2")
    @Expose
    private String addressLn2;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("emailAdd")
    @Expose
    private String emailAdd;
    @SerializedName("sin")
    @Expose
    private String sin;
    @SerializedName("serialNo")
    @Expose
    private String serialNo;
    @SerializedName("routeCode")
    @Expose
    private String routeCode;
    @SerializedName("previousBill")
    @Expose
    private BigDecimal previousBill;
    @SerializedName("lastPaymentDate")
    @Expose
    private String lastPaymentDate;
    @SerializedName("lastPaymentAmt")
    @Expose
    private BigDecimal lastPaymentAmt;
    @SerializedName("lastDepositPayment")
    @Expose
    private BigDecimal lastDepositPayment;
    @SerializedName("lastDepositPaymentDate")
    @Expose
    private String lastDepositPaymentDate;
    @SerializedName("billDepositInterest")
    @Expose
    private BigDecimal billDepositInterest;
    @SerializedName("billDeposit")
    @Expose
    private BigDecimal billDeposit;
    @SerializedName("totalBillDeposit")
    @Expose
    private BigDecimal totalBillDeposit;
    @SerializedName("c01")
    @Expose
    private BigDecimal c01;
    @SerializedName("c02")
    @Expose
    private BigDecimal c02;
    @SerializedName("c03")
    @Expose
    private BigDecimal c03;
    @SerializedName("c04")
    @Expose
    private BigDecimal c04;
    @SerializedName("c05")
    @Expose
    private BigDecimal c05;
    @SerializedName("c06")
    @Expose
    private BigDecimal c06;
    @SerializedName("c07")
    @Expose
    private BigDecimal c07;
    @SerializedName("c08")
    @Expose
    private BigDecimal c08;
    @SerializedName("c09")
    @Expose
    private BigDecimal c09;
    @SerializedName("c10")
    @Expose
    private BigDecimal c10;
    @SerializedName("c11")
    @Expose
    private BigDecimal c11;
    @SerializedName("c12")
    @Expose
    private BigDecimal c12;
    @SerializedName("currentReading")
    @Expose
    private double currentReading;
    @SerializedName("currentConsumption")
    @Expose
    private double currentConsumption;
    @SerializedName("moAvgConsumption")
    @Expose
    private double moAvgConsumption;
    @SerializedName("stopMeterFixedConsumption")
    @Expose
    private double stopMeterFixedConsumption;
    @SerializedName("meterMultiplier")
    @Expose
    private double meterMultiplier;
    @SerializedName("isSeniorCitizen")
    @Expose
    private String isSeniorCitizen;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("isRead")
    @Expose
    private int isRead;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("minimumContractedEnergy")
    @Expose
    private double minimumContractedEnergy;
    @SerializedName("minimumContractedDemand")
    @Expose
    private double minimumContractedDemand;
    @SerializedName("arrears")
    @Expose
    private double arrears;
    @SerializedName("arrearsAsOf")
    @Expose
    private String arrearsAsOf;
    @SerializedName("isForAverage")
    @Expose
    private String isForAverage;
    @SerializedName("autoComputeMode")
    @Expose
    private int autoComputeMode;
    @SerializedName("soaFooter")
    @Expose
    private String soaFooter;

    public AccountModelV2() {

    }

    public AccountModelV2(int id, int idRateMaster, int idRoute, int idRDM, long idArea, int sequenceNumber, int oldSequenceNumber, String oldAccountNumber, String accountNumber, String meterNumber, String accountName, String addressLn1, String addressLn2, String mobileNo, String emailAdd, String sin, String serialNo, String routeCode, BigDecimal previousBill, String lastPaymentDate, BigDecimal lastPaymentAmt, BigDecimal lastDepositPayment, String lastDepositPaymentDate, BigDecimal billDepositInterest, BigDecimal billDeposit, BigDecimal totalBillDeposit, BigDecimal c01, BigDecimal c02, BigDecimal c03, BigDecimal c04, BigDecimal c05, BigDecimal c06, BigDecimal c07, BigDecimal c08, BigDecimal c09, BigDecimal c10, BigDecimal c11, BigDecimal c12, double currentReading, double currentConsumption, double moAvgConsumption, double stopMeterFixedConsumption, double meterMultiplier, String isSeniorCitizen, String isActive, int isRead, String remarks, double minimumContractedEnergy, double minimumContractedDemand, double arrears, String arrearsAsOf, String isForAverage, int autoComputeMode, String soaFooter) {
        this.id = id;
        this.idRateMaster = idRateMaster;
        this.idRoute = idRoute;
        this.idRDM = idRDM;
        this.idArea = idArea;
        this.sequenceNumber = sequenceNumber;
        this.oldSequenceNumber = oldSequenceNumber;
        this.oldAccountNumber = oldAccountNumber;
        this.accountNumber = accountNumber;
        this.meterNumber = meterNumber;
        this.accountName = accountName;
        this.addressLn1 = addressLn1;
        this.addressLn2 = addressLn2;
        this.mobileNo = mobileNo;
        this.emailAdd = emailAdd;
        this.sin = sin;
        this.serialNo = serialNo;
        this.routeCode = routeCode;
        this.previousBill = previousBill;
        this.lastPaymentDate = lastPaymentDate;
        this.lastPaymentAmt = lastPaymentAmt;
        this.lastDepositPayment = lastDepositPayment;
        this.lastDepositPaymentDate = lastDepositPaymentDate;
        this.billDepositInterest = billDepositInterest;
        this.billDeposit = billDeposit;
        this.totalBillDeposit = totalBillDeposit;
        this.c01 = c01;
        this.c02 = c02;
        this.c03 = c03;
        this.c04 = c04;
        this.c05 = c05;
        this.c06 = c06;
        this.c07 = c07;
        this.c08 = c08;
        this.c09 = c09;
        this.c10 = c10;
        this.c11 = c11;
        this.c12 = c12;
        this.currentReading = currentReading;
        this.currentConsumption = currentConsumption;
        this.moAvgConsumption = moAvgConsumption;
        this.stopMeterFixedConsumption = stopMeterFixedConsumption;
        this.meterMultiplier = meterMultiplier;
        this.isSeniorCitizen = isSeniorCitizen;
        this.isActive = isActive;
        this.isRead = isRead;
        this.remarks = remarks;
        this.minimumContractedEnergy = minimumContractedEnergy;
        this.minimumContractedDemand = minimumContractedDemand;
        this.arrears = arrears;
        this.arrearsAsOf = arrearsAsOf;
        this.isForAverage = isForAverage;
        this.autoComputeMode = autoComputeMode;
        this.soaFooter = soaFooter;
    }

    protected AccountModelV2(Parcel in) {
        id = checkNull(in.readInt());
        idRateMaster = checkNull(in.readInt());
        idRoute = checkNull(in.readInt());
        idRDM = checkNull(in.readInt());
        idArea = in.readLong();
        sequenceNumber = checkNull(in.readInt());
        oldSequenceNumber = checkNull(in.readInt());
        oldAccountNumber = checkNull(in.readString());
        accountNumber = checkNull(in.readString());
        meterNumber = checkNull(in.readString());
        accountName = checkNull(in.readString());
        addressLn1 = checkNull(in.readString());
        addressLn2 = checkNull(in.readString());
        mobileNo = checkNull(in.readString());
        emailAdd = checkNull(in.readString());
        sin = checkNull(in.readString());
        serialNo = checkNull(in.readString());
        routeCode = checkNull(in.readString());
        previousBill = new BigDecimal(checkNullToBigDecimal(in.readString()));
        lastPaymentDate = checkNull(in.readString());
        lastPaymentAmt = new BigDecimal(checkNullToBigDecimal(in.readString()));
        lastDepositPayment = new BigDecimal(checkNullToBigDecimal(in.readString()));
        lastDepositPaymentDate = checkNull(in.readString());
        billDepositInterest = new BigDecimal(checkNullToBigDecimal(in.readString()));
        billDeposit = new BigDecimal(checkNullToBigDecimal(in.readString()));
        totalBillDeposit = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c01 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c02 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c03 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c04 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c05 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c06 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c07 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c08 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c09 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c10 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c11 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        c12 = new BigDecimal(checkNullToBigDecimal(in.readString()));
        currentReading = checkNull(in.readDouble());
        currentConsumption = checkNull(in.readDouble());
        moAvgConsumption = checkNull(in.readDouble());
        stopMeterFixedConsumption = checkNull(in.readDouble());
        meterMultiplier = checkNull(in.readDouble());
        isSeniorCitizen = checkNull(in.readString());
        isActive = checkNull(in.readString());
        isRead = in.readInt();
        remarks = checkNull(in.readString());
        minimumContractedEnergy =  checkNull(in.readDouble());
        minimumContractedDemand =  checkNull(in.readDouble());
        arrears = checkNull(in.readDouble());
        arrearsAsOf =checkNull(in.readString());
        isForAverage =checkNull(in.readString());
        autoComputeMode =checkNull(in.readInt());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idRateMaster);
        dest.writeInt(idRoute);
        dest.writeInt(idRDM);
        dest.writeLong(idArea);
        dest.writeInt(sequenceNumber);
        dest.writeInt(oldSequenceNumber);
        dest.writeString(oldAccountNumber);
        dest.writeString(accountNumber);
        dest.writeString(meterNumber);
        dest.writeString(accountName);
        dest.writeString(addressLn1);
        dest.writeString(addressLn2);
        dest.writeString(mobileNo);
        dest.writeString(emailAdd);
        dest.writeString(sin);
        dest.writeString(serialNo);
        dest.writeString(routeCode);
        dest.writeString(previousBill.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(lastPaymentDate);
        dest.writeString(lastPaymentAmt.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(lastDepositPayment.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(lastDepositPaymentDate);
        dest.writeString(billDepositInterest.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(billDeposit.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(totalBillDeposit.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c01.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c02.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c03.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c04.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c05.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c06.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c07.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c08.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c09.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c10.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c11.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeString(c12.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        dest.writeDouble(currentReading);
        dest.writeDouble(currentConsumption);
        dest.writeDouble(moAvgConsumption);
        dest.writeDouble(stopMeterFixedConsumption);
        dest.writeDouble(meterMultiplier);
        dest.writeString(isSeniorCitizen);
        dest.writeString(isActive);
        dest.writeInt(isRead);
        dest.writeString(remarks);
        dest.writeDouble(minimumContractedEnergy);
        dest.writeDouble(minimumContractedDemand);
        dest.writeDouble(arrears);
        dest.writeString(arrearsAsOf);
        dest.writeString(isForAverage);
        dest.writeInt(autoComputeMode);

    }

    private String checkNull(String s) {
        return (s == null) ? "N/A" : s;
    }

    private int checkNull(int s) {
        return s;
    }

    private double checkNull(double s) {
        return s;
    }

    private String checkNullToBigDecimal(String s) {
        return (s == null || s.equals("")) ? "0" : s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRateMaster() {
        return idRateMaster;
    }

    public void setIdRateMaster(int idRateMaster) {
        this.idRateMaster = idRateMaster;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public int getIdRDM() {
        return idRDM;
    }

    public void setIdRDM(int idRDM) {
        this.idRDM = idRDM;
    }

    public long getIdArea() {
        return idArea;
    }

    public void setIdArea(long idArea) {
        this.idArea = idArea;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getOldSequenceNumber() {
        return oldSequenceNumber;
    }

    public void setOldSequenceNumber(int oldSequenceNumber) {
        this.oldSequenceNumber = oldSequenceNumber;
    }

    public String getOldAccountNumber() {
        return oldAccountNumber;
    }

    public void setOldAccountNumber(String oldAccountNumber) {
        this.oldAccountNumber = oldAccountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAddressLn1() {
        return addressLn1;
    }

    public void setAddressLn1(String addressLn1) {
        this.addressLn1 = addressLn1;
    }

    public String getAddressLn2() {
        return addressLn2;
    }

    public void setAddressLn2(String addressLn2) {
        this.addressLn2 = addressLn2;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailAdd() {
        return emailAdd;
    }

    public void setEmailAdd(String emailAdd) {
        this.emailAdd = emailAdd;
    }

    public String getSin() {
        return sin;
    }

    public void setSin(String sin) {
        this.sin = sin;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public BigDecimal getPreviousBill() {
        return previousBill;
    }

    public void setPreviousBill(BigDecimal previousBill) {
        this.previousBill = previousBill;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public BigDecimal getLastPaymentAmt() {
        return lastPaymentAmt;
    }

    public void setLastPaymentAmt(BigDecimal lastPaymentAmt) {
        this.lastPaymentAmt = lastPaymentAmt;
    }

    public BigDecimal getLastDepositPayment() {
        return lastDepositPayment;
    }

    public void setLastDepositPayment(BigDecimal lastDepositPayment) {
        this.lastDepositPayment = lastDepositPayment;
    }

    public String getLastDepositPaymentDate() {
        return lastDepositPaymentDate;
    }

    public void setLastDepositPaymentDate(String lastDepositPaymentDate) {
        this.lastDepositPaymentDate = lastDepositPaymentDate;
    }

    public BigDecimal getBillDepositInterest() {
        return billDepositInterest;
    }

    public void setBillDepositInterest(BigDecimal billDepositInterest) {
        this.billDepositInterest = billDepositInterest;
    }

    public BigDecimal getBillDeposit() {
        return billDeposit;
    }

    public void setBillDeposit(BigDecimal billDeposit) {
        this.billDeposit = billDeposit;
    }

    public BigDecimal getTotalBillDeposit() {
        return totalBillDeposit;
    }

    public void setTotalBillDeposit(BigDecimal totalBillDeposit) {
        this.totalBillDeposit = totalBillDeposit;
    }

    public BigDecimal getC01() {
        return c01;
    }

    public void setC01(BigDecimal c01) {
        this.c01 = c01;
    }

    public BigDecimal getC02() {
        return c02;
    }

    public void setC02(BigDecimal c02) {
        this.c02 = c02;
    }

    public BigDecimal getC03() {
        return c03;
    }

    public void setC03(BigDecimal c03) {
        this.c03 = c03;
    }

    public BigDecimal getC04() {
        return c04;
    }

    public void setC04(BigDecimal c04) {
        this.c04 = c04;
    }

    public BigDecimal getC05() {
        return c05;
    }

    public void setC05(BigDecimal c05) {
        this.c05 = c05;
    }

    public BigDecimal getC06() {
        return c06;
    }

    public void setC06(BigDecimal c06) {
        this.c06 = c06;
    }

    public BigDecimal getC07() {
        return c07;
    }

    public void setC07(BigDecimal c07) {
        this.c07 = c07;
    }

    public BigDecimal getC08() {
        return c08;
    }

    public void setC08(BigDecimal c08) {
        this.c08 = c08;
    }

    public BigDecimal getC09() {
        return c09;
    }

    public void setC09(BigDecimal c09) {
        this.c09 = c09;
    }

    public BigDecimal getC10() {
        return c10;
    }

    public void setC10(BigDecimal c10) {
        this.c10 = c10;
    }

    public BigDecimal getC11() {
        return c11;
    }

    public void setC11(BigDecimal c11) {
        this.c11 = c11;
    }

    public BigDecimal getC12() {
        return c12;
    }

    public void setC12(BigDecimal c12) {
        this.c12 = c12;
    }

    public double getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(double currentReading) {
        this.currentReading = currentReading;
    }

    public double getMoAvgConsumption() {
        return moAvgConsumption;
    }

    public void setMoAvgConsumption(double moAvgConsumption) {
        this.moAvgConsumption = moAvgConsumption;
    }

    public double getStopMeterFixedConsumption() {
        return stopMeterFixedConsumption;
    }

    public void setStopMeterFixedConsumption(double stopMeterFixedConsumption) {
        this.stopMeterFixedConsumption = stopMeterFixedConsumption;
    }

    public double getMeterMultiplier() {
        return meterMultiplier;
    }

    public void setMeterMultiplier(double meterMultiplier) {
        this.meterMultiplier = meterMultiplier;
    }

    public String getIsSeniorCitizen() {
        return isSeniorCitizen;
    }

    public void setIsSeniorCitizen(String isSeniorCitizen) {
        this.isSeniorCitizen = isSeniorCitizen;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public double getCurrentConsumption() {
        return currentConsumption;
    }

    public void setCurrentConsumption(double currentConsumption) {
        this.currentConsumption = currentConsumption;
    }


    public double getArrears() {
        return arrears;
    }

    public void setArrears(double arrears) {
        this.arrears = arrears;
    }

    public String getArrearsAsOf() {
        return arrearsAsOf;
    }

    public void setArrearsAsOf(String arrearsAsOf) {
        this.arrearsAsOf = arrearsAsOf;
    }

    public String getIsForAverage() {
        return isForAverage;
    }

    public void setIsForAverage(String isForAverage) {
        this.isForAverage = isForAverage;
    }

    public int getAutoComputeMode() {
        return autoComputeMode;
    }

    public void setAutoComputeMode(int autoComputeMode) {
        this.autoComputeMode = autoComputeMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getSoaFooter() {
        return soaFooter;
    }

    public void setSoaFooter(String soaFooter) {
        this.soaFooter = soaFooter;
    }
}

