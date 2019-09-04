package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.download.OtherCharges;

import java.util.List;

/**
 * Created by Daryll-POGI on 12/04/2019.
 */

public class OtherChargesDao extends BaseDAO {

    private String TABLE_NAME = "arm_other_charges";

    public OtherChargesDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public OtherCharges getOtherCharge(long id) {
        OtherCharges otherCharges = new OtherCharges();
        String selectQuery = " SELECT * FROM " + TABLE_NAME + " WHERE `id` = '" + id + "'";
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()){
                otherCharges.setId(cursor.getInt(cursor.getColumnIndex("id")));
                otherCharges.setChargeName(cursor.getString(cursor.getColumnIndex("chargeName")));
                otherCharges.setAmountPerKw(cursor.getDouble(cursor.getColumnIndex("amountPerKw")));
                otherCharges.setAmountFixed(cursor.getDouble(cursor.getColumnIndex("amountFixed")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mcfDB.close();
        }
        return otherCharges;
    }
}
