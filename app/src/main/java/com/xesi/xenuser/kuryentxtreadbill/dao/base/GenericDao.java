package com.xesi.xenuser.kuryentxtreadbill.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by Daryll Sabate on 10/17/2017.
 */
public class GenericDao extends BaseDAO {

    public GenericDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public String save(Map<String, Object> list, String tableName) {
        String savingStatus = "failed";
        try {
            mcfDB = this.getWritableDatabase();
            long result;
            ContentValues values = new ContentValues();
            boolean lo = true;
            for (Map.Entry<String, Object> entry : list.entrySet()) {
                if (!(entry.getValue() instanceof List)) {
                    if (entry.getValue() == null) values.put(entry.getKey(), "");
                    else values.put(entry.getKey(), entry.getValue().toString());
                } else {
                    lo = false;
                    List<Map<String, Object>> objList = (List<Map<String, Object>>) entry.getValue();
                    for (Map<String, Object> obj : objList) {
                        for (Map.Entry<String, Object> entryList : obj.entrySet()) {
                            if(entryList.getValue() == null) values.put(entryList.getKey(), "");
                            else values.put(entryList.getKey(), entryList.getValue().toString());
                        }
                        result = mcfDB.insertOrThrow(tableName, null, values);
                        if (result != -1) savingStatus = "done";
                    }
                }
            }
            if (lo) {
                result = mcfDB.insertOrThrow(tableName, null, values);
                if (result != -1) savingStatus = " Done";
            }
            values.clear();
            mcfDB.close();
        } catch (Exception e) {
            e.printStackTrace();
            savingStatus = e.getMessage();
        }
        return savingStatus;
    }

    public String getOneField(String fieldName,String tableName,String WhereFieldName,String data, String lastStatement,String defaultValue) {
        String dataInner = defaultValue;
        String query = "SELECT "+ fieldName +
                " FROM " + tableName + " "  + WhereFieldName +" '" + data + "' " +lastStatement ;
        if (data.equals("")) query = query.replace("'","");

        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) dataInner = cursor.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        if(dataInner == null)
            dataInner = defaultValue;
        return dataInner;
    }

    public String getOneField(String query,String defaultValue) {
        String dataInner = defaultValue;

        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) dataInner = cursor.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        if(dataInner == null)
            dataInner = defaultValue;
        return dataInner;
    }

}
