package com.xesi.xenuser.kuryentxtreadbill.apiHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.MasterIdCallBack;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;
import com.xesi.xenuser.kuryentxtreadbill.model.RdmModel;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xenuser on 3/15/2017.
 */
public class RetrofitHandler {
    public static final String APP_PROPERTY_SETTING = "app_config";
    private static final String TAG = "RETROFITHANDLER";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Context context;
    private APIHandler handler;

    public RetrofitHandler(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        this.handler = ServiceGenerator.createService(APIHandler.class);
    }

    public RetClassGen<NewMeterModel> insertNewMeter(NewMeterModel newMeterModel) {
        RetClassGen<NewMeterModel> retClassGen = new RetClassGen<>();
        Call<RetClassGen<NewMeterModel>> call = handler.insertNewMeter(newMeterModel);
        try {
            retClassGen = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retClassGen;
    }

    public void isRdmReady(String idRDM, final MasterIdCallBack apiCallBack) {
        Call<RdmModel> call = handler.isRdmReady(idRDM);
        call.enqueue(new Callback<RdmModel>() {
            @Override
            public void onResponse(Call<RdmModel> call, Response<RdmModel> response) {
                RdmModel rdmModel;
                if (response.isSuccessful()) {
                    rdmModel = response.body();
                    if (rdmModel.getIdrdm() != 0 && rdmModel.getIdReader() != 0
                            && rdmModel.getIsReadyForUse() != null && rdmModel.getReadersName() != null) {
                        if (rdmModel.getIdrdm() > 0) {
                            editor.putInt("idRdm", rdmModel.getIdrdm());
                            editor.putString("idRDM", Integer.toString(rdmModel.getIdrdm()));
                            editor.putInt("idReader", rdmModel.getIdReader());
                            editor.putString("assignedTo", rdmModel.getReadersName());
                            editor.commit();
                            if (rdmModel.getIsReadyForUse().equals("Y")) apiCallBack.onSuccess(1);
                            else apiCallBack.onError("RDM " + rdmModel.getIdrdm() + " is locked,\nPlease contact your administrator");
                        } else {
                            apiCallBack.onError("RDM " + rdmModel.getIdrdm() + " not found!\nPlease contact your administrator");
                        }
                    } else {
                        apiCallBack.onError("RDM " + rdmModel.getIdrdm() + " not found!\nPlease contact your administrator");
                    }
                } else {
                    apiCallBack.onError(response.message());
                }

            }

            @Override
            public void onFailure(Call<RdmModel> call, Throwable t) {
                apiCallBack.onError(t.getMessage());
            }
        });

    }

    public RetClassGen downloadData(String urlPath) {
        RetClassGen retClassGen = new RetClassGen();
        Call<RetClassGen> call = handler.downloadData(urlPath);
        try {
            retClassGen = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retClassGen;
    }

    public RetClassGen downloadVersionData(String urlPath, String version) {
        RetClassGen retClassGen = new RetClassGen();
        final Map<String, String> params = new HashMap<>();
        params.put("version",version);
        Call<RetClassGen> call = handler.downloadData(urlPath,params);
        try {
            retClassGen = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retClassGen;
    }

    public RetClassGen downloadData(String urlPath, String version) {
        RetClassGen retClassGen = new RetClassGen();
        Call<RetClassGen> call = handler.downloadData(urlPath, version);
        try {
            retClassGen = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retClassGen;
    }

    public RetClassGen downloadData(String urlPath, long idRdm) {
        RetClassGen retClassGen = new RetClassGen();
        Call<RetClassGen> call = handler.downloadData(urlPath, idRdm);
        try {
            retClassGen = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retClassGen;
    }

    public RetClassGen downloadData(String urlPath,String version, long idRdm) {
        RetClassGen retClassGen = new RetClassGen();
        Call<RetClassGen> call = handler.downloadData(urlPath, version, idRdm);
        try {
            retClassGen = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retClassGen;
    }

    public ResponseBody downloadAPK() {
        ResponseBody body = null;
        Call<ResponseBody> call = handler.downloadAPK();
        try {
            body = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

}
