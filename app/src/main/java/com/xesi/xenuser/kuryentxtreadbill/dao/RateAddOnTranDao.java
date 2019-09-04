package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonCharge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raymond Barrinuevo on 9/9/2016.
 */
public class RateAddOnTranDao extends BaseDAO {

    private String TABLE_NAME = "arm_rateaddontran";

    public RateAddOnTranDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }
    
    public List<BillAddonCharge> getBillAddonCharges(String accountNumber) {
        List<BillAddonCharge> billAddonCharges = new ArrayList<>();

        String selectQuery = "select amount, chargeName from " + TABLE_NAME + " where accountNumber = '" + accountNumber + "'";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    BillAddonCharge billAddonCharge = new BillAddonCharge();
                    billAddonCharge.setValue(cursor.getDouble(0));
                    billAddonCharge.setAddonCharge(cursor.getString(1));
                    billAddonCharges.add(billAddonCharge);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billAddonCharges;
    }

}
