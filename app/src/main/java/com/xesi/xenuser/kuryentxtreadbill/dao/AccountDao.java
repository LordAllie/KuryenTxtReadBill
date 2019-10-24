package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.RouteObj;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountDao extends BaseDAO {

    private String TABLE_NAME = "arm_account";

    public AccountDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }


    public AccountModelV2 getAccountByAcctNo(String oldAcctNo) {
        AccountModelV2 accountModel = new AccountModelV2();

        String selectQuery = " SELECT * FROM " + TABLE_NAME + " WHERE `oldAccountNumber` = '" + oldAcctNo + "'";
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
                accountModel = setQueryAccountModels(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cursor.close();
            mcfDB.close();

        }
        return accountModel;
    }

    public List<AccountModelV2> getAllAccount(int idRoute, boolean showAllActive,
                                              boolean reOrderSequence, boolean isRead, String isBottom) {
        List<AccountModelV2> contactInfoList = new ArrayList<>();
        String orderQuery;
        String addQuery = "";

        // order by Is Read
        if (isRead)
            orderQuery = " isRead DESC ";
        else
            orderQuery = " isRead ";

        // order by SeqNo
        if (reOrderSequence)
            orderQuery = orderQuery + ", sequenceNumber DESC, id DESC ";
        else
            orderQuery = orderQuery + ", sequenceNumber, id ";

        // show all active only
        if (showAllActive)
            addQuery = " AND isActive = 'Y' ";

        String selectQuery;
        if(isBottom.equals("Y"))
            selectQuery = " SELECT * FROM " + TABLE_NAME + " WHERE `idRoute` = " + idRoute + addQuery + " ORDER BY " + orderQuery;
        else
            selectQuery = " SELECT * FROM " + TABLE_NAME + " WHERE `idRoute` = " + idRoute + addQuery;
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    contactInfoList.add(setQueryAccountModels(cursor));
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // return batch list
            cursor.close();
            mcfDB.close();
        }

        return contactInfoList;
    }


    public List<AccountModelV2> getAllAccount(boolean showAllActive,
                                              boolean reOrderSequence, boolean isRead) {
        List<AccountModelV2> contactInfoList = new ArrayList<>();
        String orderQuery;
        String addQuery = "";

        // order by Is Read
        if (isRead)
            orderQuery = " isRead DESC ";
        else
            orderQuery = " isRead ";

        // order by SeqNo
        if (reOrderSequence)
            orderQuery = orderQuery + ", sequenceNumber DESC, id DESC ";
        else
            orderQuery = orderQuery + ", sequenceNumber, id ";

        // show all active only
        if (showAllActive)
            addQuery = " AND isActive = 'Y' ";


        String selectQuery = " SELECT * FROM " + TABLE_NAME +
                " WHERE isRead = 0 " +  addQuery + " ORDER BY " + orderQuery;

        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    contactInfoList.add(setQueryAccountModels(cursor));
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // return batch list
            cursor.close();
            mcfDB.close();
        }

        return contactInfoList;
    }

    public List<String> getRouteList() {
        List<String> routeList = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT(routeCode) from " + TABLE_NAME + " ORDER BY routeCode";

        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    routeList.add(cursor.getString(0));
                }
                while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mcfDB.close();
        }
        return routeList;
    }

    public List<RouteObj> getRoutes() {
        List<RouteObj> routeList = new ArrayList<>();

        String selectQuery = "select DISTINCT idRoute, routeCode from " + TABLE_NAME;

        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    RouteObj routeObj = new RouteObj();
                    routeObj.setIdRoute(cursor.getInt(0));
                    routeObj.setRouteCode(cursor.getString(1));
                    routeList.add(routeObj);
                }
                while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mcfDB.close();
        }
        return routeList;
    }

    public boolean getIsread(String seqNo, int idRoute) {
        mcfDB = this.getWritableDatabase();
        String selectQuery = "SELECT isRead FROM " + TABLE_NAME + " WHERE oldAccountNumber = '" + seqNo + "' " +
                " AND idRoute = " + idRoute;
        boolean isReadBolean = false;

        try {
            Cursor cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                int isRead = cursor.getInt(0);
                if (isRead == 1) {
                    isReadBolean = true;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mcfDB.close();
        return isReadBolean;
    }

    public boolean updateReadingAndIsRead(double reading, int seqNo, int idRoute, int acctId) {
        mcfDB = this.getWritableDatabase();
        boolean updated = false;
        try {
            ContentValues values = new ContentValues();
            values.put("currentReading", reading);
            values.put("isRead", 1);
            mcfDB.update(TABLE_NAME, values, "sequenceNumber  = " + seqNo
                    + " AND idRoute = " + idRoute + " AND id = " + acctId, null);

            updated = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        mcfDB.close();
        return updated;
    }

    private AccountModelV2 setQueryAccountModels(Cursor cursor) {
        AccountModelV2 accountDetails = new AccountModelV2();
        accountDetails.setId(cursor.getInt(cursor.getColumnIndex("id")));
        accountDetails.setIdRateMaster(cursor.getInt(cursor.getColumnIndex("idRateMaster")));
        accountDetails.setIdRDM(cursor.getInt(cursor.getColumnIndex("idRDM")));
        accountDetails.setIdRoute(cursor.getInt(cursor.getColumnIndex("idRoute")));
        accountDetails.setIdArea(cursor.getInt(cursor.getColumnIndex("idArea")));
        accountDetails.setSequenceNumber(cursor.getInt(cursor.getColumnIndex("sequenceNumber")));
        accountDetails.setOldSequenceNumber(cursor.getInt(cursor.getColumnIndex("oldSequenceNumber")));
        accountDetails.setOldAccountNumber(cursor.getString(cursor.getColumnIndex("oldAccountNumber")));
        accountDetails.setAccountNumber(cursor.getString(cursor.getColumnIndex("accountNumber")));
        accountDetails.setMeterNumber(cursor.getString(cursor.getColumnIndex("meterNumber")));
        accountDetails.setAccountName(cursor.getString(cursor.getColumnIndex("accountName")));
        accountDetails.setAddressLn1(cursor.getString(cursor.getColumnIndex("addressln1")));
        accountDetails.setAddressLn2(cursor.getString(cursor.getColumnIndex("addressln2")));
        accountDetails.setMobileNo(cursor.getString(cursor.getColumnIndex("mobileNo")));
        accountDetails.setEmailAdd(cursor.getString(cursor.getColumnIndex("emailAdd")));
        accountDetails.setSin(cursor.getString(cursor.getColumnIndex("sin")));
        accountDetails.setSerialNo(cursor.getString(cursor.getColumnIndex("serialNo")));
        accountDetails.setRouteCode(cursor.getString(cursor.getColumnIndex("routeCode")));
        accountDetails.setPreviousBill(new BigDecimal(cursor.getString(cursor.getColumnIndex("previousBill"))));
        accountDetails.setLastPaymentDate(cursor.getString(cursor.getColumnIndex("lastPaymentDate")));
        accountDetails.setLastPaymentAmt(new BigDecimal(cursor.getString(cursor.getColumnIndex("lastPaymentAmt"))));
        accountDetails.setLastDepositPayment(new BigDecimal(cursor.getString(cursor.getColumnIndex("lastDepositPayment"))));
        accountDetails.setLastDepositPaymentDate(cursor.getString(cursor.getColumnIndex("lastDepositPaymentDate")));
        accountDetails.setBillDepositInterest(new BigDecimal(cursor.getString(cursor.getColumnIndex("billDepositInterest"))));
        accountDetails.setBillDeposit(new BigDecimal(cursor.getString(cursor.getColumnIndex("billDeposit"))));
        accountDetails.setTotalBillDeposit(new BigDecimal(cursor.getString(cursor.getColumnIndex("totalBillDeposit"))));
        accountDetails.setC01(new BigDecimal(cursor.getString(cursor.getColumnIndex("c01"))));
        accountDetails.setC02(new BigDecimal(cursor.getString(cursor.getColumnIndex("c02"))));
        accountDetails.setC03(new BigDecimal(cursor.getString(cursor.getColumnIndex("c03"))));
        accountDetails.setC04(new BigDecimal(cursor.getString(cursor.getColumnIndex("c04"))));
        accountDetails.setC05(new BigDecimal(cursor.getString(cursor.getColumnIndex("c05"))));
        accountDetails.setC06(new BigDecimal(cursor.getString(cursor.getColumnIndex("c06"))));
        accountDetails.setC07(new BigDecimal(cursor.getString(cursor.getColumnIndex("c07"))));
        accountDetails.setC08(new BigDecimal(cursor.getString(cursor.getColumnIndex("c08"))));
        accountDetails.setC09(new BigDecimal(cursor.getString(cursor.getColumnIndex("c09"))));
        accountDetails.setC10(new BigDecimal(cursor.getString(cursor.getColumnIndex("c10"))));
        accountDetails.setC11(new BigDecimal(cursor.getString(cursor.getColumnIndex("c11"))));
        accountDetails.setC12(new BigDecimal(cursor.getString(cursor.getColumnIndex("c12"))));
        accountDetails.setCurrentReading(cursor.getDouble(cursor.getColumnIndex("currentReading")));
        accountDetails.setCurrentConsumption(cursor.getDouble(cursor.getColumnIndex("currentConsumption")));
        accountDetails.setMoAvgConsumption(cursor.getDouble(cursor.getColumnIndex("moAvgConsumption")));
        accountDetails.setStopMeterFixedConsumption(cursor.getDouble(cursor.getColumnIndex("stopMeterFixedConsumption")));
        accountDetails.setMeterMultiplier(cursor.getDouble(cursor.getColumnIndex("meterMultiplier")));
        accountDetails.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
        accountDetails.setIsSeniorCitizen(cursor.getString(cursor.getColumnIndex("isSeniorCitizen")));
        accountDetails.setIsActive(cursor.getString(cursor.getColumnIndex("isActive")));
        accountDetails.setIsRead(cursor.getInt(cursor.getColumnIndex("isRead")));
        accountDetails.setMinimumContractedEnergy(cursor.getDouble(cursor.getColumnIndex("minimumContractedEnergy")));
        accountDetails.setMinimumContractedDemand(cursor.getDouble(cursor.getColumnIndex("minimumContractedDemand")));
        accountDetails.setArrears(cursor.getDouble(cursor.getColumnIndex("arrears")));
        accountDetails.setArrearsAsOf(cursor.getString(cursor.getColumnIndex("arrearsAsOf")));
        accountDetails.setIsForAverage(cursor.getString(cursor.getColumnIndex("isForAverage")));
        accountDetails.setAutoComputeMode(cursor.getInt(cursor.getColumnIndex("autoComputeMode")));
        return accountDetails;
    }

    // For Navigation (Use in method reflection)
    public AccountModelV2 getReadMeterDataPrev(int seqNo, int idRoute, boolean isCheck, int acctId) {
        String addQuery = "";
        if (isCheck)
            addQuery = " AND isActive = 'Y' ";

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE `idRoute` =" + idRoute + " AND isRead = 0 " +
                " AND (sequenceNumber < " + seqNo + " " +
                " OR (sequenceNumber = " + seqNo + " AND id < '" + acctId + "')) " +
                addQuery + " ORDER BY sequenceNumber DESC, id DESC  LIMIT 1";

        AccountModelV2 accounts = new AccountModelV2();
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToNext())
                accounts = setQueryAccountModels(cursor);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mcfDB.close();
        return accounts;
    }

    // For Navigation (Use in method reflection)
    public AccountModelV2 getReadMeterDataNext(int seqNo, int idRoute, boolean isCheck, int acctId) {
        String addQuery = "";
        if (isCheck)
            addQuery = " AND isActive = 'Y' ";

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE `idRoute` =" + idRoute + " AND isRead = 0 " +
                " AND (sequenceNumber > " + seqNo + " " +
                " OR (sequenceNumber = " + seqNo + " AND id > '" + acctId + "')) " +
                addQuery + " ORDER BY sequenceNumber, id  LIMIT 1";


        AccountModelV2 accounts = new AccountModelV2();
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToNext())
                accounts = setQueryAccountModels(cursor);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mcfDB.close();
        return accounts;
    }

    public List<String> getAddressByAcctNo(String accountNo) {
        List<String> list = new ArrayList<>();
        String selectQuery = "SELECT  addressln1, addressln2 FROM " + TABLE_NAME +
                " WHERE oldAccountNumber = '" + accountNo + "'";
        mcfDB = this.getWritableDatabase();
        Cursor cursor;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                list.add(cursor.getString(0).toLowerCase());
                list.add(cursor.getString(1).toLowerCase());
            }
            cursor.close();
        } catch (Exception e) {
            //
        } finally {
            mcfDB.close();
        }
        return list;
    }

    public AccountModelV2 getArrearsByAcctNo(String oldAcctNo) {
        AccountModelV2 accountModel = new AccountModelV2();

        String selectQuery = " SELECT * FROM " + TABLE_NAME + " WHERE `oldAccountNumber` = '" + oldAcctNo + "'";
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
                accountModel = setQueryAccountModels(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cursor.close();
            mcfDB.close();

        }
        return accountModel;
    }

}
