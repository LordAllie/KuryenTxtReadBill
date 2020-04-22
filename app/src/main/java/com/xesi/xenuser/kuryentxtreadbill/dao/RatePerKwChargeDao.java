package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroupDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class RatePerKwChargeDao extends BaseDAO {

    private SQLiteDatabase db;
    private Cursor cursor;
    private String TABLE_NAME = "arm_rate_detail";
    private String query;
    private BigDecimal rate = new BigDecimal(0);
    private BigDecimal charges;
    private BigDecimal totalCharges;

    public RatePerKwChargeDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<BillChargeGroupDetail> getChargesGroupDetail(String billNo, int rateId, double consumption, String chargeType, int idChargeType,
                                                             int cgPrintOrder, boolean isLL, String isTrancated, String ifSenior) {
        String addedQuery="";
        String chargeTypeQuery="";
        if(ifSenior.equals("Y"))
            addedQuery =  " AND isOffIfSenior ='N'";
        if(isLL)
            addedQuery = addedQuery + " AND isOffIfLifeliner ='N'";
        if(idChargeType==0)
            chargeTypeQuery="chargeType = '"+chargeType+"'";
        else
            chargeTypeQuery="idChargeType = " + idChargeType;

        query = "SELECT * from " + TABLE_NAME + " WHERE " + chargeTypeQuery +
                " AND rateId = " + rateId + addedQuery +" ORDER BY printOrder";
        db = this.getWritableDatabase();
        List<BillChargeGroupDetail> initialBillDetailsList = new ArrayList<>();
        BigDecimal computedRates;
        BigDecimal bdConsumption = new BigDecimal(consumption);
        BigDecimal chargeAmount;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    BillChargeGroupDetail billChargeGroupDetail = new BillChargeGroupDetail();
                    chargeAmount = new BigDecimal(cursor.getString(cursor.getColumnIndex("totalAmount")));
                    if (isLL)
                        chargeAmount = chargeAmount.add(new BigDecimal(cursor.getString(cursor.getColumnIndex("adjToLifeline"))));
                    chargeAmount = chargeAmount.setScale(6, BigDecimal.ROUND_HALF_UP);
                    computedRates = bdConsumption.multiply(chargeAmount);
                    computedRates = computedRates.add(new BigDecimal(cursor.getString(cursor.getColumnIndex("fixedAddtl"))));
                    computedRates = new BigDecimal(computedRates.toString());
                    if (isTrancated.equals("Y"))
                        computedRates = computedRates.setScale(2, RoundingMode.DOWN);
                    else
                        computedRates = computedRates.setScale(2, BigDecimal.ROUND_HALF_UP);
                    billChargeGroupDetail.setBillNo(billNo);
                    billChargeGroupDetail.setPrintOrder(cursor.getInt(cursor.getColumnIndex("printOrder")));
                    billChargeGroupDetail.setPrintOrderMaster(cgPrintOrder);
                    billChargeGroupDetail.setChargeName(cursor.getString(cursor.getColumnIndex("perKwRateName")));
                    billChargeGroupDetail.setChargeAmount(chargeAmount);
                    billChargeGroupDetail.setChargeTotal(computedRates);
                    initialBillDetailsList.add(billChargeGroupDetail);
                    System.out.println("idchargetype: "+cursor.getInt(cursor.getColumnIndex("id")));
                    System.out.println("idchargetype: "+cursor.getString(cursor.getColumnIndex("perKwRateName")));
                    System.out.println("idchargetype: "+cursor.getInt(cursor.getColumnIndex("idChargeType")));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return initialBillDetailsList;
    }

    public BigDecimal findSTLRate(int rateId, double consumption, String isTrancated) {
        query = "SELECT totalAmount, fixedAddtl from " + TABLE_NAME +
                " where isSubjectToLifeLine = 'Y' and rateId = " + rateId +
                " order by printOrder";
        totalCharges = new BigDecimal(0);
        BigDecimal initTotalCharges;
        try {
            db = this.getWritableDatabase();
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                rate = new BigDecimal(cursor.getString(0));
                rate = rate.setScale(6, BigDecimal.ROUND_HALF_UP);
                charges = rate.multiply(new BigDecimal(consumption)).add(new BigDecimal(cursor.getString(1)));
                if (isTrancated.equals("Y")) {
                    initTotalCharges = charges.setScale(2, RoundingMode.DOWN);
                } else {
                    initTotalCharges = charges.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                totalCharges = totalCharges.add(initTotalCharges);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return totalCharges;
    }

    public BigDecimal calculateSurcharge(long rateId, double consumption) {
        query = "SELECT SUM(totalAmount), SUM(fixedAddtl) from " + TABLE_NAME +
                " where isSubToSurcharge = 'Y' and rateId = " + rateId +
                " order by printOrder";
        totalCharges = new BigDecimal(0);
        BigDecimal initTotalCharges;
        try {
            db = this.getWritableDatabase();
            cursor = db.rawQuery(query, null);
            if (cursor.moveToNext()) {
                rate = new BigDecimal(cursor.getString(0));
                rate = rate.setScale(6, BigDecimal.ROUND_HALF_UP);
                charges = rate.multiply(new BigDecimal(consumption)).add(new BigDecimal(cursor.getString(1)));
                initTotalCharges = charges.setScale(2, BigDecimal.ROUND_HALF_UP);
                totalCharges = totalCharges.add(initTotalCharges);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        totalCharges = totalCharges.setScale(2, BigDecimal.ROUND_HALF_UP);
        return totalCharges;
    }

}