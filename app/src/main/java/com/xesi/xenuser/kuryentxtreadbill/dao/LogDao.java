package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.LogModel;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daryll Sabate on 10/23/2017.
 */
public class LogDao extends BaseDAO {

    private String TABLE_NAME = "db_log";

    public LogDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public boolean insert(LogModel logModel) {
        boolean inserted = false;
        try {
            mcfDB = this.getWritableDatabase();
            long result;
            ContentValues values = new ContentValues();
            values.put("datetime", logModel.getDateTime());
            values.put("message", logModel.getMessage());
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

    public List<LogModel> getLogs() throws NullPointerException {
        List<LogModel> logModelList = new ArrayList<>();

        String selectQuery = " SELECT * from " + TABLE_NAME
                + " ORDER BY _id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    LogModel logModel = new LogModel();
                    logModel.setDateTime(cursor.getString(cursor.getColumnIndex("datetime")));
                    logModel.setMessage(cursor.getString(cursor.getColumnIndex("message")));
                    // Adding item to list
                    logModelList.add(logModel);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return logModelList;
    }
}
