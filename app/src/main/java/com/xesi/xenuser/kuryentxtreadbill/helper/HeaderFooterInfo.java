package com.xesi.xenuser.kuryentxtreadbill.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.Homepage;
import com.xesi.xenuser.kuryentxtreadbill.Logout;
import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xenuser on 5/2/2017.
 */
public class HeaderFooterInfo {
    public static final String APP_PROPERTY_SETTING = "app_config";
    String appName;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Activity activity;
    private TextView tvDate, tvReader, appVersion, ducode;
    private String duCode;
    private String ver, reader;
    private DUPropertyDAO duPropertyDAO;
    private MsgDialog dialog;
    private ImageView ivLogo;
    private Button btnNav;
    private Intent i;

    public HeaderFooterInfo(Activity activity) {
        this.activity = activity;
        duPropertyDAO = new DUPropertyDAO(activity);
        dialog = new MsgDialog(activity);
        this.sharedPref = activity.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = this.sharedPref.edit();
        this.duCode = duPropertyDAO.getPropertyValue("DU_CODE");
        this.reader = sharedPref.getString("assignedTo", "");
        this.ver = sharedPref.getString("version", "");
    }

    public void setHeaderInfo() {
        ivLogo = (ImageView) activity.findViewById(R.id.ivImgLogo);
        ducode = (TextView) activity.findViewById(R.id.ducode);
        btnNav = (Button) activity.findViewById(R.id.btnNav);
        if (this.duCode.equals("0") || this.duCode.equals("-1")) {
            ducode.setText("DU_CODE");
            dialog.showErrDialog("DU code not found. Please contact your Administrator");
        } else {
            ducode.setText(this.duCode);
        }
        String path = activity.getApplication().getFilesDir().getAbsolutePath() + "/logo.bmp";
        File file = new File(path);
        if (file.exists())
            ivLogo.setImageDrawable(Drawable.createFromPath(path));

        this.appName = activity.getClass().getSimpleName();
        Log.d("CUR_ACTIVITY", appName);
        if (appName.equals("Homepage")) {
            btnNav.setBackgroundResource(R.drawable.btn_logout);
        } else {
            btnNav.setBackgroundResource(R.drawable.btnhome);
        }
        btnNav.setOnClickListener(v -> {
            if (appName.equals("Homepage")) {
                dialog.showConfirmDialog("Are you sure you want to logout?", value -> {
                    if (value.equals("Yes")) {
                        editor.putInt("logoutKey", 1);
                        editor.commit();
                        i = new Intent(activity.getApplicationContext(), Logout.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(i);
                        activity.finish();
                    }
                });
            } else {
                i = new Intent(activity.getApplicationContext(), Homepage.class);
                activity.startActivity(i);
                activity.finish();

            }
        });
    }

    public void setFooterInfo() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String sDate = dateFormat.format(date);
        tvDate = (TextView) activity.findViewById(R.id.tvDate);
        tvReader = (TextView) activity.findViewById(R.id.tvReader);
        appVersion = (TextView) activity.findViewById(R.id.appVersion);
        tvDate.setText(sDate);
        if (reader.length() > 12)
            reader = reader.substring(0, 11) + "..";
        tvReader.setText(reader);
        appVersion.setText(ver);
    }

    public void exitApp() {
        new AlertDialog.Builder(activity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Process.killProcess(Process.myPid());
                    activity. finish();
                    System.exit(0);
                }).setNegativeButton("No", null).show();
    }
}
