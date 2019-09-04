package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonKwh;

import java.util.ArrayList;
import java.util.List;

public class KwhAddOnTranDao extends BaseDAO {

    private String TABLE_NAME = "arm_kwhaddontran";

    public KwhAddOnTranDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<BillAddonKwh> getAddonKwh(String accountNumber) {
        List<BillAddonKwh> addonKwhs = new ArrayList<BillAddonKwh>();
        String selectQuery = "select kwh, addOnKWHName from " + TABLE_NAME + " where accountNumber = '" + accountNumber + "'";
        mcfDB = this.getWritableDatabase();

        try {
            Cursor cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    BillAddonKwh billAddonKwh = new BillAddonKwh();
                    billAddonKwh.setValue(cursor.getDouble(0));
                    billAddonKwh.setAddonKwh(cursor.getString(1));
                    addonKwhs.add(billAddonKwh);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return addonKwhs;
    }
}
