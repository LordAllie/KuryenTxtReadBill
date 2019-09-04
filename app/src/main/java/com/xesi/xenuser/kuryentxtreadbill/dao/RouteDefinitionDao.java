package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;


public class RouteDefinitionDao extends BaseDAO {

    private String TABLE_NAME = "arm_route_definition";

    public RouteDefinitionDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public boolean updateIsActive(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean updated = false;
        try {
            ContentValues values = new ContentValues();
            values.put("isActive", 1);
            db.update(TABLE_NAME, values, "idRoute  = " + id, null);
            updated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return updated;
    }

    public boolean updateIsActive() {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean updated = false;
        try {
            ContentValues values = new ContentValues();
            values.put("isActive", 0);
            db.update(TABLE_NAME, values, null, null);
            updated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return updated;
    }
}