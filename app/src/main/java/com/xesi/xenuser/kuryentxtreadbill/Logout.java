package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;

import java.io.File;

/**
 * Created by xenuser on 3/16/2017.
 */
public class Logout extends BaseActivity {
    public static final String APP_PROPERTY_SETTING = "app_config";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String pass;
    private EditText etPassword;
    private HeaderFooterInfo headerFooterInfo;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_logout);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        pass = sharedPref.getString(getString(R.string.app_key), "");
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setFooterInfo();
        String path = this.getApplication().getFilesDir().getAbsolutePath() + "/logo.bmp";
        logo = (ImageView) findViewById(R.id.logo);
        File file = new File(path);
        if (file.exists())
            logo.setImageDrawable(Drawable.createFromPath(path));

        etPassword = (EditText) findViewById(R.id.etPassword);
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String password = etPassword.getText().toString();

            if (!password.equals(pass)) {
                etPassword.setError("Invalid Password");
                etPassword.setText("");
                int i = sharedPref.getInt("retry", 0) + 1;
                editor.putInt("retry", i);
                editor.commit();
            } else {
                editor.putInt("logoutKey", 0);
                editor.putInt("retry", 0);
                editor.commit();
                Intent i = new Intent(getApplicationContext(), Homepage.class);
                startActivity(i);
                finish();
            }
            if (sharedPref.getInt("retry", 0) > 3) {
                Intent i = new Intent(getApplicationContext(), LockedApp.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        headerFooterInfo.exitApp();
    }
}
