package com.xesi.xenuser.kuryentxtreadbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nbbse.mobiprint3.Printer;
import com.xesi.xenuser.kuryentxtreadbill.adapter.AccountListAdapter;
import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.RateMasterDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.RouteDefinitionDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.helper.BillGeneration;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.util.PrintReceipt;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Transaction extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String APP_PROPERTY_SETTING = "app_config";
    int TAKE_PHOTO_CODE = 0;
    private String imageDIR;
    ListView lvAccount;
    SearchView searchView;
    AccountListAdapter accountListAdapter;
    private BillGeneration billGen;
    private PrintReceipt receipt;
    private Printer print;
    private MsgDialog msgDialog;
    private Spinner spinner;
    DecimalFormat df = new DecimalFormat("#.00");
    // DAO
    private String currentReading;
    private double previousReading;
    private Button mbtnOK, mbtnCancel, btnPrintReading, btnGenerate;
    private TextView metPreviousReading, tvRouteCode;
    private EditText metCurrentReading;
    private CheckBox chkActive, chkSeq, chkIsRead;
    private int idRoute;
    private AccountModelV2 accountModelV2;
    private String consumerType;
    private HeaderFooterInfo headerFooterInfo;
    private String billNumber = "";
    private List<String> list;
    private boolean isCheck, isRead, reOrderSequence;
    private TextView tvReader, tvRecordCount,tvCount;
    private int recordCount = 0;
    private String readAcct, unreadAcct;
    private String routeCode;
    private AlertDialog alert;
    private DUPropertyDAO duPropertyDAO;
    private RouteDefinitionDao routeDefinitionDao;
    private AccountDao accountDao;
    private RateMasterDao rateMasterDao;
    private BillHeaderDAO billHeaderDAO;
    private GenericDao genericDao;
    private UniversalHelper helper;
    private String btnValue;
    private void setFooter() {
        tvReader = (TextView) findViewById(R.id.tvReader);
        tvRecordCount = (TextView) findViewById(R.id.tvRecordCount);
        tvCount = (TextView) findViewById(R.id.tvCount);
        String reader = sharedPref.getString("assignedTo", "");
        if (reader.length() > 12)
            reader = reader.substring(0, 11) + "..";
        tvReader.setText(reader);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_transact);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        duPropertyDAO = new DUPropertyDAO(this);
        routeDefinitionDao = new RouteDefinitionDao(this);
        accountDao = new AccountDao(this);
        genericDao = new GenericDao(this);
        rateMasterDao = new RateMasterDao(this);
        billHeaderDAO = new BillHeaderDAO(this);
        billHeaderDAO.instantiateDb();
        helper = new UniversalHelper(this);
        print = Printer.getInstance();
        receipt = new PrintReceipt(this, print);
        msgDialog = new MsgDialog(this);
        headerFooterInfo = new HeaderFooterInfo(this);
        headerFooterInfo.setHeaderInfo();
        setFooter();
        lvAccount = (ListView) findViewById(R.id.lvAccount);
        searchView = (SearchView) findViewById(R.id.searchView);
        spinner = (Spinner) findViewById(R.id.spRouteList);
        tvRouteCode = (TextView) findViewById(R.id.tvRouteCode);
        chkActive = (CheckBox) findViewById(R.id.chkActive);
        chkSeq = (CheckBox) findViewById(R.id.chkSeq);
        chkIsRead = (CheckBox) findViewById(R.id.chkIsRead);
        billGen = new BillGeneration(getApplication());
        if (duPropertyDAO.getPropertyValue("IS_SEARCH_KEYPAD_NUMERIC").equals("Y"))
            searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        idRoute = Integer.parseInt(genericDao.getOneField("idRoute","arm_route_definition","where isActive = 1","","","0"));
        routeCode = genericDao.getOneField("DISTINCT(routeCode)","arm_account","WHERE idRoute=", String.valueOf(idRoute),"limit 1","");
        tvRouteCode.setText(routeCode);
        list = accountDao.getRouteList();

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        list.add(0, "ROUTE CODE");
        spinner.setAdapter(adapter);
        isCheck = false;
        reOrderSequence = false;
//        if(getProperty(duPropertyDAO.getPropertyValue("IS_READ_TRANSACTION_TO_BOTTOM"), "Y").equals("N"))
//            isRead = true;
//        else
            isRead = false;
        Log.d("IDROUTE", String.valueOf(idRoute));
        accountListAdapter = new AccountListAdapter(Transaction.this, displayAccts(idRoute, isCheck, reOrderSequence, isRead));
        viewAccount(accountListAdapter, isCheck, reOrderSequence, isRead);
        editor.putInt("imageProbability", Integer.parseInt(duPropertyDAO.getPropertyValue("CAPTURE_IMAGE_PROBABILITY", "0")));
        editor.commit();
        chkActive.setOnClickListener(v -> {
            isCheck = ((CheckBox) v).isChecked();
            if (isCheck) {
                chkActive.setText("Inactive");
                accountListAdapter = new AccountListAdapter(Transaction.this, displayAccts(idRoute, isCheck, reOrderSequence, isRead));
                viewAccount(accountListAdapter, isCheck, reOrderSequence, isRead);
            } else {
                chkActive.setText("Active");
                accountListAdapter = new AccountListAdapter(Transaction.this, displayAccts(idRoute, isCheck, reOrderSequence, isRead));
                viewAccount(accountListAdapter, isCheck, reOrderSequence, isRead);
            }
        });

        chkSeq.setOnClickListener(v -> {
            reOrderSequence = ((CheckBox) v).isChecked();
            if (reOrderSequence) {
                accountListAdapter = new AccountListAdapter(Transaction.this, displayAccts(idRoute, isCheck, reOrderSequence, isRead));
                viewAccount(accountListAdapter, isCheck, reOrderSequence, isRead);
            } else {
                accountListAdapter = new AccountListAdapter(Transaction.this, displayAccts(idRoute, isCheck, reOrderSequence, isRead));
                viewAccount(accountListAdapter, isCheck, reOrderSequence, isRead);
            }
        });

        chkIsRead.setOnClickListener(v -> {
            isRead = ((CheckBox) v).isChecked();
            if (isRead) {
                chkIsRead.setText("Unread");
                accountListAdapter = new AccountListAdapter(Transaction.this, displayAccts(idRoute, isCheck, reOrderSequence, isRead));
                viewAccount(accountListAdapter, isCheck, reOrderSequence, isRead);
            } else {
                chkIsRead.setText("Read");
                accountListAdapter = new AccountListAdapter(Transaction.this, displayAccts(idRoute, isCheck, reOrderSequence, isRead));
                viewAccount(accountListAdapter, isCheck, reOrderSequence, isRead);
            }
        });

        try {

            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);
            searchView.setFocusable(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    searchView.requestFocus();
                    return false;}
                @Override
                public boolean onQueryTextChange(String query) {
                    accountListAdapter.getFilter().filter(query);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }
    private String getProperty(String val, String defVal) {
        return val == null ? defVal : val;
    }

    private void viewAccount(final AccountListAdapter accountLists, final boolean isChecked, final boolean reOrderSequence,
                             final boolean isAlreadyRead) {
        try {
            lvAccount.setOnItemClickListener((parent, view, position, id) -> {
                view.setSelected(true);
                accountModelV2 = accountLists.getSelectedItem(position);
                consumerType =  genericDao.getOneField("rateName","arm_ratemaster","where rateMasterID = ", String.valueOf(accountModelV2.getIdRateMaster()),"","");
                editor.putString("seqNo", String.valueOf(accountModelV2.getSequenceNumber()));
                editor.commit();
                boolean isRead = accountDao.getIsread(accountModelV2.getOldAccountNumber(), idRoute);
                Boolean isReset=false;
                if(isRead){
                    List<Double> reading = billHeaderDAO.getRdg(accountModelV2.getOldAccountNumber());

                    if(reading.get(0)<reading.get(1))
                        isReset=true;
                    else isReset=false;
                }

                if (isRead) {
                    int isUploaded = Integer.parseInt(genericDao.getOneField("isUploaded","armBillHeader","WHERE oldAcctNo= ",String.valueOf(accountModelV2.getOldAccountNumber()),"ORDER BY _id DESC","0"));
                    if (isUploaded == 1) msgDialog.showErrDialog("Edit reading not allowed, the reading is already uploaded on the system.");
                    else showReadMeterWarning(isReset);
                } else {
                    Intent i = new Intent(getApplicationContext(), ReadMeter.class);
                    i.putExtra("accountModel", accountModelV2);
                    i.putExtra("isCheck", isChecked);
                    i.putExtra("reOrder", reOrderSequence);
                    i.putExtra("isRead", isAlreadyRead);
                    i.putExtra("idRoute", idRoute);
                    startActivity(i);
                    finish();
                }
            });
            lvAccount.setAdapter(accountListAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please Download Account First", Toast.LENGTH_SHORT).show();
        }
        tvRecordCount.setText("Total Accounts: " + recordCount + "");
        tvCount.setText("Unread: "+unreadAcct+"     Read: "+readAcct);
    }

    public List<AccountModelV2> displayAccts(int idRoute, boolean showAll, boolean reOrderSequence, boolean isRead) {
        List<AccountModelV2> queryResult = accountDao.getAllAccount(idRoute, showAll, reOrderSequence, isRead, getProperty(duPropertyDAO.getPropertyValue("IS_READ_TRANSACTION_TO_BOTTOM"), "Y"));
        String addQuery = "";
        if (showAll)
            addQuery = "isActive = 'Y' AND ";
        String recordCountQuery = genericDao.getOneField("COUNT(id)","arm_account","WHERE " + addQuery +" idRoute=", String.valueOf(idRoute),"","");
        recordCount = Integer.parseInt(recordCountQuery.equals("") ? "0" : recordCountQuery);
        readAcct = genericDao.getOneField("SELECT COUNT(id) FROM arm_account where isRead=1","0");
        unreadAcct = genericDao.getOneField("SELECT COUNT(id) FROM arm_account where isRead=0","0");
        closeDB();
        return queryResult;
    }

    private void closeDB() {
        duPropertyDAO.close();
        routeDefinitionDao.close();
        accountDao.close();
        rateMasterDao.close();
        billHeaderDAO.close();
        genericDao.close();
    }

    public void home(View view) {
        closeDB();
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
            closeDB();
            super.onBackPressed();
            Intent i = new Intent(getApplicationContext(), Homepage.class);
            startActivity(i);
            finish();

    }

    private void showReadMeterWarning(Boolean isReset) {
        billGen.instantiateDb();
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_read_alert, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setCancelable(true);

        //coding here
        TextView  txtEditReadingTitle = (TextView) promptsView.findViewById(R.id.txtEditReadingTitle);
        Button btnEditReading = (Button) promptsView.findViewById(R.id.btnEditReading);
        Button btnPrintNotifcation = (Button) promptsView.findViewById(R.id.btnPrintNotification);
        Button btnEditRemarks = (Button) promptsView.findViewById(R.id.btnEditRemarks);
        Button btnPrintBill = (Button) promptsView.findViewById(R.id.btnPrintBill);
        Button btnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
        Button btnUnVoid = (Button) promptsView.findViewById(R.id.btnUnVoid);
        Button btnVoid = (Button) promptsView.findViewById(R.id.btnVoid);
        String billNo = genericDao.getOneField("billNo","armBillHeader","WHERE isUploaded = 0 AND oldAcctNo = ",accountModelV2.getOldAccountNumber(),"","");

        if(genericDao.getOneField("isVoid","armBillHeader","WHERE billNo = ",billNo,"","N").equals("Y")){
            btnUnVoid.setVisibility(View.VISIBLE);
            btnVoid.setVisibility(View.GONE);
        }else {
            btnUnVoid.setVisibility(View.GONE);
            btnVoid.setVisibility(View.VISIBLE);
        }

        if(accountModelV2.getAutoComputeMode() == 1){
            btnUnVoid.setVisibility(View.GONE);
            btnVoid.setVisibility(View.GONE);
            btnPrintBill.setVisibility(View.VISIBLE);
            alertDialog.setTitle("");
        }else {
            alertDialog.setTitle("Edit Reading");
            btnEditReading.setVisibility(View.VISIBLE);
            btnPrintBill.setVisibility(View.VISIBLE);
            btnPrintNotifcation.setVisibility(View.VISIBLE);
            btnEditRemarks.setVisibility(View.VISIBLE);
            txtEditReadingTitle.setVisibility(View.VISIBLE);
        }

        if(isReset){
            btnPrintBill.setEnabled(false);
        }
        alert = alertDialog.create();
        alert.show();

        btnUnVoid.setOnClickListener(v ->{
                billHeaderDAO.updateOneFieldByBillNo("N",billNo,"isVoid");
                billGen.updateBillHeader(billNo);
            Toast.makeText(this, "Bill Unvoid", Toast.LENGTH_LONG).show();
                alert.dismiss();
        });
        btnVoid.setOnClickListener(v ->{
            billHeaderDAO.updateOneFieldByBillNo("Y",billNo,"isVoid");
            billGen.updateBillHeader(billNo);
            Toast.makeText(this, "Bill Void", Toast.LENGTH_LONG).show();
            alert.dismiss();
        });
        btnCancel.setOnClickListener(v ->
                alert.dismiss());
        btnEditReading.setOnClickListener(v -> {
            alert.dismiss();
            dialogEditReading();
        });
        btnEditRemarks.setOnClickListener(v -> {
            alert.dismiss();
            dialogEditRemarks();
        });
        btnPrintBill.setOnClickListener(v -> {
            alert.dismiss();
            printBill();
        });
        btnPrintNotifcation.setOnClickListener(v -> {
            alert.dismiss();
            receipt.callPrintNotificationOnBackground(accountModelV2);
        });
    }

    private void catchErr(String s) {
        msgDialog.showErrDialog(s);
    }

    private void printBill() {
        try {
            List<Double> readings = billHeaderDAO.getCurAndPrevRdg(accountModelV2.getOldAccountNumber());
            billNumber = updateAcctBill(readings.get(0), readings.get(1));
            if (billNumber.equals("-1")) {
                catchErr("Bill not save");
            } else if (billNumber.equals("-2")) {
                catchErr("Bill Header not save");
            } else if (billNumber.equals("-3")) {
                catchErr("Failed to generate bill json");
            } else if (billNumber.substring(0, 12).equals("SQLException")) {
                catchErr(billNumber);
            } else {
                receipt.callPrintBackground(billNumber,false,"N");
                receipt.close();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error in printing please try again.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), Transaction.class);
            startActivity(i);
            finish();
        }
    }

    public void dialogEditRemarks() {
        billGen.instantiateDb();
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_edit_remarks, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Edit Remarks");
        alert = alertDialog.create();
        alert.show();

        Button btnOK = (Button) promptsView.findViewById(R.id.btnOK);
        Button btnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
        EditText etTxtRemarks = (EditText) promptsView.findViewById(R.id.etTxtRemarks);

        ArrayList<String> list = billGen.getExistingRemarks(accountModelV2.getOldAccountNumber());
        if (list.isEmpty() || list.size() == 0 || list.get(1).equals("NO REMARKS"))
            etTxtRemarks.setHint("NO REMARKS");
        else
            etTxtRemarks.setText(list.get(1).toString());

        btnCancel.setOnClickListener(v -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();
        });

        btnOK.setOnClickListener(v -> {
            billGen.updateRemarks(list.get(0), etTxtRemarks.getText().toString());
            editor.putString("remarks", etTxtRemarks.getText().toString());
            editor.commit();
            alert.dismiss();
            Toast.makeText(this, "Remarks updated", Toast.LENGTH_LONG).show();
        });
    }

    public void dialogEditReading() {
        //coding here
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_edit_reading, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Edit Reading");
        alert = alertDialog.create();
        alert.show();

        btnPrintReading = (Button) promptsView.findViewById(R.id.btnPrintReading);
        btnGenerate = (Button) promptsView.findViewById(R.id.btnGenerate);
        mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
        mbtnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
        metPreviousReading = (TextView) promptsView.findViewById(R.id.etPreviousReading);
        metCurrentReading = (EditText) promptsView.findViewById(R.id.etCurrentReading);
        List<Double> readings = billHeaderDAO.getCurAndPrevRdg(accountModelV2.getOldAccountNumber());
        metPreviousReading.setText(df.format(readings.get(1)));
        metCurrentReading.setText(df.format(readings.get(0)));
        metCurrentReading.setSelection(metCurrentReading.getText().length());

        if(Double.parseDouble(metCurrentReading.getText().toString()) < Double.parseDouble(df.format(readings.get(1)))) {
            btnPrintReading.setEnabled(false);
            btnGenerate.setEnabled(false);
        }

        metCurrentReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.equals(""))
                    metCurrentReading.setText("0");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //onTextChange
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !s.equals("")) {
                    String cur;
                    if (metCurrentReading.getText().toString().equals(""))
                        cur="0";
                    else cur=metCurrentReading.getText().toString();
                    if (Double.parseDouble(cur) < Double.parseDouble(df.format(readings.get(1)))) {
                        btnPrintReading.setEnabled(false);
                        btnGenerate.setEnabled(false);
                    } else {
                        btnPrintReading.setEnabled(true);
                        btnGenerate.setEnabled(true);
                    }
                }else metCurrentReading.setText("0");
            }
        });
        mbtnCancel.setOnClickListener(v -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();
        });

        mbtnOK.setOnClickListener(v -> processBill("saved"));
        btnPrintReading.setOnClickListener(v -> processBill("print"));
        btnGenerate.setOnClickListener(v -> {
            alert.dismiss();
            processBill("generate");
        });
    }

    private void processBill(String print) {
        currentReading = metCurrentReading.getText().toString().replace(",", "");
        previousReading = Double.parseDouble(metPreviousReading.getText().toString().replace(",", ""));
        if (currentReading.trim().isEmpty()) metCurrentReading.setError("Please Enter Current Reading");
        else {
            billNumber = updateAcctBill(Double.parseDouble(currentReading), previousReading);
            btnValue = print;
            if (sharedPref.getInt("imageProbability", 0) > 0) takePicture(billNumber);
            else generateBill(btnValue);
            alert.dismiss();
        }
    }
    public void generateBill(String value) {
        if (!billNumber.equals("")) {
            BillHeader billHeader = billHeaderDAO.findBillByBillNo(billNumber);
            switch (value) {
                case "print":
                    receipt.callPrintReadingBackground(billHeader, false);
                    break;
                case "generate":
                    receipt.callPrintBackground(billNumber, false,"N");
                    break;
                case "saved":
                    Toast.makeText(getApplication(), "Successfully Updated.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
    public String updateAcctBill(double currentReading, double prevReading) {
        billGen.instantiateDb();
        if (sharedPref.getString("remarks", "").equals("Meter Reset")) {
            if (currentReading < prevReading) {
                double dKwhConsumption = helper.calculateResetMeterConsumption(currentReading, prevReading);
                editor.putString("remarks", "Meter Reset");
                editor.commit();
                billNumber = billGen.readingComputation(accountModelV2, currentReading, consumerType, true, prevReading, dKwhConsumption);
            } else {
                billNumber = billGen.readingComputation(accountModelV2, currentReading, consumerType, true, prevReading, currentReading - prevReading);
            }
        } else {
            if (currentReading < prevReading) {
                msgDialog.showErrDialog("This is not a meter reset\nCurrent reading must be greater than previous reading");
            } else {
                billNumber = billGen.readingComputation(accountModelV2, currentReading, consumerType, true, prevReading, currentReading - prevReading);
            }
        }
        return billNumber;
    }

    @Override
    protected void onDestroy() {
        closeDB();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String routeCode = parent.getItemAtPosition(position).toString();
        if (!routeCode.equals("ROUTE CODE")) {
            routeDefinitionDao.updateIsActive();
            String data = genericDao.getOneField("idRoute","arm_account","WHERE routeCode=",routeCode,"LIMIT 1","");
            idRoute = Integer.parseInt(!data.equals("") ? data : "0");
            routeDefinitionDao.updateIsActive(idRoute);
            finish();
            startActivity(getIntent());
        }
    }

    /* TO BE USED FOR RANDOM PHOTO */
    public void takePicture(String fileName) {
        if(UniversalHelper.externalMemoryAvailable())
            imageDIR= "/sdcard";
        else
            imageDIR= String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        final String dir = imageDIR + "/RnB/";
        File newdir = new File(dir);
        newdir.mkdirs();
        File newfile = new File(dir + fileName + ".jpg");
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            Log.d("TAKEPHOTO", e.getMessage());
        }
        Uri outputFileUri = Uri.fromFile(newfile);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK){
            Log.d("TAKEPHOTO", "Pic saved");
        }
        generateBill(btnValue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //
    }
}