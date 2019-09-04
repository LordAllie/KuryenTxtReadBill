package com.xesi.xenuser.kuryentxtreadbill.dao.billdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonCharge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 2/16/2017.
 */
public class BillAddonChargeDAO extends BaseDAO {

    private ContentValues contentValues;
    private String TABLE_NAME = "armBillAddonCharge";

    public BillAddonChargeDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public boolean insertRecord(List<BillAddonCharge> billAddonCharges, String newBillNumber) {
        mcfDB = this.getReadableDatabase();
        boolean isInserted = false;
        try {
            for (BillAddonCharge billAddonCharge : billAddonCharges) {
                contentValues = new ContentValues();
                contentValues.put("billNo", newBillNumber);
                contentValues.put("addonCharge", billAddonCharge.getAddonCharge());
                contentValues.put("valueAddOn", billAddonCharge.getValue());
                long res = mcfDB.insertOrThrow(TABLE_NAME, null, contentValues);
                isInserted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isInserted = false;
        } finally {
            mcfDB.close();
        }
        return isInserted;
    }

    public boolean updateRecord(List<BillAddonCharge> billAddonCharges, String billNo) {
        mcfDB = this.getReadableDatabase();
        boolean isUploaded = false;
        try {
            for (BillAddonCharge billAddonCharge : billAddonCharges) {
                ContentValues values = new ContentValues();
                contentValues.put("valueAddOn", billAddonCharge.getValue());
                mcfDB.update(TABLE_NAME, values, "billNo =  ? AND addonCharge = ?", new String[]{billNo, billAddonCharge.getAddonCharge()});
                isUploaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isUploaded = false;
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }

    public boolean updateRecord(BillAddonCharge billAddonCharge) {
        mcfDB = this.getReadableDatabase();
        boolean isUploaded = false;
        try {
            contentValues = new ContentValues();
            contentValues.put("valueAddOn", billAddonCharge.getValue());
            mcfDB.update(TABLE_NAME, contentValues, "billNo =  ? AND addonCharge = ?",
                    new String[]{billAddonCharge.getBillNo(), billAddonCharge.getAddonCharge()});
            isUploaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }

    public List<BillAddonCharge> getAllBillAddonCharges(String billNo) {
        List<BillAddonCharge> billAddonCharges = new ArrayList<>();
        String query = "SELECT _id, billNo, addonCharge, valueAddOn" +
                " FROM " + TABLE_NAME +
                " WHERE billNo = '" + billNo + "' ORDER BY _id DESC";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    BillAddonCharge billAddonCharge = new BillAddonCharge();
                    billAddonCharge.set_id(cursor.getInt(0));
                    billAddonCharge.setBillNo(cursor.getString(1));
                    billAddonCharge.setAddonCharge(cursor.getString(2));
                    billAddonCharge.setValue(cursor.getDouble(3));
                    billAddonCharges.add(billAddonCharge);
                } while (cursor.moveToNext());
            } else {
                billAddonCharges = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billAddonCharges;
    }
}