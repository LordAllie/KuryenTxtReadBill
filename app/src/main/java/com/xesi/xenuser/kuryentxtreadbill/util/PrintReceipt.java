package com.xesi.xenuser.kuryentxtreadbill.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nbbse.mobiprint3.Printer;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountBillAuxDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillAddonChargeDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillAddonKwhDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillChargeGroupDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillChargeGroupDetailDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillSurchargeDao;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonCharge;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonKwh;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroup;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroupDetail;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillGroupCategory;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillModel;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillSurcharge;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountBillAux;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;
import com.xesi.xenuser.kuryentxtreadbill.model.download.DUProperty;

import org.apache.commons.lang3.text.WordUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Route;

/**
 * Created by xenuser on 2/21/2017.
 */
public class PrintReceipt {
    public static final String APP_PROPERTY_SETTING = "app_config";
    private String graph;
    /* FOR QR CODE GENERATION */
    private String qrPath;
    private String barCodePath;
    private String logoPath;
    //BILL DAO
    private GenericDao genericDao;
    private UniversalHelper formatHelper;
    private AccountDao accountDao;
    private BillHeaderDAO billHeaderDAO;
    private BillChargeGroupDAO billChargeGroupDAO;
    private BillChargeGroupDetailDAO billChargeGroupDetailDAO;
    private BillAddonKwhDAO billAddonKwhDAO;
    private BillSurchargeDao billSurchargeDao;
    private BillAddonChargeDAO billAddonChargeDAO;
    private BillHeader billHeader;
    private List<BillChargeGroup> billChargeGroups;
    private List<BillAddonKwh> billAddonKwhs;
    private List<BillAddonCharge> billAddonCharges;
    private AccountBillAuxDao accountBillAuxDao;
    private SharedPreferences sharedPref;
    private Printer print;
    private Context context;
    private DecimalFormat dformatter = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dueDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private DUPropertyDAO duPropertyDAO;
    private MsgDialog msgDialog;
    private int counter = 0;
    public static ProgressDialog progressBar;
    public int x;
    private int printerStatus;
    private Gson gson;
    private List<BillGroupCategory> billGroupCategories;
    private String conType, isSpikeDrop = "N";
    private int delayTimer = 0;
    private BillHeader header;
    private AccountModelV2 account;
    private Map<String, String> map;
    private List<DUProperty> properties;
    private List<BillSurcharge> billSurchargesList;
    private List<AccountBillAux> accountBillAuxList;
    private boolean isLastSeq = false;

    public PrintReceipt(Context context, Printer print) {
        this.print = print;
        this.context = context;
        genericDao = new GenericDao(context);
        sharedPref = context.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        accountBillAuxDao = new AccountBillAuxDao(context);
        billSurchargeDao = new BillSurchargeDao(context);
        duPropertyDAO = new DUPropertyDAO(context);
        billHeaderDAO = new BillHeaderDAO(context);
        billHeaderDAO.instantiateDb();
        billAddonKwhDAO = new BillAddonKwhDAO(context);
        billChargeGroupDAO = new BillChargeGroupDAO(context);
        billChargeGroupDetailDAO = new BillChargeGroupDetailDAO(context);
        billAddonChargeDAO = new BillAddonChargeDAO(context);
        accountDao = new AccountDao(context);
        qrPath = context.getFilesDir().getAbsolutePath() + "/bill_qr_code.bmp";
        barCodePath = context.getFilesDir().getAbsolutePath() + "/bill_bar_code.bmp";
        graph = context.getFilesDir().getAbsolutePath() + "/chart.bmp";
        logoPath = context.getFilesDir().getAbsolutePath() + "/logo.bmp";
        formatHelper = new UniversalHelper(context);
        msgDialog = new MsgDialog(context);
        progressBar = new ProgressDialog(context);
        isLastSeq =false;
    }

    public synchronized void close() {
        genericDao.close();
        billHeaderDAO.close();
        billAddonKwhDAO.close();
        billChargeGroupDAO.close();
        billChargeGroupDetailDAO.close();
        billAddonChargeDAO.close();
    }

    private int checkPaperStatus() {
        int status = checkPrinterStatus();
        if (status == 1) {
            switch (print.getPaperStatus()) {
                case Printer.PRINTER_EXIST_PAPER:
                    status = Printer.PRINTER_EXIST_PAPER;
                    break;
                case Printer.PRINTER_NO_PAPER:
                    status = Printer.PRINTER_NO_PAPER;
                    break;
                case Printer.PRINTER_PAPER_ERROR:
                    status = Printer.PRINTER_PAPER_ERROR;
                    break;
                default:
                    break;
            }
        }
        return status;
    }

    private int checkPrinterStatus() {
        int printerStatus = 0;
        switch (print.getPrinterStatus()) {
            case Printer.PRINTER_STATUS_OK:
                printerStatus = Printer.PRINTER_STATUS_OK;
                break;
            case Printer.PRINTER_STATUS_NO_PAPER:
                printerStatus = Printer.PRINTER_STATUS_NO_PAPER;
                break;
            case Printer.PRINTER_STATUS_OVER_HEAT:
                printerStatus = Printer.PRINTER_STATUS_OVER_HEAT;
                break;
            case Printer.PRINTER_STATUS_GET_FAILED:
                printerStatus = Printer.PRINTER_STATUS_GET_FAILED;
                break;
        }
        return printerStatus;
    }

    private void coolDown() {
        boolean overHeat = true;
        while (overHeat) {
            try {
                Thread.sleep(1000);
                overHeat = checkPrinterStatus() == -1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void runDelayEveryChunk() {
        try {
            // delayTimer = (sharedPref.getInt("printDelay", 3) * 150) + 1000;
            Thread.sleep(1200);
            counter = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runDelay() {
        try {
            delayTimer= sharedPref.getInt("printDelay", 1)*10;
            Thread.sleep(delayTimer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int chunk() {return 14 - (sharedPref.getInt("printDelay", 1)*2);}

    private void printTextWithDelay(String data) {
        try {
            printerStatus = checkPrinterStatus();
            if (printerStatus == -1) coolDown();
            if (counter % chunk() == 0) {
                if(sharedPref.getString("isSuppressedPrintBuffer", "N").equals("Y"))
                    runDelayEveryChunk();
                Log.d("PRINTING", "DELAY COUNTING " + counter);
                print.printText(data);
            } else {
                runDelay();
                Log.d("PRINTING", "COUNTING " + counter);
                print.printText(data);
            }
            counter++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printTextWithDelay(String data, int fontSize) {
        try {
            printerStatus = checkPrinterStatus();
            if (printerStatus == -1) coolDown();
            if (counter % chunk() == 0) {
                if(sharedPref.getString("isSuppressedPrintBuffer", "N").equals("Y"))
                    runDelayEveryChunk();
                Log.d("PRINTING", "DELAY COUNTING " + counter);
                print.printText(data, fontSize);
            } else {
                runDelay();
                Log.d("PRINTING", "COUNTING " + counter);
                print.printText(data, fontSize);
            }
            counter++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printBitMapWithDelay(String data, int speed) {
        try {
            printerStatus = checkPrinterStatus();
            if (printerStatus == -1) coolDown();
            print.printBitmap(data, speed);
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printMultiplier(double meterMultiplier) {
        if (meterMultiplier > 1) {
            printTextWithDelay(formatHelper.padRight("Meter Multiplier", 12) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getMeterMultiplier()).toString(), 16));
        }
    }

    private void generateQRCode(BillHeader billHeader) {
        String daysOfValidity = duPropertyDAO.getPropertyValue("BILLS_VALID_AFTER_DUE");
        if (daysOfValidity.equals("") || daysOfValidity.equals("0"))
            daysOfValidity = "10";

         /* PASS DATA FOR QR CODE GENERATION */
        try {
            String newDueDateFormat = formatHelper.getBillValidityDate(billHeader.getDueDate(), "0");
            String newDateValidity = formatHelper.getBillValidityDate(billHeader.getDueDate(), daysOfValidity);
            String mobileNo = "9171234567";
            String totalAmountDue = new BigDecimal(billHeader.getTotalAmountDue()).setScale(2, BigDecimal.ROUND_HALF_UP).toString().replace(".", "");
            String sToQRCode = duPropertyDAO.getPropertyValue("DU_CODE") + ","
                    + billHeader.getBillNo() + "," + newDueDateFormat + ","
                    + totalAmountDue + "," + mobileNo + "," + newDateValidity;
            formatHelper.encodeAsBitmap(sToQRCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateBarcode(String billNumber) {
        try {
            formatHelper.encodeAsBitmapBarcode(billNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int printGroup(List<BillChargeGroup> billChargeGroups, List<BillGroupCategory> billGroupCategories, Map<String, String> map) {
        String Str = new String("lldisc,lifelinedisc, lldiscount, lifelinediscount");
        String Str_ll_sub = new String("llsub,lifelinesub, llsubsidies, lifelinesubsidies");
        int cnt=0,cnt_llSub=0;
        boolean llDisc=false, llSub=false;
        int status = checkPaperStatus();
        if (status == 1) {
            UniversalHelper.dformat.setRoundingMode(RoundingMode.FLOOR);
            Collections.sort(billChargeGroups, BillChargeGroup.SORT_BY_PRINT_ORDER);
            for (BillChargeGroup billChargeGroup : billChargeGroups) {
                if (getProperty(map.get("IS_PRINT_CHARGE_HEADER"), "Y").equals("Y")) {
                    printTextWithDelay(billChargeGroup.getChargeTypeName());
                    printTextWithDelay("--------------------------------");
                }

                for (BillGroupCategory groupCategory : billGroupCategories) {
                    Collections.sort(groupCategory.getBillChargeGroupDetailList(), BillChargeGroupDetail.SORT_BY_PRINT_ORDER);
                    for (BillChargeGroupDetail detail : groupCategory.getBillChargeGroupDetailList()) {
                        if (detail.getPrintOrderMaster() == billChargeGroup.getPrintOrder()) {
                            llDisc=Str.matches("(.*)"+detail.getChargeName().toLowerCase().replace(" ","")+"(.*)");
                            llSub=Str_ll_sub.matches("(.*)"+detail.getChargeName().toLowerCase().replace(" ","")+"(.*)");
                            BillChargeGroupDetail billChargeGroupDetail = billChargeGroupDetailDAO.getLLDisc(detail.getBillNo());
                            if((llDisc==true && cnt>0) || (llSub==true && cnt_llSub>0)){}
                            else if(llDisc==true && cnt==0){
                                String chargeAmount;
                                String chargeTotal;
                                if(Double.parseDouble(billChargeGroupDetail.getChargeAmount().toString()) > 0.00) chargeAmount="0";
                                else chargeAmount=billChargeGroupDetail.getChargeAmount().toString();

                                if(Double.parseDouble(billChargeGroupDetail.getChargeTotal().toString()) > 0.00) chargeTotal="0";
                                else chargeTotal=billChargeGroupDetail.getChargeTotal().toString();

                                if (getProperty(map.get("IS_PRINT_ZERO"), "Y").equals("N")) {
                                    if (detail.getChargeTotal().compareTo(BigDecimal.ZERO) != 0) {
                                        //changes
                                        if(detail.getChargeAmount().compareTo(BigDecimal.ZERO) == 0)
                                            printChargeDetail(detail.getChargeName(),"",UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                        else
                                            printChargeDetail(detail.getChargeName(),UniversalHelper.dformat.format(chargeAmount),UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                    }
                                } else {
                                    //changes
                                    if(detail.getChargeAmount().compareTo(BigDecimal.ZERO) == 0)
                                        printChargeDetail(detail.getChargeName(),"",UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                    else
                                        printChargeDetail(detail.getChargeName(),UniversalHelper.dformat.format(billChargeGroupDetail.getChargeAmount()),UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                }
                            }
                            else if(llSub==true && cnt_llSub==0){
                                String chargeAmount;
                                String chargeTotal;
                                if(Double.parseDouble(billChargeGroupDetail.getChargeAmount().toString()) < 0.00) chargeAmount="0";
                                else chargeAmount=billChargeGroupDetail.getChargeAmount().toString();

                                if(Double.parseDouble(billChargeGroupDetail.getChargeTotal().toString()) < 0.00) chargeTotal="0";
                                else chargeTotal=billChargeGroupDetail.getChargeTotal().toString();

                                if (getProperty(map.get("IS_PRINT_ZERO"), "Y").equals("N")) {
                                    if (detail.getChargeTotal().compareTo(BigDecimal.ZERO) != 0) {
                                        //changes
                                        if(detail.getChargeAmount().compareTo(BigDecimal.ZERO) == 0)
                                            printChargeDetail(detail.getChargeName(),"",UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                        else
                                            printChargeDetail(detail.getChargeName(),UniversalHelper.dformat.format(chargeAmount),UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                    }
                                } else {
                                    //changes
                                    if(detail.getChargeAmount().compareTo(BigDecimal.ZERO) == 0)
                                        printChargeDetail(detail.getChargeName(),"",UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                    else
                                        printChargeDetail(detail.getChargeName(),UniversalHelper.dformat.format(billChargeGroupDetail.getChargeAmount()),UniversalHelper.dformatter.format(Double.parseDouble(chargeTotal)));
                                }
                            }
                            else {
                                if (getProperty(map.get("IS_PRINT_ZERO"), "Y").equals("N")) {
                                    if (detail.getChargeTotal().compareTo(BigDecimal.ZERO) != 0) {
                                        //changes
                                        if(detail.getChargeAmount().compareTo(BigDecimal.ZERO) == 0)
                                            printChargeDetail(detail.getChargeName(),"",UniversalHelper.dformatter.format(Double.parseDouble(detail.getChargeTotal().toString())));
                                        else
                                            printChargeDetail(detail.getChargeName(),UniversalHelper.dformat.format(detail.getChargeAmount()),UniversalHelper.dformatter.format(Double.parseDouble(detail.getChargeTotal().toString())));
                                    }
                                } else {
                                    //changes
                                    if(detail.getChargeAmount().compareTo(BigDecimal.ZERO) == 0)
                                        printChargeDetail(detail.getChargeName(),"",UniversalHelper.dformatter.format(Double.parseDouble(detail.getChargeTotal().toString())));
                                    else
                                        printChargeDetail(detail.getChargeName(),UniversalHelper.dformat.format(detail.getChargeAmount()),UniversalHelper.dformatter.format(Double.parseDouble(detail.getChargeTotal().toString())));
                                }
                            }
                            if(llDisc)
                                cnt++;
                            if(llSub)
                                cnt_llSub++;
                        }
                    }

                }
                printTextWithDelay("--------------------------------");
                if (getProperty(map.get("IS_PRINT_SUB_TOTAL"), "Y").equals("Y")) {
                    if (getProperty(map.get("IS_PRINT_RATE_SUB_TOTAL"), "Y").equals("Y"))
                        printTextWithDelay(formatHelper.padRight(getProperty(billChargeGroup.getSubtotalName(),"Sub Total "), 13) +
                            formatHelper.padLeft(UniversalHelper.dformat.format(billChargeGroup.getSubtotalCharges()), 9)
                            + formatHelper.padLeft(dformatter.format(Double.parseDouble(billChargeGroup.getTotalCharges().toString())), 10));
                    else
                        printTextWithDelay(formatHelper.padRight(getProperty(billChargeGroup.getSubtotalName(),"Sub Total "), 13)
                                + formatHelper.padLeft(dformatter.format(Double.parseDouble(billChargeGroup.getTotalCharges().toString())), 19));
                    printTextWithDelay("--------------------------------");
                }
                synchronized (this) {
                    try {
                        this.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error in printing please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        status = checkPaperStatus();
        return status;
    }

    private void printChargeDetail(String chargeName,String chargeAmount,String chargeTotal){
        printTextWithDelay(formatHelper.padRight(chargeName, 13) + formatHelper.padLeft(chargeAmount, 9) + formatHelper.padLeft(chargeTotal, 10));
    }

    private String getProperty(String val, String defVal) {
//        return val == null ? defVal : val;
        if(val == null || val.equals("")){
            val=defVal;
        }
        return val;
    }

    private int printBill(String billNo) {
        gson = new Gson();
        int status = checkPaperStatus();
        if (status == 1) {
            BigDecimal pleasePayTotal = new BigDecimal(0);
            billSurchargesList = billSurchargeDao.getSurchargeByBillNo(billNo);
            String billJson = genericDao.getOneField("billJson","armBillHeader","WHERE billNo =",billNo,"ORDER BY _id DESC","");
            BillModel billModel = gson.fromJson(billJson, BillModel.class);
            header = billModel.getBillHeaders();
            billHeader = header;
            billAddonKwhs = billModel.getBillAddonKwh();
            billAddonCharges = billModel.getBillAddonCharge();
            billChargeGroups = billModel.getBillChargeGroups();
            billGroupCategories = billModel.getBillGroupCategories();
            properties = duPropertyDAO.getProperties();
            accountBillAuxList = accountBillAuxDao.getAccBillAux(header.getAcctNo());
            map = new HashMap<>();
            for (DUProperty i : properties)
                map.put(i.getPropertyName(), i.getPropertyValue());

            account = accountDao.getAccountByAcctNo(header.getOldAccountNo());

            if (getProperty(map.get("IS_PRINT_LOGO"), "Y").equals("Y")) {
                printBitMapWithDelay(logoPath, Printer.BMP_PRINT_FAST);
                printTextWithDelay(formatHelper.padLeftDynamic(".", 32));
            }
            printTextWithDelay(formatHelper.padLeftDynamic(getProperty(map.get("DU_CODE").trim(), "DU CODE"), 16), 2);
            printTextWithDelay(formatHelper.padLeftDynamic(getProperty(map.get("DU_ADDRESSLN1"), "Addrln1"), 32));
            printTextWithDelay(formatHelper.padLeftDynamic(getProperty(map.get("DU_ADDRESSLN2"), "Addrln2"), 32));
            printTextWithDelay(formatHelper.padLeftDynamic(getProperty(map.get("DU_CONTACT_NO"), "ContactNumber"), 32));
            printTextWithDelay(formatHelper.padLeftDynamic(getProperty(map.get("DU_VAT_NO"), "VAT_NO"), 32));
            if (!getProperty(map.get("HEADER_LINE_1"), "-1").equals("-1"))
                printTextWithDelay(formatHelper.padLeftDynamic(map.get("HEADER_LINE_1"),32), Integer.parseInt(getProperty(map.get("FS_HEADERLN1"), "1")));
            printTextWithDelay(" ");
            String soaLabel = getProperty(map.get("LABELED_SOA"),"").equals("") ? "STATEMENT OF ACCOUNT" : getProperty(map.get("LABELED_SOA"),"");

            if(getProperty(map.get("IS_SOA_FONT_BIG"), "N").equals("N"))
                printLabelSOA(soaLabel.split("%").length,soaLabel,1,32);
            else
                printLabelSOA(soaLabel.split("%").length,soaLabel,2,16);

            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padRight("Rundatetime", 11) + formatHelper.padLeft(header.getRunDate(), 21));
            printTextWithDelay(formatHelper.padRight("Bill No", 12) + formatHelper.padLeft(header.getBillNo(), 20));
            printTextWithDelay("--------------------------------");
            String acctNumLabel;
            int acctNumLength=0;
            if(getProperty(map.get("ACCT_NO_LABEL"), "Acct No").toLowerCase().equals("null"))
                acctNumLabel="";
            else acctNumLabel=getProperty(map.get("ACCT_NO_LABEL"), "Acct No");

            //Accout number property is on top(Y)
            if(getProperty(map.get("IS_ACCT_NO_TOP_OF_NAME"), "N").equals("Y")) {
                if (getProperty(map.get("IS_HIGHLIGHT_ACCT_NO"), "N").equals("Y"))
                    printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                else if (getProperty(map.get("IS_ACCT_NO_FONT_BIG"), "N").equals("Y"))
                    if (header.getOldAccountNo().length() > 9) {
                        if (acctNumLabel.equals(""))
                            printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                        else {
                            printTextWithDelay(formatHelper.padRight(acctNumLabel, 12), 2);
                            printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                        }
                    } else {
                        if (acctNumLabel.equals(""))
                            printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                        else
                            printTextWithDelay(formatHelper.padRight(acctNumLabel, 7) + formatHelper.padLeft(header.getOldAccountNo(), 9), 2);
                    }
                else{
                    if(acctNumLabel.equals(""))
                        printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 32));
                    else
                        printTextWithDelay(formatHelper.padRight(acctNumLabel, 12) + formatHelper.padLeft(header.getOldAccountNo(), 20));
                }

            }

            List<String> nameList = formatHelper.formatStringByMaxLength(header.getAcctName(), 32);
            for (String name : nameList)
                if(getProperty(map.get("IS_ACCOUNT_NAME_FONT_BIG"), "N").equals("Y"))
                    printTextWithDelay(formatHelper.padRightEllipses(name, 16),2);
                else
                    printTextWithDelay(formatHelper.padLeftDynamic(name, 32));


            if(getProperty(map.get("IS_ADDRESS_BOTTOM_OF_NAME"), "N").equals("Y")){
                if(getProperty(map.get("ADDRESS_LABEL"), "Address").toLowerCase().equals("null")) {
                    printTextWithDelay(formatHelper.padLeftDynamic(WordUtils.capitalizeFully(account.getAddressLn1()), 32));
                    printTextWithDelay(formatHelper.padLeftDynamic(WordUtils.capitalizeFully(account.getAddressLn2()), 32));
                }else {
                    printTextWithDelay(formatHelper.padRight("Address", 9) + formatHelper.padLeft(WordUtils.capitalizeFully(account.getAddressLn1()), 23));
                    printTextWithDelay(formatHelper.padRight(" ", 2) + formatHelper.padLeft(WordUtils.capitalizeFully(account.getAddressLn2()), 30));
                }
            }

            //Accout number property is on top(N)
            if(getProperty(map.get("IS_ACCT_NO_TOP_OF_NAME"), "N").equals("N")) {
                if (getProperty(map.get("IS_HIGHLIGHT_ACCT_NO"), "N").equals("Y"))
                    printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                else if (getProperty(map.get("IS_ACCT_NO_FONT_BIG"), "N").equals("Y"))
                    if (header.getOldAccountNo().length() > 9) {
                        if (acctNumLabel.equals(""))
                            printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                        else {
                            printTextWithDelay(formatHelper.padRight(acctNumLabel, 12), 2);
                            printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                        }
                    } else {
                        if (acctNumLabel.equals(""))
                            printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 16), 2);
                        else
                            printTextWithDelay(formatHelper.padRight(acctNumLabel, 7) + formatHelper.padLeft(header.getOldAccountNo(), 9), 2);
                    }
                else{
                    if(acctNumLabel.equals(""))
                        printTextWithDelay(formatHelper.padLeftDynamic(header.getOldAccountNo(), 32));
                    else
                        printTextWithDelay(formatHelper.padRight(acctNumLabel, 12) + formatHelper.padLeft(header.getOldAccountNo(), 20));
                }
            }

            if (account.getSin() != null && !account.getSin().equals(""))
                if(getProperty(map.get("IS_SIN_FONT_BIG"), "N").equals("Y"))
                    if(account.getSin().length()>13){
                        printTextWithDelay(formatHelper.padRight("SIN ", 16),2);
                        printTextWithDelay(formatHelper.padLeftDynamic(account.getSin(), 16),2);
                    }else {
                        printTextWithDelay(formatHelper.padRight("SIN", 3)+formatHelper.padLeft(account.getSin(), 13),2);
                    }
                else
                    printTextWithDelay(formatHelper.padRight("SIN", 12) + formatHelper.padLeft(account.getSin(), 20));

            if (getProperty(map.get("IS_METER_NO_TOP_OF_ROUTE"), "N").equals("Y"))
                printTextWithDelay(formatHelper.padRight("Meter No", 12) + formatHelper.padLeft(header.getMeterNo(), 20));
            if (getProperty(map.get("IS_PRINT_ROUTE_CODE"), "N").equals("Y"))
                printTextWithDelay(formatHelper.padRight("Route Code", 12) + formatHelper.padLeft(header.getRouteCode(), 20));
            if (getProperty(map.get("IS_PRINT_SEQUENCE_NUMBER"), "N").equals("Y"))
                printTextWithDelay(formatHelper.padRight("Seq#", 12) + formatHelper.padLeft(String.valueOf(header.getSequenceNo()), 20));
            if (getProperty(map.get("IS_METER_NO_TOP_OF_ROUTE"), "N").equals("N"))
                printTextWithDelay(formatHelper.padRight("Meter No", 12) + formatHelper.padLeft(header.getMeterNo(), 20));
            if(getProperty(map.get("IS_ADDRESS_BOTTOM_OF_NAME"), "N").equals("N")){
                if(getProperty(map.get("ADDRESS_LABEL"), "Address").toLowerCase().equals("null")) {
                    printTextWithDelay(formatHelper.padLeftDynamic(WordUtils.capitalizeFully(account.getAddressLn1()), 32));
                    printTextWithDelay(formatHelper.padLeftDynamic(WordUtils.capitalizeFully(account.getAddressLn2()), 32));
                }else {
                    printTextWithDelay(formatHelper.padRight("Address", 9) + formatHelper.padLeft(WordUtils.capitalizeFully(account.getAddressLn1()), 23));
                    printTextWithDelay(formatHelper.padRight(" ", 2) + formatHelper.padLeft(WordUtils.capitalizeFully(account.getAddressLn2()), 30));
                }
            }

            if (header.getConsumerType().length() < 11)
                conType = header.getConsumerType();
            else
                conType = header.getConsumerType().substring(0, 11) + "...";
            if(account.getMinimumContractedEnergy() > 0)
                printTextWithDelay(formatHelper.padRight("MCE", 12) + formatHelper.padLeft(UniversalHelper.df.format(account.getMinimumContractedEnergy()), 20));
            printTextWithDelay(formatHelper.padRight("Type", 5) + formatHelper.padLeft(conType, 27));

            printTextWithDelay("--------------------------------");
            if(getProperty(map.get("IS_PRINT_PREVIOUS_CONSUMPTION"), "N").equals("Y"))
                printTextWithDelay(formatHelper.padRight("Previous kWh Used", 17) + formatHelper.padLeft(UniversalHelper.df.format(account.getCurrentConsumption()), 15));
            printTextWithDelay(formatHelper.padRight("Current Reading", 17) + formatHelper.padLeft(UniversalHelper.df.format(header.getCurReading()), 15));
            printTextWithDelay(formatHelper.padRight("Previous Reading", 17) + formatHelper.padLeft(UniversalHelper.df.format(header.getPrevReading()), 15));
            if (header.getCoreloss() == 0) {
                if (header.getConsumption() == header.getTotalConsumption()) {
                    printMultiplier(header.getMeterMultiplier());
                } else {
                    printTextWithDelay(formatHelper.padRight("Current kWh", 17) + formatHelper.padLeft(UniversalHelper.df.format(header.getConsumption()).toString(), 15));
                    printMultiplier(header.getMeterMultiplier());
                }
            } else {
                printTextWithDelay(formatHelper.padRight("Current kWh", 17) + formatHelper.padLeft(UniversalHelper.df.format(header.getConsumption()).toString(), 15));
                printMultiplier(header.getMeterMultiplier());
                printTextWithDelay(formatHelper.padRight("Coreloss", 17) + formatHelper.padLeft(UniversalHelper.df.format(header.getCoreloss()).toString(), 15));
            }
            // Print Addon Kwh
            if (billAddonKwhs != null && billAddonKwhs.size() > 0) {
                for (BillAddonKwh kwAddon : billAddonKwhs) {
                    String formattedData = WordUtils.capitalizeFully(kwAddon.getAddonKwh());
                    printTextWithDelay(formatHelper.padRight(formattedData, 24) + formatHelper.padLeft(UniversalHelper.df.format(kwAddon.getValue()), 8));
                }
                synchronized (this) {
                    try {
                        this.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error in printing bill add-on kWh. please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            printTextWithDelay(formatHelper.padRight("kWh Used", 17) + formatHelper.padLeft(UniversalHelper.df.format(header.getTotalConsumption()).toString(), 15));
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padRight("Period from", 17) + formatHelper.padLeft(header.getPeriodFrom(), 15));
            printTextWithDelay(formatHelper.padRight("Period to", 17) + formatHelper.padLeft(header.getPeriodTo(), 15));
            printTextWithDelay(formatHelper.padRight("Billing Mo", 17) + formatHelper.padLeft(header.getBillingMonth(), 15));
            printTextWithDelay("--------------------------------");
            printGroup(billChargeGroups, billGroupCategories, map);
            if (getProperty(map.get("IS_CURRENT_BILL_FONT_BIG"), "N").equals("Y"))
                printTextWithDelay(formatHelper.padRight(getProperty(map.get("CURRENT_BILL_LABEL"),"Current Bill"), 12) + formatHelper.padLeft("Php " + dformatter.format(Double.parseDouble(header.getCurBill())), 20),2);
            else
                printTextWithDelay(formatHelper.padRight(getProperty(map.get("CURRENT_BILL_LABEL"),"Current Bill"), 12) + formatHelper.padLeft("Php " + dformatter.format(Double.parseDouble(header.getCurBill())), 20));

            if (billAddonCharges != null && billAddonCharges.size() > 0) {
                printTextWithDelay("--------------------------------");
                if (billAddonCharges != null && billAddonCharges.size() > 0) {
                    for (BillAddonCharge billAddonCharge : billAddonCharges)
                        printTextWithDelay(formatHelper.padRight(billAddonCharge.getAddonCharge(), 17) + formatHelper.padLeft("Php " + dformatter.format(billAddonCharge.getValue()), 15));
                    synchronized (this) {
                        try {
                            this.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error in printing add-on charges please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            if(header.getAccountArrears() != 0 ){
                printTextWithDelay("--------------------------------");
                printTextWithDelay(formatHelper.padRight("Balance ", 16) +
                        formatHelper.padLeft("Php " + dformatter.format(header.getAccountArrears()), 16));
                if(header.getArrearsAsOf() != null && !header.getArrearsAsOf().equals("")){
                    try {
                        printTextWithDelay(formatHelper.padRight("Balance as of ", 16) +
                                formatHelper.padLeft(dueDateFormat.format(sdf.parse(header.getArrearsAsOf())), 16));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            printTextWithDelay("--------------------------------");
            if (getProperty(map.get("IS_TOTAL_AMOUNT_DUE_FONT_BIG"), "N").equals("Y"))
                printTextWithDelay(formatHelper.padRight("Total Amount Due", 16) + formatHelper.padLeft("Php " + dformatter.format(Double.parseDouble(header.getTotalAmountDue())), 16), 2);
            else
                printTextWithDelay(formatHelper.padRight("Total Amount Due", 16) + formatHelper.padLeft("Php " + dformatter.format(Double.parseDouble(header.getTotalAmountDue())), 16));


            if (getProperty(map.get("IS_PRINT_BILL_AFTER_DUE"), "N").equals("Y")) {
                printTextWithDelay("--------------------------------");
                if (getProperty(map.get("IS_PRINT_SURCHARGE"), "N").equals("Y")) {
                      /* Temporary - setup for FLECO */
                    BigDecimal totalAmountDue = new BigDecimal(header.getTotalAmountDue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal totalAmountAfterDue = new BigDecimal(header.getTotalBillAfterDueDate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal surcharge = totalAmountAfterDue.subtract(totalAmountDue);
                    if (totalAmountDue.compareTo(BigDecimal.ZERO) > 0) {
                        printTextWithDelay(formatHelper.padRight("Surcharge", 10) + formatHelper.padLeft(dformatter.format(surcharge), 22));
                        printTextWithDelay("--------------------------------");
                    }
                }
                printTextWithDelay(formatHelper.padRight("After Due Date", 16) + formatHelper.padLeft("Php " + dformatter.format(Double.parseDouble(header.getTotalBillAfterDueDate())), 16), 2);
            }

            if(accountBillAuxList != null && accountBillAuxList.size() > 0){
                printTextWithDelay("--------------------------------");
                printTextWithDelay(formatHelper.padRight("On Or Before Due Date", 32));
                printTextWithDelay("--------------------------------");
                pleasePayTotal = printAccountBillAux(accountBillAuxList).add(new BigDecimal(header.getTotalAmountDue()));
                printTextWithDelay(formatHelper.padRight("Total Amount Due", 16) + formatHelper.padLeft("Php " + dformatter.format(Double.parseDouble(header.getTotalAmountDue())), 16));
                printTextWithDelay(formatHelper.padRight("Please Pay", 16) + formatHelper.padLeft("Php " + dformatter.format(pleasePayTotal.setScale(2,BigDecimal.ROUND_HALF_UP)), 16));
            }else
                pleasePayTotal = pleasePayTotal.add(new BigDecimal(header.getTotalAmountDue()));
            BigDecimal percent = new BigDecimal(0);
            if(billSurchargesList != null && billSurchargesList.size() > 0){
                for(BillSurcharge billSurcharge : billSurchargesList){
                    printTextWithDelay("--------------------------------");
                    printTextWithDelay(formatHelper.padRight("W/in " + billSurcharge.getDays() + " Day(s) After Due Date", 32));
                    printTextWithDelay("--------------------------------");
                    if(accountBillAuxList != null && accountBillAuxList.size() > 0)
                        printAccountBillAux(accountBillAuxList);
                    pleasePayTotal = pleasePayTotal.add(billSurcharge.getSurcharge()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    percent = new BigDecimal(100).multiply(billSurcharge.getSurchargeRate()).setScale(0,BigDecimal.ROUND_DOWN);
                    printTextWithDelay(formatHelper.padRight("Surcharge(" + percent +"%)", 16) + formatHelper.padLeft("Php " + dformatter.format(billSurcharge.getSurcharge()), 16));
                    printTextWithDelay(formatHelper.padRight("Total Amount Due", 16) + formatHelper.padLeft("Php " + dformatter.format(Double.parseDouble(header.getTotalAmountDue())), 16));
                    printTextWithDelay(formatHelper.padRight("Please Pay", 16) + formatHelper.padLeft("Php " + dformatter.format(pleasePayTotal), 16));
                    pleasePayTotal = pleasePayTotal.subtract(billSurcharge.getSurcharge());
                }
                synchronized (this) {
                    try {
                        this.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error in printing bill surcharge. please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padRight("Reader", 12) + formatHelper.padLeft(header.getReader(), 20));
            printTextWithDelay(formatHelper.padRight("Device ID", 12) + formatHelper.padLeft(Integer.toString(header.getDeviceId()), 20));
            try {
                if (getProperty(map.get("IS_DUE_DATE_FONT_BIG"), "N").equals("Y"))
                    printTextWithDelay(formatHelper.padRight("Due Date", 12) + formatHelper.padLeft(dueDateFormat.format(sdf.parse(header.getDueDate())), 20),2);
                else
                    printTextWithDelay(formatHelper.padRight("Due Date", 12) + formatHelper.padLeft(dueDateFormat.format(sdf.parse(header.getDueDate())), 20));

                if (getProperty(map.get("IS_PRINT_DISCONNECTION_DATE"), "N").equals("Y"))
                    printTextWithDelay(formatHelper.padRight("Disconnection Date", 20) + formatHelper.padLeft(dueDateFormat.format(sdf.parse(header.getDiscoDate())), 12));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            printTextWithDelay("--------------------------------");
            if (getProperty(map.get("IS_DISCONNECTION_NOTICE_ALWAYS_ON"), "Y").equals("Y")){
                if (!getProperty(map.get("FOOTER_LINE_1"), "-1").equals("-1"))
                    printTextWithDelay(formatHelper.padLeftDynamic(map.get("FOOTER_LINE_1"),32), Integer.parseInt(getProperty(map.get("FS_FOOTERLN1"), "1")));


                if (!getProperty(map.get("FOOTER_LINE_2"), "-1").equals("-1"))
                    printTextWithDelay(formatHelper.padLeftDynamic(map.get("FOOTER_LINE_2"),32), Integer.parseInt(getProperty(map.get("FS_FOOTERLN2"), "1")));

                if (!getProperty(map.get("FOOTER_LINE_3"), "-1").equals("-1"))
                    printTextWithDelay(formatHelper.padLeftDynamic(map.get("FOOTER_LINE_3"),32), Integer.parseInt(getProperty(map.get("FS_FOOTERLN3"), "1")));
            }



            if (getProperty(map.get("IS_PRINT_BAR_CODE"), "N").equals("Y")) {
                printTextWithDelay(formatHelper.padLeftDynamic(" "  , 32));
                generateBarcode(header.getOldAccountNo());
                printBitMapWithDelay(barCodePath, Printer.BMP_PRINT_FAST);
                printTextWithDelay(formatHelper.padLeftDynamic(" "  , 32));
                printTextWithDelay(formatHelper.padRight("Acct Name", 12) + formatHelper.padLeft(header.getAcctName(), 20));
                printTextWithDelay(formatHelper.padRight("Amount", 12) + formatHelper.padLeft(dformatter.format(Double.parseDouble(header.getTotalAmountDue())), 20));
            }
            if (getProperty(map.get("IS_PRINT_QR_CODE"), "N").equals("Y")) {
                generateQRCode(header);
                printBitMapWithDelay(qrPath, Printer.BMP_PRINT_FAST);

            }
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padLeftDynamic("This is NOT an official receipt", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("Please pay at any " + getProperty(map.get("DU_CODE").trim(), "DU CODE") , 32));
            printTextWithDelay(formatHelper.padLeftDynamic("Business Center", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("Powered by: XESI", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("Version: " + sharedPref.getString("version", " "), 32));
            printTextWithDelay(formatHelper.padLeftDynamic("www.xenenergy.com.ph", 32));

            if (getProperty(map.get("IS_ARREARS_DISCONNECTION_NOTICE"), "N").equals("Y")){
                if(accountDao.getArrearsByAcctNo(account.getOldAccountNumber()).getArrears()>0) {

                    printTextWithDelay("");
                    printTextWithDelay("");
                    printTextWithDelay("--------------------------------");
                    printTextWithDelay(formatHelper.padLeftDynamic("DISCONNECTION", 16),2);
                    printTextWithDelay(formatHelper.padLeftDynamic("NOTICE", 16),2);
                    printTextWithDelay("");
                    printTextWithDelay(formatHelper.padRight("Acct No", 7)+formatHelper.padLeft(header.getOldAccountNo(), 25));
                    printTextWithDelay(formatHelper.padRight("Balance", 7)+formatHelper.padLeft("Php "+dformatter.format(header.getAccountArrears()), 25));
                    printTextWithDelay("");
                    if(!getProperty(map.get("ARREARS_DISCONNECTION_MESSAGE1"), "").equals(""))
                        printTextWithDelay(formatHelper.padLeftDynamic(map.get("ARREARS_DISCONNECTION_MESSAGE1"), 32));
                    if(!getProperty(map.get("ARREARS_DISCONNECTION_MESSAGE2"), "").equals(""))
                        printTextWithDelay(formatHelper.padLeftDynamic(map.get("ARREARS_DISCONNECTION_MESSAGE2"), 32));
                    if(!getProperty(map.get("ARREARS_DISCONNECTION_MESSAGE3"), "").equals(""))
                        printTextWithDelay(formatHelper.padLeftDynamic(map.get("ARREARS_DISCONNECTION_MESSAGE3"), 32));
                    if(!getProperty(map.get("ARREARS_DISCONNECTION_MESSAGE4"), "").equals(""))
                        printTextWithDelay(formatHelper.padLeftDynamic(map.get("ARREARS_DISCONNECTION_MESSAGE4"), 32));
                    if(!getProperty(map.get("ARREARS_DISCONNECTION_MESSAGE5"), "").equals(""))
                        printTextWithDelay(formatHelper.padLeftDynamic(map.get("ARREARS_DISCONNECTION_MESSAGE5"), 32));
                }
            }
            print.printEndLine();
        }
        status = checkPaperStatus();
        return status;
    }

    private void printLabelSOA(int count,String soaLabel, int fontSize, int lengthParam){
        int start = 0;
        int percentChecker =0;
        for(int i = 0; i < count; i++){
            percentChecker =soaLabel.indexOf("%",start);
            if(percentChecker > 0){
                printTextWithDelay(formatHelper.padLeftDynamic(soaLabel.substring(start,percentChecker), lengthParam),fontSize);
                start = percentChecker + 1;
            }else
                printTextWithDelay(formatHelper.padLeftDynamic(soaLabel.substring(soaLabel.lastIndexOf("%",soaLabel.length()) + 1,soaLabel.length()), lengthParam),fontSize);
        }
    }
    private BigDecimal printAccountBillAux(List<AccountBillAux> accountBillAuxes){
        BigDecimal auxSurChargeTotal = new BigDecimal(0);
        for(AccountBillAux accountBillAux : accountBillAuxes){
            auxSurChargeTotal = auxSurChargeTotal.add(accountBillAux.getChargeAmount());
            printTextWithDelay(formatHelper.padRight(accountBillAux.getChargeName(), 16) + formatHelper.padLeft("Php " + dformatter.format(accountBillAux.getChargeAmount()), 16));
        }
        return auxSurChargeTotal;
    }

    private int printDOE() {
        int status = checkPaperStatus();
        if (status == 1) {
            printTextWithDelay(formatHelper.padLeftDynamic("MONTHLY CONSUMPTION", 32));
            printBitMapWithDelay(graph, Printer.BMP_PRINT_FAST);
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padLeftDynamic("SUMMARY OF CHARGES", 32));
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padRight("Generation", 19) + formatHelper.padLeft("537.64", 13));
            printTextWithDelay(formatHelper.padRight("Transmission", 15) + formatHelper.padLeft("58.80", 17));
            printTextWithDelay(formatHelper.padRight("System Loss", 19) + formatHelper.padLeft("78.41", 13));
            printTextWithDelay(formatHelper.padRight("Distribution", 19) + formatHelper.padLeft("51.43", 13));
            printTextWithDelay(formatHelper.padRight("Sub/Disc", 22) + formatHelper.padLeft("2.94", 10));
            printTextWithDelay(formatHelper.padRight("Gov't Taxes", 22) + formatHelper.padLeft("101.71", 10));
            printTextWithDelay(formatHelper.padRight("Universal Charge", 22) + formatHelper.padLeft("25.63", 10));
            printTextWithDelay(formatHelper.padRight("Total", 22) + formatHelper.padLeft("856.56", 10));
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padLeftDynamic("BILL DEPOSIT", 32));
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padRight("Bill Deposit", 15) + formatHelper.padLeft(UniversalHelper.df.format(account.getBillDeposit() != null ? account.getBillDeposit() : "0.00"), 17));
            printTextWithDelay(formatHelper.padRight("Interest Earned", 19) + formatHelper.padLeft(UniversalHelper.df.format(account.getBillDepositInterest() != null ? account.getBillDepositInterest() : "0.00"), 13));
            printTextWithDelay(formatHelper.padRight("Total Bill Deposit", 19) + formatHelper.padLeft(UniversalHelper.df.format(account.getTotalBillDeposit() != null ? account.getTotalBillDeposit() : "0.00"), 13));
            printTextWithDelay(formatHelper.padRight("Last Payment Date", 19) + formatHelper.padLeft(account.getLastPaymentDate() != null ? account.getLastPaymentDate() : " ", 13));
            printTextWithDelay(formatHelper.padRight("Last Deposit Payment", 22) + formatHelper.padLeft(UniversalHelper.df.format(account.getLastDepositPayment() != null ? account.getLastDepositPayment() : "0.00"), 10));
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padLeftDynamic("CUSTOMER CARE", 32));
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padRight("Email", 7) + formatHelper.padLeft(map.get("DU_EMAIL") != null ? map.get("DU_EMAIL") : "", 25));
            printTextWithDelay(formatHelper.padRight("Website", 10) + formatHelper.padLeft(map.get("DU_WEBSITE") != null ? map.get("DU_WEBSITE") : "", 22));
            printTextWithDelay(formatHelper.padRight("FB Acct", 8) + formatHelper.padLeft(map.get("DU_FB") != null ? map.get("DU_FB") : "", 24));
            printTextWithDelay(formatHelper.padRight("Twitter Acct", 12) + formatHelper.padLeft(map.get("DU_TWITTER") != null ? map.get("DU_TWITTER") : "", 20));
            printTextWithDelay(formatHelper.padRight("ERC Contact #", 17) + formatHelper.padLeft(map.get("ERC_CONTACT_NUMBER") != null ? map.get("ERC_CONTACT_NUMBER") : "", 15));
            printTextWithDelay(formatHelper.padRight("ERC Email", 10) + formatHelper.padLeft(map.get("ERC_EMAIL") != null ? map.get("ERC_EMAIL") : "", 22));
            printTextWithDelay("--------------------------------");
            printTextWithDelay(formatHelper.padLeftDynamic("ALL DISPUTE THAT CANNOT BE", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("SETTLED CAN BE ELEVATED TO ERC", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("Note: Please present this bill", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("upon payment on any of our", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("authorized payment collection", 32));
            printTextWithDelay(formatHelper.padLeftDynamic("partners", 32));
            print.printEndLine();
        }
        return status;
    }


    public int printReceivingCopy() {
        int status = checkPaperStatus();
        if (status == 1) {
            printTextWithDelay(formatHelper.padLeft("Receiving Copy", 15), 2);
            printTextWithDelay("--------------------------------");
            printTextWithDelay("This is to certify that I have ");
            printTextWithDelay("received the STATEMENT OF");
            printTextWithDelay("ACCOUNT for:");
            printTextWithDelay(" ");
            printTextWithDelay(formatHelper.padRight("Date:", 12) + formatHelper.padLeft(billHeader.getRunDate(), 20));
            printTextWithDelay(formatHelper.padRight("Account No:", 12) + formatHelper.padLeft(billHeader.getOldAccountNo(), 20));
            printTextWithDelay(formatHelper.padRight("Meter No:", 12) + formatHelper.padLeft(billHeader.getMeterNo(), 20));
            printTextWithDelay(formatHelper.padRight("Name:", 12) + formatHelper.padLeft(WordUtils.capitalizeFully(billHeader.getAcctName()), 20));
            printTextWithDelay(formatHelper.padRight("Bill No:", 12) + formatHelper.padLeft(billHeader.getBillNo(), 20));
            printTextWithDelay(formatHelper.padRight("Current Rdg:", 17) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getCurReading()).toString(), 15));
            printTextWithDelay(formatHelper.padRight("Previous Rdg:", 17) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getPrevReading()).toString(), 15));
            printTextWithDelay(formatHelper.padRight("Consumption:", 17) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getTotalConsumption()).toString(), 15));
            print.printEndLine();
            printTextWithDelay("--------------------------------");
            printTextWithDelay("      PRINT NAME/SIGNATURE");
            print.printEndLine();
        }
        status = checkPaperStatus();
        return status;
    }

    public int printReading(BillHeader billHeader) {
        int status = checkPaperStatus();
        if (status == 1) {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date;
            printTextWithDelay(" Meter  Reading ", 2);
            printTextWithDelay("\n");
            try {
                date = dt.parse(billHeader.getRunDate());
                printTextWithDelay(formatHelper.padRight("Reading Date:", 15) + formatHelper.padLeft(dt.format(date).toString(), 17));
            } catch (ParseException e) {
                printTextWithDelay(formatHelper.padRight("Reading Date:", 17) + formatHelper.padLeft(billHeader.getRunDate(), 15));
            }
            printTextWithDelay(formatHelper.padRight("Bill No:", 12) + formatHelper.padLeft(billHeader.getBillNo(), 20));
            printTextWithDelay(formatHelper.padRight("Acct No:", 15) + formatHelper.padLeft(billHeader.getOldAccountNo(), 17));
            printTextWithDelay(formatHelper.padRight("Acct Name:", 10) + formatHelper.padLeft(WordUtils.capitalizeFully(billHeader.getAcctName()), 22));
            printTextWithDelay(formatHelper.padRight("Previous Reading:", 20) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getPrevReading()).toString(), 12));
            printTextWithDelay(formatHelper.padRight("Current Reading:", 20) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getCurReading()).toString(), 12));
            if (billHeader.getConsumption() < billHeader.getTotalConsumption()) {
                printTextWithDelay(formatHelper.padRight("Consumption:", 20) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getConsumption()).toString(), 12));
            }
            if (billHeader.getMeterMultiplier() > 1) {
                printTextWithDelay(formatHelper.padRight("Meter Multiplier:", 22) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getMeterMultiplier()).toString(), 10));
            }
            if (billHeader.getCoreloss() > 0) {
                printTextWithDelay(formatHelper.padRight("Coreloss:", 20) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getCoreloss()).toString(), 12));
            }
            if (billHeader.getAddonKwhTotal() > 0) {
                printTextWithDelay(formatHelper.padRight("Add-on kWh:", 25) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getAddonKwhTotal()).toString(), 7));
            }
            printTextWithDelay(formatHelper.padRight("kWh Used:", 25) + formatHelper.padLeft(UniversalHelper.df.format(billHeader.getTotalConsumption()).toString(), 7));

            print.printEndLine();
            status = checkPaperStatus();
        }
        return status;
    }

    //Print Bill Summary
    private int printSummaryInTabular() {
        int status = checkPaperStatus();
        if (status == 1) {
            List<BillHeader> bills;
            String acctNo;
            int a =0;
            List<String> routeList = accountDao.getRouteList();
            if (routeList.size() > 0) {
                printTextWithDelay("  Bill Summary  ", 2);
                printTextWithDelay("");
                for (String route : routeList) {
                    bills = billHeaderDAO.getAllBillList("*","WHERE routeCode =",route,"ORDER BY isUploaded , seqNo");
                    if (bills.size() > 0) {
                        printTextWithDelay("Route : " + bills.get(0).getRouteCode());
                        printTextWithDelay("Bill Month : " + bills.get(0).getBillingMonth());
                        printTextWithDelay("--------------------------------");
                        printTextWithDelay(UniversalHelper.padRight("Seq#", 4) + " |"
                                + UniversalHelper.padRight("Acct", 4) + " |"
                                + UniversalHelper.padLeft("Pres. Rdg", 9) + "|"
                                + UniversalHelper.padLeft("Prev. Rdg", 9));
                        printTextWithDelay("--------------------------------");
                        for (BillHeader billHeader : bills) {
                            a++;
                            acctNo = billHeader.getOldAccountNo();
                            if (acctNo.length() > 4)
                                acctNo = acctNo.substring(acctNo.length() - 4);
                            printTextWithDelay(
                                    UniversalHelper.padRight(Integer.toString(billHeader.getSequenceNo()), 4) + " |"
                                            + UniversalHelper.padRight(acctNo, 4) + " |"
                                            + UniversalHelper.padLeft(UniversalHelper.df.format(billHeader.getCurReading()), 9) + "|"
                                            + UniversalHelper.padLeft(UniversalHelper.df.format(billHeader.getPrevReading()), 9));
                            if(a == 10){
                                try {
                                    Thread.sleep(1200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                a=0;
                            }
                        }
                        printTextWithDelay("--------------------------------");
                        printTextWithDelay("Total : " + bills.size());
                        printTextWithDelay("--------------------------------");
                    }
                    bills.clear();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("LOOPDELAY", "Delaying 2 Seconds");
                }
                routeList.clear();
            }
        }
        print.printEndLine();
        status = checkPaperStatus();
        return status;
    }

    private int printSummary() {
        int status = checkPaperStatus();
        Double totKwh=0.00, totBill=0.00;
        String reader="";
        if (status == 1) {
            List<BillHeader> bills = billHeaderDAO.getAllBillList("*","where isArchive = ","N","ORDER BY isUploaded ASC, _id DESC");
            for (BillHeader billHeader : bills) {
                printTextWithDelay(UniversalHelper.padRight("Reading Date.", 14)
                        + UniversalHelper.padLeft(billHeader.getRunDate(), 18));
                printTextWithDelay(UniversalHelper.padRight("Bill No.", 15)
                        + UniversalHelper.padLeft(billHeader.getBillNo(), 17));
                printTextWithDelay(UniversalHelper.padRight("Acct No.", 15)
                        + UniversalHelper.padLeft(billHeader.getOldAccountNo(), 17));
                printTextWithDelay(UniversalHelper.padRight("Meter No.", 15)
                        + UniversalHelper.padLeft(billHeader.getMeterNo(), 17));
                printTextWithDelay(UniversalHelper.padRight("Acct Name", 11)
                        + UniversalHelper.padLeft(billHeader.getAcctName(), 21));
                printTextWithDelay(UniversalHelper.padRight("Previous Reading.", 18)
                        + UniversalHelper.padLeft(UniversalHelper.df.format(billHeader.getPrevReading()), 14));
                printTextWithDelay(UniversalHelper.padRight("Current Reading.", 17)
                        + UniversalHelper.padLeft(UniversalHelper.df.format(billHeader.getCurReading()), 15));
                printTextWithDelay(UniversalHelper.padRight("kWh Used.", 19)
                        + UniversalHelper.padLeft(UniversalHelper.df.format(billHeader.getTotalConsumption()), 13));
                printTextWithDelay(UniversalHelper.padRight("Total Bill", 16)
                        + UniversalHelper.padLeft(  "Php " + dformatter.format(Double.parseDouble(billHeader.getTotalAmountDue())), 16));
                printTextWithDelay(UniversalHelper.padRight("Remarks.", 15)
                        + UniversalHelper.padLeft(billHeader.getRemarks(), 17));
                printTextWithDelay("------------------------------");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("LOOPDELAY", "Delaying 2 Seconds");
                totKwh=totKwh+billHeader.getTotalConsumption();
                totBill=totBill+Double.parseDouble(billHeader.getTotalAmountDue());
                reader=billHeader.getReader();
            }
            printTextWithDelay(UniversalHelper.padRight("Total Number Of Bills.", 22)
                    + UniversalHelper.padLeft(String.valueOf(bills.size()), 10));
            printTextWithDelay("------------------------------");
            List<String> routes = accountDao.getRouteList();
            String bookNo="";
            for (int i=0; i<routes.size();i++){
                bookNo=bookNo+","+routes.get(i);
            }
            String recordCountQuery = genericDao.getOneField("SELECT COUNT(id) FROM arm_account","0");
            String readAcct = genericDao.getOneField("SELECT COUNT(id) FROM arm_account where isRead=1","0");
            String unreadAcct = genericDao.getOneField("SELECT COUNT(id) FROM arm_account where isRead=0","0");

            printTextWithDelay(UniversalHelper.padRight("Book# ",16) + UniversalHelper.padLeft(bookNo.substring(1),16));
            printTextWithDelay(UniversalHelper.padRight("Total Read ",16) + UniversalHelper.padLeft(readAcct,16));
            printTextWithDelay(UniversalHelper.padRight("Total Unread ",16) + UniversalHelper.padLeft(unreadAcct,16));
            printTextWithDelay(UniversalHelper.padRight("Total Records ",16) + UniversalHelper.padLeft(recordCountQuery,16));
            printTextWithDelay(UniversalHelper.padRight("Total kwh ",16) + UniversalHelper.padLeft(String.valueOf(totKwh),16));
            printTextWithDelay(UniversalHelper.padRight("Total Amount ",16) +UniversalHelper.padLeft("Php " + dformatter.format(totBill),16));
            printTextWithDelay(UniversalHelper.padRight("Meter Reader ",16) + UniversalHelper.padLeft(reader,16));
            print.printEndLine();
        }
        status = checkPaperStatus();
        return status;
    }

    private int printNotification(AccountModelV2 accountModelV2) {
        //coding here
        int status = checkPaperStatus();
        if (status == 1) {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();
            printTextWithDelay(formatHelper.padLeftDynamic("Notification",16), 2);
            printTextWithDelay("\n");
            printTextWithDelay(formatHelper.padRight("Acct No.", 15)
                    + formatHelper.padLeft(accountModelV2.getOldAccountNumber(), 17));
            printTextWithDelay(formatHelper.padRight("Acct Name", 11)
                    + formatHelper.padLeft(accountModelV2.getAccountName(), 21));
            printTextWithDelay(formatHelper.padRight("Meter No.", 15)
                    + formatHelper.padLeft(accountModelV2.getMeterNumber(), 17));
            printTextWithDelay(formatHelper.padRight("Route Code", 15)
                    + formatHelper.padLeft(accountModelV2.getRouteCode(), 17));
            printTextWithDelay(formatHelper.padRight("Address", 9) + formatHelper.padLeft(WordUtils.capitalizeFully(accountModelV2.getAddressLn1()), 23));
            printTextWithDelay(formatHelper.padRight(" ", 2) + formatHelper.padLeft(WordUtils.capitalizeFully(accountModelV2.getAddressLn2()), 30));
            printTextWithDelay(formatHelper.padRight("Date & Time", 16) +
                    formatHelper.padLeft(dt.format(date).toString(), 16));
            printTextWithDelay(" ");
            if (duPropertyDAO.getPropertyValue("IS_PRINT_BAR_CODE").equals("Y")) {
                generateBarcode(accountModelV2.getOldAccountNumber());
                printBitMapWithDelay(barCodePath, Printer.BMP_PRINT_FAST);
            }

            printTextWithDelay(" ");
            printTextWithDelay(formatHelper.padLeftDynamic("Your bill will be delivered soon" , 32));
            printTextWithDelay(formatHelper.padLeftDynamic("or you may visit our" , 32));
            printTextWithDelay(formatHelper.padLeftDynamic( "nearest " +duPropertyDAO.getPropertyValue("DU_CODE") + " office", 32));
            printTextWithDelay(formatHelper.padLeftDynamic( "for bill clarification", 32));
            printTextWithDelay(formatHelper.padLeftDynamic( "within 5 working days.", 32));

            print.printEndLine();
        }
        status = checkPaperStatus();
        return status;

    }

    private void updateIsPrinted(String billNo) {
        String billJson = genericDao.getOneField("billJson","armBillHeader","WHERE billNo =",billNo,"ORDER BY _id DESC","");
        String revBillJson = billJson.replace("\"isPrinted\":0", "\"isPrinted\":1");
        billHeaderDAO.updateIsPrinted(billNo, revBillJson);
    }

    private class ClusterPrintOnBackground extends AsyncTask<Void, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setTitle("Printing");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            String message;
            if (progressBar != null) {
                progressBar.dismiss();
            }
            if (integer == 1)
                msgDialog.showReportSuccessDialog("Printing finished");
            else if (integer == -2)
                msgDialog.showReportSuccessDialog("All bills are already printed.");
            else {
                message = UniversalHelper.checkPaperStatus(integer);
                if (!message.equals("OK"))
                    msgDialog.showErrDialog("Printer error encounter\nError Code: " + integer + "\nMessage: " + message);
            }

        }

        @Override
        protected Integer doInBackground(Void... aVoid) {
            List<String> billNoList = billHeaderDAO.printAllBills();
            int status = -3;
            if (billNoList.size() == 0) {
                status = -2;
            } else {
                for (String billNo : billNoList) {
                    try {
                        Log.d("PRINT", "Start Printing " + billNo);
                        if (print.voltageCheck()) {
                            //  status = printCluster(billNo);
                            status = printBill(billNo);
                            if (status == 1) {
                                updateIsPrinted(billNo);
                            } else {
                                Log.d("PRINT", "Error Status " + status);
                                break;
                            }
                            for (int x = 5; x >= 1; x--) {
                                publishProgress("Cooling Down, will resume " +
                                        "\nin " + x + " seconds");
                                Thread.sleep(1000);
                            }
                            publishProgress("Printing continue");
                        } else {
                            status = -4;
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            return status;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressBar.setMessage(values[0]);
        }
    }

    private class PrintOnBackground extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(context);
            progressBar.setTitle("Printing");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            String message;
            if (progressBar != null) {
                progressBar.dismiss();
            }
            if (integer == 1) {
                if(isSpikeDrop.equals("Y")){
                    msgDialog.showAlert(context, "Print Notification.", value1 -> {
                        if (value1.equals("OK")){
                            PrintNotificationOnBackground printNotificationOnBackground = new PrintNotificationOnBackground();
                            printNotificationOnBackground.execute(account);
                        }
                    });
                }else if (getProperty(map.get("IS_PRINT_DOE_COMPLIANCE"), "Y").equals("Y")){
                    msgDialog.showAlert(context, "Print DOE compliance.", value1 -> {
                        if (value1.equals("OK")){
                            PrintDOEOnBackground printDOEOnBackground = new PrintDOEOnBackground();
                            printDOEOnBackground.execute();
                        }
                    });
                } else{
                    msgDialog.showAlert(context, "Print receiving copy.", value2 -> {
                        if (value2.equals("OK")) {
                            PrintReceiveCopyOnBackground receiveCopyOnBackground = new PrintReceiveCopyOnBackground();
                            receiveCopyOnBackground.execute();
                        }else
                            msgDialog.showPrintSuccessDialog("Finished Printing",isLastSeq);
                    });
                }
            }  else {
                message = UniversalHelper.checkPaperStatus(integer);
                if (!message.equals("OK"))
                    msgDialog.showErrDialog("Printer error encounter\nError Code: " + integer + "\nMessage: " + message);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            int status = -3;
            try {
                Log.d("PRINT", "Start Printing " + params[0]);
                if (print.voltageCheck()) {
                    //   status = printCluster(params[0]);
                    status = printBill(params[0]);
                    if (status == 1)
                        updateIsPrinted(params[0]);
                } else {
                    status = -4;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressBar.setMessage(values[0]);
        }
    }

    private class PrintSummaryOnBackground extends AsyncTask<Void, Void, Integer> {
        ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(context);
            progressBar.setTitle("Printing");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            String message;
            if (progressBar != null) {
                progressBar.dismiss();
            }
            if (integer == 1)
                msgDialog.showReportSuccessDialog("Printing finished");
            else if (integer == -2)
                msgDialog.showReportSuccessDialog("All bills are already printed.");
            else {
                message = UniversalHelper.checkPaperStatus(integer);
                if (!message.equals("OK"))
                    msgDialog.showErrDialog("Printer error encounter\nError Code: " + integer + "\nMessage: " + message);
            }
        }

        @Override
        protected Integer doInBackground(Void... aVoid) {
            int status;
            if (duPropertyDAO.getPropertyValue("IS_PRINT_IN_TABULAR_FORM").equals("Y")) {
                status = printSummaryInTabular();
            } else {
                status = printSummary();
            }
            return status;
        }
    }

    private class PrintReadingOnBackground extends AsyncTask<BillHeader, String, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setTitle("Printing");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            String message;
            if (progressBar != null) {
                progressBar.dismiss();
            }
            if (integer != 1){
                message = UniversalHelper.checkPaperStatus(integer);
                if (!message.equals("OK"))
                    msgDialog.showErrDialog("Printer error encounter\nError Code: " + integer + "\nMessage: " + message);
            }
            msgDialog.showPrintSuccessDialog("Finished Printing",isLastSeq);
        }

        @Override
        protected Integer doInBackground(BillHeader... billHeader) {
            int status = -3;
            try {
                Log.d("PRINT", "Start Printing " + billHeader[0].getBillNo());
                if (print.voltageCheck()) {
                    status = printReading(billHeader[0]);
                    if (status == 1)
                        updateIsPrinted(billHeader[0].getBillNo());
                } else {
                    status = -4;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressBar.setMessage(values[0]);
        }
    }

    public class PrintReceiveCopyOnBackground extends AsyncTask<String, String, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setTitle("Printing");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (progressBar != null)
                progressBar.dismiss();
            if (x != 1) {
                if (!UniversalHelper.checkPaperStatus(x).equals("OK"))
                    msgDialog.showErrDialog(UniversalHelper.checkPaperStatus(x));
            }
            msgDialog.showPrintSuccessDialog("Finished Printing",isLastSeq);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            x = printReceivingCopy();
            return null;
        }
    }

    public class PrintNotificationOnBackground extends AsyncTask<AccountModelV2, String, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setTitle("Printing");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (progressBar != null)
                progressBar.dismiss();
            if (x != 1) {
                if (!UniversalHelper.checkPaperStatus(x).equals("OK"))
                    msgDialog.showErrDialog(UniversalHelper.checkPaperStatus(x));
            }else {
                if(isSpikeDrop.equals("Y")){
                    if (getProperty(map.get("IS_PRINT_DOE_COMPLIANCE"), "Y").equals("Y")){
                        msgDialog.showAlert(context, "Print DOE compliance.", value1 -> {
                            if (value1.equals("OK")){
                                PrintDOEOnBackground printDOEOnBackground = new PrintDOEOnBackground();
                                printDOEOnBackground.execute();
                            }
                        });
                    } else{
                        msgDialog.showAlert(context, "Print receiving copy.", value2 -> {
                            if (value2.equals("OK")) {
                                PrintReceiveCopyOnBackground receiveCopyOnBackground = new PrintReceiveCopyOnBackground();
                                receiveCopyOnBackground.execute();
                            }else
                                msgDialog.showPrintSuccessDialog("Finished Printing",isLastSeq);
                        });
                    }

                }else
                    msgDialog.showPrintSuccessDialog("Finished Printing",isLastSeq);
            }
        }

        @Override
        protected Integer doInBackground(AccountModelV2... accountModelV2s) {
            x = printNotification(accountModelV2s[0]);
            return null;
        }
    }

    private class PrintDOEOnBackground extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setTitle("Printing");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Void avoid ) {
            super.onPostExecute(avoid);
            if (progressBar != null)
                progressBar.dismiss();
            if (x != 1) {
                if (!UniversalHelper.checkPaperStatus(x).equals("OK"))
                    msgDialog.showErrDialog(UniversalHelper.checkPaperStatus(x));
            }else{
                msgDialog.showAlert(context, "Print receiving copy.", value2 -> {
                    if (value2.equals("OK")) {
                        PrintReceiveCopyOnBackground receiveCopyOnBackground = new PrintReceiveCopyOnBackground();
                        receiveCopyOnBackground.execute();
                    }else
                        msgDialog.showPrintSuccessDialog("Finished Printing",isLastSeq);
                });
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            x = printDOE();
            return null;
        }

    }

    public void callClusterPrintBackground() {
        ClusterPrintOnBackground printOnBackground = new ClusterPrintOnBackground();
        printOnBackground.execute();
    }

    public void callPrintBackground(String billNo,boolean lastSeq,String spike) {
        isLastSeq = lastSeq;
        isSpikeDrop = spike;
        PrintOnBackground printOnBackground = new PrintOnBackground();
        printOnBackground.execute(billNo);
    }

    public void callPrintSummaryBackground() {
        PrintSummaryOnBackground printOnBackground = new PrintSummaryOnBackground();
        printOnBackground.execute();
    }

    public void callPrintReadingBackground(BillHeader billHeader,boolean lastSeq) {
        isLastSeq = lastSeq;
        PrintReadingOnBackground readingOnBackground = new PrintReadingOnBackground();
        readingOnBackground.execute(billHeader);
    }

    public void callPrintNotificationOnBackground(AccountModelV2 accountModelV2) {
        PrintNotificationOnBackground printNotificationOnBackground = new PrintNotificationOnBackground();
        printNotificationOnBackground.execute(accountModelV2);
    }

}