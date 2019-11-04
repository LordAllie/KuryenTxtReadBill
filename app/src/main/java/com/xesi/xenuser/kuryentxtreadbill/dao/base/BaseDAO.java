package com.xesi.xenuser.kuryentxtreadbill.dao.base;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.MyObservables;
import com.xesi.xenuser.kuryentxtreadbill.apiHandler.ServiceGenerator;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.APIHandler;
import com.xesi.xenuser.kuryentxtreadbill.model.DatabaseChecker;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Daryll Sabate on 6/20/2017.
 */

public class BaseDAO extends SQLiteOpenHelper {

    public static final String APP_PROPERTY_SETTING = "app_config";
    public static final String SP_KEY_DB_VER = "db_ver";
    public static final int DB_VERSION = 24; //20 lastVersion 2018-08-13 updated version 2019-06-14 //18 to prod 20 updated 08/13/18
    public static String DB_PATH = "";
    public static String DB_NAME = "read&bill.db";
    public SharedPreferences prefs;
    public SQLiteDatabase mcfDB;
    public Context mContext;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private MyObservables observables;
    private String BASE_URL;

    public BaseDAO(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
        DB_PATH = mContext.getDatabasePath(DB_NAME).toString();
        sharedPref = mContext.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        BASE_URL = mContext.getResources().getString(R.string.http) + sharedPref.getString("serverIPKey", "172.16.0.192") + ":" +  sharedPref.getString("serverPortKey", "8088")
                + mContext.getResources().getString(R.string.base_url);
        ServiceGenerator.changeApiBaseUrl(BASE_URL);
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), mContext);
        editor = sharedPref.edit();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* USE THIS WHEN ADD OR DROP TABLE, ADD COLUMN - to prevent on loosing data */
        /* update the db/tables only applicable for new, drop, rename table. add new table column*/
        final SQLiteDatabase[] dbCon = {null};
        observables.checkDatabaseUpdates().subscribe(new Observer<List<DatabaseChecker>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(List<DatabaseChecker> databaseCheckerList) {
                try {
                    if (newVersion != oldVersion) {
                        dbCon[0] = SQLiteDatabase.openDatabase(DB_PATH, null, db.OPEN_READWRITE);
                        for (DatabaseChecker databaseChecker : databaseCheckerList){
                            if(databaseChecker.getDatabaseVersion() > oldVersion){
                                dbCon[0].beginTransaction();
                                dbCon[0].execSQL("PRAGMA foreign_keys=OFF;");
                                dbCon[0].execSQL(databaseChecker.getUpdateQuery());
                                dbCon[0].setTransactionSuccessful();
                                dbCon[0].endTransaction();
                            }
                            Log.d("UPDATECHECKER" ,databaseChecker.getUpdateQuery());
                        }
                    }
                }catch (Exception e){

                }

            }
            @Override
            public void onError(Throwable e) {
                Log.d("UPDATECHECKER", e.getMessage());
                dbCon[0].close();
            }

            @Override
            public void onComplete() {
                Log.d("UPDATECHECKER", "FINISH");
                dbCon[0].close();
            }
        });
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys='ON';");
        }
    }

    public boolean deleteTable(String TABLE_NAME) {
        boolean isDeleted = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int returnRows = db.delete(TABLE_NAME, null, null);
            if (returnRows > 0) {
                isDeleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public boolean deleteTable(String TABLE_NAME, String where) {
        boolean isDeleted = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int returnRows = db.delete(TABLE_NAME, where, null);
            if (returnRows > 0) {
                isDeleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public synchronized void close() {
        if (mcfDB != null)
            mcfDB.close();
        super.close();
    }

    public boolean untaggedBills(String TABLE_NAME) {
        mcfDB = this.getWritableDatabase();
        boolean isUploaded = false;
        try {
            ContentValues values = new ContentValues();
            values.put("isUploaded", "0");
            mcfDB.update(TABLE_NAME, values, null, null);
            isUploaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mcfDB.close();
        }
        return isUploaded;
    }



}
