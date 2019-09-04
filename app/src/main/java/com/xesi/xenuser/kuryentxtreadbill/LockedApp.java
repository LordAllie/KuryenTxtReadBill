package com.xesi.xenuser.kuryentxtreadbill;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;

/**
 * Created by xenuser on 4/11/2017.
 */
public class LockedApp extends BaseActivity {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private Intent i;
    private HeaderFooterInfo headerFooterInfo;
    private TextView deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_application_lock);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        deviceID = (TextView) findViewById(R.id.tvdevID);
        deviceID.setText(sharedPref.getString("authID", ""));
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setFooterInfo();
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockDialog();
            }
        });
    }

    private void unlockDialog() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_device_unlock, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Unlock App");
        final AlertDialog alert = alertDialog.create();
        alert.show();
        final EditText etMasterPass = (EditText) promptsView.findViewById(R.id.etMasterPass);
        promptsView.findViewById(R.id.btnOK)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String unlocker = sharedPref.getString("unlockerCode", "");
                                String masterPass = etMasterPass.getText().toString();
                                if (masterPass.isEmpty()) {
                                    etMasterPass.setError("Master password is empty");
                                } else {
                                    if (masterPass.equals(unlocker)) {
                                        i = new Intent(getApplicationContext(), PasswordCreation.class);
                                        editor.putInt("retry", 0);
                                        editor.putInt("logoutKey", 0);
                                        editor.remove(getString(R.string.app_key));
                                        editor.commit();

                                    } else {
                                        i = getIntent();
                                    }
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                );
    }
}
