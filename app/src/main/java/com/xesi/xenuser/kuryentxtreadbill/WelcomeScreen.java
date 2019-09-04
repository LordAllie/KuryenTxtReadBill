package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.DbCreate;

public class WelcomeScreen extends BaseActivity {
    public static final String APP_PROPERTY_SETTING = "app_config";
    public static final String SERVERIP = "serverIPKey";
    public static final String ISACTIVE = "isActive";
    Thread splashTread;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView tvDate, tvVer;
    private Intent launcher;
    private DbCreate _dbCreate;
    private String versionName;
    private int versionCode;
    private Intent i;

    private void getAppVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    this.getPackageName(), 0);
            versionName = info.versionName;
            versionCode = info.versionCode;
            Log.d("RNB", versionCode + versionName);
            editor.putString("version", versionName);
            editor.putInt("versionCode", versionCode);
            editor.putString("relDate", getString(R.string.app_released_date)); // to be change every time there's an update.
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFooterInfo() {
        tvVer = (TextView) findViewById(R.id.appVersion);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvVer.setText(sharedPref.getString("version", "0.00"));
        tvDate.setText(sharedPref.getString("relDate", ""));
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome_screen);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        _dbCreate = new DbCreate(getApplication());
        _dbCreate.createDatabase();
        getAppVersion();
        setFooterInfo();
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 5000) {
                        sleep(100);
                        waited += 100;
                    }
                    if (sharedPref.getString(ISACTIVE, "").equals("Y"))
                        redirect(sharedPref.getString(ISACTIVE, ""));
                    else checkServerConfig();
                } catch (InterruptedException e) {
                } finally {
                    finish();
                }

            }
        };
        splashTread.start();
    }

    private void redirect(String checkData) {
        if (!checkData.matches("")) {
            if (sharedPref.getString(getString(R.string.app_key), "").equals("")) {
                editor.putInt("logoutKey", 0);
                editor.commit();
                i = new Intent(getApplicationContext(), PasswordCreation.class);
                startActivity(i);
                finish();
            } else {
                if (sharedPref.getInt("retry", 0) > 3) {
                    i = new Intent(getApplicationContext(), LockedApp.class);
                    startActivity(i);
                    finish();
                } else {
                    if (sharedPref.getInt("logoutKey", 0) > 0) {
                        i = new Intent(getApplicationContext(), Logout.class);
                        startActivity(i);
                        finish();
                    } else {
                        i = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        }
    }

    private void checkServerConfig() {
        String ipAddress = sharedPref.getString(SERVERIP, "");
        if (!ipAddress.matches("")) {
            launcher = new Intent(getApplicationContext(), Authenticate.class);
            launcher.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launcher);
            finish();
        } else {
            launcher = new Intent(getApplicationContext(), IPServerSetting.class);
            launcher.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launcher);
            finish();
        }
    }
}