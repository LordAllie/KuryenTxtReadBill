package com.xesi.xenuser.kuryentxtreadbill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.RetrofitHandler;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountOtherChargesDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.LogDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.NewMeterDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.OtherChargesDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.DbCreate;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.global.GlobalVariable;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.MasterIdCallBack;
import com.xesi.xenuser.kuryentxtreadbill.model.Diagnostic;
import com.xesi.xenuser.kuryentxtreadbill.model.LogModel;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;
import com.xesi.xenuser.kuryentxtreadbill.model.RateMasterStatus;
import com.xesi.xenuser.kuryentxtreadbill.model.StringBill;
import com.xesi.xenuser.kuryentxtreadbill.model.UploadData;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.UploadBillMaster;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;
import com.xesi.xenuser.kuryentxtreadbill.model.message.Message;
import com.xesi.xenuser.kuryentxtreadbill.network.Kuryentxt;
import com.xesi.xenuser.kuryentxtreadbill.network.NetworkReceiver;
import com.xesi.xenuser.kuryentxtreadbill.util.PrintReceipt;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;


import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Sync extends BaseActivity implements NetworkReceiver.ConnectivityReceiverListener {
    public static final String APP_PROPERTY_SETTING = "app_config";
    public static final String SERVERIP = "serverIPKey";
    public static final String SERVERPORT = "serverPortKey";
    private static final String TAG = "Sync";
    private MsgDialog msgDialog;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private RetrofitHandler retrofitHandler;
    private Dialog progressUpload;
    private ProgressDialog progress;
    private ProgressBar progressBar;
    private PowerManager.WakeLock wakelock;
    private String ipAdd = "", idRDM,imageDIR,jsonDIR;
    private int billRecordCount, percentage, recordCounts, count = 0, meterCnt;
    private Button btnDownload, btnUpload, btnCancelUpload,mbtnOK, mbtnCancel;
    private EditText etInput;
    private TextView diagnostics, progress_text, tvLoading, errorlogs, ulDateTime, dlDateTime,tvSubTitle1,tvTitle;
    private HeaderFooterInfo headerFooterInfo;
    private DateFormat df;
    private String reportDate;
    private SimpleDateFormat sdf;
    private String currentDateandTime;
    private UploadBillMaster uploadBillMaster;
    private MyObservables observables;
    private UploadData uploadData;
    private List<StringBill> billJsonList;
    private ProgressDialog dialog;
    private GenericDao genericDao;
    private UniversalHelper helper;
    private BillHeaderDAO billHeaderDAO;
    private NewMeterDao newMeterDao;
    private LogDao logDao;
    private List<Diagnostic> diagnosticList;
    private int totalAccounts;
    private DbCreate _dbCreate;
    public final int[] checkingError = new int[1];
    private int uploadedCnt=0;
    private boolean isConnected;
    int cnt=0;
    protected void onCreate(Bundle savedInstanceState) {

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "UploadWakeLock");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sync);
        headerFooterInfo = new HeaderFooterInfo(this);
        _dbCreate = new DbCreate(this);
        genericDao = new GenericDao(this);
        billHeaderDAO = new BillHeaderDAO(this);
        billHeaderDAO.instantiateDb();
        newMeterDao = new NewMeterDao(this);
        logDao = new LogDao(this);
        msgDialog = new MsgDialog(this);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        uploadData = new UploadData();
        helper = new UniversalHelper(this);
        uploadBillMaster = new UploadBillMaster();
        idRDM = sharedPref.getString("idRDM", "");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateandTime = sdf.format(new Date());
        df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date today = Calendar.getInstance().getTime();
        reportDate = df.format(today);
        syncData();
        progress = new ProgressDialog(Sync.this);
        diagnostics = (TextView) findViewById(R.id.diagnostics);
        errorlogs = (TextView) findViewById(R.id.errorlogs);
        dlDateTime = (TextView) findViewById(R.id.dlDateTime);
        dlDateTime.setText("Last: "+sharedPref.getString("dlDateTime", "No download yet"));
        ulDateTime = (TextView) findViewById(R.id.ulDateTime);
        ulDateTime.setText("Last: "+sharedPref.getString("ulDateTime", "No upload yet"));
        diagnosticList = new ArrayList<>();
        checkConnection();
        // Checking Error For Images
        checkingError[0]=0;
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        ipAdd = sharedPref.getString(SERVERIP, "");
        String port = sharedPref.getString(SERVERPORT, "");
        ServiceGenerator.changeApiBaseUrl(helper.setUpBaseURL(ipAdd, port));
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), this);
        retrofitHandler = new RetrofitHandler(this);
        billRecordCount = Integer.parseInt(genericDao.getOneField("SELECT COUNT(_id) FROM armBillHeader WHERE isUploaded = 0 AND isArchive = 'N'","0"));
        totalAccounts = Integer.parseInt(genericDao.getOneField("COUNT(_id)","arm_account","","","","0"));
        GlobalVariable.billNolist=billHeaderDAO.getAllBillNo();
        if(UniversalHelper.externalMemoryAvailable()) {
            imageDIR= "/sdcard";
            jsonDIR= "/sdcard";
        } else {
            imageDIR= String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            jsonDIR= String.valueOf(Environment.getExternalStorageDirectory());
        }
        diagnostics.setOnClickListener(v -> {
            Intent launcher = new Intent(getApplicationContext(), Diagnostics.class);
            startActivityForResult(launcher, 2);
            finish();
        });
        errorlogs.setOnClickListener(v -> {
            Intent launcher = new Intent(getApplicationContext(), Logs.class);
            startActivityForResult(launcher, 2);
            finish();
        });
    }

    private void syncData() {
        btnDownload = (Button) findViewById(R.id.btnDL);
        btnUpload = (Button) findViewById(R.id.btnUL);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected)
                    isRdmReady(idRDM);
                else
                    msgDialog.showDialog("Connection","No Wifi/Internet connection.");
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected)
                    uploadData();
                else
                    msgDialog.showDialog("Connection","No Wifi/Internet connection.");
            }
        });
    }

    private void isRdmReady(String idRDM) {
            retrofitHandler.isRdmReady(idRDM, new MasterIdCallBack() {
                @Override
                public void onSuccess(int id) {
                    if (id > 0) {
                        downloadData();
                    }
                }

                @Override
                public void onSuccess(Message message) {
                    if (message.getCode().equals("0")) {
                        downloadData();
                    }
                }

                @Override
                public void onError(String message) {
                    msgDialog.showErrDialog(message);
                }
            });
    }

    public void downloadData() {
        DownloadAccountDetails DownloadAccountDetails;
        if (billRecordCount > 0) {
            msgDialog.showConfirmDialog("Existing bill(s) found. Upload the bills to your local server.\n Do you want to proceed?"
                    , value -> {
                        if (value.equals("Yes")) {
                            LayoutInflater inflate = LayoutInflater.from(this);
                            View promptsView = inflate.inflate(R.layout.dialog_type_data, null);
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                            alertDialog.setView(promptsView);
                            alertDialog.setCancelable(false);
                            alertDialog.setTitle("Download");
                            final AlertDialog alert = alertDialog.create();
                            alert.show();
                            tvTitle = (TextView) promptsView.findViewById(R.id.tvTitle);
                            tvTitle.setText("Please type yes to proceed.");
                            tvSubTitle1 = (TextView) promptsView.findViewById(R.id.tvSubTitle1);
                            tvSubTitle1.setText("");
                            mbtnOK = (Button) promptsView.findViewById(R.id.btnOK);
                            mbtnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
                            etInput = (EditText) promptsView.findViewById(R.id.etInput);
                            etInput.setInputType(InputType.TYPE_CLASS_TEXT);
                            mbtnCancel.setOnClickListener(v -> {
                                Bundle b = new Bundle();
                                Intent intent = new Intent();
                                intent.putExtras(b);
                                alert.dismiss();
                            });

                            mbtnOK.setOnClickListener(new View.OnClickListener() {
                                String input = "";
                                @Override
                                public void onClick(View arg0) {
                                    input = etInput.getText().toString();
                                    if (input.isEmpty()) etInput.setError("Please type yes");
                                    else {
                                        if (!input.toUpperCase().equals("YES")) {
                                            etInput.setText("");
                                            etInput.setError("Invalid Input.");
                                        } else {
                                            clearData();
                                            int newIdRdm = sharedPref.getInt("idRdm", 0);
                                            int oldIdRdm = sharedPref.getInt("idRdmOld", 0);
                                            if (newIdRdm != oldIdRdm) {
                                                editor.putInt("idRdmOld", sharedPref.getInt("idRdm", 0));
                                                editor.commit();
                                            }
                                            saveLogsToDB("dls", Long.parseLong(sharedPref.getString("authID", "0")),
                                                    Long.parseLong(idRDM));
                                            DownloadAccountDetails downloadAccountDetail = new DownloadAccountDetails();
                                            downloadAccountDetail.execute();
                                            alert.dismiss();
                                        }
                                    }
                                }
                            });
                        }
                    });
        } else {
            recordCounts = Integer.parseInt(genericDao.getOneField("COUNT(id)","arm_account","","","","0"));
            if (recordCounts > 0) {
                msgDialog.showConfirmDialog("Records found, this will overwrite all the existing data.\n Do you want to proceed?"
                        , value -> {
                            if (value.equals("Yes")) {
                                int newIdRdm = sharedPref.getInt("idRdm", 0);
                                int oldIdRdm = sharedPref.getInt("idRdmOld", 0);
                                if (newIdRdm != oldIdRdm) {
                                    editor.putInt("idRdmOld", sharedPref.getInt("idRdm", 0));
                                    editor.commit();
                                }
                                saveLogsToDB("dls", Long.parseLong(sharedPref.getString("authID", "0")),
                                        Long.parseLong(idRDM));
                                DownloadAccountDetails downloadAccountDetail = new DownloadAccountDetails();
                                downloadAccountDetail.execute();
                            }
                        });

            } else {
                saveLogsToDB("dls", Long.parseLong(sharedPref.getString("authID", "0")),
                        Long.parseLong(idRDM));
                DownloadAccountDetails = new DownloadAccountDetails();
                DownloadAccountDetails.execute();
            }
        }
    }

    public void clearData(){
        Toast.makeText(getApplication(), "All Transaction archived", Toast.LENGTH_SHORT).show();
//        _dbCreate.deleteDatabase();
//        _dbCreate.createDatabase();
        String[] tableList={"armChargeType","armDuAreaRate","armDuProperties","armNewMeter","armRoute","arm_account","arm_account_bill_aux","arm_account_other_charges","arm_coreloss_tran","arm_kwhaddontran","arm_lifelinedetails","arm_other_charges","arm_rate_detail","arm_rateaddontran","arm_rateaddontran_special","arm_ratemaster","arm_remarks","arm_route_definition","arm_surcharge","db_log","sqlite_sequence"};
        int i = 0;
        while (tableList.length > i) {
            genericDao.deleteTable(tableList[i]);
            i++;
        }
        try{
            genericDao.updateIsArchive("armBillHeader");
        }catch (Exception e){
            genericDao.onUpgrade(genericDao.mcfDB,23,25);
        }
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

    private void checkRateStatus() {
        observables.checkRateStatus().subscribe(new Observer<List<RateMasterStatus>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("RATESTATUS", "OPEN CONNECTION ");
            }

            @Override
            public void onNext(List<RateMasterStatus> rateMasterStatuses) {
                if (rateMasterStatuses.size() > 0)
                    msgDialog.displayRateWarning(rateMasterStatuses, value -> {
                        if (value.equals("OK")) {
                            msgDialog.showWarningDialog("Reminder", getString(R.string.reminder));
                        }
                    });
                else
                    msgDialog.showWarningDialog("Reminder", getString(R.string.reminder));
            }

            @Override
            public void onError(Throwable e) {
                msgDialog.showErrDialog(e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("RATESTATUS", "FINISH");
            }
        });
    }

    private void showSendButton(boolean isConnected) {
        String message;
        if (!isConnected) {
            this.isConnected=false;
//            btnDownload.setEnabled(false);
//            btnUpload.setEnabled(false);
//            message = "No Internet connection. Download and upload are disabled.";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else {
//            btnDownload.setEnabled(true);
//            btnUpload.setEnabled(true);
            this.isConnected=true;
        }
    }

    public void uploadData() {
        if ((wakelock != null) && (wakelock.isHeld() == false))
            wakelock.acquire();
        int newMeterCount = Integer.parseInt(genericDao.getOneField("COUNT(id)","armNewMeter","WHERE isUploaded = 0","","","0"));
        if (newMeterCount == 0) insertBillUploadMaster();
        else {
            UploadNewMeters uploadNewMeters = new UploadNewMeters();
            uploadNewMeters.execute();
        }
    }

    private UploadBillMaster setUploadMasterData() {
        uploadBillMaster.setDevId(Integer.parseInt(sharedPref.getString("authID", "0")));
        uploadBillMaster.setIdReader(sharedPref.getInt("idReader", 0));
        uploadBillMaster.setIdRdm(sharedPref.getInt("idRdm", 0));
        return uploadBillMaster;
    }

    private void insertBillUploadMaster() {
        UploadBillMaster uploadBillMaster = setUploadMasterData();
        if (billRecordCount == 0) {
            msgDialog.showAlertNoCancel(Sync.this ,"","No bill(s) to upload", value1 -> {
                if (value1.equals("OK")) {
                    msgDialog.showAlert(Sync.this ,"Untagged all bills?", value2 -> {
                        if (value2.equals("OK")){
                            BillHeaderDAO untaggedBHD = new BillHeaderDAO(Sync.this);
                            untaggedBHD.instantiateDb();
                            untaggedBHD.untaggedBills();
                            untaggedBHD.close();
                            msgDialog.showAlertNoCancel(Sync.this ,"","Bill successfully untagged as uploaded", value3 -> {
                                if (value3.equals("OK"))
                                    uploadFile();
                            });
                        }else
                            uploadFile();
                    });
                }
            });
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
                        // Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
                    } else {
                        msgDialog.showErrDialog("Failed to upload bill: \n" + retClassGen.getRespMsg());
                        saveToLogs("Failed to upload bill: \n" + retClassGen.getRespMsg());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    msgDialog.showErrDialog("Error in inserting upload master cause: " + e.getLocalizedMessage());
                    saveToLogs("Error in inserting upload master cause: " + e.getLocalizedMessage());
                    dialog.dismiss();
                }

                @Override
                public void onComplete() {
                }
            });

        }
    }

    private void uploadBills(int billMasterId) {
        billJsonList = billHeaderDAO.getAllStringBills();
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
                    int i=0;
//                    do{
//                        matchBillNo(GlobalVariable.billNolist.get(i));
//                        i++;
//                    }while(i<GlobalVariable.billNolist.size());
                        billHeaderDAO.updateAll();
                    dialog.dismiss();
                    int billLeft = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillHeader","WHERE isUploaded =","0","","0"));
                    if(billLeft<billRecordCount){
                        msgDialog.showReupload(Sync.this,"Successfully uploaded\n" + billLeft+"/"+billRecordCount + " records",value1 -> {
                            if (value1.equals("OK"))
                                insertBillUploadMaster();
                        });
                    }
                    msgDialog.showAlertNoCancel(Sync.this, "Upload bill(s)","Successfully uploaded\n" + billLeft+"/"+billRecordCount + " records", value1 -> {
                        if (value1.equals("OK"))
                            uploadFile();
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
                saveToLogs("Error in uploading bill(s) cause: " + e.getLocalizedMessage());
                msgDialog.showErrDialog("Error in uploading bill(s) cause: " + e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
                editor.putString("ulDateTime", df.format(new Date()));
                editor.commit();
            }
        });
    }

    public void uploadFile(){
        File newdir = new File(imageDIR + "/RnB/");
        if(!newdir.exists())
            newdir.mkdirs();
        File[] files = new File(imageDIR+ "/RnB").listFiles();
        if (files.length > 0) {
            if(checkingError[0] == 1)
                checkingError[0]=0;
            UploadMeterImages uploadMeterImages = new UploadMeterImages();
            uploadMeterImages.execute(String.valueOf(files.length));
        }else{
            Intent intent = new Intent(Sync.this.getApplicationContext(), Sync.class);
            Sync.this.startActivity(intent);
            finish();
        }

    }

    public void threadSleep(int sleep){
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class UploadMeterImages extends AsyncTask<String, String, Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setTitle("Uploading Image(s)");
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
                Toast.makeText(Sync.this, "Image(s) upload complete", Toast.LENGTH_LONG).show();
                uploadFile();
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            if(!isCancelled()){
                cnt=0;
                File[] files = new File(imageDIR + "/RnB").listFiles();
                MultipartBody.Part part;
                RequestBody fileReqBody;

                for(int loop= 0; loop < files.length; loop++){
                    if(!isCancelled()) {
                        fileReqBody = RequestBody.create(MediaType.parse("*/*"), files[loop]);
                        part = MultipartBody.Part.createFormData("file", files[loop].getName(), fileReqBody);
                        int countImage = loop;
                        cnt++;
                        threadSleep(200);
                        observables.uploadFile(part).subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(String check) {
                                publishProgress(cnt + "/" + files.length);
                                if (check.equals("OK"))
                                    files[countImage].delete();
                                else {
                                    cancel(true);
                                    if (checkingError[0] != 1) {
                                        checkingError[0] = 1;
                                        msgDialog.showErrDialogCancelAbleWithIntent(Sync.this,"Error in uploading image(s) cause: Please check your connection");
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                progress.dismiss();
                                cancel(true);
                                if (checkingError[0] != 1) {
                                    checkingError[0] = 1;
                                    msgDialog.showErrDialogCancelAbleWithIntent(Sync.this,"Error in uploading image(s) cause: " + e.getLocalizedMessage());
                                }
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
                    }
                    if (isCancelled())
                        break;
                }
                int i=0;
                while (i < 1){
                    if (new File(imageDIR + "/RnB").listFiles().length == 0)
                        break;
                    if(isCancelled())
                        break;
                }
            }

            return Integer.valueOf(strings[0]);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progress.setMessage("Uploading " +values[0]);
        }
    }

    private void dialog(String message, int counter) {
        progressUpload = new Dialog(Sync.this);
        progressUpload.setCancelable(false);
        progressUpload.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressUpload.setContentView(R.layout.progress_dialog);
        progressBar = (ProgressBar) progressUpload.findViewById(R.id.progressBar1);
        tvLoading = (TextView) progressUpload.findViewById(R.id.tv1);
        progress_text = (TextView) progressUpload.findViewById(R.id.progress_text);
        tvLoading.setText(message + " " + counter + " record(s)...");
        btnCancelUpload = (Button) progressUpload.findViewById(R.id.btncancel);
        btnCancelUpload.setOnClickListener(v -> {
            progressUpload.dismiss();
        });
        progressUpload.show();
    }

    @Override
    public void onPause() {
        closeDB();
        super.onPause();
        if (progressUpload != null)
            progressUpload.dismiss();
    }

    @Override
    public void onBackPressed() {
        closeDB();
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Kuryentxt.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSendButton(isConnected);
        this.isConnected=isConnected;
    }

    private boolean checkConnection() {
        boolean isConnected = NetworkReceiver.isConnected();
        showSendButton(isConnected);
        return isConnected;
    }

    @Override
    protected void onStop() {
        closeDB();
        super.onStop();
        Kuryentxt.getInstance().setConnectivityListener(this);
    }

    private void closeDB() {
        genericDao = new GenericDao(this);
        billHeaderDAO = new BillHeaderDAO(this);
        billHeaderDAO.instantiateDb();
        newMeterDao = new NewMeterDao(this);
        logDao = new LogDao(this);
    }

    private LogModel saveToLogs(String message) {
        LogModel logModel = new LogModel();
        logModel.setDateTime(currentDateandTime);
        logModel.setMessage(message);
        logDao.insert(logModel);
        return logModel;
    }

    /* SPecial Dialog for Upload*/
    private void showMsgDialog(String msg, int count) {
        final Dialog dialog = new Dialog(Sync.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView);
        status.setText(msg);
        if (count == 0) {
            image.setImageResource(R.drawable.success);
        }
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            insertBillUploadMaster();
        });
        dialog.show();
    }

    private class DownloadAccountDetails extends AsyncTask<String, String, String> {

        private String[] downloadMessage = {"Accounts", "Rates", "RDM"
                , "Rate Details", "Coreloss", "kWh Add-ons", "Rate Add-ons", "Remarks",
                "Lifeline Rates", "Charge Types", "DU Properties", "Rate Add-on Special", "Area", "Routes","Account Other Charges", "Other Charges","Surcharge","Account Bill Auxiliary"};

        private String[] urlList = {getResources().getString(R.string.acct_url)
                , getResources().getString(R.string.rate_master_url), getResources().getString(R.string.route_def_url)
                , getResources().getString(R.string.rateperkwchargedetail), getResources().getString(R.string.coreloss_url)
                , getResources().getString(R.string.kwh_addon_url), getResources().getString(R.string.rate_addon_url)
                , getResources().getString(R.string.remarks_url), getResources().getString(R.string.ll_detail_url)
                , getResources().getString(R.string.charge_type), getResources().getString(R.string.du_prop)
                , getResources().getString(R.string.rateaddontranspecial), getResources().getString(R.string.area), getResources().getString(R.string.routes), getResources().getString(R.string.accountOtherCharges)
                , getResources().getString(R.string.otherCharges), getResources().getString(R.string.surcharges),getResources().getString(R.string.accountBillAux)};

        @Override
        protected String doInBackground(String... strings) {
            RetClassGen retClassGen;
            String status = "Failed";
            try {
                int i = 0;
                while (urlList.length > i) {
                    if (!NetworkReceiver.isConnected()) {
                        cancel(true);
                        break;
                    } else {
                        publishProgress("Downloading " + downloadMessage[i]);
                        if (i == 2 || i == 5 || i == 6 || i == 11 || i == 13)
                            retClassGen = executeApiCall(urlList[i], Long.parseLong(idRDM));
                        else if(i == 0)
                            retClassGen = executeApiCall(urlList[i],getResources().getString(R.string.acct_version),Long.parseLong(idRDM));
                        else if (i == 1)
                            retClassGen = executeApiCall(urlList[i], "v2");
                        else
                            retClassGen = executeApiCall(urlList[i]);
                        if (retClassGen.getRespCode() == 200) {
                            genericDao.deleteTable(retClassGen.getTableName());
                            List<Map<String, Object>> list = retClassGen.getResponseBodyList();
                            if (list == null) {
                                publishProgress("No Data Found", downloadMessage[i]);
                            } else {
                                if (list.size() > 0) {
                                    for (Map<String, Object> obj : list) {
                                        status = "Saving " + downloadMessage[i] + " " + genericDao.save(obj, retClassGen.getTableName());
                                        publishProgress(status);
                                    }
                                    addToDiagnostics(downloadMessage[i], list.size(), reportDate);
                                }
                                status = "OK";
                            }
                        } else {
                            publishProgress("No Data Found", downloadMessage[i]);
                        }
                    }
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }

        private void addToDiagnostics(String status, int size, String reportDate) {
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setTitle(status);
            diagnostic.setTotal(size);
            diagnostic.setTs(reportDate);
            diagnosticList.add(diagnostic);
        }

        private RetClassGen executeApiCall(String urlPath, long idRdm) {
            return retrofitHandler.downloadData(urlPath, idRdm);
        }
        private RetClassGen executeApiCall(String urlPath,String version, long idRdm) {
            return retrofitHandler.downloadData(urlPath,version,idRdm);
        }

        private RetClassGen executeApiCall(String urlPath) {
            return retrofitHandler.downloadData(urlPath);
        }

        private RetClassGen executeApiCall(String urlPath, String version) {
            return retrofitHandler.downloadData(urlPath, version);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setTitle("Downloading Data");
            progress.setMessage("Please wait for a few seconds.");
            progress.setCancelable(false);
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progress.setMessage(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progress.dismiss();
            msgDialog.showErrDialog("Connection lost, \nPlease check your internet connection");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("OK")) {
                DateFormat yeardateFormat = new SimpleDateFormat("yyyy-MM");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MONTH, 0);
                Date monthToday = c.getTime();
                c.add(Calendar.MONTH, -1);
                Date lastMonth = c.getTime();
                c.add(Calendar.MONTH, -1);
                Date last2Month = c.getTime();

                File[] files = new File(jsonDIR+ "/RNBFile").listFiles();
                for(int i =0;i < files.length; i++)
                    if (!yeardateFormat.format(monthToday).equals(yeardateFormat.format(files[i].lastModified()))
                            && !yeardateFormat.format(lastMonth).equals(files[i].lastModified())
                            &&  !yeardateFormat.format(last2Month).equals(yeardateFormat.format(files[i].lastModified())))
                        files[i].delete();
                editor.putString("dlDateTime", df.format(new Date()));
                editor.commit();
                observables.updateRDM(idRDM).subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equals("OK")) {
                            String dataStr = new Gson().toJson(diagnosticList);
                            editor.putString("diagnostics", dataStr);
                            editor.commit();
                            saveLogsToDB("dlf", Long.parseLong(sharedPref.getString("authID", "0")),
                                    Long.parseLong(idRDM));
                            checkRateStatus();
                        } else {
                            startActivity(new Intent(Sync.this, Sync.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        msgDialog.showErrDialog("Error Found: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            } else {
                msgDialog.showErrDialog("Error Found: " + s);
            }
            progress.dismiss();
        }

    }

    private class UploadNewMeters extends AsyncTask<Integer, String, RetClassGen<NewMeterModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog("Uploading new meters", meterCnt);
        }

        @Override
        protected RetClassGen<NewMeterModel> doInBackground(Integer... cid) {
            RetClassGen<NewMeterModel> retClassGen = new RetClassGen<>();
            NewMeterModel nm;
            try {
                List<NewMeterModel> newMeterModels = newMeterDao.getAll();
                meterCnt = newMeterModels.size();
                for (NewMeterModel newMeterModel : newMeterModels) {
                    if (!NetworkReceiver.isConnected()) {
                        cancel(true);
                        break;
                    } else {
                        count++;
                        percentage = 100 * count / meterCnt;
                        retClassGen = retrofitHandler.insertNewMeter(newMeterModel);
                        nm = retClassGen.getResposeBody();
                        if (retClassGen.getRespCode() == 200)
                            newMeterDao.updateIsUpload(nm.getMsn());
                    }
                    publishProgress(Integer.toString(percentage), retClassGen.getRespMsg());
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return retClassGen;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(Integer.parseInt(values[0]));
            tvLoading.setText("Uploading " + meterCnt + " meter(s) " + values[0] + "%");
            progress_text.setText(values[1]);
        }

        @Override
        protected void onPostExecute(RetClassGen<NewMeterModel> retClassGen) {
            super.onPostExecute(retClassGen);
            progressUpload.dismiss();
            if (retClassGen.getRespCode() == 200) {
                int meterCount = Integer.parseInt(genericDao.getOneField("COUNT(id)","armNewMeter","WHERE isUploaded = 0","","","0"));
                if (meterCount > 0) {
                    showMsgDialog("New Meter(s) successfully uploaded, except for " + meterCount + " record(s)", 1);
                } else {
                    showMsgDialog("All meter(s) are successfully uploaded", 0);
                }
            } else {
                msgDialog.showErrDialog("Failed to upload new meters\n"
                        + retClassGen.getRespMsg() + "\nPlease try again");
            }
            if (progressUpload != null)
                progressUpload.dismiss();
            if (wakelock != null) {
                wakelock.release();
                wakelock = null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressUpload.dismiss();
            msgDialog.showErrDialog("Connection lost");
        }

    }

    public void matchBillNo(String billNo){
        observables.matchBillNo(billNo).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                if (s.equals("OK")) {
                    billHeaderDAO.updateByBillNo("1",billNo,"isUploaded");
                    GlobalVariable.billNolist.remove(billNo);
                    uploadedCnt=GlobalVariable.billNolist.size();
                }
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
}