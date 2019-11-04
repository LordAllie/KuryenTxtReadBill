package com.xesi.xenuser.kuryentxtreadbill.dao.billdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.StringBill;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillParams;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.UploadBillMaster;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by xenuser on 2/13/2017.
 */

public class BillHeaderDAO extends BaseDAO {

    private ContentValues contentValues;
    private String TABLE_NAME = "armBillHeader";
    private final String BILLNO = "billNo";
    private GenericDao genericDao;

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public BillHeaderDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public String insertRecord(BillHeader bill) {
        mcfDB = this.getReadableDatabase();
        String isInserted;
        try {
            contentValues = new ContentValues();
            contentValues.put("billNo", bill.getBillNo());
            contentValues.put("runDate", bill.getRunDate());
            contentValues.put("oldAcctNo", bill.getOldAccountNo());
            contentValues.put("routeCode", bill.getRouteCode());
            contentValues.put("seqNo", bill.getSequenceNo());
            contentValues.put("accntName", bill.getAcctName());
            contentValues.put("meterNo", bill.getMeterNo());
            contentValues.put("acctNo", bill.getAcctNo());
            contentValues.put("consumerType", bill.getConsumerType());
            contentValues.put("curRdg", bill.getCurReading());
            contentValues.put("prevRdg", bill.getPrevReading());
            contentValues.put("consumption", bill.getConsumption());
            contentValues.put("multiplier", bill.getMeterMultiplier());
            contentValues.put("coreloss", bill.getCoreloss());
            contentValues.put("addonKwhTotal", bill.getAddonKwhTotal());
            contentValues.put("totalConsumption", bill.getTotalConsumption());
            contentValues.put("periodFrom", bill.getPeriodFrom());
            contentValues.put("periodTo", bill.getPeriodTo());
            contentValues.put("billingMonth", bill.getBillingMonth());
            contentValues.put("dueDate", bill.getDueDate());
            contentValues.put("discoDate", bill.getDiscoDate());
            contentValues.put("reader", bill.getReader().trim());
            contentValues.put("devId", bill.getDeviceId());
            contentValues.put("remarks", bill.getRemarks());
            contentValues.put("idRoute", bill.getIdRoute());
            contentValues.put("minimumContractedEnergy", bill.getMinimumContractedEnergy());
            contentValues.put("minimumContractedDemand", bill.getMinimumContractedDemand());
            contentValues.put("previousConsumption", bill.getPreviousConsumption());
            contentValues.put("accountArrears", bill.getAccountArrears());
            contentValues.put("arrearsAsOf", bill.getArrearsAsOf());
            contentValues.put("editCount",bill.getEditCount());
            contentValues.put("isVoid",bill.getIsVoid());
            contentValues.put("isArchive",bill.getIsArchive());
            if (mcfDB.insertOrThrow(TABLE_NAME, null, contentValues) == -1) {
                isInserted = "-2";
            } else {
                isInserted = "0";
            }
        } catch (Exception e) {
            isInserted = "SQLException: " + String.valueOf(e.getMessage()) + " \nPlease contact your Administrator";
        } finally {
            mcfDB.close();
        }
        return isInserted;
    }

    public boolean updateOneFieldByBillNo(String data, String billNo,String fieldName) {
        mcfDB = this.getWritableDatabase();
        boolean isUpdated = false;
        try {
            ContentValues values = new ContentValues();
            values.put(fieldName, data);
            mcfDB.update(TABLE_NAME, values, BILLNO + " =  ?", new String[]{billNo});
            isUpdated = true;
        } catch (SQLiteConstraintException e) {
            e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUpdated;
    }

    public boolean updateFieldByBillNo(String data, String[] billNo,String fieldName) {
        mcfDB = this.getWritableDatabase();
        boolean isUpdated = false;
        try {
            for (int i = 0; i < billNo.length; i++) {
                ContentValues values = new ContentValues();
                values.put(fieldName, data);
                mcfDB.update(TABLE_NAME, values, BILLNO + " =  ?", new String[]{billNo[i]});
                isUpdated = true;
            }
        } catch (SQLiteConstraintException e) {
            e.getMessage();
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
        } finally {
            mcfDB.close();
        }
        return isUpdated;
    }

    public boolean updateByBillNo(String data, String billNo,String fieldName) {
        mcfDB = this.getWritableDatabase();
        boolean isUpdated = false;
        try {
            ContentValues values = new ContentValues();
            values.put(fieldName, data);
            mcfDB.update(TABLE_NAME, values, BILLNO + " =  ?", new String[]{billNo});
            isUpdated = true;

        } catch (SQLiteConstraintException e) {
            e.getMessage();
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
        } finally {
            mcfDB.close();
        }
        return isUpdated;
    }

    public boolean updateAll() {
        mcfDB = this.getWritableDatabase();
        boolean isUploaded = false;
        try {
            ContentValues values = new ContentValues();
            values.put("isUploaded", "1");
            mcfDB.update(TABLE_NAME, values, null, null);
            isUploaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }

    public boolean updateRecord(BillParams billParams) {
        mcfDB = this.getWritableDatabase();
        boolean isUpdated = false;
        try {
            ContentValues values = new ContentValues();
            values.put("curRdg", billParams.getCurrentReading());
            values.put("prevRdg", billParams.getPrevRdg());
            values.put("consumption", billParams.getKwhConsumption());
            values.put("totalConsumption", billParams.getTotalKwhConsumption());
            values.put("remarks", billParams.getRemarks());
            values.put("editCount",  + Integer.parseInt(genericDao.getOneField("editCount","armBillHeader","WHERE billNo=",billParams.getBillNumber(),"ORDER BY _id LIMIT 1","0")) + 1);
            mcfDB.update(TABLE_NAME, values, BILLNO + " =  ?", new String[]{billParams.getBillNumber()});
            isUpdated = true;
        } catch (SQLiteConstraintException e) {
            e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUpdated;
    }

    public boolean untaggedBills() {
        return untaggedBills(TABLE_NAME);
    }

    public boolean updateIsPrinted(String billNo, String billJson) {
        mcfDB = this.getWritableDatabase();
        boolean isUploaded = false;
        try {
            ContentValues values = new ContentValues();
            values.put("isPrinted", 1);
            values.put("billJson", billJson);
            mcfDB.update(TABLE_NAME, values, BILLNO + " =  ?", new String[]{billNo});
            isUploaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }

    public BillHeader findBillByBillNo(String billNo) {
        mcfDB = this.getWritableDatabase();
        BillHeader billHeader = new BillHeader();
        String query = "SELECT billNo, runDate, oldAcctNo, routeCode, seqNo, accntName" +
                ",meterNo, acctNo, consumerType, curRdg, prevRdg, multiplier, coreloss " +
                ",totalConsumption, periodFrom, periodTo, billingMonth, curBill" +
                ",totalBill, reader, devId, dueDate, remarks, idRoute, _id, consumption, addonKwhTotal " +
                ",totalBillAfterDueDate, discoDate, minimumContractedEnergy, previousConsumption, accountArrears, arrearsAsOf, editCount, isVoid FROM " + TABLE_NAME +
                " WHERE billNo ='" + billNo + "' ORDER BY _id LIMIT 1";
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst())
                billHeader =setQuerySelectedBillHeaderModel(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billHeader;
    }

    private String convertToPrint(double val) {
        return new BigDecimal(Double.toString(val)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public List<BillHeader> getAllBillList(String fieldName,String WhereFieldName,String data, String lastStatement) {
        List<BillHeader> billHeaderLists = new ArrayList<>();
        mcfDB = this.getWritableDatabase();
        String query = "SELECT "+ fieldName +
                " FROM " + TABLE_NAME + " "  + WhereFieldName +" '" + data + "' " +lastStatement ;
        if(data.equals(""))
            query = query.replace("'","");
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst())
                do {
                    billHeaderLists.add(setQueryBillHeaderModel(cursor));
                } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billHeaderLists;
    }

    public BillHeader setQuerySelectedBillHeaderModel(Cursor cursor){
        BillHeader billHeader = new BillHeader();
        billHeader.setBillNo(cursor.getString(cursor.getColumnIndex("billNo")));
        billHeader.setRunDate(cursor.getString(cursor.getColumnIndex("runDate")));
        billHeader.setOldAccountNo(cursor.getString(cursor.getColumnIndex("oldAcctNo")));
        billHeader.setRouteCode(cursor.getString(cursor.getColumnIndex("routeCode")));
        billHeader.setSequenceNo(cursor.getInt(cursor.getColumnIndex("seqNo")));
        billHeader.setAcctName(cursor.getString(cursor.getColumnIndex("accntName")));
        billHeader.setMeterNo(cursor.getString(cursor.getColumnIndex("meterNo")));
        billHeader.setAcctNo(cursor.getString(cursor.getColumnIndex("acctNo")));
        billHeader.setConsumerType(cursor.getString(cursor.getColumnIndex("consumerType")));
        billHeader.setCurReading(cursor.getDouble(cursor.getColumnIndex("curRdg")));
        billHeader.setPrevReading(cursor.getDouble(cursor.getColumnIndex("prevRdg")));
        billHeader.setMeterMultiplier(cursor.getDouble(cursor.getColumnIndex("multiplier")));
        billHeader.setCoreloss(cursor.getDouble(cursor.getColumnIndex("coreloss")));
        billHeader.setTotalConsumption(cursor.getDouble(cursor.getColumnIndex("totalConsumption")));
        billHeader.setPeriodFrom(cursor.getString(cursor.getColumnIndex("periodFrom")));
        billHeader.setPeriodTo(cursor.getString(cursor.getColumnIndex("periodTo")));
        billHeader.setBillingMonth(cursor.getString(cursor.getColumnIndex("billingMonth")));
        billHeader.setCurBill(convertToPrint(cursor.getDouble(cursor.getColumnIndex("curBill"))));
        billHeader.setTotalAmountDue(convertToPrint(cursor.getDouble(cursor.getColumnIndex("totalBill"))));
        billHeader.setReader(cursor.getString(cursor.getColumnIndex("reader")));
        billHeader.setDeviceId(cursor.getInt(cursor.getColumnIndex("devId")));
        billHeader.setDueDate(cursor.getString(cursor.getColumnIndex("dueDate")));
        billHeader.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
        billHeader.setIdRoute(cursor.getInt(cursor.getColumnIndex("idRoute")));
        billHeader.setIdBh(cursor.getInt(cursor.getColumnIndex("_id")));
        billHeader.setConsumption(cursor.getDouble(cursor.getColumnIndex("consumption")));
        billHeader.setAddonKwhTotal(cursor.getDouble(cursor.getColumnIndex("addonKwhTotal")));
        billHeader.setTotalBillAfterDueDate(cursor.getString(cursor.getColumnIndex("totalBillAfterDueDate")));
        billHeader.setDiscoDate(cursor.getString(cursor.getColumnIndex("discoDate")));
        billHeader.setMinimumContractedEnergy(cursor.getDouble(cursor.getColumnIndex("minimumContractedEnergy")));
        billHeader.setPreviousConsumption(cursor.getDouble(cursor.getColumnIndex("previousConsumption")));
        billHeader.setAccountArrears(cursor.getDouble(cursor.getColumnIndex("accountArrears")));
        billHeader.setArrearsAsOf(cursor.getString(cursor.getColumnIndex("arrearsAsOf")));
        billHeader.setEditCount(cursor.getInt(cursor.getColumnIndex("editCount")));
        billHeader.setIsVoid(cursor.getString(cursor.getColumnIndex("isVoid")));
        return billHeader;
    }

    public BillHeader setQueryBillHeaderModel(Cursor cursor){
        BillHeader billHeader = new BillHeader();
        billHeader.setBillNo(cursor.getString(cursor.getColumnIndex("billNo")));
        billHeader.setRunDate(cursor.getString(cursor.getColumnIndex("runDate")));
        billHeader.setOldAccountNo(cursor.getString(cursor.getColumnIndex("oldAcctNo")));
        billHeader.setRouteCode(cursor.getString(cursor.getColumnIndex("routeCode")));
        billHeader.setSequenceNo(cursor.getInt(cursor.getColumnIndex("seqNo")));
        billHeader.setAcctName(cursor.getString(cursor.getColumnIndex("accntName")));
        billHeader.setMeterNo(cursor.getString(cursor.getColumnIndex("meterNo")));
        billHeader.setAcctNo(cursor.getString(cursor.getColumnIndex("acctNo")));
        billHeader.setConsumerType(cursor.getString(cursor.getColumnIndex("consumerType")));
        billHeader.setCurReading(cursor.getDouble(cursor.getColumnIndex("curRdg")));
        billHeader.setPrevReading(cursor.getDouble(cursor.getColumnIndex("prevRdg")));
        billHeader.setMeterMultiplier(cursor.getDouble(cursor.getColumnIndex("multiplier")));
        billHeader.setCoreloss(cursor.getDouble(cursor.getColumnIndex("coreloss")));
        billHeader.setTotalConsumption(cursor.getDouble(cursor.getColumnIndex("totalConsumption")));
        billHeader.setPeriodFrom(cursor.getString(cursor.getColumnIndex("periodFrom")));
        billHeader.setPeriodTo(cursor.getString(cursor.getColumnIndex("periodTo")));
        billHeader.setBillingMonth(cursor.getString(cursor.getColumnIndex("billingMonth")));
        billHeader.setCurBill(cursor.getString(cursor.getColumnIndex("curBill")));
        billHeader.setTotalAmountDue(cursor.getString(cursor.getColumnIndex("totalBill")));
        billHeader.setReader(cursor.getString(cursor.getColumnIndex("reader")));
        billHeader.setDeviceId(cursor.getInt(cursor.getColumnIndex("devId")));
        billHeader.setDueDate(cursor.getString(cursor.getColumnIndex("dueDate")));
        billHeader.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
        billHeader.setIdRoute(cursor.getInt(cursor.getColumnIndex("idRoute")));
        billHeader.setIdBh(cursor.getInt(cursor.getColumnIndex("_id")));
        billHeader.setConsumption(cursor.getDouble(cursor.getColumnIndex("consumption")));
        billHeader.setAddonKwhTotal(cursor.getDouble(cursor.getColumnIndex("addonKwhTotal")));
        billHeader.setTotalBillAfterDueDate(cursor.getString(cursor.getColumnIndex("totalBillAfterDueDate")));
        billHeader.setDiscoDate(cursor.getString(cursor.getColumnIndex("discoDate")));
        billHeader.setIsUploaded(cursor.getInt(cursor.getColumnIndex("isUploaded")));
        billHeader.setIsPrinted(cursor.getInt(cursor.getColumnIndex("isPrinted")));
        billHeader.setIsArchive(cursor.getString(cursor.getColumnIndex("isArchive")));
        return billHeader;
    }

    public List<String> printAllBills() {
        List<String> billNoList = new ArrayList<>();
        mcfDB = this.getWritableDatabase();

        String query = "SELECT billNo FROM " + TABLE_NAME +
                " WHERE isPrinted = 0 ORDER BY isUploaded ASC, _id DESC";
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    billNoList.add(cursor.getString(cursor.getColumnIndex("billNo")));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billNoList;
    }

    public List<StringBill> getAllStringBills() {
        List<StringBill> billJsonList = new ArrayList<>();
        mcfDB = this.getWritableDatabase();

        String query = "SELECT billNo, billJson " +
                " FROM " + TABLE_NAME +
                " WHERE isUploaded = 0";
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    StringBill bill = new StringBill();
                    bill.setBillNo(cursor.getString(0));
                    bill.setBillJson(cursor.getString(1));
                    billJsonList.add(bill);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billJsonList;
    }

    List<StringBill> uploadBackupBillJsonList = new ArrayList<>();
    public List<StringBill> getOneStringBill(String billNo) {

        mcfDB = this.getWritableDatabase();

        String query = "SELECT billNo, billJson " +
                " FROM " + TABLE_NAME +
                " WHERE isUploaded = 0 AND billNo = '" + billNo+"'";
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    StringBill bill = new StringBill();
                    bill.setBillNo(cursor.getString(0));
                    bill.setBillJson(cursor.getString(1));
                    uploadBackupBillJsonList.add(bill);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return uploadBackupBillJsonList;
    }


    public List<String> getAllBillNo() {
        List<String> billJsonList = new ArrayList<>();
        mcfDB = this.getWritableDatabase();

        String query = "SELECT billNo " +
                " FROM " + TABLE_NAME +
                " WHERE isUploaded = 0";
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String bill;
                    bill=cursor.getString(0);
                    billJsonList.add(bill);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billJsonList;
    }

    public List<String> getAllArchiveBillNo() {
        List<String> billJsonList = new ArrayList<>();
        mcfDB = this.getWritableDatabase();

        String query = "SELECT billNo " +
                " FROM " + TABLE_NAME +
                " WHERE isUploaded = 0 AND isArchive='Y'";
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String bill;
                    bill=cursor.getString(0);
                    billJsonList.add(bill);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billJsonList;
    }

    public boolean checkBillNoIfExist(String newBillNumber) {
        boolean acctBillNoExist = false;
        mcfDB = this.getWritableDatabase();
        String query = "SELECT COUNT(_id) " +
                " FROM " + TABLE_NAME +
                " WHERE billNo = '" + newBillNumber + "'";
        int count = 0;
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        if (count > 0) {
            acctBillNoExist = true;
        }
        return acctBillNoExist;
    }

    public List<Double> getCurAndPrevRdg(String gAccountNo) {
        mcfDB = this.getWritableDatabase();
        String query = "SELECT curRdg, prevRdg " +
                " FROM " + TABLE_NAME + " WHERE isUploaded = 0 AND oldAcctNo = '" + gAccountNo + "' ORDER BY _id DESC";
        List<Double> reading = new ArrayList<>();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                reading.add(cursor.getDouble(0));
                reading.add(cursor.getDouble(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return reading;
    }

    public List<Double> getRdg(String gAccountNo) {
        mcfDB = this.getWritableDatabase();
        String query = "SELECT curRdg, prevRdg " +
                " FROM " + TABLE_NAME + " WHERE oldAcctNo = '" + gAccountNo + "'";
        List<Double> reading = new ArrayList<>();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                reading.add(cursor.getDouble(0));
                reading.add(cursor.getDouble(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return reading;
    }

    public ArrayList<String> getBillAndRemarksByAcct(String oldAcctNo) {
        ArrayList<String> sReturn = new ArrayList<>();
        mcfDB = this.getWritableDatabase();
        String query = "SELECT billNo, remarks " +
                " FROM " + TABLE_NAME +
                " WHERE isUploaded = 0 AND oldAcctNo = '" + oldAcctNo + "' ORDER BY _id DESC LIMIT 1";
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                sReturn.add(cursor.getString(0));
                if (cursor.getString(1).equals("") || cursor.getString(1) == null)
                    sReturn.add("NO REMARKS");
                else
                    sReturn.add(cursor.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return sReturn;
    }

    public void instantiateDb() {
        genericDao = new GenericDao(mContext);
    }
    public synchronized void close() {
        genericDao.close();
        super.close();
    }

    public void deleteOldRecord() {
        try {
            Calendar calNow = Calendar.getInstance();
            calNow.add(Calendar.MONTH, -2);
            Date dateBefore2Month = calNow.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String date2Months=dateFormat.format(dateBefore2Month);
            mcfDB = this.getWritableDatabase();
            mcfDB.execSQL("DELETE FROM " + TABLE_NAME + " WHERE (substr(runDate,0,5)||''||substr(runDate,6,2)||''||substr(runDate,9,2)||''||substr(runDate,12,2)||''||substr(runDate,15,2)) < '"+date2Months+"' AND isArchive='Y'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}