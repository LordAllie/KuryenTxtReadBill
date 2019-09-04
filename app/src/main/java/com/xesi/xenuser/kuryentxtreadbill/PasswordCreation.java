package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;

/**
 * Created by xenuser on 5/11/2016.
 */

public class PasswordCreation extends BaseActivity {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private EditText etPassword, etRetypePass;
    private String password, retypePass;
    private TextView tvDate, tvVer;
    private HeaderFooterInfo headerFooterInfo;

    private void setFooterInfo() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvVer = (TextView) findViewById(R.id.appVersion);
        tvVer.setText(sharedPref.getString("version", "0.00"));
        tvDate.setText(sharedPref.getString("relDate", ""));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_password_creation);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        headerFooterInfo = new HeaderFooterInfo(this);
        editor = sharedPref.edit();
       etPassword = (EditText) findViewById(R.id.etPassword);
        etRetypePass = (EditText) findViewById(R.id.etRetypePass);
        setFooterInfo();
    }

    public void sendPassword(View view) {
        password = etPassword.getText().toString();
        retypePass = etRetypePass.getText().toString();
        if (password.isEmpty()) {
            etPassword.setError("Password should not be empty");
        } else if (retypePass.isEmpty()) {
            etRetypePass.setError("Confirm password should not be empty");
        } else {
            if (!password.equals(retypePass)) {
                etRetypePass.setError("Password did not match!");
            } else {
                editor.putString(getString(R.string.app_key), password);
                editor.commit();
                Intent i = new Intent(getApplicationContext(), Homepage.class);
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("onBackPressed", "OnBackPressed");
        headerFooterInfo.exitApp();
    }
}
