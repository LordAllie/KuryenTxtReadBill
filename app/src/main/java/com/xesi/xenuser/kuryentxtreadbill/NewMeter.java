package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.MySpinnerAdapter;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.NewMeterDao;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;
import com.xesi.xenuser.kuryentxtreadbill.model.RouteObj;

import java.util.List;

/**
 * Created by Daryll Sabate on 7/21/2017.
 */
public class NewMeter extends BaseActivity {

    private HeaderFooterInfo headerFooterInfo;
    private Spinner spRouteList;
    private TextView etMsn, etReading;
    private Button btnSave;
    private List<RouteObj> routeObjList;
    private AccountDao accountDao;
    private NewMeterDao newMeterDao;
    private int idRoute = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_meter);
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        accountDao = new AccountDao(getApplication());
        newMeterDao = new NewMeterDao(getApplication());
        spRouteList = (Spinner) findViewById(R.id.spRouteList);
        etMsn = (TextView) findViewById(R.id.etMsn);
        etReading = (TextView) findViewById(R.id.etReading);
        btnSave = (Button) findViewById(R.id.btnSave);
        routeObjList = accountDao.getRoutes();

        MySpinnerAdapter adapter =
                new MySpinnerAdapter(NewMeter.this,
                        android.R.layout.simple_spinner_item, routeObjList);

        spRouteList.setAdapter(adapter);
        spRouteList.setOnItemSelectedListener(onItemSelectedListener2);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msn = etMsn.getText().toString();
                int reading = Integer.parseInt(etReading.getText().toString());
                NewMeterModel newMeter = setNewMeterData(idRoute, msn, reading);
                boolean isSaved = newMeterDao.insert(newMeter);
                if (isSaved) {
                    Toast.makeText(getApplicationContext(), "Successfully saved new meter", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), NewMeterViewer.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add new meter", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private NewMeterModel setNewMeterData(int idRoute, String msn, int reading) {
        NewMeterModel newMeter = new NewMeterModel();
        newMeter.setIdRoute(idRoute);
        newMeter.setMsn(msn);
        newMeter.setReading(reading);
        return newMeter;
    }

    private OnItemSelectedListener onItemSelectedListener2 =
            new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    RouteObj obj = (RouteObj) (parent.getItemAtPosition(position));
                    idRoute = obj.getIdRoute();
                    Log.d("IDROUTE", Integer.toString(idRoute));

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), NewMeterViewer.class);
        startActivity(i);
        finish();
    }
}
