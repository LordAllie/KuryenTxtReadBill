package com.xesi.xenuser.kuryentxtreadbill.dao.billdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 2/15/2017.
 */
public class BillChargeGroupDAO extends BaseDAO {

    private ContentValues contentValues;
    private String TABLE_NAME = "armBillChargeGroup";

    public BillChargeGroupDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    // Insert record
    public boolean insertRecord(BillChargeGroup billgroup) {
        mcfDB = this.getReadableDatabase();
        boolean isInserted = false;
        try {
            contentValues = new ContentValues();
            contentValues.put("billNo", billgroup.getBillNumber());
            contentValues.put("printOrder", billgroup.getPrintOrder());
            contentValues.put("chargeGroupName", billgroup.getChargeTypeName());
            mcfDB.insert(TABLE_NAME, null, contentValues);
            isInserted = true;
        } catch (Exception e) {
            e.printStackTrace();
            isInserted = false;
        } finally {
            mcfDB.close();
        }
        return isInserted;
    }

    // Insert record
    public boolean updateRecord(BillChargeGroup billgroup) {
        mcfDB = this.getReadableDatabase();
        boolean isInserted = false;
        try {
            contentValues = new ContentValues();
            contentValues.put("chargeTotal", billgroup.getTotalCharges().toString());
            mcfDB.update(TABLE_NAME, contentValues, "billNo =  ? AND printOrder = " + billgroup.getPrintOrder(),
                    new String[]{billgroup.getBillNumber()});
            isInserted = true;
        } catch (Exception e) {
            e.printStackTrace();
            isInserted = false;
        } finally {
            mcfDB.close();
        }
        return isInserted;
    }

    public List<BillChargeGroup> getAllChargeGroups(String billNo) {
        List<BillChargeGroup> billHeaderLists = new ArrayList<>();
        String query = "SELECT billNo, printOrder, chargeGroupName, chargeSubTotal, chargeTotal" +
                " FROM " + TABLE_NAME +
                " WHERE billNo = '" + billNo + "' ORDER BY printOrder";
        mcfDB = this.getWritableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    BillChargeGroup billChargeGroup = new BillChargeGroup();
                    billChargeGroup.setBillNumber(cursor.getString(0));
                    billChargeGroup.setPrintOrder(cursor.getInt(1));
                    billChargeGroup.setChargeTypeName(cursor.getString(2));
                    BigDecimal subtotal = new BigDecimal(cursor.getString(3));
                    billChargeGroup.setSubtotalCharges(subtotal);
                    double dbTotal = cursor.getDouble(4);
                    BigDecimal total = new BigDecimal(Double.toString(dbTotal));
                    billChargeGroup.setTotalCharges(total);
                    billHeaderLists.add(billChargeGroup);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billHeaderLists;
    }

    //get Charge Total Of Specific Charge Group
    public BigDecimal getCTOfCG(String billNo,String chargeGroupName){
        String query = "SELECT chargeTotal from " + TABLE_NAME + " WHERE TRIM(chargeGroupName) = '" + chargeGroupName + "' COLLATE NOCASE " +
                " AND billNo ='" + billNo + "'";
        mcfDB = this.getWritableDatabase();
        BigDecimal chargeTotal = new BigDecimal(0);

        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                chargeTotal = BigDecimal.valueOf(cursor.getDouble(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return chargeTotal;
    }

}