package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountOtherCharges;
import com.xesi.xenuser.kuryentxtreadbill.model.download.Surcharge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daryll-POGI on 21/05/2019.
 */

public class SurchargeDao extends BaseDAO {


    private String TABLE_NAME = "arm_surcharge";

    public SurchargeDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<Surcharge> getAll() {
        List<Surcharge> surchargeList = new ArrayList<>();
        String selectQuery = " SELECT * FROM " + TABLE_NAME +" ORDER BY id";
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
                do {
                    Surcharge surcharge = new Surcharge();
                    surcharge.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    surcharge.setDays(cursor.getInt(cursor.getColumnIndex("days")));
                    surcharge.setSurcharge(new BigDecimal(cursor.getString(cursor.getColumnIndex("surcharge"))));
                    surchargeList.add(surcharge);
                } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mcfDB.close();
        }
        return surchargeList;
    }


}
