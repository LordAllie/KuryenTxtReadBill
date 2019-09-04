package com.xesi.xenuser.kuryentxtreadbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.MissingDuProperties;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.RetrofitHandler;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.RouteDefinitionDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.ConChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.UpdateChecker;
import com.xesi.xenuser.kuryentxtreadbill.network.Kuryentxt;
import com.xesi.xenuser.kuryentxtreadbill.network.NetworkReceiver;
import com.xesi.xenuser.kuryentxtreadbill.util.PropertyChecker;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.ResponseBody;

/**
 * Created by xenuser on 5/12/2016.
 */
public class Homepage extends BaseActivity implements NetworkReceiver.ConnectivityReceiverListener {

    public static final String APP_PROPERTY_SETTING = "app_config";
    public static final String SERVERIP = "serverIPKey";
    public static final String SERVERPORT = "serverPortKey";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private AccountDao accountDao;
    private GenericDao genericDao;
    private RouteDefinitionDao routeDefinitionDao;
    //   private ImageView dulogo;
    private String port;
    private MsgDialog msgDialog;
    private ProgressDialog mProgressDialog;
    private HeaderFooterInfo headerFooterInfo;
    private Button sync;
    private MyObservables observables;
    private RetrofitHandler retrofitHandler;
    private UniversalHelper helper;
    private ProgressDialog bar;
    private MissingDuProperties missingDuProperties;
    private PropertyChecker propertyChecker;
    private Intent i;
    boolean isConnected;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_homepage);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        headerFooterInfo = new HeaderFooterInfo(this);
        editor = sharedPref.edit();
        bar = new ProgressDialog(this);
        msgDialog = new MsgDialog(this);
        helper = new UniversalHelper(this);
        headerFooterInfo.setHeaderInfo();
        sync = (Button) findViewById(R.id.btnSync);
        headerFooterInfo.setFooterInfo();
        propertyChecker = new PropertyChecker(this);
        accountDao = new AccountDao((getApplication()));
        genericDao = new GenericDao((getApplication()));
        routeDefinitionDao = new RouteDefinitionDao((getApplication()));
        String ipAdd = sharedPref.getString(SERVERIP, "");
        port = sharedPref.getString(SERVERPORT, "");
        ServiceGenerator.changeApiBaseUrl(helper.setUpBaseURL(ipAdd, port));
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), this);
        retrofitHandler = new RetrofitHandler(this);
        isConnected = NetworkReceiver.isConnected();
        List<String> missingProp = propertyChecker.checkProperties();
        if (missingProp.size() > 0)
            displayMissingProperties(missingProp);
        checkConnection();
        if(isConnected)
            checkForUpdates();
    }

    private void displayMissingProperties(List<String> missingProp) {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.layout_missing_properties, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Missing " + missingProp.size() + " DU Properties");

        final AlertDialog alert = alertDialog.create();
        alert.show();
        Button btnUpdate = (Button) promptsView.findViewById(R.id.btnUpdate);
        Button btnOk = (Button) promptsView.findViewById(R.id.btnOK);
        ListView lvProperties = (ListView) promptsView.findViewById(R.id.lvMissingDuProperties);
        missingDuProperties = new MissingDuProperties(this, missingProp);
        lvProperties.setAdapter(missingDuProperties);
        btnUpdate.setOnClickListener(v -> {
            PropertyChecker propertyChecker = new PropertyChecker(this);
            propertyChecker.updateProperties(alert);
        });
        btnOk.setOnClickListener(arg0 -> {
            Intent intent = new Intent(Homepage.this, Tools.class);
            startActivity(intent);
            finish();
            alert.dismiss();
        });
    }

    private void checkForUpdates() {

        observables.checkUpdates().subscribe(new Observer<UpdateChecker>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(UpdateChecker updateChecker) {
                Log.d("UPDATECHECKER", new Gson().toJson(updateChecker));
                if (updateChecker.getLatestVersionCode() > sharedPref.getInt("versionCode", 0)) {
                    msgDialog.showUpdateDialog("An update for Kuryentxt Read & Bill has been detected.", result -> {
                        if (result) {
                            editor.putBoolean("updateLater", false);
                            editor.commit();
                            new DownloadNewVersion().execute();
                        } else {
                            editor.putBoolean("updateLater", true);
                            editor.commit();
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("UPDATECHECKER", e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("UPDATECHECKER", "FINISH");
            }
        });

        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                startActivity(new Intent(this, Homepage.class));
                finish();
                Runtime.getRuntime().exit(0);
            }
        });
    }

    private void settingDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    //Transact
    public void transact(View view) {
        int recordCount;
        int isActive;
        int checkData;
        recordCount = Integer.parseInt(genericDao.getOneField("COUNT(id)","arm_account","","","","0"));
        if (recordCount <= 0)
            msgDialog.showErrDialog(getResources().getString(R.string.no_dl_account_err_msg));
        else {
            isActive = Integer.parseInt(genericDao.getOneField("id","arm_route_definition","WHERE isActive = 1","","LIMIT 1","0"));
            if (isActive == 0) {
                checkData = Integer.parseInt(genericDao.getOneField("idRoute","arm_route_definition","","","limit 1","0"));
                routeDefinitionDao.updateIsActive(checkData);
            }
            i = new Intent(getApplicationContext(), Transaction.class);
            startActivity(i);
            finish();
        }
    }

    //Sync
    public void sync(View view) {

        settingDialog("Checking your server, please wait...");
        observables.checkServer().subscribe(new Observer<ConChecker>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ConChecker conChecker) {
                if (conChecker.getCode() == 200 && conChecker.getMessage().equals("Connected")) {
                    mProgressDialog.dismiss();
                    Intent i = new Intent(getApplicationContext(), Sync.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                mProgressDialog.dismiss();
                msgDialog.checkConnectionDialog(e.getMessage() + "\n\nPlease check your server IP or\n the API Service");
            }

            @Override
            public void onComplete() {
                mProgressDialog.dismiss();
            }
        });
    }

    //Tools
    public void btnTools(View view) {
        i = new Intent(getApplicationContext(), Tools.class);
        startActivity(i);
        finish();
    }

    //Reports
    public void btnReport(View view) {
        i = new Intent(getApplicationContext(), Reports.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_DEL:
                return true;
            case KeyEvent.KEYCODE_BACK:
                Log.d("onBackPressed", "OnBackPressed KEY CODE BACK");
                headerFooterInfo.exitApp();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Kuryentxt.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSendButton(isConnected);
    }

    private void checkConnection() {
        showSendButton(isConnected);
    }

    private void showSendButton(boolean isConnected) {
        if (!isConnected) {
            sync.setEnabled(false);
        } else {
            sync.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("onBackPressed", "OnBackPressed");
        headerFooterInfo.exitApp();
    }

    void OpenNewVersion(String location) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location + "app-release.apk")),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bar.setCancelable(false);
            bar.setMessage("Downloading...");
            bar.setIndeterminate(true);
            bar.setCanceledOnTouchOutside(false);
            bar.show();
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            bar.setIndeterminate(false);
            bar.setMax(100);
            bar.setProgress(progress[0]);
            String msg;
            if (progress[0] > 99) {
                msg = "Finishing... ";
            } else {
                msg = "Downloading... " + progress[0] + "%";
            }
            bar.setMessage(msg);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            bar.dismiss();
            if (result) {
                Toast.makeText(getApplicationContext(), "Update Done",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error: Try Again",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean isSaved = false;
            ResponseBody body = retrofitHandler.downloadAPK();
            try {
                InputStream inputStream;
                File fileDir = new File(Environment.getExternalStorageDirectory(), "APK");

                if (!fileDir.exists()) {
                    fileDir.mkdir();
                    Log.d("RNB", "Directory Created");
                } else
                    Log.d("RNB", "Directory Found");

                String PATH = Environment.getExternalStorageDirectory() + "/APK/";
                File outputFile = new File(fileDir.getAbsolutePath(), "app-release.apk");
                if (outputFile.exists())
                    outputFile.delete();
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                inputStream = body.byteStream();
                long apkTotalSize = body.contentLength();
                Log.d("RNB", "Total size " + String.valueOf(apkTotalSize));
                byte[] fileReader = new byte[1024];
                int len1 = 0;
                int per = 0;
                int downloaded = 0;
                while ((len1 = inputStream.read(fileReader)) != -1) {
                    outputStream.write(fileReader, 0, len1);
                    downloaded += len1;
                    per = (int) (downloaded * 100 / apkTotalSize);
                    Log.d("RNB", "Per " + String.valueOf(per));
                    publishProgress(per);
                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();
                OpenNewVersion(PATH);
                isSaved = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("RNB", String.valueOf(e.getMessage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isSaved;
        }
    }
}