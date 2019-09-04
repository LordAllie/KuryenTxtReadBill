package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.NewMeterListAdapter;
import com.xesi.xenuser.kuryentxtreadbill.dao.NewMeterDao;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;

import java.util.List;

/**
 * Created by Daryll Sabate on 7/24/2017.
 */
public class NewMeterViewer extends BaseActivity {

    private HeaderFooterInfo headerFooterInfo;
    private NewMeterDao newMeterDao;
    private ListView lvNewMeters;
    private TextView tvHeaderCount;
    private Button btnAdd;
    private NewMeterListAdapter displayRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_new_meter);
        newMeterDao = new NewMeterDao(this);
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        lvNewMeters = (ListView) findViewById(R.id.lvNewMeters);
        tvHeaderCount = (TextView) findViewById(R.id.tvHeaderCount);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        displayRecords = new NewMeterListAdapter(this, newMeterModelList());
        tvHeaderCount.setText("New Meters(" + newMeterModelList().size() + ")");
        lvNewMeters.setOnItemClickListener((parent, view, position, id) -> {
            view.setSelected(true);
            String msn = displayRecords.getSelectedItem(position);
        });
        lvNewMeters.setAdapter(displayRecords);

        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), NewMeter.class);
            startActivity(i);
            finish();
        });
    }

    public List<NewMeterModel> newMeterModelList() {
        List<NewMeterModel> newMeterModelList = newMeterDao.getAll();
        return newMeterModelList;
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
