package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountBillAux;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountOtherCharges;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daryll-POGI on 30/05/2019.
 */

public class AccountBillAuxDao  extends BaseDAO {

    private String TABLE_NAME = "arm_account_bill_aux";

    public AccountBillAuxDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    public List<AccountBillAux> getAccBillAux(String acctNo) {
        List<AccountBillAux> accountBillAuxList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME +" WHERE accountNo ='" + acctNo + "' ORDER BY id";
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()){
                do {
                    AccountBillAux accountBillAux = new AccountBillAux();
                    accountBillAux.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    accountBillAux.setChargeAmount(new BigDecimal(cursor.getString(cursor.getColumnIndex("chargeAmount"))));
                    accountBillAux.setChargeName(cursor.getString(cursor.getColumnIndex("chargeName")));
                    accountBillAuxList.add(accountBillAux);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mcfDB.close();
        }
        return accountBillAuxList;
    }

}
