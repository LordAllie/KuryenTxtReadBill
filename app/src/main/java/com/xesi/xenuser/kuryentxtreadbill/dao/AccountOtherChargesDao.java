package com.xesi.xenuser.kuryentxtreadbill.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.BaseDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountOtherCharges;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daryll-POGI on 12/04/2019.
 */

public class AccountOtherChargesDao extends BaseDAO {

    private String TABLE_NAME = "arm_account_other_charges";

    public AccountOtherChargesDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        Log.i("sqlite", mContext.toString());
    }

    public List<AccountOtherCharges> getAcctNocharge(String acctNo) {
        List<AccountOtherCharges> accountOtherChargeList = new ArrayList<>();
        String selectQuery = " SELECT * FROM " + TABLE_NAME + " WHERE `accountNo` = '" + acctNo + "' ORDER BY id";
        mcfDB = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mcfDB.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
                do {
                    AccountOtherCharges accountOtherCharges = new AccountOtherCharges();
                    accountOtherCharges.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    accountOtherCharges.setIdAccount(cursor.getInt(cursor.getColumnIndex("idAccount")));
                    accountOtherCharges.setIdCharge(cursor.getInt(cursor.getColumnIndex("idCharge")));
                    accountOtherCharges.setAccountNo(cursor.getString(cursor.getColumnIndex("accountNo")));
                    accountOtherChargeList.add(accountOtherCharges);
                } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mcfDB.close();
        }
        return accountOtherChargeList;
    }
}
