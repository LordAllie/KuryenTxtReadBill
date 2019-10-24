package com.xesi.xenuser.kuryentxtreadbill.dao.base;

import android.content.Context;

/**
 * Created by Daryll-POGI on 24/10/2019.
 */

public class DeleteTableData {

    GenericDao genericDao;
    public DeleteTableData(Context context){
        genericDao = new GenericDao(context);
        String[] tableList={"armChargeType","armDuAreaRate","armDuProperties","armNewMeter","armRoute","arm_account","arm_account_bill_aux","arm_account_other_charges","arm_coreloss_tran","arm_kwhaddontran","arm_lifelinedetails","arm_other_charges","arm_rate_detail","arm_rateaddontran","arm_rateaddontran_special","arm_ratemaster","arm_remarks","arm_route_definition","arm_surcharge","db_log","sqlite_sequence"};
        int i = 0;
        while (tableList.length > i) {
            genericDao.deleteTable(tableList[i]);
        }
    }
}
