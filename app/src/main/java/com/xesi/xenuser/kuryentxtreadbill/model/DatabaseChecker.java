package com.xesi.xenuser.kuryentxtreadbill.model;

/**
 * Created by Daryll Sabate on 4/2/2018.
 */
public class DatabaseChecker {


    private int databaseVersion;
    private String updateQuery;
    public DatabaseChecker(){}
    public DatabaseChecker(int databaseVersion, String updateQuery) {
        this.databaseVersion = databaseVersion;
        this.updateQuery = updateQuery;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getUpdateQuery() {
        return updateQuery;
    }

    public void setUpdateQuery(String updateQuery) {
        this.updateQuery = updateQuery;
    }
}
