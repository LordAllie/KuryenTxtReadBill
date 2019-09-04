package com.xesi.xenuser.kuryentxtreadbill.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountOtherChargesDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.KwhAddOnTranDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.OtherChargesDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.RateAddOnTranDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.RateMasterDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.RatePerKwChargeDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.SurchargeDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillAddonChargeDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillAddonKwhDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillChargeGroupDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillChargeGroupDetailDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillSurchargeDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.ChargeTypeDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.HeaderJson;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonCharge;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonKwh;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroup;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroupDetail;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillGroupCategory;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillModel;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillParams;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillSurcharge;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.ChargeTypeModel;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountOtherCharges;
import com.xesi.xenuser.kuryentxtreadbill.model.download.OtherCharges;
import com.xesi.xenuser.kuryentxtreadbill.model.download.Surcharge;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by xenuser on 1/17/2017.
 */
public class BillGeneration extends BaseDAO {
    public static final String APP_PROPERTY_SETTING = "app_config";
    private GenericDao genericDao;
    private static final int DB_VERSION = 1;
    private static String DB_NAME = "read&bill.db";
    private final Context mContext;
    private SharedPreferences sharedPref;
    private ComputeConsumption computeConsumption;
    private BillParams billParams;
    private BillToDB billToDb;
    private KwhAddOnTranDao kwhAddOnTranDao;
    private RateAddOnTranDao rateAddOnTranDao;
    private RateMasterDao rateMasterDao;
    private DUPropertyDAO duPropertyDAO;
    private RatePerKwChargeDao ratePerKwChargeDao;
    private BillHeaderDAO billHeaderDAO;
    private ChargeTypeDAO chargeTypeDAO;
    private BillChargeGroupDAO billChargeGroupDAO;
    private BillChargeGroupDetailDAO billChargeGroupDetailDAO;
    private BillAddonKwhDAO billAddonKwhDAO;
    private BillAddonChargeDAO billAddonChargeDAO;
    private SurchargeDao surchargeDao;
    private BillSurchargeDao billSurchargeDao;
    private AccountOtherChargesDao accountOtherChargesDao;
    private OtherChargesDao otherChargesDao;
    private String deviceId;
    private String isSenior;
    private boolean isSaved;
    private String isSTL, isSTSCS, isSTSCD;
    private double iTotalKwhAddOnTran;
    private double consumption;
    private double totalConsumption;
    private double coreLossKWH;
    private double stopMeterFixedConsumption;
    private BigDecimal bdSCDiscount, lifeLineDiscount, llDiscRate,
            bdSubToLifelineRate;
    private List<BillAddonCharge> billAddonChargeList;
    private List<ChargeTypeModel> chargeTypeObj;
    private String duCode;
    private int addonCount;
    private boolean isLifeliner;
    private int lastChargeTypePrintOrder;
    private int lastPrintOrder;
    private String isForTrancated;
    private String scsRounding,scdRounding,llsRounding,lldRounding;
    private List<BillChargeGroup> billChargeGroupList;
    private BillChargeGroup lastBillChargeGroup;
    private DecimalFormat df = new DecimalFormat("#0.00");
    private String retValue = "0";
    private String totalBillAfterDue = "0.00";
    private BigDecimal surchargeValue = new BigDecimal(0);
    private BigDecimal totalBillAfterDueDate;
    private double multiplier;
    private UniversalHelper helper;
    private BigDecimal scSubDisc = new BigDecimal("0");
    private BigDecimal lifeRateSub = new BigDecimal("0");
    private String effectivityDate;
    private BigDecimal forVatOtherVal,forVatDist;
    private String sorecoII = "SORECO II",aleco = "ALECO",apec = "APEC";
    public BillGeneration(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
        sharedPref = mContext.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
    }

    private void initializeVariable() {
        forVatOtherVal = new BigDecimal("0");
        forVatDist = new BigDecimal("0");
        isForTrancated = "N";
        lastPrintOrder = 0;
        lastChargeTypePrintOrder = 0;
        billAddonChargeList = new ArrayList<>();
        lifeLineDiscount = new BigDecimal(0);
        llDiscRate = new BigDecimal(0);
        bdSCDiscount = new BigDecimal(0);
        totalBillAfterDueDate = new BigDecimal(0);
        iTotalKwhAddOnTran = 0;
        llDiscRate = new BigDecimal(0);
        bdSubToLifelineRate = new BigDecimal(0);
        multiplier = 1.00;
        consumption = 0.00;
        totalConsumption = 0;
        stopMeterFixedConsumption = 0.00;
        coreLossKWH = 0;
        addonCount = 0;
        isSenior = "N";
        isLifeliner = false;
        duCode = duPropertyDAO.getPropertyValue("DU_CODE");
        if (duCode.equals("-1"))
            duCode = "DUCODE";
        chargeTypeObj = new ArrayList<>();
    }

    public synchronized void close() {
        genericDao.close();
        otherChargesDao.close();
        accountOtherChargesDao.close();
        rateMasterDao.close();
        ratePerKwChargeDao.close();
        kwhAddOnTranDao.close();
        rateAddOnTranDao.close();
        billHeaderDAO.close();
        chargeTypeDAO.close();
        billChargeGroupDAO.close();
        billChargeGroupDetailDAO.close();
        billToDb.close();
        billAddonKwhDAO.close();
        billAddonChargeDAO.close();
        duPropertyDAO.close();
        surchargeDao.close();
        billSurchargeDao.close();
        super.close();
    }

    public void instantiateDb() {
        genericDao = new GenericDao(mContext);
        surchargeDao = new SurchargeDao(mContext);
        billSurchargeDao = new BillSurchargeDao(mContext);
        otherChargesDao = new OtherChargesDao(mContext);
        accountOtherChargesDao = new AccountOtherChargesDao(mContext);
        billParams = new BillParams();
        billToDb = new BillToDB(mContext);
        billToDb.instantiateDb();
        kwhAddOnTranDao = new KwhAddOnTranDao(mContext);
        rateAddOnTranDao = new RateAddOnTranDao(mContext);
        rateMasterDao = new RateMasterDao(mContext);
        ratePerKwChargeDao = new RatePerKwChargeDao(mContext);
        billHeaderDAO = new BillHeaderDAO(mContext);
        billHeaderDAO.instantiateDb();
        chargeTypeDAO = new ChargeTypeDAO(mContext);
        billChargeGroupDAO = new BillChargeGroupDAO(mContext);
        billAddonKwhDAO = new BillAddonKwhDAO(mContext);
        billAddonChargeDAO = new BillAddonChargeDAO(mContext);
        billChargeGroupDetailDAO = new BillChargeGroupDetailDAO(mContext);
        duPropertyDAO = new DUPropertyDAO(mContext);
        helper = new UniversalHelper(mContext);
    }

    public String readingComputation(AccountModelV2 account, double currentReading, String consumerType,
                                     boolean isForUpdate, double previousReading, double dKwhConsumption) {

        initializeVariable();
        if(account.getMinimumContractedEnergy() > 0)
            if (dKwhConsumption < account.getMinimumContractedEnergy()) dKwhConsumption = account.getMinimumContractedEnergy();
        List<Surcharge> surchargeList = surchargeDao.getAll();
        String remarks = sharedPref.getString("remarks", "");
        deviceId = sharedPref.getString("authID", "");
        String newBillNumber = billToDb.generateBillNo(deviceId, account.getOldAccountNumber());
        computeConsumption = new ComputeConsumption();
        multiplier = account.getMeterMultiplier();
        isSTL = genericDao.getOneField("isSTL","arm_ratemaster","WHERE rateMasterID = ", String.valueOf(account.getIdRateMaster()),"","");
        isSTSCS = genericDao.getOneField("isSTSCS","arm_ratemaster","WHERE rateMasterID = ", String.valueOf(account.getIdRateMaster()),"","");
        isSTSCD = genericDao.getOneField("isSTSCD","arm_ratemaster","WHERE rateMasterID = ", String.valueOf(account.getIdRateMaster()),"","");
        effectivityDate = genericDao.getOneField("effectivityDate","arm_ratemaster","WHERE rateMasterID = ", String.valueOf(account.getIdRateMaster()),"","");
        isSaved = false;
        consumption = dKwhConsumption;
        stopMeterFixedConsumption = account.getStopMeterFixedConsumption();
        iTotalKwhAddOnTran = Double.parseDouble(genericDao.getOneField("SUM(kwh)","arm_kwhaddontran","WHERE accountNumber = ",account.getOldAccountNumber(),"","0"));
        if (consumption == 0)
            iTotalKwhAddOnTran += stopMeterFixedConsumption;
        totalConsumption = computeConsumption.computeInitialConsumption(consumption,
                multiplier, iTotalKwhAddOnTran);
        if (Integer.parseInt(genericDao.getOneField("COUNT(accountNumber)","arm_coreloss_tran","WHERE accountNumber = ",account.getOldAccountNumber(),"","0")) >= 1) {
            double coreLossLimitKWH = Double.parseDouble(genericDao.getOneField("coreLossLimitKWH","arm_coreloss_tran","","","LIMIT 1","0.00"));
            coreLossKWH = Double.parseDouble(genericDao.getOneField("coreLossKWH","arm_coreloss_tran","WHERE accountNumber = ",account.getOldAccountNumber(),"","0.00"));
            BigDecimal bdCoreLossLimitKWH = new BigDecimal(coreLossLimitKWH);
            BigDecimal bdConsumption = new BigDecimal(consumption);
            if (bdConsumption.compareTo(bdCoreLossLimitKWH) == -1) totalConsumption = computeConsumption.computeCoreloss(totalConsumption, coreLossKWH);
        }
        billAddonChargeList = rateAddOnTranDao.getBillAddonCharges(account.getOldAccountNumber());
        isSenior = account.getIsSeniorCitizen();
        chargeTypeObj = chargeTypeDAO.getChargeType(account.getIdRateMaster());
        isForTrancated = duPropertyDAO.getPropertyValue("IS_TRUNCATED_VALUE");
        scdRounding = duPropertyDAO.getPropertyValue("ROUNDING_MODE_SCD");
        scsRounding = duPropertyDAO.getPropertyValue("ROUNDING_MODE_SCS");
        lldRounding = duPropertyDAO.getPropertyValue("ROUNDING_MODE_LLD");
        llsRounding = duPropertyDAO.getPropertyValue("ROUNDING_MODE_LLS");
        if (isSTL.equals("Y")) {
            isLifeliner = totalConsumption <= Double.parseDouble(genericDao.getOneField("SELECT MAX(toKwh) FROM arm_lifelinedetails","0"));
            bdSubToLifelineRate = ratePerKwChargeDao.findSTLRate(account.getIdRateMaster(), totalConsumption, isForTrancated);
        }
        billChargeGroupList = generateChargeGroup(newBillNumber);
        if (billChargeGroupList.size() == 0) return "-1";
        else {
            boolean billNoExist = billHeaderDAO.checkBillNoIfExist(newBillNumber);
            if (billNoExist) isForUpdate = true;
            lastBillChargeGroup = billChargeGroupList.get(billChargeGroupList.size() - 1);
            List<BillChargeGroupDetail> billChargeGroupDetails = new ArrayList<>();
            if (!isForUpdate) {
                addOnKwh(newBillNumber, account.getOldAccountNumber(), stopMeterFixedConsumption);
                String retVal = billToDb.saveBillHeader(consumption, currentReading, coreLossKWH, consumerType, newBillNumber,
                        iTotalKwhAddOnTran, totalConsumption, account, remarks, effectivityDate);
                if (!retVal.equals("0")) return retVal;
                else {
                    int billChargeGroupSize = 0;
                    addOnCharge(newBillNumber);
                    for (BillChargeGroup chargeGroup : billChargeGroupList) {
                        isSaved = billChargeGroupDAO.insertRecord(chargeGroup);
                        billChargeGroupDetails = ratePerKwChargeDao.getChargesGroupDetail(chargeGroup.getBillNumber(), account.getIdRateMaster(),
                                totalConsumption, chargeGroup.getChargeTypeCode().trim(), chargeGroup.getPrintOrder(), isLifeliner, isForTrancated,isSenior);
                        billChargeGroupSize = billChargeGroupDetails.size();
                            for (BillChargeGroupDetail chargeGroupDetail : billChargeGroupDetails) {
                                BigDecimal rateAddonTotal = new BigDecimal(genericDao.getOneField("select IFNULL(SUM(amount), 0) from arm_rateaddontran_special" +
                                        " where accountNumber = '" + account.getOldAccountNumber() + "' and chargeName = '" + chargeGroupDetail.getChargeName() + "'","0"));
                                chargeGroupDetail.setChargeTotal(chargeGroupDetail.getChargeTotal().add(rateAddonTotal));
                                isSaved = billChargeGroupDetailDAO.insertRecord(chargeGroupDetail);
                            }
                    }
                    lastChargeTypePrintOrder = lastBillChargeGroup.getPrintOrder();
                    if (billChargeGroupSize > 0)
                        lastPrintOrder = billChargeGroupDetails.get(billChargeGroupSize - 1).getPrintOrder();
                    billChargeGroupDetails.clear();
                    generateAccountOtherCharge(lastBillChargeGroup.getBillNumber(),
                            lastChargeTypePrintOrder,account.getAccountNumber(),consumption, isForUpdate);
                    generateLocalFranchiseLLAndSC(lastBillChargeGroup.getBillNumber(),
                            lastChargeTypePrintOrder, lastPrintOrder, account.getIdArea(), account.getIdRateMaster(), isForUpdate);
                }
            } else {
                int billChargeGroupSize = 0;
                if (consumption != 0) billAddonKwhDAO.deleteRecord("Fixed Consumption", newBillNumber);
                addOnKwh(newBillNumber, account.getOldAccountNumber(), stopMeterFixedConsumption);
                billParams.setBillNumber(newBillNumber);
                billParams.setCurrentReading(currentReading);
                billParams.setPrevRdg(previousReading);
                billParams.setKwhConsumption(consumption);
                billParams.setTotalKwhConsumption(totalConsumption);
                billParams.setRemarks(remarks);
                isSaved = billHeaderDAO.updateRecord(billParams);
                addOnCharge(newBillNumber);
                for (BillChargeGroup chargeGroup : billChargeGroupList) {
                    isSaved = billChargeGroupDAO.updateRecord(chargeGroup);
                    billChargeGroupDetails = ratePerKwChargeDao.getChargesGroupDetail(chargeGroup.getBillNumber(), account.getIdRateMaster(),
                            totalConsumption, chargeGroup.getChargeTypeCode().trim(), chargeGroup.getPrintOrder(), isLifeliner, isForTrancated,isSenior);
                    billChargeGroupSize = billChargeGroupDetails.size();
                        for (BillChargeGroupDetail chargeGroupDetail : billChargeGroupDetails) {
                            BigDecimal rateAddonTotal = new BigDecimal(genericDao.getOneField("select IFNULL(SUM(amount), 0) from arm_rateaddontran_special" +
                                    " where accountNumber = '" + account.getOldAccountNumber() + "' and chargeName = '" + chargeGroupDetail.getChargeName() + "'","0"));
                            chargeGroupDetail.setChargeTotal(chargeGroupDetail.getChargeTotal().add(rateAddonTotal));
                            isSaved = billChargeGroupDetailDAO.updateGroupDetails(chargeGroupDetail);
                        }
                }
                lastChargeTypePrintOrder = lastBillChargeGroup.getPrintOrder();
                if (billChargeGroupSize > 0) lastPrintOrder = billChargeGroupDetails.get(billChargeGroupDetails.size() - 1).getPrintOrder();
                billChargeGroupDetails.clear();
                generateAccountOtherCharge(lastBillChargeGroup.getBillNumber(),
                        lastChargeTypePrintOrder,account.getAccountNumber(),consumption, isForUpdate);
                generateLocalFranchiseLLAndSC(lastBillChargeGroup.getBillNumber(),
                        lastChargeTypePrintOrder, lastPrintOrder, account.getIdArea(), account.getIdRateMaster(), isForUpdate);
            }
            String vatRate = duPropertyDAO.getPropertyValue("VAT_RATE");
            BillChargeGroupDetail vatOthers = billChargeGroupDetailDAO.selectBillByBillNo("VAT-Others","chargeName",lastBillChargeGroup.getBillNumber());
            vatOthers.setChargeAmount(null);
            vatOthers.setChargeTotal(vatOthers.getChargeTotal() != null ? vatOthers.getChargeTotal().add(new BigDecimal(genericDao.getOneField("SELECT valueAddOn FROM armBillAddonCharge WHERE billNo='"+ newBillNumber +"' AND addonCharge ='Trans Rental'" ,"0")).multiply(new BigDecimal(!vatRate.equals("-1") ? vatRate : "0.12")))
                                    .setScale(2,BigDecimal.ROUND_HALF_UP) : new BigDecimal("0"));
            updateBill(vatOthers);

            //Hard Coded
            if(duCode.equals(apec) || duCode.equals(aleco)){
                // other vat
                forVatOtherVal = forVatOtherVal.add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"RFSC"))
                                               .add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"IIC Sub Adj"));
                updateBill(calculateVatOthers(lastBillChargeGroup.getBillNumber(),forVatOtherVal));

                //vat dist
                BillChargeGroupDetail rates = billChargeGroupDetailDAO.selectBillByBillNo("VAT-Dist","chargeName",lastBillChargeGroup.getBillNumber());
                rates.setChargeAmount(null);
                rates.setChargeTotal(billChargeGroupDAO.getCTOfCG(lastBillChargeGroup.getBillNumber(),"Dist/Supply/Metering Charges")
                        .multiply(new BigDecimal(!vatRate.equals("-1") ? vatRate : "0.12")).setScale(2,BigDecimal.ROUND_HALF_UP));
                updateBill(rates);
            } else if(duCode.equals(sorecoII)) {
                BillChargeGroupDetail rates = billChargeGroupDetailDAO.selectBillByBillNo("MKP","chargeName",lastBillChargeGroup.getBillNumber());
                rates.setChargeAmount(null);
                rates.setChargeTotal(bdSubToLifelineRate.add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"SC SUB")
                        .add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"LL DISC"))
                        .add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"LL SUB"))).multiply(new BigDecimal("0.3")).setScale(2, BigDecimal.ROUND_HALF_UP));
                updateBill(rates);
            }
            //End Hard Coded

            retValue = updateBillHeader(newBillNumber);
            if(surchargeList != null && surchargeList.size() > 0){
                double kwhLimit = !duPropertyDAO.getPropertyValue("SC_KW_LIMIT").equals("-1") ? Double.parseDouble(duPropertyDAO.getPropertyValue("SC_KW_LIMIT")) : 0;
                BigDecimal totalBill =  new BigDecimal(genericDao.getOneField("totalBill","armBillHeader","WHERE billNo =",newBillNumber,"ORDER BY _id DESC","0"));
                if(duCode.equals(sorecoII)){
                    totalBill =  new BigDecimal(0).add(bdSubToLifelineRate.add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"LL SUB")).subtract(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"RFSC")))
                            .add(billChargeGroupDAO.getCTOfCG(lastBillChargeGroup.getBillNumber(),"Universal Charges"))
                            .add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"SC SUB")
                                    .add(billChargeGroupDetailDAO.getCTOfCGD(lastBillChargeGroup.getBillNumber(),"MKP")));
                    if(!isSenior.equals("Y") && isLifeliner == false)
                        billSurchargeInsertUpdate(isForUpdate,newBillNumber,surchargeList,totalBill);
                    else if(isSenior.equals("Y") && totalConsumption > kwhLimit)
                        billSurchargeInsertUpdate(isForUpdate,newBillNumber,surchargeList,totalBill);
                }else billSurchargeInsertUpdate(isForUpdate,newBillNumber,surchargeList,totalBill);
            }
            return newBillNumber;
        }
    }

    public void updateBill(BillChargeGroupDetail rates){
        if (rates != null || rates.getBillNo() != null || rates.getChargeName() != null)
            isSaved = billChargeGroupDetailDAO.updateBill(rates);
    }

    public void billSurchargeInsertUpdate(boolean isForUpdate,String newBillNumber,List<Surcharge> surchargeList,BigDecimal totalBill){
        BillSurcharge billSurcharge = null;
        for (Surcharge surcharge : surchargeList){
            billSurcharge = new BillSurcharge();
            billSurcharge.setDays(surcharge.getDays());
            billSurcharge.setSurcharge(totalBill.multiply(surcharge.getSurcharge()).setScale(2, BigDecimal.ROUND_HALF_UP));
            billSurcharge.setSurchargeRate(surcharge.getSurcharge());
            if(!isForUpdate) billSurchargeDao.insertRecord(billSurcharge,newBillNumber);
            else billSurchargeDao.updateRecord(billSurcharge,newBillNumber);
        }
    }

    private void generateLocalFranchiseLLAndSC(String billNumber, int lastChargeTypePrintOrder, int lastPrintOrder, long idArea,
                                               int idRateMaster, boolean isForUpdate) {
        generateLocalAndFranchiseTax(billNumber,
                lastChargeTypePrintOrder, lastPrintOrder, idArea, idRateMaster, isForUpdate);
        generateLLSC(lastBillChargeGroup.getBillNumber(),
                lastChargeTypePrintOrder, lastPrintOrder, idRateMaster, isForUpdate);
    }

    private void generateAccountOtherCharge(String billNumber, int lastChargeTypePrintOrder,  String accountNo, double consumption, boolean isForUpdate) {
        List<AccountOtherCharges> accountOtherChargesList = accountOtherChargesDao.getAcctNocharge(accountNo);
        BigDecimal computedRates = new BigDecimal(0);
        if(accountOtherChargesList.size() > 0){
            for(AccountOtherCharges accountOtherCharges : accountOtherChargesList){
                BillChargeGroupDetail chargeGroupDetail = new BillChargeGroupDetail();
                OtherCharges otherCharges = otherChargesDao.getOtherCharge(accountOtherCharges.getIdCharge());
                chargeGroupDetail.setChargeAmount(new BigDecimal("0"));
                if(otherCharges.getAmountFixed() != 0 && otherCharges.getAmountPerKw() == 0) chargeGroupDetail.setChargeAmount(new BigDecimal(otherCharges.getAmountFixed()));
                else if(otherCharges.getAmountFixed() == 0 && otherCharges.getAmountPerKw() != 0) chargeGroupDetail.setChargeAmount(new BigDecimal(otherCharges.getAmountPerKw()));
                chargeGroupDetail.setChargeName(otherCharges.getChargeName());
                computedRates = new BigDecimal(0).add(BigDecimal.valueOf(otherCharges.getAmountPerKw()).multiply(BigDecimal.valueOf(consumption))).add(BigDecimal.valueOf(otherCharges.getAmountFixed()));
                if (isForTrancated.equals("Y")) computedRates = computedRates.setScale(2, RoundingMode.DOWN);
                else computedRates = computedRates.setScale(2, BigDecimal.ROUND_HALF_UP);
                chargeGroupDetail.setChargeTotal(computedRates);
                chargeGroupDetail.setBillNo(billNumber);
                chargeGroupDetail.setPrintOrderMaster(lastChargeTypePrintOrder);
                lastPrintOrder+=1;
                chargeGroupDetail.setPrintOrder(lastPrintOrder);
                if (!isForUpdate) billChargeGroupDetailDAO.insertRecord(chargeGroupDetail);
                else billChargeGroupDetailDAO.updateGroupDetails(chargeGroupDetail);
            }
        }
    }

    private void saveAdditionalRates(List<BillChargeGroupDetail> billChargeGroupDetails, boolean isForUpdate) {
        if (billChargeGroupDetails.size() > 0)
            for (BillChargeGroupDetail billDetailSpecial : billChargeGroupDetails)
                if (!isForUpdate) isSaved = billChargeGroupDetailDAO.insertRecord(billDetailSpecial);
                else isSaved = billChargeGroupDetailDAO.updateGroupDetails(billDetailSpecial);
    }

    private BillChargeGroupDetail calculateVatOthers(String billNumber, BigDecimal vatOtherVal) {
        String vatRate = duPropertyDAO.getPropertyValue("VAT_RATE");
        BillChargeGroupDetail rates = billChargeGroupDetailDAO.selectBillByBillNo("Other VAT","chargeName",billNumber);
        rates.setChargeAmount(new BigDecimal(!vatRate.equals("-1") ? vatRate : "0.12"));
        rates.setChargeTotal(vatOtherVal.multiply(new BigDecimal(!vatRate.equals("-1") ? vatRate : "0.12")));
        return rates;
    }

    private void calculateSurchargeIfNotBaseOnTotal(long idRateMaster, List<BillChargeGroupDetail> billChargeGroupDetails) {
        if (duPropertyDAO.getPropertyValue("IS_SURCHARGE_BASED_ON_TOTAL").equals("N")) {
            surchargeValue = ratePerKwChargeDao.calculateSurcharge(idRateMaster, totalConsumption);
            // add the SC Sub and Lifeline Sub in Surcharge Computation
            for (BillChargeGroupDetail detail : billChargeGroupDetails) {
                if (detail.getChargeTotal() != null)
                    surchargeValue = surchargeValue.add(detail.getChargeTotal());
            }
            totalBillAfterDue = surchargeValue.multiply(new BigDecimal(duPropertyDAO.getPropertyValue("SURCHARGE_RATE", "1"))).toString();
        }
    }

    public String updateBillHeader(String newBillNumber) {
        String billJson = generateBillJson(newBillNumber);
        if (billJson.equals(""))
            retValue = "-3";
        else {
            billHeaderDAO.updateOneFieldByBillNo(billJson, newBillNumber,"billJson");
            helper.saveBackup(billJson, newBillNumber);
            retValue = newBillNumber;
        }
        return retValue;
    }

    private void generateLocalAndFranchiseTax(String billNo, int lastChargeTypePrintOrder,
                                              int lastPrintOrders, long idArea, long idRateMaster, boolean isForUpdate) {
        List<BillChargeGroupDetail> billChargeGroupDetails = new ArrayList<>();
        BillChargeGroupDetail subsidyRates;
        // Local Tax  +1 to the last printOrder */
        lastPrintOrders += 1;
        subsidyRates = generateLocalTax(billNo, lastChargeTypePrintOrder, lastPrintOrders, idArea, idRateMaster);
        billChargeGroupDetails.add(subsidyRates);

        lastPrintOrders += 1;
        subsidyRates = generateFranchiseTax(billNo, lastChargeTypePrintOrder, lastPrintOrders, idArea, idRateMaster);
        billChargeGroupDetails.add(subsidyRates);
        lastPrintOrder = lastPrintOrders;
        saveAdditionalRates(billChargeGroupDetails, isForUpdate);
        // return billChargeGroupDetails;
    }

    private void generateLLSC(String billNo, int lastChargeTypePrintOrder,
                              int lastPrintOrders, long idRateMaster, boolean isForUpdate) {

        List<BillChargeGroupDetail> billChargeGroupDetails = new ArrayList<>();
        BillChargeGroupDetail subsidyRates;
        // Lifeline Sub/Disc
        /* +1 to the last printOrder */
        lastPrintOrders += 1;
        subsidyRates = generateLifeline(billNo, lastChargeTypePrintOrder, lastPrintOrders);
        //HARDCODED ALECO FOR Other Vat Total
        if(duCode.equals(apec) || duCode.equals(aleco))
            forVatOtherVal = forVatOtherVal.add(subsidyRates.getChargeTotal());

        if(!duPropertyDAO.getPropertyValue("IS_LIFELINE_SUB_BASED_ON_RATE").equals("Y")){
            billChargeGroupDetails.add(subsidyRates);
            lastPrintOrders += 1;
        }else{
            if(!subsidyRates.getChargeName().equals("LL Sub")){
                billChargeGroupDetails.add(subsidyRates);
                lastPrintOrders += 1;
            }
        }
        lifeRateSub = subsidyRates.getChargeTotal();
        String isLLSInclToSCD = duPropertyDAO.getPropertyValue("IS_LLS_INCL_TO_SCD");

        /* No SC Sub/Disc if the meter type is Commercial/Low Voltage - 1/19/2018 */
        //   if (isSTL.equals("Y")) {
        subsidyRates = generateSC(billNo, lastChargeTypePrintOrder, lastPrintOrders, lifeRateSub, isLLSInclToSCD);
        scSubDisc = subsidyRates.getChargeTotal();
        //HARDCODED ALECO FOR Other Vat Total
        if(duCode.equals(apec) || duCode.equals(aleco))
                 forVatOtherVal = forVatOtherVal.add(subsidyRates.getChargeTotal());
        billChargeGroupDetails.add(subsidyRates);
        saveAdditionalRates(billChargeGroupDetails, isForUpdate);
        if (billChargeGroupDetails.size() > 0)
            calculateSurchargeIfNotBaseOnTotal(idRateMaster, billChargeGroupDetails);
    }

    private BillChargeGroupDetail generateLocalTax(String billNo, int lastChargeTypePrintOrder,
                                                   int lastPrintOrders, long idArea, long idRateMaster) {
        BillChargeGroupDetail subsidyRates = new BillChargeGroupDetail();
        BigDecimal localTax = new BigDecimal(genericDao.getOneField("SELECT localTax FROM armDuAreaRate WHERE idArea = " + idArea + " AND idRateMaster = " + idRateMaster,"0.0000"));
        if (localTax.compareTo(BigDecimal.ZERO) > 0) {
            subsidyRates.setBillNo(billNo);
            subsidyRates.setPrintOrderMaster(lastChargeTypePrintOrder);
            subsidyRates.setPrintOrder(lastPrintOrders);
            subsidyRates.setChargeName("Local Tax");
            subsidyRates.setChargeAmount(localTax);
            // Local Tax = Total Consumption * Local Tax Rate
            subsidyRates.setChargeTotal(new BigDecimal(String.valueOf(totalConsumption)).multiply(localTax).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        return subsidyRates;
    }

    private BillChargeGroupDetail generateFranchiseTax(String billNo, int lastChargeTypePrintOrder,
                                                       int lastPrintOrders, long idArea, long idRateMaster) {
        BillChargeGroupDetail subsidyRates = new BillChargeGroupDetail();
        BigDecimal franchiseTax = new BigDecimal(genericDao.getOneField("SELECT franchiseTax FROM armDuAreaRate WHERE idArea = " + idArea + " AND idRateMaster = " + idRateMaster,"0.0000"));
        if (franchiseTax.compareTo(BigDecimal.ZERO) > 0) {
            subsidyRates.setBillNo(billNo);
            subsidyRates.setPrintOrderMaster(lastChargeTypePrintOrder);
            subsidyRates.setPrintOrder(lastPrintOrders);
            subsidyRates.setChargeName("Franchise Tax");
            subsidyRates.setChargeAmount(franchiseTax);
            // Franchise Tax = Total Consumption * Franchise Tax Rate
            subsidyRates.setChargeTotal(new BigDecimal(String.valueOf(totalConsumption)).multiply(franchiseTax).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        return subsidyRates;
    }

    private BillChargeGroupDetail generateSC(String billNo, int lastChargeTypePrintOrder, int lastPrintOrders,
                                             BigDecimal lifeRateSub, String isLLSInclToSCD) {
        BillChargeGroupDetail subsidyRates = new BillChargeGroupDetail();
        String lifelineRateValues = duPropertyDAO.getPropertyValue("SC_SUB");
        BigDecimal bdrateSC = new BigDecimal(duPropertyDAO.getPropertyValue("SC_DISCOUNT_RATE"));
        if (isSTSCS.equals("Y") && isSenior.equals("N")) {
            subsidyRates = generateSubsidy(1, totalConsumption, lifelineRateValues, lastPrintOrders, lastChargeTypePrintOrder, billNo);
            if ((isLifeliner) && duPropertyDAO.getPropertyValue("IS_SCS_ZERO_IF_LIFELINER").equals("Y")) {
                subsidyRates.setBillNo(billNo);
                subsidyRates.setPrintOrder(lastPrintOrders);
                subsidyRates.setPrintOrderMaster(lastChargeTypePrintOrder);
                subsidyRates.setChargeName("SC Sub"); // changes
                subsidyRates.setChargeAmount(new BigDecimal("0.0000"));
                subsidyRates.setChargeTotal(new BigDecimal("0.00"));
            }
        }

        if (isSTSCD.equals("Y") && isSenior.equals("Y")) {
            double kwhLimit = !duPropertyDAO.getPropertyValue("SC_KW_LIMIT").equals("-1") ? Double.parseDouble(duPropertyDAO.getPropertyValue("SC_KW_LIMIT")) : 0;
            double kwhLimitMin = !duPropertyDAO.getPropertyValue("SC_KW_LIMIT_MIN").equals("-1") ? Double.parseDouble(duPropertyDAO.getPropertyValue("SC_KW_LIMIT_MIN")) : 0;
            if (totalConsumption >= kwhLimitMin && totalConsumption <= kwhLimit) subsidyRates = scDiscount(lifeRateSub, bdrateSC, isLLSInclToSCD, billNo, lastPrintOrders);
            else subsidyRates = generateSubsidy(1, totalConsumption, lifelineRateValues, lastPrintOrders, lastChargeTypePrintOrder, billNo);
        }

        return subsidyRates;
    }

    private BillChargeGroupDetail scDiscount(BigDecimal lifeRateSub, BigDecimal bdrateSC, String isLLSInclToSCD,
                                             String billNo, int lastPrintOrders) {
        String scdRateValue = duPropertyDAO.getPropertyValue("SCD_DIRECTLY_COMPUTED_RATE");
        if(duCode.equals(sorecoII))
                bdSCDiscount = computeConsumption.computeSCDiscountSoreco(bdSubToLifelineRate.add(lifeRateSub),billChargeGroupDetailDAO.getCTOfCGD(billNo,"RFSC"),
                        llDiscRate, lifeRateSub);
        else{
            if (!scdRateValue.equals("-1"))
                try {
                    bdSCDiscount = new BigDecimal(String.valueOf(totalConsumption)).multiply(new BigDecimal(scdRateValue));
                } catch (NumberFormatException e) {
                    e.getMessage();
                }
            else
                bdSCDiscount = computeConsumption.computeSCDiscount(bdSubToLifelineRate,
                            lifeLineDiscount, lifeRateSub, bdrateSC, isLLSInclToSCD);
        }
        bdSCDiscount = new BigDecimal(bdSCDiscount.toString());
        if (isForTrancated.equals("Y")) bdSCDiscount = bdSCDiscount.setScale(2, RoundingMode.DOWN);
        else bdSCDiscount =  UniversalHelper.rounding(bdSCDiscount,scdRounding.equals("-1") ? "ROUND_HALF_UP" : scdRounding);

        return generateDiscount(1, bdSCDiscount, lastPrintOrders, lastChargeTypePrintOrder, billNo);
    }

    private BillChargeGroupDetail generateLifeline(String billNo, int lastChargeTypePrintOrder, int lastPrintOrders) {
        BillChargeGroupDetail subsidyRates = new BillChargeGroupDetail();
        String lifelineRateValues = duPropertyDAO.getPropertyValue("LIFELINE_RATE_SUB");

        if (isSTL.equals("N") || (!isLifeliner)){
                subsidyRates = generateSubsidy(0, totalConsumption, lifelineRateValues, lastPrintOrders, lastChargeTypePrintOrder, billNo);
        }else  if (isLifeliner) {
            llDiscRate = new BigDecimal(genericDao.getOneField( "SELECT discountRate FROM arm_lifelinedetails WHERE fromKwh <= " + totalConsumption + " AND toKwh >= " + totalConsumption,"0"));
                String isLLDirectComputed = duPropertyDAO.getPropertyValue("IS_LL_DIRECTLY_COMPUTED");
                if (isLLDirectComputed.equals("Y"))
                    // LL Disc = 0 - (totalConsumption * LL Disc Rate)
                    lifeLineDiscount = new BigDecimal(0).subtract(new BigDecimal(totalConsumption).multiply(llDiscRate));
                 else
                    // LL Disc = 0 - (bdSubToLifelineRate * LL Disc Rate)
                    lifeLineDiscount = new BigDecimal(0).subtract((bdSubToLifelineRate.multiply(llDiscRate)));

                if (isForTrancated.equals("Y")) lifeLineDiscount = lifeLineDiscount.setScale(2, RoundingMode.DOWN);
                else lifeLineDiscount =  UniversalHelper.rounding(lifeLineDiscount,lldRounding.equals("-1") ? "ROUND_HALF_UP" : lldRounding);

                subsidyRates = generateDiscount(0, lifeLineDiscount, lastPrintOrders, lastChargeTypePrintOrder, billNo);
        }

        return subsidyRates;
    }

    private BillChargeGroupDetail generateDiscount(int check, BigDecimal discount, int lastPrintOrders, int lastChargeTypePrintOrder, String billNo) {
        BillChargeGroupDetail llList = new BillChargeGroupDetail();
        if (isForTrancated.equals("Y"))
            discount = discount.setScale(2, RoundingMode.DOWN);
        else{
            if (check == 0)
                discount =  UniversalHelper.rounding(discount,lldRounding.equals("-1") ? "ROUND_HALF_UP" : lldRounding);
            else
                discount =  UniversalHelper.rounding(discount,scdRounding.equals("-1") ? "ROUND_HALF_UP" : scdRounding);
        }
        llList.setBillNo(billNo);
        llList.setPrintOrder(lastPrintOrders);
        llList.setPrintOrderMaster(lastChargeTypePrintOrder);
        if (check == 0)
            llList.setChargeName("LL Disc"); //changes
        else
            llList.setChargeName("SC Disc");
        llList.setChargeAmount(new BigDecimal("0.0000"));
        llList.setChargeTotal(discount);
        return llList;
    }

    private BillChargeGroupDetail generateSubsidy(int check, double totalConsumption, String lifelineRateValues, int printOrder,
                                                  int printOrderMaster, String billNo) {
        BillChargeGroupDetail llList = new BillChargeGroupDetail();
        BigDecimal lifelineRateSub = new BigDecimal(lifelineRateValues).multiply(new BigDecimal(totalConsumption));
        if (isForTrancated.equals("Y")) lifelineRateSub = lifelineRateSub.setScale(2, RoundingMode.DOWN);
        else{
            if (check == 0) lifelineRateSub = UniversalHelper.rounding(lifelineRateSub,llsRounding.equals("-1") ? "ROUND_HALF_UP" : llsRounding);
            else lifelineRateSub = UniversalHelper.rounding(lifelineRateSub,scsRounding.equals("-1") ? "ROUND_HALF_UP" : scsRounding);
        }

        llList.setBillNo(billNo);
        llList.setPrintOrderMaster(printOrderMaster);
        llList.setPrintOrder(printOrder);
        if (check == 0) llList.setChargeName("LL Sub"); //changes
        else llList.setChargeName("SC Sub");
        llList.setChargeAmount(new BigDecimal(lifelineRateValues));
        llList.setChargeTotal(lifelineRateSub);
        return llList;
    }

    private void addOnKwh(String newBillNumber, String oldAccountNumber, double fixedConsumption) {
        if (iTotalKwhAddOnTran > 0) {
            List<BillAddonKwh> billAddonKwh = kwhAddOnTranDao.getAddonKwh(oldAccountNumber);
            if (consumption == 0) {
                BillAddonKwh addonKwh = new BillAddonKwh();
                addonKwh.setBillNo(newBillNumber);
                addonKwh.setAddonKwh("Fixed Consumption");
                addonKwh.setValue(fixedConsumption);
                billAddonKwh.add(addonKwh);
            }
            int recordCount = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillAddonKwh","WHERE isUploaded = 0 AND billNo = ",newBillNumber," ORDER BY _id","0"));
            if (recordCount == 0) {
                billAddonKwhDAO.insertRecord(billAddonKwh, newBillNumber);
            } else {
                billAddonKwhDAO.updateRecord(billAddonKwh, newBillNumber);
            }
        }
    }

    private void addOnCharge(String newBillNumber) {
        if (billAddonChargeList.size() > 0) {
            addonCount = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillAddonCharge","WHERE isUploaded = 0 AND billNo =",newBillNumber,"ORDER BY _id","0"));
            if (addonCount == 0)
                billAddonChargeDAO.insertRecord(billAddonChargeList, newBillNumber);
             else
                billAddonChargeDAO.updateRecord(billAddonChargeList, newBillNumber);
        }
    }

    public String generateBillJson(String billNo) {
        BillHeader billHeader = billHeaderDAO.findBillByBillNo(billNo);
        BillModel billModel = new BillModel();
        Gson gson = new Gson();
        List<BillChargeGroup> billChargeGroups;
        List<BillChargeGroupDetail> billChargeGroupDetails;
        List<BillAddonKwh> billAddonKwhs;
        List<BillAddonCharge> billAddonCharges;
        List<BillGroupCategory> billGroupCategories = new ArrayList<>();

        billModel.setBillHeaders(billHeader);
        if (!billHeader.getBillNo().equals(null)) {
            int recordCount;
            billChargeGroups = billChargeGroupDAO.getAllChargeGroups(billHeader.getBillNo());
            billModel.setBillChargeGroups(billChargeGroups);
            for (BillChargeGroup billChargeGroup : billChargeGroups) {
                billChargeGroupDetails = billChargeGroupDetailDAO.getAllChargeGroupDetails(billChargeGroup.getBillNumber(), billChargeGroup.getPrintOrder());
                BillGroupCategory billGroupCategory = new BillGroupCategory();
                billGroupCategory.setBillChargeGroupDetailList(billChargeGroupDetails);
                billGroupCategory.setChargeType(billChargeGroup.getChargeTypeName());
                billGroupCategories.add(billGroupCategory);
                billModel.setBillGroupCategories(billGroupCategories);
            }
            recordCount = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillAddonKwh","WHERE isUploaded = 0 AND billNo = ",billHeader.getBillNo(),"ORDER BY _id DESC","0"));
            if (recordCount > 0) {
                billAddonKwhs = billAddonKwhDAO.getAllBillAddonKwh(billHeader.getBillNo());
                billModel.setBillAddonKwh(billAddonKwhs);
            }
            recordCount = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillAddonCharge","WHERE isUploaded = 0 AND billNo = ",billHeader.getBillNo(),"ORDER BY _id DESC","0"));
            if (recordCount > 0) {
                billAddonCharges = billAddonChargeDAO.getAllBillAddonCharges(billHeader.getBillNo());
                billModel.setBillAddonCharge(billAddonCharges);
            }

            BigDecimal totalBill = new BigDecimal(billModel.getBillHeaders().getTotalAmountDue()).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (totalBill.compareTo(BigDecimal.ZERO) < 0) {
                totalBill = updateAddonRec(billModel);
                billModel.getBillHeaders().setTotalAmountDue(df.format(totalBill));
                billModel.getBillHeaders().setTotalBillAfterDueDate(df.format(totalBill));
                totalBill.setScale(2, BigDecimal.ROUND_HALF_UP);
                billHeaderDAO.updateOneFieldByBillNo(String.valueOf(billModel.getBillHeaders().getTotalAmountDue()),billModel.getBillHeaders().getBillNo(),"totalBill");
            }
            if (duPropertyDAO.getPropertyValue("IS_SURCHARGE_BASED_ON_TOTAL").equals("Y")) {
                BigDecimal surchargeVal = new BigDecimal(billModel.getBillHeaders().getTotalAmountDue()).setScale(2, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal(duPropertyDAO.getPropertyValue("SURCHARGE_RATE")));
                totalBillAfterDueDate = new BigDecimal(billModel.getBillHeaders().getTotalAmountDue())
                        .add(surchargeVal);
            } else
                totalBillAfterDueDate = new BigDecimal(billModel.getBillHeaders().getTotalAmountDue())
                        .add(new BigDecimal(totalBillAfterDue));

            totalBillAfterDueDate = totalBillAfterDueDate.setScale(2, BigDecimal.ROUND_HALF_UP);
            billHeaderDAO.updateOneFieldByBillNo(totalBillAfterDueDate.toString(), billModel.getBillHeaders().getBillNo(),"totalBillAfterDueDate");
            billHeader.setTotalBillAfterDueDate(totalBillAfterDueDate.toString());
            billHeader.setApkVersion(sharedPref.getString("version", ""));
            billModel.setBillHeaders(billHeader);
            return gson.toJson(billModel);

        } else return null;
    }

    private BigDecimal updateAddonRec(BillModel billModel) {
        BigDecimal refundValue;
        BigDecimal newRefVal;
        BigDecimal currentBill = new BigDecimal(billModel.getBillHeaders().getCurBill()).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalBill = new BigDecimal(billModel.getBillHeaders().getTotalAmountDue()).setScale(2, BigDecimal.ROUND_HALF_UP);
        try {
            if (billModel.getBillAddonCharge() != null || billModel.getBillAddonCharge().size() > 0) {
                for (BillAddonCharge addonCharge : billModel.getBillAddonCharge()) {
                    if (addonCharge.getValue() < 0) {
                        refundValue = new BigDecimal(Math.abs(addonCharge.getValue())).setScale(2, BigDecimal.ROUND_HALF_UP);
                        if (totalBill.compareTo(BigDecimal.ZERO) < 0) {
                            if (refundValue.compareTo(currentBill) >= 0) {
                                newRefVal = currentBill.multiply(new BigDecimal(-1));
                                addonCharge.setValue(newRefVal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                            } else {
                                newRefVal = refundValue.multiply(new BigDecimal(-1));
                                addonCharge.setValue(newRefVal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                                currentBill = currentBill.add(newRefVal);
                            }
                            totalBill = currentBill.add(newRefVal);
                            totalBill.setScale(2, BigDecimal.ROUND_HALF_UP);
                        } else if (totalBill.compareTo(BigDecimal.ZERO) == 0) {
                            addonCharge.setValue(0.00);
                        }
                        billAddonChargeDAO.updateRecord(addonCharge);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalBill;
    }

    private List<BillChargeGroup> generateChargeGroup(String newBillNumber) {
        List<BillChargeGroup> billChargeGroupList = new ArrayList<>();
        if (chargeTypeObj.size() > 0) {
            for (ChargeTypeModel chargeType : chargeTypeObj) {
                BillChargeGroup billChargeGroup = new BillChargeGroup();
                billChargeGroup.setBillNumber(newBillNumber);
                billChargeGroup.setChargeTypeCode(chargeType.getChargeTypeCode());
                billChargeGroup.setChargeTypeName(chargeType.getChargeTypeName());
                billChargeGroup.setPrintOrder(chargeType.getPrintOrder());
                billChargeGroupList.add(billChargeGroup);
            }
        }
        return billChargeGroupList;
    }

    public ArrayList<String> getExistingRemarks(String oldAccountNumber) {
        return billHeaderDAO.getBillAndRemarksByAcct(oldAccountNumber);
    }

    public void updateRemarks(String billNumber, String remarks) {
        if (!billNumber.equals(""))
            billHeaderDAO.updateOneFieldByBillNo(billNumber, remarks,"remarks");
    }


}