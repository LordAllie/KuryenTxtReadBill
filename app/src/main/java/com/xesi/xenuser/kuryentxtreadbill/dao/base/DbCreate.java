package com.xesi.xenuser.kuryentxtreadbill.dao.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DbCreate extends BaseDAO {

    public DbCreate(Context context) {
        super(context, DB_NAME, null, DB_VERSION);// 1? its Database Version
        DB_PATH = context.getDatabasePath(DB_NAME).toString();
        this.mContext = context;
        prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

    }

    public void createDatabase() {
        initializeDatabase();
    }

    public void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }

    public void initializeDatabase() {

        /* NOTE: COMMENT THIS CODE WHEN UPDATING DB (DROP/ADD TABLE OR ADD COLUMN)
        *  TO PREVENT OF LOOSING THE EXISTING DATA
        *  - USE onUpgrade() method instead */

        /* Starts here: Check if db version is change - delete db and create a new one. (All data will be loss) */

        if (!checkDataBase()) //database does't exist yet.
        {
            this.getReadableDatabase();
            try {
                createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                Log.w(DB_NAME, e.getMessage());
            }
        }

        try {
            openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.w(DB_NAME, e.getMessage());
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        File databasePath = mContext.getDatabasePath(this.DB_NAME);
        return databasePath.exists();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        mcfDB = SQLiteDatabase.openDatabase(this.DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
    }


    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring ByteStream.
     */
    private void createDataBase() throws IOException {
        String parentPath = mContext.getDatabasePath(DB_NAME).getParent();
        File file = new File(parentPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.w(DB_NAME, "Unable to create database directory");
                return;
            }
        }

        InputStream myInput = null;
        OutputStream myOutput = null;

        try {
            //Open your local DB as the input stream
            myInput = mContext.getAssets().open(this.DB_NAME);
            //Open the empty db as the output stream
            myOutput = new FileOutputStream(this.DB_PATH);
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            //Close the streams
            myOutput.flush();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SP_KEY_DB_VER, DB_VERSION);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                try {
                    myInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (myOutput != null) {
                try {
                    myOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}