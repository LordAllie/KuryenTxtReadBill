package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.TblDatas;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.model.Diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 1/23/2017.
 */
public class Diagnostics extends BaseActivity {
    private SharedPreferences sharedPref;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private HeaderFooterInfo headerFooterInfo;
    private TableLayout tblDiagnostic;
    private TblDatas tblDatas;
    private String strDiagnostic;
    private Type type;
    private TextView tvNoRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_diagnostic);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        tblDatas = new TblDatas(this);
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        strDiagnostic = sharedPref.getString("diagnostics", "");
        type = new TypeToken<ArrayList<Diagnostic>>() {
        }.getType();
        List<Diagnostic> diagnostics = new Gson().fromJson(strDiagnostic, type);
        tblDiagnostic = (TableLayout) findViewById(R.id.tbl_diagnostic);
        tvNoRecord = (TextView) findViewById(R.id.tvNoRecord);
        if (diagnostics == null)
            tblDiagnostic.setVisibility(View.GONE);
        else {
            tvNoRecord.setVisibility(View.GONE);
            tblDatas.initDiagnostic(diagnostics, tblDiagnostic);
        }
    }

    public void home(View view) {
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }
}
