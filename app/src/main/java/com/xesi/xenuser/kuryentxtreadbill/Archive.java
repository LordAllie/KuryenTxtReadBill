package com.xesi.xenuser.kuryentxtreadbill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.nbbse.mobiprint3.Printer;
import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.RecordListAdapter;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.RetrofitHandler;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.LogDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.NewMeterDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.global.GlobalVariable;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.LogModel;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;
import com.xesi.xenuser.kuryentxtreadbill.model.StringBill;
import com.xesi.xenuser.kuryentxtreadbill.model.UploadData;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.UploadBillMaster;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;
import com.xesi.xenuser.kuryentxtreadbill.network.NetworkReceiver;
import com.xesi.xenuser.kuryentxtreadbill.util.PrintReceipt;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Daryll-POGI on 24/10/2019.
 */

public class Archive extends BaseActivity{

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
    private Dialog progressUpload;
    private ProgressBar progressBar;
    private TextView progress_text, tvLoading;
    private Button btnCancelUpload;
    private int meterCnt,billRecordCount,percentage, count = 0,totalAccounts, uploadedCnt=0;
    private NewMeterDao newMeterDao;
    private UploadBillMaster uploadBillMaster;
    private MyObservables observables;
    private RetrofitHandler retrofitHandler;
    private List<StringBill> billJsonList;
    private UploadData uploadData;
    private ProgressDialog dialog;
    private LogDao logDao;
    private SimpleDateFormat sdf;
    private String currentDateandTime,idRDM;
    private SharedPreferences.Editor editor;
    private DateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_archive);
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
        newMeterDao = new NewMeterDao(this);
        uploadBillMaster = new UploadBillMaster();
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), this);
        retrofitHandler = new RetrofitHandler(this);
        uploadData = new UploadData();
        logDao = new LogDao(this);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateandTime = sdf.format(new Date());
        df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        editor = sharedPref.edit();
        idRDM = sharedPref.getString("idRDM", "");
        genericDao = new GenericDao(getApplication());
        duPropertyDAO = new DUPropertyDAO(getApplication());
        if (duPropertyDAO.getPropertyValue("IS_SEARCH_KEYPAD_NUMERIC").equals("Y"))
            searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        billsCount = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillHeader","where isArchive = ","Y","","0"));
        billRecordCount = Integer.parseInt(genericDao.getOneField("SELECT COUNT(_id) FROM armBillHeader WHERE isUploaded = 0 AND isArchive = 'Y'","0"));
        totalAccounts = Integer.parseInt(genericDao.getOneField("SELECT COUNT(_id) FROM armBillHeader WHERE isUploaded = 0 AND isArchive = 'Y'","0"));
        GlobalVariable.billNolist=billHeaderDAO.getAllArchiveBillNo();
        totalBillsCount.setText("Total Archives: " + billsCount);
        recordListAdapter = new RecordListAdapter(this, displayRecords());
        lvReports.setOnItemClickListener((parent, view, position, id) -> {
            view.setSelected(true);
            String billNo = recordListAdapter.getSelectedItem(position);
            Intent i = new Intent(getApplicationContext(), ArchiveDetailedBill.class);
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
        List<BillHeader> billHeaders = billHeaderDAO.getAllBillList("*","where isArchive = ","Y","ORDER BY isUploaded ASC, _id DESC");
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


    private void dialog(String message, int counter) {
        progressUpload = new Dialog(this);
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

    public void uploadData(View view) {
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

//    public void uploadFile(){
//        File newdir = new File(imageDIR + "/RnB/");
//        if(!newdir.exists())
//            newdir.mkdirs();
//        File[] files = new File(imageDIR+ "/RnB").listFiles();
//        if (files.length > 0) {
//            if(checkingError[0] == 1)
//                checkingError[0]=0;
//            Sync.UploadMeterImages uploadMeterImages = new Sync.UploadMeterImages();
//            uploadMeterImages.execute(String.valueOf(files.length));
//        }else{
//            Intent intent = new Intent(Sync.this.getApplicationContext(), Sync.class);
//            Sync.this.startActivity(intent);
//            finish();
//        }
//
//    }

    private void insertBillUploadMaster() {
        UploadBillMaster uploadBillMaster = setUploadMasterData();
        if (billRecordCount == 0) {
            msgDialog.showAlertNoCancel(this ,"","No bill(s) to upload", value1 -> {
                if (value1.equals("OK")) {
                    msgDialog.showAlert(this ,"Untagged all bills?", value2 -> {
                        if (value2.equals("OK")){
                            BillHeaderDAO untaggedBHD = new BillHeaderDAO(this);
                            untaggedBHD.instantiateDb();
                            untaggedBHD.untaggedBills();
                            untaggedBHD.close();
                            msgDialog.showAlertNoCancel(this ,"","Bill successfully untagged as uploaded", value3 -> {
                                if (value3.equals("OK")){}
//                                    uploadFile();
                            });
                        }else{}
//                            uploadFile();
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

    private class UploadNewMeters extends AsyncTask<Integer, String, RetClassGen<NewMeterModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog("Uploading meters", meterCnt);
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

    private void showMsgDialog(String msg, int count) {
        final Dialog dialog = new Dialog(this);
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
                    do{
                        matchBillNo(GlobalVariable.billNolist.get(i));
                        i++;
                    }while(i< GlobalVariable.billNolist.size());
//                    billHeaderDAO.updateAll();
                    dialog.dismiss();
                    int billLeft = Integer.parseInt(genericDao.getOneField("COUNT(_id)","armBillHeader","WHERE isUploaded =","0","","0"));
                    if(billLeft<billRecordCount){
                        msgDialog.showReupload(Archive.this,"Successfully uploaded\n" + billLeft+"/"+billRecordCount + " records",value1 -> {
                            if (value1.equals("OK"))
                                insertBillUploadMaster();
                        });
                    }
                    msgDialog.showAlertNoCancel(Archive.this, "Upload bill(s)","Successfully uploaded\n" + billLeft+"/"+billRecordCount + " records", value1 -> {
                        if (value1.equals("OK")){}
//                            uploadFile();
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

    private LogModel saveToLogs(String message) {
        LogModel logModel = new LogModel();
        logModel.setDateTime(currentDateandTime);
        logModel.setMessage(message);
        logDao.insert(logModel);
        return logModel;
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
