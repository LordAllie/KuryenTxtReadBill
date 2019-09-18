package com.xesi.xenuser.kuryentxtreadbill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xesi.core.library.DateHandler;
import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.DuPropertiesAdapter;
import com.xesi.xenuser.kuryentxtreadbill.adapter.RateMasterListAdapter;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.RetrofitHandler;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.LogDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.NewMeterDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.RateMasterDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.DbCreate;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.global.GlobalVariable;
import com.xesi.xenuser.kuryentxtreadbill.helper.BillGeneration;
import com.xesi.xenuser.kuryentxtreadbill.helper.BillToDB;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.HeaderJson;
import com.xesi.xenuser.kuryentxtreadbill.model.LogModel;
import com.xesi.xenuser.kuryentxtreadbill.model.StringBill;
import com.xesi.xenuser.kuryentxtreadbill.model.UploadData;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.UploadBillMaster;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;
import com.xesi.xenuser.kuryentxtreadbill.model.download.DUProperty;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RateMaster;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;
import com.xesi.xenuser.kuryentxtreadbill.util.PropertyChecker;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;

import org.bouncycastle.util.StringList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by xenuser on 9/10/2016.
 */
public class Tools extends BaseActivity {
    public static final String APP_PROPERTY_SETTING = "app_config";
    public static final String SERVERIP = "serverIPKey";
    public static final String SERVERPORT = "serverPortKey";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private final int DEFAULT_READING_VALUE = 100;
    private DateHandler dateHandler;
    private BillGeneration billGen;

    private List<String> routeList;
    //DAO
    private BillToDB billToDb;
    private AccountDao accountDao;
    private DbCreate _dbCreate;
    private MsgDialog msgDialog;
    private Button mbtnOK, mbtnCancel;
    private EditText metNewIP, metNewPort, etOldPassword, etNewPassword, etConfirmPassword, etInput;
    private TextView tvIdDevice, tvAppVer, tvRelDate, tvPrintingDelay, tvIsSuppressedPrintBuffer, tvSubTitle1,tvTitle;
    private TextView tvReader;
    private ListView lvRateMaster;
    private RateMasterListAdapter rateMasterListAdapter;
    private DuPropertiesAdapter duPropertiesAdapter;
    private String pass;
    private HeaderFooterInfo headerFooterInfo;
    private MyObservables observables;
    private UniversalHelper helper;
    private RateMasterDao rateMasterDao;
    private GenericDao genericDao;
    private DUPropertyDAO duPropertyDAO;
    private CheckBox cbOldShowPwd, cbNewShowPwd, cbConfirmShowPwd;
    private boolean isCheckOldPwd = false, isCheckNewPwd = false, isCheckConfirmPwd = false;
    private String deviceId;
    private String consumerType = "";
    private Calendar calendar;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private int recordCount = 0;
    private String jsonDIR;
    private BillHeaderDAO billHeaderDAO;
    private UploadBillMaster uploadBillMaster;
    private int billRecordCount=0;
    private String idRDM;
    private ProgressDialog dialog;
    private ProgressDialog progress;
    private UploadData uploadData;
    private List<StringBill> billJsonList=new ArrayList<>();
    private int totalAccounts;
    private DateFormat df;
    private SimpleDateFormat sdf;
    private String currentDateandTime;
    private LogDao logDao;
    private List<String> jsonBillNo=new ArrayList<String>();
    public final int[] checkingError = new int[1];
    private ProgressDialog bar;
    private RetrofitHandler retrofitHandler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tools);
        helper = new UniversalHelper(this);
        _dbCreate = new DbCreate(this);
        genericDao = new GenericDao(this);
        msgDialog = new MsgDialog(this);
        rateMasterDao = new RateMasterDao(this);
        duPropertyDAO = new DUPropertyDAO(this);
        billHeaderDAO = new BillHeaderDAO(this);
        accountDao = new AccountDao(this);

        billToDb = new BillToDB(this);
        billToDb.instantiateDb();
        billGen = new BillGeneration(this);
        billGen.instantiateDb();
        headerFooterInfo = new HeaderFooterInfo(this);
        dateHandler = new DateHandler();
        calendar = Calendar.getInstance();
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        deviceId = sharedPref.getString("authID", "");
        editor = sharedPref.edit();
        pass = sharedPref.getString(getString(R.string.app_key), "");
        String ipAdd = sharedPref.getString(SERVERIP, "");
        String port = sharedPref.getString(SERVERPORT, "");
        ServiceGenerator.changeApiBaseUrl(helper.setUpBaseURL(ipAdd, port));
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        if(UniversalHelper.externalMemoryAvailable()) {
            jsonDIR= "/sdcard";
        } else {
            jsonDIR= String.valueOf(Environment.getExternalStorageDirectory());
        }
        progress = new ProgressDialog(this);
        bar = new ProgressDialog(this);
        checkingError[0]=0;
        logDao = new LogDao(this);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateandTime = sdf.format(new Date());
        df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        uploadData = new UploadData();
        idRDM = sharedPref.getString("idRDM", "");
        uploadBillMaster = new UploadBillMaster();
        totalAccounts = Integer.parseInt(genericDao.getOneField("COUNT(_id)","arm_account","","","","0"));
        retrofitHandler = new RetrofitHandler(this);
        List<AccountModelV2> accountModelV2List = displayAccts(false, false, false);
    }

    /*New Meter */
    public void btnNewMeter(View view) {
        Intent i = new Intent(getApplicationContext(), NewMeterViewer.class);
        startActivity(i);
        finish();
    }

    /*Change Pass*/
    public void btnChangePass(View view) {
        dialogChangePassword();
    }

    /*Server IP*/
    public void btnServerIP(View view) {
        if(duPropertyDAO.getPropertyValue("IS_SETTING_MASTER_KEY_PROTECTED").equals("Y")) masterKey("dialogChangeIP","Change Local IP");
        else dialogChangeIP();
    }

    /*Dev Info*/
    public void btnDevId(View view) {
        dialogViewDeviceInfo();
    }

    /*Clear Date*/
    public void btnClearData(View view) {
        showAlert(Tools.this, getResources().getString(R.string.clearDataMsg));
    }

    /*Rate Viewer*/
    public void btnRmViewer(View view) {

        String checkData = genericDao.getOneField("rateMasterID","arm_ratemaster","","","limit 1","null");
        if (checkData.equals("null")) msgDialog.showErrDialog("No downloaded data");
        else dialogViewRateMaster();

    }

    public void btnBackupToPC(View view){
        String json = headerJson(sharedPref.getString("authID", ""), sharedPref.getString("idRDM", ""), String.valueOf(sharedPref.getInt("idReader", 0)));
        helper.saveJsonHeader(json);
        if(!sharedPref.getString("authID", "").equals("") && !sharedPref.getString("idRDM", "").equals("0") && sharedPref.getInt("idReader", 0)!=0) {
            File[] files = new File(jsonDIR + "/RNBFile").listFiles();
            if (files.length > 0) {
                uploadFile();
            } else
                Toast.makeText(getApplicationContext(), "No files to backup.", Toast.LENGTH_LONG).show();
        }else{
            msgDialog.showErrDialog("RDM and downloads are needed before backup");
        }
    }

    public void masterKey(String methodName, String title){
        try {
            Method method = Tools.this.getClass().getMethod(methodName);
            LayoutInflater inflate = LayoutInflater.from(this);
            View promptsView = inflate.inflate(R.layout.dialog_master_key, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setView(promptsView);
            alertDialog.setCancelable(true);
            alertDialog.setTitle(title);
            AlertDialog alert = alertDialog.create();
            alert.show();
            Button btnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(v -> {
                alert.dismiss();
            });
            EditText etMasterPass = (EditText) promptsView.findViewById(R.id.etMasterPass);
            promptsView.findViewById(R.id.btnOK)
                    .setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String unlocker = sharedPref.getString("unlockerCode", "");
                                    String masterPass = etMasterPass.getText().toString();
                                    if (masterPass.isEmpty()) {
                                        etMasterPass.setError("Master key is empty");
                                    } else {
                                        if (masterPass.equals(unlocker)) {
                                            try {
                                                method.invoke(Tools.this);
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            } catch (InvocationTargetException e) {
                                                e.printStackTrace();
                                            }
                                            alert.dismiss();

                                        } else {
                                            etMasterPass.setError("Invalid Master key");
                                            ((EditText) promptsView.findViewById(R.id.etMasterPass)).setText("");
                                        }

                                    }
                                }
                            }
                    );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /*RDM*/
    public void btnIdRDM(View view) {
        if(duPropertyDAO.getPropertyValue("IS_SETTING_MASTER_KEY_PROTECTED").equals("Y")) masterKey("dialogChangeRDM","Change RDM");
        else dialogChangeRDM();
    }

    /*Properties*/
    public void btnProperties(View view) {
        dialogViewProperties();
    }

    /**
     *
     * @param view
     * Generate Transactions
     */
    public void btnGenerateTransactions (View view) {
        generateTransactions();
    }



    @Override
    public void onBackPressed() {
        closeDB();
        super.onBackPressed();
        goToHomePage();
    }

    private void generateTransactions() {
        boolean isCheck = false;
        boolean reOrderSequence = false;
        boolean isRead = false;
        List<AccountModelV2> accountModelV2List = displayAccts(isCheck, reOrderSequence, isRead);
        for (int i = 0; i < accountModelV2List.size(); i++) {
            AccountModelV2 accountModelV2 = accountModelV2List.get(i);
            double currentReading =  accountModelV2List.get(i).getCurrentReading() + DEFAULT_READING_VALUE;
            consumerType = genericDao.getOneField("rateName","arm_ratemaster","WHERE rateMasterID = ", String.valueOf(accountModelV2.getIdRateMaster()),"","");

            billToDb.generateBillNo(deviceId, accountModelV2.getOldAccountNumber());
            if (accountModelV2.getIsRead() == 0) {
                billGen.readingComputation(accountModelV2, currentReading, consumerType, false, accountModelV2.getCurrentReading(), currentReading - accountModelV2.getCurrentReading());
            } else {
                billGen.readingComputation(accountModelV2, currentReading, consumerType, true, accountModelV2.getCurrentReading(), currentReading - accountModelV2.getCurrentReading());
            }
            accountDao.updateReadingAndIsRead(currentReading, accountModelV2.getSequenceNumber(), accountModelV2.getIdRoute(), accountModelV2.getId());

        }
    }



    public List<AccountModelV2> displayAccts(boolean showAll, boolean reOrderSequence, boolean isRead) {

        return accountDao.getAllAccount(showAll, reOrderSequence, isRead);
    }


    public void dialogChangePassword() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.layout_change_password, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Change Password");

        final AlertDialog alert = alertDialog.create();
        alert.show();
        mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
        mbtnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
        etOldPassword = (EditText) promptsView.findViewById(R.id.etOldPassword);
        etNewPassword = (EditText) promptsView.findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) promptsView.findViewById(R.id.etConfirmPassword);
        cbOldShowPwd = (CheckBox) promptsView.findViewById(R.id.cbOldShowPwd);
        cbNewShowPwd = (CheckBox) promptsView.findViewById(R.id.cbNewShowPwd);
        cbConfirmShowPwd = (CheckBox) promptsView.findViewById(R.id.cbConfirmShowPwd);

        cbOldShowPwd.setOnClickListener(v -> {
            isCheckOldPwd = ((CheckBox) v).isChecked();
            if (!isCheckOldPwd)
                etOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            else
                etOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        });
        cbNewShowPwd.setOnClickListener(v -> {
            isCheckNewPwd = ((CheckBox) v).isChecked();
            if (!isCheckNewPwd)
                etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            else
                etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        });
        cbConfirmShowPwd.setOnClickListener(v -> {
            isCheckConfirmPwd = ((CheckBox) v).isChecked();
            if (!isCheckConfirmPwd)
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            else
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        });
        mbtnCancel.setOnClickListener(v -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();

        });

        mbtnOK.setOnClickListener(new View.OnClickListener() {
            String oldPassword, newPassword, confirmPassword;

            @Override
            public void onClick(View arg0) {
                oldPassword = etOldPassword.getText().toString();
                newPassword = etNewPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();

                if (oldPassword.isEmpty()) {
                    etOldPassword.setError("Enter old password");
                } else if (newPassword.isEmpty()) {
                    etNewPassword.setError("Enter new password");
                } else if (confirmPassword.isEmpty()) {
                    etConfirmPassword.setError("Enter confirm password");
                } else {
                    if (!pass.equals(oldPassword)) {
                        etOldPassword.setError("Invalid password");
                    } else {
                        if (newPassword.equals(confirmPassword)) {
                            editor.putString(getString(R.string.app_key), newPassword);
                            editor.commit();
                            Toast.makeText(getApplicationContext(), "Password successfully changed!", Toast.LENGTH_SHORT).show();
                            alert.dismiss();
                        } else {
                            etConfirmPassword.setError("Password did not match");
                            etConfirmPassword.setText("");
                        }
                    }
                }
            }
        });
    }

    private void goToHomePage() {
        startActivity(new Intent(getApplicationContext(), Homepage.class));
        finish();
    }

    public void dialogChangeIP() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_change_ip, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Change Local IP");
        final AlertDialog alert = alertDialog.create();
        alert.show();

        mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
        mbtnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
        metNewIP = (EditText) promptsView.findViewById(R.id.etNewIP);
        metNewPort = (EditText) promptsView.findViewById(R.id.etNewPort);
        String port = sharedPref.getString(SERVERPORT, "");
        metNewIP.setText(sharedPref.getString(SERVERIP, ""));
        metNewPort.setText(port);
        /**********Setting for IP Address input format****************/
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
        metNewIP.setFilters(filters);
        /**********end Setting for IP Address input format****************/
        metNewIP.requestFocus();
        mbtnCancel.setOnClickListener(v -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();
        });

        mbtnOK.setOnClickListener(new View.OnClickListener() {
            String oldIP, newIP, newPort;

            @Override
            public void onClick(View arg0) {
                oldIP = sharedPref.getString(SERVERIP, "");
                newIP = metNewIP.getText().toString();
                newPort = metNewPort.getText().toString();
                if (newIP.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter New IP", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString(SERVERIP, newIP);
                    editor.putString(SERVERPORT, newPort);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Server IP Successfully Updated", Toast.LENGTH_SHORT).show();
                    alert.dismiss();
                    goToHomePage();
                }
            }
        });
    }

    public void dialogViewDeviceInfo() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_view_device_detail, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Device Info");
        final AlertDialog alert = alertDialog.create();
        alert.show();

        mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
        tvIdDevice = (TextView) promptsView.findViewById(R.id.tvDeviceId);
        tvReader = (TextView) promptsView.findViewById(R.id.tvReader);
        tvAppVer = (TextView) promptsView.findViewById(R.id.tvAppVer);
        tvRelDate = (TextView) promptsView.findViewById(R.id.tvRelDate);
        tvPrintingDelay = (TextView) promptsView.findViewById(R.id.tvPrintingDelay);
        tvIsSuppressedPrintBuffer = (TextView) promptsView.findViewById(R.id.tvIsSuppressedPrintBuffer);

        String DeviceId = sharedPref.getString("authID", "");
        String reader = sharedPref.getString("assignedTo", "");
        String version = sharedPref.getString("version", "");
        String relDate = sharedPref.getString("relDate", "");
        int printDelay = sharedPref.getInt("printDelay", 1);
        String isSuppressedPrintBuffer = sharedPref.getString("isSuppressedPrintBuffer", "N");

        tvIdDevice.setText(DeviceId);
        tvReader.setText(reader);
        tvAppVer.setText(version);
        tvRelDate.setText(relDate);
        tvPrintingDelay.setText(String.valueOf(printDelay));
        tvIsSuppressedPrintBuffer.setText(isSuppressedPrintBuffer);

        mbtnOK.setOnClickListener(arg0 -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();
        });
    }

    public void showAlert(Context contextName, String message) {
        //TODO showAlert
        new AlertDialog.Builder(contextName)
                .setTitle(getResources().getString(R.string.warning))
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Cancel", (dlg, something) -> {
                        }
                )
                .setPositiveButton("OK", (dlg, something) -> dialogClearData()
                ).show();
    }

    public void dialogClearData() {
        if(duPropertyDAO.getPropertyValue("IS_SETTING_MASTER_KEY_PROTECTED").equals("Y")) masterKey("clearData","Clear Data");
        else {
            LayoutInflater inflate = LayoutInflater.from(this);
            View promptsView = inflate.inflate(R.layout.dialog_type_data, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setView(promptsView);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Clear Data");
            final AlertDialog alert = alertDialog.create();
            alert.show();
            tvTitle = (TextView) promptsView.findViewById(R.id.tvTitle);
            tvTitle.setText("Please enter your password");
            tvSubTitle1 = (TextView) promptsView.findViewById(R.id.tvSubTitle1);
            tvSubTitle1.setText("Password");
            mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
            mbtnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
            etInput = (EditText) promptsView.findViewById(R.id.etInput);
            mbtnCancel.setOnClickListener(v -> {
                Bundle b = new Bundle();
                Intent intent = new Intent();
                intent.putExtras(b);
                alert.dismiss();
            });

            mbtnOK.setOnClickListener(new View.OnClickListener() {
                String password = "";

                @Override
                public void onClick(View arg0) {
                    password = etInput.getText().toString();

                    if (password.isEmpty()) {
                        etInput.setError("Enter device password");
                    } else {
                        if (!password.equals(pass.trim())) {
                            etInput.setText("");
                            etInput.setError("Invalid Password.");
                        } else {
                            clearData();
                            alert.dismiss();
                        }
                    }
                }
            });
        }
    }

    public void clearData(){
        Toast.makeText(getApplication(), getResources().getString(R.string.clearDataSuccessMsg), Toast.LENGTH_SHORT).show();
        _dbCreate.deleteDatabase();
        _dbCreate.createDatabase();
    }

    public void dialogViewRateMaster() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_view_rate_master, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Rate Master");

        final AlertDialog alert = alertDialog.create();
        alert.show();
        mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
        lvRateMaster = (ListView) promptsView.findViewById(R.id.lvRateMaster);
        rateMasterListAdapter = new RateMasterListAdapter(this, displayRateMasterDetails());
        lvRateMaster.setAdapter(rateMasterListAdapter);
        mbtnOK.setOnClickListener(arg0 -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();
        });
    }

    public List<RateMaster> displayRateMasterDetails() {
        List<RateMaster> queryResult = rateMasterDao.getAllRateMasterDetails();
        return queryResult;
    }

    public List<DUProperty> displayDuProperties() {
        List<DUProperty> duPropertyList = duPropertyDAO.getAll();
        return duPropertyList;
    }

    public void dialogViewProperties() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.layout_du_properties, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(true);
        alertDialog.setTitle("DU Properties");

        final AlertDialog alert = alertDialog.create();
        alert.show();
        mbtnOK = (Button) promptsView.findViewById(R.id.btnUpdate);
        Button btnBack = (Button) promptsView.findViewById(R.id.btnBack);
        ListView lvProperties = (ListView) promptsView.findViewById(R.id.lvDuProperties);
        duPropertiesAdapter = new DuPropertiesAdapter(this, displayDuProperties());
        lvProperties.setAdapter(duPropertiesAdapter);
        btnBack.setOnClickListener(arg0 -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();
        });
        mbtnOK.setOnClickListener(v -> {
            updatePrintingDelay(Integer.parseInt(sharedPref.getString("authID", "1")));
            updateIsSuppressed(Integer.parseInt(sharedPref.getString("authID", "1")));
            PropertyChecker props = new PropertyChecker(this);
            props.updateProperties(alert);
        });
    }

    private void updatePrintingDelay(int authID) {
        observables.getPrintingDelay(authID).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d("PRINTINGDELAY", String.valueOf(integer.intValue()));
                editor.putInt("printDelay", integer.intValue());
                editor.commit();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("PRINTINGDELAY", String.valueOf(e));
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void updateIsSuppressed(int authID) {
        observables.getIsSuppressed(authID).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                editor.putString("isSuppressedPrintBuffer", s);
                editor.commit();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("isSuppressedPrintBuffer", String.valueOf(e));
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void dialogChangeRDM() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_change_rdm, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Change RDM");
        final AlertDialog alert = alertDialog.create();
        alert.show();

        mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
        mbtnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
        etNewPassword = (EditText) promptsView.findViewById(R.id.etNewRDM);
        mbtnCancel.setOnClickListener(v -> {
            Bundle b = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(b);
            alert.dismiss();
        });
        etNewPassword.requestFocus();
        etNewPassword.setText(sharedPref.getString("idRDM", ""));
        mbtnOK.setOnClickListener(new View.OnClickListener() {
            String newRDM;

            @Override
            public void onClick(View arg0) {
                newRDM = etNewPassword.getText().toString();
                etNewPassword.requestFocus();
                String idRDM = sharedPref.getString("idRDM", "");

                if (newRDM.equals("")) {
                    etNewPassword.setError("Please enter RDM");
                } else {
                    if (idRDM.equals(newRDM)) {
                        etNewPassword.setError("RDM not changed");
                    } else {
                        editor.putString("idRDM", newRDM);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "RDM updated successfully", Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        });
    }

    private void closeDB() {
        genericDao = new GenericDao(this);
        billHeaderDAO = new BillHeaderDAO(this);
        billHeaderDAO.instantiateDb();
        logDao = new LogDao(this);
    }

    /*For Backup Upload*/
    private UploadBillMaster setUploadMasterData() {
        uploadBillMaster.setDevId(Integer.parseInt(sharedPref.getString("authID", "0")));
        uploadBillMaster.setIdReader(sharedPref.getInt("idReader", 0));
        uploadBillMaster.setIdRdm(sharedPref.getInt("idRdm", 0));
        return uploadBillMaster;
    }

    private void insertBillUploadMaster() {
        UploadBillMaster uploadBillMaster = setUploadMasterData();
        if (billRecordCount == 0) {
            msgDialog.showDialog("Upload Backup","All backup reading are already uploaded.");
        } else {
            saveLogsToDB("uls", Long.parseLong(sharedPref.getString("authID", "0")),
                    Long.parseLong(idRDM));
            dialog = ProgressDialog.show(this, "Loading...", "Uploading " + billRecordCount + "/" + totalAccounts + " bill(s)");
            observables.insertUploadMaster(uploadBillMaster).subscribe(new Observer<RetClassGen<UploadBillMaster>>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(RetClassGen<UploadBillMaster> retClassGen) {
                    if (retClassGen.getRespCode() == 200) {
                        UploadBillMaster billMaster = retClassGen.getResposeBody();
                        uploadBills(billMaster.getId());
                    } else {
                        msgDialog.showErrDialog("Failed to upload bill: \n" + retClassGen.getRespMsg());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    msgDialog.showErrDialog("Error in inserting upload master cause: " + e.getLocalizedMessage());
                    dialog.dismiss();
                }

                @Override
                public void onComplete() {
                }
            });
        }
    }

    private void uploadBills(int billMasterId) {

        uploadData.setBillMasterId(billMasterId);
        uploadData.setBills(billJsonList);
        observables.uploadBills(uploadData).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {
                dialog.show();
                editor.putString("ulDateTime", df.format(new Date()));
                editor.commit();
                saveLogsToDB("ulf", Long.parseLong(sharedPref.getString("authID", "0")),
                        Long.parseLong(idRDM));
                if (s.equals("OK")) {
                    billHeaderDAO.updateFieldByBillNo("1",jsonBillNo.toArray(new String[2]),"isUploaded");
                    dialog.dismiss();
                    msgDialog.showAlertNoCancel(Tools.this, "Upload bill(s)","Successfully uploaded\n" + billRecordCount + " records", value1 -> {
                        startActivity(new Intent(getApplicationContext(),Tools.class));
                        finish();
                    });
                } else {
                    msgDialog.showErrDialog("Failed to upload\n" + billRecordCount + " records");
                    saveToLogs(s);
                    dialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
                msgDialog.showErrDialog("Error in uploading bill(s) cause: " + e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
                editor.putString("ulDateTime", df.format(new Date()));
                editor.commit();
            }
        });
    }

    private void saveLogsToDB(String url, long devID, long idRdm) {
        Log.d("SAVELOG", "DEVID " + devID + " " + idRdm);
        observables.savelogs(url, devID, idRdm).subscribe(new Observer<Void>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Void aVoid) {
                Log.d("SAVELOG", "LOGS SUCCESS");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("SAVELOG", "LOGS " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("SAVELOG", "LOGS FINISH");
            }
        });
    }

    private LogModel saveToLogs(String message) {
        LogModel logModel = new LogModel();
        logModel.setDateTime(currentDateandTime);
        logModel.setMessage(message);
        logDao.insert(logModel);
        return logModel;
    }

    /*For backup to PC*/
    public void threadSleep(int sleep){
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(){
        File[] files = new File(jsonDIR+ "/RNBFile").listFiles();
        if (files.length > 0) {
            if(checkingError[0] == 1)
                checkingError[0]=0;

            UploadMeterJson uploadMeterJson = new UploadMeterJson();
            uploadMeterJson.execute(String.valueOf(files.length));
        }else{
            Intent intent = new Intent(getApplicationContext(), Tools.class);
            startActivity(intent);
            finish();
        }

    }

    public class UploadMeterJson extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setTitle("Backup in progress.");
            progress.setMessage("Please wait...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (progress != null)
                progress.dismiss();
            if(!isCancelled()){
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "JSON upload complete", Toast.LENGTH_LONG).show();
//                uploadFile();
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            if(!isCancelled()){
                File[] files = new File(jsonDIR + "/RNBFile").listFiles();
                MultipartBody.Part part;
                RequestBody fileReqBody;
                int i=0;
                do{
                    if(!isCancelled()) {
                        int countJson = i;
                        fileReqBody = RequestBody.create(MediaType.parse("*/*"), files[i]);
                        part = MultipartBody.Part.createFormData("file", files[i].getName(), fileReqBody);
                        threadSleep(200);
                        observables.uploadBackupFile(part,Integer.parseInt(sharedPref.getString("authID", ""))).subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(String check) {
                                publishProgress(countJson + "/" + files.length);
                                if (!check.equals("OK"))
//                                    files[countJson].delete();
                                {
                                    cancel(true);
                                    if (checkingError[0] != 1) {
                                        checkingError[0] = 1;
                                        msgDialog.showErrDialogCancelAbleWithIntent(Tools.this,"Error in backup json(s) cause: Please check your connection");
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                progress.dismiss();
                                cancel(true);
                                if (checkingError[0] != 1) {
                                    checkingError[0] = 1;
                                    msgDialog.showErrDialogCancelAbleWithIntent(Tools.this,"Error in backup json(s) cause: " + e.getLocalizedMessage());
                                }
                            }

                            @Override
                            public void onComplete() {
                                if((countJson+1)==files.length) {
                                    progress.dismiss();
                                    startActivity(new Intent(getApplicationContext(), Tools.class));
                                    finish();
                                }
                            }
                        });
                    }
                    i++;
                }while (i<files.length);
//                for(int loop= 0; loop < files.length; loop++){
//
//                    if (isCancelled())
//                        break;
//                }
//                int i=0;
//                while (i < 1){
//                    if (new File(jsonDIR + "/RNBFile").listFiles().length == 0)
//                        break;
//                    if(isCancelled())
//                        break;
//                }
            }

            return Integer.valueOf(strings[0]);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progress.setMessage("Uploading " +values[0]);
        }
    }

    public String headerJson(String deviceId, String rdm_id, String reader_id) {
        Gson gson = new Gson();
        HeaderJson headerJson=new HeaderJson();
        headerJson.setDevice_id(deviceId);
        headerJson.setRdm_id(rdm_id);
        headerJson.setReader_id(reader_id);
        return  gson.toJson(headerJson);
    }

}
