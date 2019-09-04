package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.LogsAdapter;
import com.xesi.xenuser.kuryentxtreadbill.dao.LogDao;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.model.LogModel;

import java.util.List;

/**
 * Created by Daryll Sabate on 10/23/2017.
 */
public class Logs extends BaseActivity {

    private SharedPreferences sharedPref;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private HeaderFooterInfo headerFooterInfo;
    private LogDao logDao;
    private LogsAdapter logsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_logs);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        headerFooterInfo = new HeaderFooterInfo(this);
        logDao = new LogDao(this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        TextView noRecordsFound = (TextView) findViewById(R.id.noRecordsFound);
        ListView logs = (ListView) findViewById(R.id.lvLogs);
        logsAdapter = new LogsAdapter(Logs.this, getLogs());
        if (getLogs().size() > 0)
            noRecordsFound.setVisibility(View.INVISIBLE);
        logs.setAdapter(logsAdapter);
    }

    private List<LogModel> getLogs() throws NullPointerException {
        return logDao.getLogs();
    }

    @Override
    protected void onDestroy() {
        logDao.close();
        super.onDestroy();
    }

    public void home(View view) {
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }
}
