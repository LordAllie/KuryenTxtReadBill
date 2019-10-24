package com.xesi.xenuser.kuryentxtreadbill.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xesi.core.library.*;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.RouteDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.RouteModel;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xenuser on 2/14/2017.
 */

public class BillToDB extends BaseDAO {
    public static final String APP_PROPERTY_SETTING = "app_config";
    private static final int DB_VERSION = 1;
    private static String DB_NAME = "read&bill.db";
    private final Context mContext;
    private Calendar calendar;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private BillHeaderDAO billHeaderDAO;
    private DUPropertyDAO duPropertyDAO;
    private RouteDao routeDao;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private DateHandler dateHandler;
    private String dueDate;
    private RouteModel routeModel;
    private GenericDao genericDao;


    public BillToDB(Context mContext) {
        super(mContext, DB_NAME, null, DB_VERSION);
        this.mContext = mContext;
        Log.i("sqlite", mContext.toString());
        sharedPref = mContext.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        dateHandler = new DateHandler();
    }

    public String saveBillHeader(double kwhUsed, double currentReading, double coreloss, String cunsumerType,
                                 String newBillNumber, double iTotalKwhAddOnTran, double totalConsumption,
                                 AccountModelV2 accounts, String remarks, String effectivityDate) {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        calendar = Calendar.getInstance();
        routeModel = routeDao.getRouteDetail(accounts.getRouteCode());
        if (routeModel == null &&
                (routeModel.getId() == 0 && routeModel.getDueDay() == 0
                        && routeModel.getBillingDayStart() == 0
                        && routeModel.getBillingDayEnd() == 0)) {

            routeModel.setDueDay(1);
            routeModel.setBillingDayStart(1);
            routeModel.setBillingDayEnd(1);
        }
        String deviceId = sharedPref.getString("authID", "");
        // ArrayList<String> listGetDate = dateManipulation.getPeriodCovered(routeModel.getBillingDayStart(), routeModel.getBillingDayEnd());
        ArrayList<String> listGetDate = dateHandler.getPeriodCovered(routeModel.getBillingDayStart(),
                routeModel.getBillingDayEnd(),
                duPropertyDAO.getPropertyValue("IS_PERIOD_COVERED_BASED_ON_ROUTE"),
                duPropertyDAO.getPropertyValue("ADDITIONAL_DAY_ON_PERIOD_FROM"),
                duPropertyDAO.getPropertyValue("BILLING_MONTH_SETUP"),
                duPropertyDAO.getPropertyValue("BILLING_MONTH_FORMAT"),
                effectivityDate);

        if (duPropertyDAO.getPropertyValue("IS_DUE_DATE_BASED_ON_ROUTE").equals("Y")) {
            // dueDate = dateManipulation.generateDueDateByRoute(routeModel.getDueDay());
            dueDate = dateHandler.generateDueDateByRoute(routeModel.getDueDay());
        } else {
            //dueDate = dateManipulation.generateDueDate();
            dueDate = dateHandler.generateDueDate(Integer.parseInt(duPropertyDAO.getPropertyValue("DAYS_TO_DUE")),
                    duPropertyDAO.getPropertyValue("IS_NO_WEEKEND_DUE"));
        }
        BillHeader bill = new BillHeader();
        bill.setBillNo(newBillNumber);
        bill.setRunDate(sdf.format(calendar.getTime()));
        bill.setOldAccountNo(accounts.getOldAccountNumber());
        bill.setRouteCode(accounts.getRouteCode());
        bill.setSequenceNo(accounts.getSequenceNumber());
        bill.setAcctName(accounts.getAccountName());
        bill.setMeterNo(accounts.getMeterNumber());
        bill.setAcctNo(accounts.getAccountNumber());
        bill.setConsumerType(cunsumerType);
        bill.setCurReading(currentReading);
        bill.setPrevReading(accounts.getCurrentReading());
        bill.setConsumption(kwhUsed);
        bill.setCoreloss(coreloss);
        bill.setMeterMultiplier(accounts.getMeterMultiplier());
        bill.setAddonKwhTotal(iTotalKwhAddOnTran);
        bill.setTotalConsumption(totalConsumption);
        bill.setPeriodFrom(listGetDate.get(0));
        bill.setPeriodTo(listGetDate.get(1));
        bill.setBillingMonth(listGetDate.get(2));
        bill.setDueDate(dueDate);
        //bill.setDiscoDate(dateManipulation.daysToDisconnect(dueDate));
        bill.setDiscoDate(dateHandler.daysToDisconnect(dueDate, duPropertyDAO.getPropertyValue("DAYS_TO_DISCONNECT")));
        bill.setReader(sharedPref.getString("assignedTo", ""));
        bill.setDeviceId(Integer.parseInt(deviceId));
        bill.setRemarks(remarks);
        bill.setIdRoute(accounts.getIdRoute());
        bill.setMinimumContractedEnergy(accounts.getMinimumContractedEnergy());
        bill.setPreviousConsumption(accounts.getCurrentConsumption());
        bill.setMinimumContractedDemand(accounts.getMinimumContractedDemand());
        bill.setAccountArrears(accounts.getArrears());
        bill.setArrearsAsOf(accounts.getArrearsAsOf());
        bill.setEditCount(0);
        bill.setIsVoid("N");
        return billHeaderDAO.insertRecord(bill);
    }

    /* FOR BILL NO GENERATION */
    public String generateBillNo(String deviceId, String oldAcctNo) {
        String billNumber = genericDao.getOneField("billNo","armBillHeader","WHERE isUploaded = 0 AND oldAcctNo = ",oldAcctNo,"ORDER BY _id DESC LIMIT 1","");
        if (billNumber.trim().equals("") && billNumber != null) {
            String billFormat = duPropertyDAO.getPropertyValue("BILL_NUMBER_FORMAT");
            billNumber = !billFormat.equals("-1") ? billFormat : "%DEVICE%YY%MM%DD%CTR" ;
            int billCode = checkDate();


            billNumber = BillNumberFormat.generateBillNo(String.format("%03d", Integer.parseInt(deviceId)),String.format("%03d", billCode),billNumber);

        }
        return billNumber;
    }


    public Integer checkDate() {
        Calendar c1 = Calendar.getInstance();
        if (sharedPref.getLong("currentDate",  0)  == c1.get(Calendar.DATE)) {
            System.out.println("Same Time: " + c1.get(Calendar.DATE));
            editor.putInt("billCode", sharedPref.getInt("billCode", 0) + 1);
            editor.commit();
            return sharedPref.getInt("billCode", 0);
        } else {
            System.out.println("Diff Time: " +  c1.get(Calendar.DATE));
            editor.putLong("currentDate",c1.get(Calendar.DATE )).apply();
            editor.commit();
            editor.putInt("billCode", 1);
            editor.commit();
            return sharedPref.getInt("billCode", 0);
        }
    }

    public void instantiateDb() {
        genericDao = new GenericDao(mContext);
        billHeaderDAO = new BillHeaderDAO(mContext);
        billHeaderDAO.instantiateDb();
        duPropertyDAO = new DUPropertyDAO(mContext);
        routeDao = new RouteDao(mContext);
    }

    public synchronized void close() {
        genericDao.close();
        billHeaderDAO.close();
        duPropertyDAO.close();
        super.close();
    }
}
