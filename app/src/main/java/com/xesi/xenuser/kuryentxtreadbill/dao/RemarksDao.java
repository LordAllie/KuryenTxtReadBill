package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RemarksDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 11/22/2016.
 */
public class RemarksDao extends BaseDAO {

    private String TABLE_NAME = "arm_remarks";

    public RemarksDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<String> getRemarsList() {
        List<String> routeList = new ArrayList<String>();

        String selectQuery = "select DISTINCT(remarks) from " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                routeList.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        return routeList;
    }
}