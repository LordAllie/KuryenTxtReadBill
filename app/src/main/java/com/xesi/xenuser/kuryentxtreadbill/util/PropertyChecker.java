package com.xesi.xenuser.kuryentxtreadbill.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.Homepage;
import com.xesi.xenuser.kuryentxtreadbill.Tools;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Daryll Sabate on 5/17/2018.
 */

public class PropertyChecker {

    private Context context;
    private DUPropertyDAO propertyDAO;
    private List<String> propertyList;
    private List<String> duProperties;
    private List<String> missingProperties;
    private MyObservables observables;
    private GenericDao genericDao;

    public PropertyChecker(Context context) {
        this.context = context;
        this.propertyDAO = new DUPropertyDAO(context);
        this.propertyList = new ArrayList<>();
        this.missingProperties = new ArrayList<>();
        genericDao = new GenericDao(context);
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), context);
    }

    public List<String> checkProperties() {
        // FROM DB
        duProperties = propertyDAO.getAllPropertyName();

        // HARDCODED+
        propertyList = setPropertyList();

        for (int x = 0; x < propertyList.size(); x++)
            if (duProperties.indexOf(propertyList.get(x)) == -1)
                missingProperties.add(propertyList.get(x));

        return missingProperties;
    }

    private List<String> setPropertyList() {
        propertyList = new ArrayList<>();
        propertyList.add("ADDITIONAL_DAY_ON_PERIOD_FROM");
        propertyList.add("BILLING_MONTH_FORMAT");
        propertyList.add("BILLING_MONTH_SETUP");
        propertyList.add("BILLS_VALID_AFTER_DUE");
        propertyList.add("CAPTURE_IMAGE_PROBABILITY");
        propertyList.add("DAYS_TO_DISCONNECT");
        propertyList.add("DAYS_TO_DUE");
        propertyList.add("DU_ADDRESSLN1");
        propertyList.add("DU_ADDRESSLN2");
        propertyList.add("DU_CODE");
        propertyList.add("DU_VAT_NO");
        propertyList.add("FOOTER_LINE_1");
        propertyList.add("FOOTER_LINE_2");
        propertyList.add("FOOTER_LINE_3");
        propertyList.add("FS_FOOTERLN1");
        propertyList.add("FS_FOOTERLN2");
        propertyList.add("FS_FOOTERLN3");
        propertyList.add("IS_CURRENT_BILL_PRIORITY_PRINT");
        propertyList.add("IS_DUE_DATE_BASED_ON_ROUTE");
        propertyList.add("IS_HIGHLIGHT_ACCT_NO");
        propertyList.add("IS_KWH_ADD_ON_FOR_ZERO_READING");
        propertyList.add("IS_LL_DIRECTLY_COMPUTED");
        propertyList.add("IS_LLS_INCL_TO_SCD");
        propertyList.add("IS_NO_WEEKEND_DUE");
        propertyList.add("IS_PERIOD_COVERED_BASED_ON_ROUTE");
        propertyList.add("IS_PRINT_BILL_AFTER_DUE");
        propertyList.add("IS_PRINT_DISCONNECTION_DATE");
        propertyList.add("IS_PRINT_IN_TABULAR_FORM");
        propertyList.add("IS_PRINT_LOGO");
        propertyList.add("IS_PRINT_OLD_SEQ_NO");
        propertyList.add("IS_PRINT_QR_CODE");
        propertyList.add("IS_PRINT_SUB_TOTAL");
        propertyList.add("IS_PRINT_SURCHARGE");
        propertyList.add("IS_PRINT_ZERO");
        propertyList.add("IS_SCS_ZERO_IF_LIFELINER");
        propertyList.add("IS_SEARCH_KEYPAD_NUMERIC");
        propertyList.add("IS_SURCHARGE_BASED_ON_TOTAL");
        propertyList.add("IS_TRUNCATED_VALUE");
        propertyList.add("SC_DISCOUNT_RATE");
        propertyList.add("SC_KW_LIMIT");
        propertyList.add("SC_KW_LIMIT_MIN");
        propertyList.add("SCD_DIRECTLY_COMPUTED_RATE");
        propertyList.add("SURCHARGE_RATE");
        propertyList.add("LIFELINE_RATE_SUB");
        propertyList.add("SC_SUB");
        propertyList.add("HOME_DIR");
        // propertyList.add("IS_VAT_DIST_SPECIAL");
        propertyList.add("VAT_RATE");
        propertyList.add("IS_PRINT_DOE_COMPLIANCE");
        propertyList.add("IS_PREVIOUS_READING_HIDDEN");
        propertyList.add("IS_PRINT_ROUTE_CODE");
        propertyList.add("IS_PRINT_READING_DISABLE");
        propertyList.add("IS_PRINT_BILL_SUMMARY_PROTECTED");
        return propertyList;
    }

    public void updateProperties(AlertDialog alert) {
        observables.duProperties().subscribe(new Observer<RetClassGen>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(RetClassGen retClassGen) {
                if (retClassGen.getRespCode() == 200) {
                    List<Map<String, Object>> list = retClassGen.getResponseBodyList();
                    if (list != null) {
                        if (list.size() > 0) {
                            genericDao.deleteTable(retClassGen.getTableName());
                            for (Map<String, Object> obj : list) {
                                genericDao.save(obj, retClassGen.getTableName());
                            }
                        } else
                            Toast.makeText(context,
                                    "No Data Found\nFor table " + retClassGen.getTableName(),
                                    Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context,
                            "No Data Found\nFor table " + retClassGen.getTableName(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                alert.dismiss();
                Intent intent = new Intent(context, Tools.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }

            @Override
            public void onComplete() {
                Toast.makeText(context, "DU Properties successfully saved", Toast.LENGTH_LONG).show();
                alert.dismiss();
                Intent intent = new Intent(context, Homepage.class);
                context.startActivity(intent);
                ((Activity) context).finish();

            }
        });
    }
}
