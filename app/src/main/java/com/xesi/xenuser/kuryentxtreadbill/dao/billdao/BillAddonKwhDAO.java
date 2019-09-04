package com.xesi.xenuser.kuryentxtreadbill.dao.billdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonKwh;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 2/16/2017.
 */
public class BillAddonKwhDAO extends BaseDAO {

    private ContentValues contentValues;
    private String TABLE_NAME = "armBillAddonKwh";

    public BillAddonKwhDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public boolean insertRecord(List<BillAddonKwh> billAddonKwhs, String newBillNumber) {
        mcfDB = this.getReadableDatabase();
        boolean isInserted = false;
        try {
            for (BillAddonKwh billAddonKwh : billAddonKwhs) {
                contentValues = new ContentValues();
                contentValues.put("billNo", newBillNumber);
                contentValues.put("addonKwh", billAddonKwh.getAddonKwh());
                contentValues.put("valueKwh", billAddonKwh.getValue());
                long inserted = mcfDB.insertOrThrow(TABLE_NAME, null, contentValues);
                if (inserted > 0) {
                    isInserted = true;
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            isInserted = false;
        } finally {
            mcfDB.close();
        }
        return isInserted;
    }

    public boolean updateRecord(List<BillAddonKwh> billAddonKwhs, String billNo) {
        mcfDB = this.getReadableDatabase();
        boolean isUploaded = false;
        try {
            for (BillAddonKwh billAddonKwh : billAddonKwhs) {
                ContentValues values = new ContentValues();
                if (checkRecord(billNo, billAddonKwh.getAddonKwh())) {
                    values.put("addonKwh", billAddonKwh.getAddonKwh());
                    values.put("valueKwh", billAddonKwh.getValue());
                    mcfDB.update(TABLE_NAME, values, "billNo =  ? ", new String[]{billNo});
                    isUploaded = true;
                } else {
                    values.put("billNo", billAddonKwh.getBillNo());
                    values.put("addonKwh", billAddonKwh.getAddonKwh());
                    values.put("valueKwh", billAddonKwh.getValue());
                    isUploaded = mcfDB.insertOrThrow(TABLE_NAME, null, values) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isUploaded = false;
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }

    public List<BillAddonKwh> getAllBillAddonKwh(String billNo) {
        List<BillAddonKwh> billAddonKwhs = new ArrayList<>();
        String query = "SELECT billNo, addonKwh, valueKwh" +
                " FROM " + TABLE_NAME +
                " WHERE billNo = '" + billNo + "' ORDER BY _id DESC";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    BillAddonKwh billChargeGroup = new BillAddonKwh();
                    billChargeGroup.setBillNo(cursor.getString(0));
                    billChargeGroup.setAddonKwh(cursor.getString(1));
                    billChargeGroup.setValue(cursor.getDouble(2));
                    billAddonKwhs.add(billChargeGroup);
                } while (cursor.moveToNext());
            } else {
                billAddonKwhs = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billAddonKwhs;
    }

    public boolean checkRecord(String newBillNumber, String kwhAddOnName) {
        String query = "SELECT COUNT(_id)" +
                " FROM " + TABLE_NAME +
                " WHERE isUploaded = 0 AND billNo = '" + newBillNumber + "' AND addonKwh = '" + kwhAddOnName + "' ORDER BY _id ";
        mcfDB = this.getWritableDatabase();
        boolean isRecordExist = false;
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                isRecordExist = cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRecordExist;
    }

    public void deleteRecord(String s, String billNo) {
        try {
            mcfDB = this.getWritableDatabase();
            mcfDB.execSQL("DELETE FROM " + TABLE_NAME + " WHERE addonKwh = '" + s + "' AND billNo = '" + billNo + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}