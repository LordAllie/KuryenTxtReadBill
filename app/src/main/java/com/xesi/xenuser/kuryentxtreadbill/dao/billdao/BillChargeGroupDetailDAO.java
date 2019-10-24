package com.xesi.xenuser.kuryentxtreadbill.dao.billdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroupDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 2/17/2017.
 */
public class BillChargeGroupDetailDAO extends BaseDAO {

    private ContentValues contentValues;
    private String TABLE_NAME = "armBillChargeGroupDetails";

    public BillChargeGroupDetailDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public boolean insertRecord(BillChargeGroupDetail billChargeDetail) {
        mcfDB = this.getReadableDatabase();
        boolean isInserted = false;
        try {
            contentValues = new ContentValues();
            contentValues.put("billNo", billChargeDetail.getBillNo());
            contentValues.put("printOrderMaster", billChargeDetail.getPrintOrderMaster());
            contentValues.put("printOrder", billChargeDetail.getPrintOrder());
            contentValues.put("chargeName", billChargeDetail.getChargeName());
            contentValues.put("chargeAmount", billChargeDetail.getChargeAmount().toString());
            contentValues.put("chargeTotal", billChargeDetail.getChargeTotal().toString());
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

    public boolean updateGroupDetails(BillChargeGroupDetail billChargeDetail) {
        mcfDB = this.getReadableDatabase();
        boolean isUpdated = false;
        try {
            contentValues = new ContentValues();
            contentValues.put("billNo", billChargeDetail.getBillNo());
            contentValues.put("printOrderMaster", billChargeDetail.getPrintOrderMaster());
            contentValues.put("chargeName", billChargeDetail.getChargeName());
            contentValues.put("chargeAmount", billChargeDetail.getChargeAmount().toString());
            contentValues.put("chargeTotal", billChargeDetail.getChargeTotal().toString());
            mcfDB.update(TABLE_NAME, contentValues, "billNo =  ? AND printOrderMaster = ? AND printOrder = ?",
                    new String[]{billChargeDetail.getBillNo(),
                            Integer.toString(billChargeDetail.getPrintOrderMaster()),
                            Integer.toString(billChargeDetail.getPrintOrder())});
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
            isUpdated = false;
        } finally {
            mcfDB.close();
        }
        return isUpdated;
    }

    public List<BillChargeGroupDetail> getAllChargeGroupDetails(String billNo, int printOrderMaster) {
        List<BillChargeGroupDetail> billGroupDetails = new ArrayList<>();
        String query = "SELECT billNo, printOrderMaster, printOrder,chargeName, chargeAmount, chargeTotal" +
                " FROM " + TABLE_NAME +
                " WHERE billNo = '" + billNo + "' AND printOrderMaster =" + printOrderMaster +
                " ORDER BY printOrderMaster";
        mcfDB = this.getWritableDatabase();
        BigDecimal chargeTotal;
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    BigDecimal chargeAmount = new BigDecimal(cursor.getString(4));
                    chargeAmount = chargeAmount.setScale(6, BigDecimal.ROUND_HALF_UP);
                    double dbchargeTotal = cursor.getDouble(5);
                    chargeTotal = new BigDecimal(Double.toString(dbchargeTotal));
                    BillChargeGroupDetail billChargeGroup = new BillChargeGroupDetail();
                    billChargeGroup.setBillNo(cursor.getString(0));
                    billChargeGroup.setPrintOrderMaster(cursor.getInt(1));
                    billChargeGroup.setPrintOrder(cursor.getInt(2));
                    billChargeGroup.setChargeName(cursor.getString(3));
                    billChargeGroup.setChargeAmount(chargeAmount);
                    billChargeGroup.setChargeTotal(chargeTotal);

                    billGroupDetails.add(billChargeGroup);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billGroupDetails;
    }


    public boolean updateBill(BillChargeGroupDetail billChargeDetail) {
        mcfDB = this.getReadableDatabase();
        boolean isUpdated;
        try {
            contentValues = new ContentValues();
            contentValues.put("billNo", billChargeDetail.getBillNo());
            if(billChargeDetail.getChargeAmount() != null)
                contentValues.put("chargeAmount", billChargeDetail.getChargeAmount().toString());
            contentValues.put("chargeTotal", billChargeDetail.getChargeTotal().toString());
            mcfDB.update(TABLE_NAME, contentValues, "billNo =  ? AND chargeName = ?",
                    new String[]{billChargeDetail.getBillNo(),
                            billChargeDetail.getChargeName()});
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
            isUpdated = false;
        } finally {
            mcfDB.close();
        }
        return isUpdated;
    }

    public BillChargeGroupDetail selectBillByBillNo(String data,String field,String billNumber) {
        BillChargeGroupDetail billChargeGroup = new BillChargeGroupDetail();
        String query = "SELECT * FROM " + TABLE_NAME+ " WHERE TRIM(" + field + ") = '" +data+ "' COLLATE NOCASE AND billNo ='" +billNumber +  "'";
        mcfDB = this.getReadableDatabase();
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                BigDecimal chargeAmount = new BigDecimal(cursor.getString(cursor.getColumnIndex("chargeAmount")));
                chargeAmount = chargeAmount.setScale(6, BigDecimal.ROUND_HALF_UP);
                double dbchargeTotal = cursor.getDouble(cursor.getColumnIndex("chargeTotal"));
                BigDecimal chargeTotal = new BigDecimal(Double.toString(dbchargeTotal));
                billChargeGroup.setBillNo(cursor.getString(cursor.getColumnIndex("billNo")));
                billChargeGroup.setPrintOrderMaster(cursor.getInt(cursor.getColumnIndex("printOrderMaster")));
                billChargeGroup.setPrintOrder(cursor.getInt(cursor.getColumnIndex("printOrder")));
                billChargeGroup.setChargeName(cursor.getString(cursor.getColumnIndex("chargeName")));
                billChargeGroup.setChargeAmount(chargeAmount);
                billChargeGroup.setChargeTotal(chargeTotal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billChargeGroup;
    }
    //get Charge Total Of Specific Charge Group Detail
    public BigDecimal getCTOfCGD(String billNo,String chargeName){
        String query = "SELECT chargeTotal from " + TABLE_NAME + " WHERE TRIM(chargeName) = '" + chargeName + "' COLLATE NOCASE " +
                " AND billNo ='" + billNo + "'";
        mcfDB = this.getWritableDatabase();
        BigDecimal chargeAmount = new BigDecimal(0);

        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                chargeAmount = BigDecimal.valueOf(cursor.getDouble(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return chargeAmount;
    }

    public BillChargeGroupDetail getLLDisc(String billNo) {
        BillChargeGroupDetail billGroupDetails = new BillChargeGroupDetail();
        String query = "SELECT *" +
                " FROM " + TABLE_NAME +
                " WHERE billNo = '" + billNo +"'"+
                " ORDER BY _id DESC LIMIT 2";
        mcfDB = this.getWritableDatabase();
        BigDecimal chargeTotal;
        try {
            Cursor cursor = mcfDB.rawQuery(query, null);
            if (cursor.moveToLast()) {
                BigDecimal chargeAmount = new BigDecimal(cursor.getString(5));
                chargeAmount = chargeAmount.setScale(6, BigDecimal.ROUND_HALF_UP);
                double dbchargeTotal = cursor.getDouble(6);
                chargeTotal = new BigDecimal(Double.toString(dbchargeTotal));
                billGroupDetails.setChargeName(cursor.getString(4));
                billGroupDetails.setChargeAmount(chargeAmount);
                billGroupDetails.setChargeTotal(chargeTotal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return billGroupDetails;
    }

}