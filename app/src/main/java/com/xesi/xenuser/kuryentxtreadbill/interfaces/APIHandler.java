package com.xesi.xenuser.kuryentxtreadbill.interfaces;

import com.xesi.xenuser.kuryentxtreadbill.model.AuthenticateEntity;
import com.xesi.xenuser.kuryentxtreadbill.model.ConChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.DatabaseChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;
import com.xesi.xenuser.kuryentxtreadbill.model.RateMasterStatus;
import com.xesi.xenuser.kuryentxtreadbill.model.RdmModel;
import com.xesi.xenuser.kuryentxtreadbill.model.UpdateChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.UploadData;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.UploadBillMaster;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by xenuser on 2/2/2017.
 */
public interface APIHandler {

    /* Activation Code */
    @GET("authenticateapp/1.1/{activationCode}")
    Observable<AuthenticateEntity> authApp(@Path("activationCode") String code);

    @GET("authenticateapp/printdelay/{idDevice}")
    Observable<Integer> getPrintingDelay(@Path("idDevice") int idDevice);

    @GET("authenticateapp/isSuppressed/{idDevice}")
    Observable<String> getIsSuppressed(@Path("idDevice") int idDevice);

    /* Download DU Properties*/
    @GET("duproperties")
    Observable<RetClassGen> getProperties();

    /* Download DU Logo*/
    @GET("downloads/logo")
    Observable<ResponseBody> downloadLogo();

    /*Insert Upload Master Record*/
    @POST("bills/saveuploadmaster")
    Observable<RetClassGen<UploadBillMaster>> insertMasterId(@Body UploadBillMaster uploadBillMaster);

    /* Upload Bills */
    @POST("bills/bulk/insert")
    Observable<String> uploadbills(@Body UploadData uploadData);

    @Multipart
    @POST("bills/file/insert")
    Observable<String> uploadFile(@Part MultipartBody.Part file);

    /*Check Server status */
    @GET("check")
    Observable<ConChecker> checkNetwork();

    /* Check if RDM is ready*/
    @GET("rdm/ready")
    Call<RdmModel> isRdmReady(@Query("idrdm") String idrdm);

    /*  *//* lock RDM*/
    @GET("rdm/update")
    Observable<String> updateRdm(@Query("idrdm") long idrdm);

    @POST("newmeter/save")
    Call<RetClassGen<NewMeterModel>> insertNewMeter(@Body NewMeterModel newMeterModel);

    @GET("{urlPath}")
    Call<RetClassGen> downloadData(@Path("urlPath") String urlPath);

    @GET("{urlPath}")
    Call<RetClassGen> downloadData(@Path("urlPath") String urlPath,@QueryMap(encoded = true) Map<String, String> params);

    @GET("{urlPath}/{idRdm}")
    Call<RetClassGen> downloadData(@Path("urlPath") String urlPath, @Path("idRdm") long idRdm);

    @GET("{urlPath}/{version}")
    Call<RetClassGen> downloadData(@Path("urlPath") String urlPath, @Path("version") String version);

    @GET("{urlPath}/{version}/{idRdm}")
    Call<RetClassGen> downloadData(@Path("urlPath") String urlPath, @Path("version") String version, @Path("idRdm") long idRdm);

    @GET("downloads/check-version")
    Observable<UpdateChecker> checkUpdates();

    @GET("downloads/check-database-update")
    Observable<List<DatabaseChecker>> checkDatabaseUpdates();

    @GET("downloads/apk")
    Call<ResponseBody> downloadAPK();

    @GET("ratemaster/check-rate-status")
    Observable<List<RateMasterStatus>> checkRateStatus();

    /* @GET("{urlPath}/{devId}/{idRdm}")
     Call<Void> savelog(@Path("urlPath") String urlPath,
                        @Path("devId") long devId,
                        @Path("idRdm") long idRdm);
 */
    @GET("logs/{urlPath}")
    Observable<Void> savelog(@Path("urlPath") String path,
                             @Query("devId") long devId,
                             @Query("idRdm") long idRdm);

   /* @GET("{urlPath}/{devId}/{idRdm}")
    Observable<Void> dlf(@Path("devId") String devId,
                         @Path("idRdm") String idRdm);

    @GET("logs/uls/{devId}/{idRdm}")
    Observable<Void> uls(@Path("devId") String devId,
                         @Path("idRdm") String idRdm);

    @GET("logs/ulf/{devId}/{idRdm}")
    Observable<Void> ulf(@Path("devId") String devId,
                         @Path("idRdm") String idRdm);*/

    @Multipart
    @POST("backup/file/backupJson")
    Observable<String> uploadBackupFile(@Part MultipartBody.Part file,@Query("deviceID") int deviceID);

    /*match bill No from local to server*/
    @POST("billNo/find/billNo")
    Observable<String> matchBillNo(@Query("billNo") String billNo);

    /*download JSON from server to mobile*/
    @GET("downloads/downloadBackup")
    Observable<String> downloadJsonBackup(@Query("folderName") String folderName);

}
