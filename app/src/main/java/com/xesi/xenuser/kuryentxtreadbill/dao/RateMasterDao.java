package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RateMaster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RateMasterDao extends BaseDAO {

    private String retVal;
    private String TABLE_NAME = "arm_ratemaster";

    public RateMasterDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<RateMaster> getAllRateMasterDetails() {
        List<RateMaster> rateMasterDetailsList = new ArrayList<>();
        String selectQuery = "select rateName, totalPerKwCharge, totalFixedCharge, totalPerKwChargeSTL from " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RateMaster rateMaster = new RateMaster();
                rateMaster.setRateName(cursor.getString(0));
                rateMaster.setTotalPerKwCharge(cursor.getDouble(1));
                rateMaster.setTotalFixedCharge(cursor.getDouble(2));
                rateMaster.setTotalPerKwChargeSTL(cursor.getDouble(3));
                rateMasterDetailsList.add(rateMaster);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rateMasterDetailsList;
    }

}
