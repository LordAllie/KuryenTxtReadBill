package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 11/22/2016.
 */
public class NewMeterDao extends BaseDAO {

    private String TABLE_NAME = "armNewMeter";

    public NewMeterDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public boolean insert(NewMeterModel newMeter) {
        boolean inserted = false;
        try {
            mcfDB = this.getWritableDatabase();
            long result;
            ContentValues values = new ContentValues();
            values.put("idRoute", newMeter.getIdRoute());
            values.put("msn", newMeter.getMsn());
            values.put("reading", newMeter.getReading());
            values.put("isUploaded", 0);
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

    public List<NewMeterModel> getAll() {
        List<NewMeterModel> meterModels = new ArrayList<>();
        String selectQuery = "select idRoute, msn, dateRead, timeRead, reading, isUploaded from " +
                TABLE_NAME + " where isUploaded = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NewMeterModel meterModel = new NewMeterModel();
                meterModel.setIdRoute(cursor.getInt(0));
                meterModel.setMsn(cursor.getString(1));
                meterModel.setDateRead(cursor.getString(2));
                meterModel.setTimeRead(cursor.getString(3));
                meterModel.setReading(cursor.getInt(4));
                meterModel.setIsUploaded(cursor.getInt(5));
                meterModels.add(meterModel);
            }
            while (cursor.moveToNext());
        }
        return meterModels;
    }

    public boolean updateIsUpload(String msn) {
        mcfDB = this.getWritableDatabase();
        boolean isUploaded = false;
        try {
            ContentValues values = new ContentValues();
            values.put("isUploaded", "1");
            mcfDB.update(TABLE_NAME, values, "msn  =  ?", new String[]{msn});
            isUploaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }
}