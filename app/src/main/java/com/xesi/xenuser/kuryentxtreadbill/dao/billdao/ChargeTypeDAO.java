package com.xesi.xenuser.kuryentxtreadbill.dao.billdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.ChargeTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 2/14/2017.
 */
public class ChargeTypeDAO extends BaseDAO {

    private ContentValues contentValues;
    private String TABLE_NAME = "armChargeType";

    public ChargeTypeDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<ChargeTypeModel> getChargeType(int idRateMaster) {
        List<ChargeTypeModel> chargeTypeList = new ArrayList<>();

        String query = "SELECT id, idRateMaster, printOrder, chargeTypeCode, chargeTypeName, subtotalName " +
                "FROM " + TABLE_NAME + " WHERE idRateMaster = " + idRateMaster + " ORDER BY printOrder";
        mcfDB = this.getWritableDatabase();

        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    ChargeTypeModel chargeType = new ChargeTypeModel();
                    chargeType.setIdRateMaster(cursor.getInt(1));
                    chargeType.setPrintOrder(cursor.getInt(2));
                    chargeType.setChargeTypeCode(cursor.getString(3));
                    chargeType.setChargeTypeName(cursor.getString(4));
                    chargeType.setSubtotalName(cursor.getString(5));
                    chargeType.setIdChargeType(cursor.getInt(0));
                    chargeTypeList.add(chargeType);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return chargeTypeList;
    }
}