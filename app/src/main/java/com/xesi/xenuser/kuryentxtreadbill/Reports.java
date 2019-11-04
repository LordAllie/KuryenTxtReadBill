package com.xesi.xenuser.kuryentxtreadbill;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.RecordListAdapter;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.nbbse.mobiprint3.Printer;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.util.PrintReceipt;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;

import java.util.List;

/**
 * Created by Raymond P. Barrinuveo on 9/18/2016.
 * Enhanced and modify by Daryll Sabate 02/15/2017
 */
public class Reports extends BaseActivity {
    private SharedPreferences sharedPref;
    private TextView totalBillsCount;
    private PowerManager.WakeLock wakelock;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private Button btnPrintAllBill, btnPrintReading;
    private BillHeaderDAO billHeaderDAO;
    RecordListAdapter recordListAdapter;
    private DUPropertyDAO duPropertyDAO;
    private Context context;
    private PrintReceipt receipt;
    private Printer print;
    ListView lvReports;
    private HeaderFooterInfo headerFooterInfo;
    private MsgDialog msgDialog;
    private SearchView searchView;
    private GenericDao genericDao;
    private int billsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_reports);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        this.context = this.getApplicationContext();
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        msgDialog = new MsgDialog(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "UploadWakeLock");
        instantiate();
        print = Printer.getInstance();
        receipt = new PrintReceipt(this, print);
        searchView = (SearchView) findViewById(R.id.searchView);
        billHeaderDAO = new BillHeaderDAO(getApplication());
        billHeaderDAO.instantiateDb();
        genericDao = new GenericDao(getApplication());
        duPropertyDAO = new DUPropertyDAO(getApplication());
        if (duPropertyDAO.getPropertyValue("IS_SEARCH_KEYPAD_NUMERIC").equals("Y"))
            searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        billsCount = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillHeader","where isArchive = ","N","","0"));
        totalBillsCount.setText("Total Bills: " + billsCount);
        recordListAdapter = new RecordListAdapter(this, displayRecords());
        lvReports.setOnItemClickListener((parent, view, position, id) -> {
            view.setSelected(true);
            String billNo = recordListAdapter.getSelectedItem(position);
            Intent i = new Intent(getApplicationContext(), ShowDetailedBill.class);
            i.putExtra("billNo", billNo);
            startActivityForResult(i, 1);
            finish();
        });
        lvReports.setAdapter(recordListAdapter);

        try {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    recordListAdapter.getFilter().filter(query);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void instantiate() {
        totalBillsCount = (TextView) findViewById(R.id.totalBillsCount);
        lvReports = (ListView) findViewById(R.id.lvReports);
        btnPrintAllBill = (Button) findViewById(R.id.btnPrintAllBill);
        btnPrintReading = (Button) findViewById(R.id.btnPrintReading);
    }

    public List<BillHeader> displayRecords() {
        List<BillHeader> billHeaders = billHeaderDAO.getAllBillList("*","where isArchive = ","N","ORDER BY isUploaded ASC, _id DESC");
        return billHeaders;
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

    public void btnPrintAllBill(View view) {
        if(billsCount>0) {
            receipt.callClusterPrintBackground();
            receipt.close();
        }
    }

    public void btnPrintReading(View view) {
        if(billsCount>0) {
            if (duPropertyDAO.getPropertyValue("IS_PRINT_BILL_SUMMARY_PROTECTED").equals("Y")) {
                LayoutInflater inflate = LayoutInflater.from(this);
                View promptsView = inflate.inflate(R.layout.dialog_master_key, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setView(promptsView);
                alertDialog.setCancelable(true);
                alertDialog.setTitle("");
                AlertDialog alert = alertDialog.create();
                alert.show();
                Button btnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(v -> {
                    alert.dismiss();
                });
                EditText etMasterPass = (EditText) promptsView.findViewById(R.id.etMasterPass);
                promptsView.findViewById(R.id.btnOK)
                        .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String unlocker = sharedPref.getString("unlockerCode", "");
                                        String masterPass = etMasterPass.getText().toString();
                                        if (masterPass.isEmpty()) {
                                            etMasterPass.setError("Master key is empty");
                                        } else {
                                            if (masterPass.equals(unlocker)) {
                                                alert.dismiss();
                                                receipt.callPrintSummaryBackground();
                                                receipt.close();
                                            } else {
                                                etMasterPass.setError("Invalid Master key");
                                                ((EditText) promptsView.findViewById(R.id.etMasterPass)).setText("");
                                            }

                                        }
                                    }
                                }
                        );
            } else {
                receipt.callPrintSummaryBackground();
                receipt.close();
            }
        }
    }

}
