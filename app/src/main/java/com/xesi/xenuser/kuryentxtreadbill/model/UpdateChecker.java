package com.xesi.xenuser.kuryentxtreadbill.model;

/**
 * Created by Daryll Sabate on 4/2/2018.
 */
public class UpdateChecker {
    private String latestVersion;
    private int latestVersionCode;
    private String url;

    public UpdateChecker() {
    }

    public UpdateChecker(String latestVersion, int latestVersionCode,
                         String url) {
        this.latestVersion = latestVersion;
        this.latestVersionCode = latestVersionCode;
        this.url = url;

    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public int getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(int latestVersionCode) {
        this.latestVersionCode = latestVersionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
