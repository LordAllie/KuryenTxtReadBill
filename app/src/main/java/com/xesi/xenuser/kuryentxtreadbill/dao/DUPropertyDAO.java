package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.download.DUProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 4/20/2017.
 */
public class DUPropertyDAO extends BaseDAO {

    private String TABLE_NAME = "armDuProperties";

    public DUPropertyDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<DUProperty> getAll() {
        List<DUProperty> propertyList = new ArrayList<>();

        String query = "SELECT propertyName, propertyValue FROM " + TABLE_NAME + " ORDER BY propertyName";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    DUProperty duProperty = new DUProperty();
                    duProperty.setPropertyName(cursor.getString(0));
                    duProperty.setPropertyValue(cursor.getString(1));
                    // Adding item to list
                    propertyList.add(duProperty);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return propertyList;
    }

    public List<String> getAllPropertyName() {
        List<String> propertyList = new ArrayList<>();

        String query = "SELECT propertyName FROM " + TABLE_NAME + " ORDER BY propertyName";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    propertyList.add(cursor.getString(0));
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return propertyList;
    }

    public String getPropertyValue(String propertyName) {
        String value = "-1";
        String query = "SELECT propertyValue FROM " + TABLE_NAME +
                " WHERE  propertyName = '" + propertyName + "'";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                value = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return value;
    }

    public String getPropertyValue(String propertyName, String defVal) {
        String query = "SELECT propertyValue FROM " + TABLE_NAME +
                " WHERE  propertyName = '" + propertyName + "'";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                defVal = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return defVal;
    }

    public List<DUProperty> getProperties() {
        List<DUProperty> value = new ArrayList<>();
        String query = "SELECT propertyName, propertyValue  FROM " + TABLE_NAME;
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    DUProperty property = new DUProperty();
                    property.setPropertyName(cursor.getString(0));
                    property.setPropertyValue(cursor.getString(1));
                    value.add(property);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return value;
    }

}
