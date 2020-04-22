package com.xesi.xenuser.kuryentxtreadbill.apiHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.AuthenticateEntity;
import com.xesi.xenuser.kuryentxtreadbill.model.ConChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.DatabaseChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.DuPropertyZip;
import com.xesi.xenuser.kuryentxtreadbill.model.RateMasterStatus;
import com.xesi.xenuser.kuryentxtreadbill.model.UpdateChecker;
import com.xesi.xenuser.kuryentxtreadbill.model.UploadData;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.UploadBillMaster;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RetClassGen;
import com.xesi.xenuser.kuryentxtreadbill.util.AndroidBmpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;

/**
 * Created by Daryll Sabate on 12/27/2017.
 */

public class MyObservables {
    private Context context;
    private APIHandler handler;
    private ByteBuffer buffer;

    public MyObservables(APIHandler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    public Observable<Integer> getPrintingDelay(int idDevice) {
        return handler.getPrintingDelay(idDevice)
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getIsSuppressed(int idDevice) {
        return handler.getIsSuppressed(idDevice)
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ConChecker> checkServer() {
        return handler.checkNetwork()
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AuthenticateEntity> authenticateApp(String activationCode) {
        return handler.authApp(activationCode)
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RetClassGen> duProperties() {
        return handler.getProperties()
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<ResponseBody> dulogo() {
        return handler.downloadLogo()
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DuPropertyZip> downloadDuProperties() {
        Observable<RetClassGen> property = duProperties();
        Observable<ResponseBody> logo = dulogo();
        return Observable
                .zip(property, logo, (duProperties, logos) ->
                        new DuPropertyZip(duProperties, logos));
    }

    public Observable<List<RateMasterStatus>> checkRateStatus() {
        return handler.checkRateStatus()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> updateRDM(String idRdm) {
        return handler.updateRdm(Long.parseLong(idRdm))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RetClassGen<UploadBillMaster>> insertUploadMaster(UploadBillMaster uploadBillMaster) {
        return handler.insertMasterId(uploadBillMaster)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> uploadFile(MultipartBody.Part file) {
        return handler.uploadFile(file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UpdateChecker> checkUpdates() {
        return handler.checkUpdates()
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<DatabaseChecker>> checkDatabaseUpdates() {
        return handler.checkDatabaseUpdates()
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<String> uploadBills(UploadData uploadData) {
        return handler.uploadbills(uploadData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> savelogs(String url, long idDev, long idRdm) {
        return handler.savelog(url, idDev, idRdm)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void saveLogoAsBitmap(String path) {
        OutputStream outputStream = null;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            bitmap.createScaledBitmap(bitmap, width, height, false);
            Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(myBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, 0, 0, null);
            buffer = AndroidBmpUtil.save(myBitmap);
            outputStream = context.openFileOutput("logo.bmp", Context.MODE_PRIVATE);
            outputStream.write(buffer.array());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean writeResponseBodyToDisk(ResponseBody body, String filename) {
        boolean isSaved = false;

        try {
            // todo change the file location/name according to your needs
            File imgFile = new File(context.getFilesDir().getAbsolutePath() + "/" + filename + ".jpg");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(imgFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("SAVELOGO", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                saveLogoAsBitmap(imgFile.getAbsolutePath());
                isSaved = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSaved;
    }

    public Observable<String> uploadBackupFile(MultipartBody.Part file, int deviceID) {
        return handler.uploadBackupFile(file,deviceID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> matchBillNo(String billNo) {
        return handler.matchBillNo(billNo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> downloadJsonBackup(String folderName) {
        return handler.downloadJsonBackup(folderName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}