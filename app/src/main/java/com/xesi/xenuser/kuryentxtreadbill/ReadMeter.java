package com.xesi.xenuser.kuryentxtreadbill;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;

/**
 * Created by Raymond P Barrinuevo on 9/11/2016.
 */
public class ReadMeter extends BaseActivity {
    public static final String APP_PROPERTY_SETTING = "app_config";
    FragmentTransaction fragmentTransaction;
    ReadMeterFragment readMeterFragment;
    FragmentManager fragmentManager;
    private HeaderFooterInfo headerFooterInfo;
    private AccountModelV2 accountModelV2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_readmeter);
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        accountModelV2 = getIntent().getParcelableExtra("accountModel");
        boolean isChecked = getIntent().getExtras().getBoolean("isCheck");
        boolean reOrder = getIntent().getExtras().getBoolean("reOrder");
        boolean isRead = getIntent().getExtras().getBoolean("isRead");
        int idRoute = getIntent().getExtras().getInt("idRoute");
        Bundle bundle = new Bundle();
        bundle.putParcelable("accountModel", accountModelV2);
        bundle.putBoolean("isCheck", isChecked);
        bundle.putBoolean("reOrder", reOrder);
        bundle.putBoolean("isRead", isRead);
        bundle.putInt("idRoute", idRoute);
        readMeterFragment = new ReadMeterFragment();
        readMeterFragment.setArguments(bundle);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.group, readMeterFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void home(View view) {
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Transaction.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
