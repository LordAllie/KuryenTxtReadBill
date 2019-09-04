package com.xesi.xenuser.kuryentxtreadbill.model.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xenuser on 4/20/2017.
 */
public class DUProperty {
    @SerializedName("propertyName")
    @Expose
    private long id;
    @SerializedName("propertyName")
    @Expose
    private String propertyName;
    @SerializedName("propertyValue")
    @Expose
    private String propertyValue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
