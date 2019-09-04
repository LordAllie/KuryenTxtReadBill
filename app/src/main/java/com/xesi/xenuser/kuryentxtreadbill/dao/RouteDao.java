package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.RouteModel;

/**
 * Created by Daryll Sabate on 12/14/2017.
 */

public class RouteDao extends BaseDAO {
    private String TABLE_NAME = "armRoute";

    public RouteDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public RouteModel getRouteDetail(String routeCode) {
        mcfDB = this.getWritableDatabase();
        String query = "SELECT * " +
                " FROM " + TABLE_NAME + " WHERE routeCode = '" + routeCode + "'";
        RouteModel routeModel = new RouteModel();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                routeModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
                routeModel.setRouteCode(cursor.getString(cursor.getColumnIndex("routeCode")));
                routeModel.setDueDay(cursor.getInt(cursor.getColumnIndex("dueDay")));
                routeModel.setBillingDayStart(cursor.getInt(cursor.getColumnIndex("billingDayStart")));
                routeModel.setBillingDayEnd(cursor.getInt(cursor.getColumnIndex("billingDayEnd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return routeModel;
    }
}
