package com.xesi.xenuser.kuryentxtreadbill;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.AuthenticateEntity;
import com.xesi.xenuser.kuryentxtreadbill.model.ConChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;
import com.xesi.xenuser.kuryentxtreadbill.network.Kuryentxt;
import com.xesi.xenuser.kuryentxtreadbill.network.NetworkReceiver;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by xenuser on 9/7/2016.
 */
public class Authenticate extends BaseActivity implements NetworkReceiver.ConnectivityReceiverListener {
    public static final String APP_PROPERTY_SETTING = "app_config";
    public static final String SERVERIP = "serverIPKey";
    public static final String SERVERPORT = "serverPortKey";
    public static final String ISACTIVE = "isActive";
    private static final String TAG = "Authenticate";
    private static final String filename = "logo";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    private EditText etActivationCode, metNewIP, metNewPort;
    private TextView tvIP, tvPort;
    private LinearLayout serverProp;
    private Button mbtnOK, mbtnCancel, btnSend;
    private MsgDialog msgDialog;
    private NetworkReceiver receiver;
    private TextView tvDate, tvVer, noInternet;
    private LinearLayout llNoInternet;
    private Intent i;
    private String BASE_URL, server, ipAdd, port, activationCode;
    private MyObservables observables;
    private HeaderFooterInfo headerFooterInfo;
    private String jsonDIR;

    private void setFooterInfo() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvVer = (TextView) findViewById(R.id.appVersion);
        tvVer.setText(sharedPref.getString("version", "0.00"));
        tvDate.setText(sharedPref.getString("relDate", ""));
    }

    private void initializedUIProperties() {
        llNoInternet = (LinearLayout) findViewById(R.id.llNoInternet);
        noInternet = (TextView) findViewById(R.id.noInternet);
        btnSend = (Button) findViewById(R.id.btnSend);
        serverProp = (LinearLayout) findViewById(R.id.serverProp);
        tvIP = (TextView) findViewById(R.id.tvIPAddress);
        tvPort = (TextView) findViewById(R.id.tvPort);
        etActivationCode = (EditText) findViewById(R.id.etActivationCode);
    }

    private void redirect(String checkData) {
        if (!checkData.matches("")) {
            if (sharedPref.getString(getString(R.string.app_key), "").equals("")) {
                editor.putInt("logoutKey", 0);
                editor.commit();
                Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show();
                i = new Intent(getApplicationContext(), PasswordCreation.class);
                createDirectory();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_authenticate);
        receiver = new NetworkReceiver();
        msgDialog = new MsgDialog(this);
        dialog = new ProgressDialog(this);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        headerFooterInfo = new HeaderFooterInfo(this);
        initializedUIProperties();
        checkConnection();
        setFooterInfo();
        serverProp.setOnClickListener(v -> {
            dialogChangeIP();
            //  return false;
        });
        ipAdd = sharedPref.getString(SERVERIP, "");
        port = sharedPref.getString(SERVERPORT, "");
        tvIP.setText(ipAdd);
        tvPort.setText(port);

        if (port.matches("")) {
            server = ipAdd.trim();
        } else {
            server = ipAdd.trim() + ":" + port.trim();
        }

        BASE_URL = getResources().getString(R.string.http) + server
                + getResources().getString(R.string.base_url);
        ServiceGenerator.changeApiBaseUrl(BASE_URL);
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), this);
        redirect(sharedPref.getString(ISACTIVE, ""));

        btnSend.setOnClickListener(v -> {
            checkConnection();
            activationCode = etActivationCode.getText().toString().trim();
            if (!activationCode.isEmpty()) {
                activateApp(activationCode);
            } else {
                Toast.makeText(getApplicationContext(), "Please Enter Activation Code. ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void activateApp(String activationCode) {
        dialog.setMessage("Authenticating...");
        dialog.show();
        observables.checkServer().subscribe(new Observer<ConChecker>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ConChecker conChecker) {
                if (conChecker.getCode() == 200 && conChecker.getMessage().equals("Connected")) {
                    observables.authenticateApp(activationCode).subscribe(new Observer<AuthenticateEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(AuthenticateEntity authenticateDetails) {
                            verifyAuthCode(authenticateDetails);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (dialog != null)
                                dialog.dismiss();
                            msgDialog.showErrDialog("Activation code not found!");
                            etActivationCode.setText("");
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "Authentication Complete");
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                if (dialog != null)
                    dialog.dismiss();
                msgDialog.showErrDialog( e.getMessage()
                        + "\nNote: Please recheck the\nIP Address or Port entered");
                etActivationCode.setText("");
            }

            @Override
            public void onComplete() {
            }
        });

    }


    private void verifyAuthCode(AuthenticateEntity authenticateDetails) {
        if (authenticateDetails.getIsActive().equals("Y")) {
            dialog.dismiss();
            msgDialog.showErrDialog("Activation code is already used");
            etActivationCode.setText("");
        } else {
            saveAppConfig(authenticateDetails);
            downloadProperties();
        }
    }

    private void downloadProperties() {
        observables.duProperties().subscribe(new Observer<RetClassGen>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RetClassGen retClassGen) {
                GenericDao genericDao = new GenericDao(getApplicationContext());
                if (retClassGen.getRespCode() == 200) {
                    List<Map<String, Object>> list = retClassGen.getResponseBodyList();
                    if (list != null) {
                        if (list.size() > 0) {
                            for (Map<String, Object> obj : list) {
                                genericDao.save(obj, retClassGen.getTableName());
                            }
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Properties saved", Toast.LENGTH_LONG).show();
                } else {
                    msgDialog.showErrDialog("Saving Failed");
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onComplete() {
                if (dialog != null) {
                    dialog.dismiss();
                    downloadLogo();
                    startActivity(new Intent(Authenticate.this, PasswordCreation.class));
                    createDirectory();
                    finish();
                }
            }
        });

    }

    public static boolean externalMemoryAvailable() {
        if (Environment.isExternalStorageRemovable()) {
            //device support sd card. We need to check sd card availability.
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED) || state.equals(
                    Environment.MEDIA_MOUNTED_READ_ONLY);
        } else {
            //device not support sd card.
            return false;
        }
    }

    private void createDirectory(){
        if(externalMemoryAvailable())
            jsonDIR= "/sdcard";
        else
            jsonDIR= String.valueOf(Environment.getExternalStorageDirectory());

        File fileDir = new File(jsonDIR, "RNBFile");
        if (!fileDir.exists()) {
            fileDir.mkdir();
            Log.d("RNB", "Directory Created");
        } else
            Log.d("RNB", "Directory Found");
    }

    private void downloadLogo() {
        observables.dulogo().subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody body) {
                observables.writeResponseBodyToDisk(body, filename);
                Toast.makeText(getApplicationContext(), "FINISH", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), "DU logo not found", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                Toast.makeText(getApplicationContext(), "Logo Save", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveAppConfig(AuthenticateEntity authenticationInfo) {
        try {
            editor.putString("authID", Integer.toString(authenticationInfo.getId()));
            editor.putString("assignedTo", authenticationInfo.getAssignedTo());
            editor.putString("idRDM", "0");
            editor.putInt("idRdmOld", 0);
            editor.putString(ISACTIVE, "Y");
            editor.putString("unlockerCode", authenticationInfo.getMasterKey());
            editor.putInt("printDelay", authenticationInfo.getPrintDelay());
            editor.putString("isSuppressedPrintBuffer", authenticationInfo.getIsSuppressedPrintBuffer());
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_DEL:
                return true;
            case KeyEvent.KEYCODE_BACK:
                headerFooterInfo.exitApp();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void dialogChangeIP() {
        LayoutInflater inflate = LayoutInflater.from(this);
        View promptsView = inflate.inflate(R.layout.dialog_change_ip, null);
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Change Local IP");

        final android.app.AlertDialog alert = alertDialog.create();
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
                    alert.dismiss();
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        Kuryentxt.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSendButton(isConnected);
    }

    private void checkConnection() {
        boolean isConnected = receiver.isConnected();
        showSendButton(isConnected);
    }

    private void showSendButton(boolean isConnected) {
        String message;
        if (!isConnected) {
            btnSend.setEnabled(false);
            llNoInternet.setVisibility(View.VISIBLE);
            message = "No Internet connection. Activate button is disabled.";
            noInternet.setText(message);
        } else {
            llNoInternet.setVisibility(View.GONE);
            btnSend.setEnabled(true);
        }
    }
}