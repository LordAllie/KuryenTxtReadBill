package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.BillJson;
import com.xesi.xenuser.kuryentxtreadbill.model.StringBill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daryll-POGI on 08/01/2020.
 */

public class BillJsonDao extends BaseDAO {

    private String TABLE_NAME = "armBillJson";

    public BillJsonDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public boolean insert(String billNo, String json) {
        boolean inserted = false;
        try {
            mcfDB = this.getWritableDatabase();
            long result;
            ContentValues values = new ContentValues();
            values.put("billNo", billNo);
            values.put("json", json);
            values.put("isUploaded", 0);
            values.put("isArchived", 0);
            result = mcfDB.insertOrThrow(TABLE_NAME, null, values);
            if (result > 0) {
                inserted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }

        return inserted;
    }

    public List<StringBill> getAllStringBills() {
        List<StringBill> billJsonList = new ArrayList<>();
        mcfDB = this.getWritableDatabase();

        String query = "SELECT billNo, json " +
                " FROM " + TABLE_NAME +
                " WHERE isUploaded = 0 AND isArchived = 0";
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

    public boolean updateAll() {
        mcfDB = this.getWritableDatabase();
        boolean isUploaded = false;
        try {
            ContentValues values = new ContentValues();
            values.put("isUploaded", 1);
            mcfDB.update(TABLE_NAME, values, "isArchived = ?", new String[]{"0"});
            isUploaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }

}
