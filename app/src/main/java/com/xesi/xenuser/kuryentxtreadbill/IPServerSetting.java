package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;

/**
 * Created by xenuser on 1/5/2017.
 */
public class IPServerSetting extends BaseActivity implements View.OnClickListener {
   private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String APP_PROPERTY_SETTING = "app_config";
    public static final String SERVERIP = "serverIPKey";
    public static final String SERVERPORT = "serverPortKey";

    private EditText etIPAddress, etPort;
    private Button btnNext;
    private String ipAdd, port;
    private TextView tvDate, tvVer;
    private PackageManager manager;
    private PackageInfo info;

    private HeaderFooterInfo headerFooterInfo;

    private void setFooterInfo() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvVer = (TextView) findViewById(R.id.appVersion);
        tvVer.setText(sharedPref.getString("version", "0.00"));
        tvDate.setText(sharedPref.getString("relDate", ""));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_ip_and_port);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        headerFooterInfo = new HeaderFooterInfo(this);
        editor = sharedPref.edit();
        getAppVersion();
        checkServerConfig();
        setFooterInfo();
    }

    private void checkServerConfig() {
        String ipAddress = sharedPref.getString(SERVERIP, "");
        if (!ipAddress.matches("")) {
            Intent launcher = new Intent(getApplicationContext(), Authenticate.class);
            startActivityForResult(launcher, 2);
            finish();
        } else {
            etIPAddress = (EditText) findViewById(R.id.etServerIP);
            etPort = (EditText) findViewById(R.id.etServerPort);
            btnNext = (Button) findViewById(R.id.btnNext);
            InputFilter[] filters = new InputFilter[1];
            filters[0] = (source, start, end, dest, dstart, dend) -> {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            };
            etIPAddress.setFilters(filters);
            btnNext.setOnClickListener(this);
        }
    }
    private void getAppVersion() {
        String version;
        try {
             manager = this.getPackageManager();
             info = manager.getPackageInfo(
                    this.getPackageName(), 0);
            version = info.versionName;
            //relDate = info.;
            editor.putString("version", version);
            editor.putString("relDate", getString(R.string.app_released_date)); // to be change every time there's an update.
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendIpAndPort(String ipAdd, String port, View v) {
        editor.putString(SERVERIP, ipAdd);
        editor.putString(SERVERPORT, port);
        editor.commit();
        Intent launcher = new Intent(getApplicationContext(), Authenticate.class);
        launcher.putExtra("ip", ipAdd);
        launcher.putExtra("port", port);
        startActivity(launcher);
        finish();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                ipAdd = etIPAddress.getText().toString().trim();
                port = etPort.getText().toString().trim();
                if (ipAdd.matches("") || ipAdd.equals(null)) {
                    Toast.makeText(getApplication(), "You did not set your IP Address", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    sendIpAndPort(ipAdd, port, v);
                }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("onBackPressed", "OnBackPressed");
        headerFooterInfo.exitApp();
    }
}
