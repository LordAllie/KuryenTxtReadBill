package com.xesi.xenuser.kuryentxtreadbill.dao.billdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillSurcharge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daryll-POGI on 21/05/2019.
 */

public class BillSurchargeDao extends BaseDAO {

    private ContentValues contentValues;
    private String TABLE_NAME = "armBillSurcharge";
    private final String BILLNO = "billNo";

    public BillSurchargeDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<BillSurcharge> getSurchargeByBillNo(String billNo) {
        List<BillSurcharge> billSurchargeList = new ArrayList<>();
        String selectQuery = " SELECT * FROM " + TABLE_NAME +" WHERE billNo ='" +billNo + "' ORDER BY id";
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
                do {
                    BillSurcharge billSurcharge = new BillSurcharge();
                    billSurcharge.setDays(cursor.getInt(cursor.getColumnIndex("days")));
                    billSurcharge.setSurcharge(new BigDecimal(cursor.getString(cursor.getColumnIndex("surcharge"))));
                    billSurcharge.setSurchargeRate(new BigDecimal(cursor.getString(cursor.getColumnIndex("surchargeRate"))));
                    billSurchargeList.add(billSurcharge);
                } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mcfDB.close();
        }
        return billSurchargeList;
    }

    public boolean insertRecord(BillSurcharge billSurcharge, String newBillNumber) {
        mcfDB = this.getReadableDatabase();
        boolean isInserted = false;
        try {
                contentValues = new ContentValues();
                contentValues.put("billNo", newBillNumber);
                contentValues.put("days", billSurcharge.getDays());
                contentValues.put("surcharge", String.valueOf(billSurcharge.getSurcharge()));
                contentValues.put("surchargeRate", String.valueOf(billSurcharge.getSurchargeRate()));
                mcfDB.insertOrThrow(TABLE_NAME, null, contentValues);
                isInserted = true;
        } catch (Exception e) {
            e.printStackTrace();
            isInserted = false;
        } finally {
            mcfDB.close();
        }
        return isInserted;
    }

    public boolean updateRecord(BillSurcharge billSurcharge, String newBillNumber) {
        mcfDB = this.getWritableDatabase();
        boolean isUpdated = false;
        try {
            contentValues = new ContentValues();
            contentValues.put("billNo", newBillNumber);
            contentValues.put("days", billSurcharge.getDays());
            contentValues.put("surcharge", String.valueOf(billSurcharge.getSurcharge()));
            contentValues.put("surchargeRate", String.valueOf(billSurcharge.getSurchargeRate()));
            mcfDB.update(TABLE_NAME, contentValues, BILLNO + " =  ? AND days = ?", new String[]{newBillNumber,String.valueOf(billSurcharge.getDays())});
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
}
