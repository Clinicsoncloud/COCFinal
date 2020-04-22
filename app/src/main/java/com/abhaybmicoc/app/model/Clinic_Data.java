package com.abhaybmicoc.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clinic_Data {

    @SerializedName("data")
    @Expose
    private Clinic_Data_Info data;

    @SerializedName("found")
    @Expose
    private Boolean found;

    public Clinic_Data_Info getData() {
        return data;
    }

    public void setData(Clinic_Data_Info data) {
        this.data = data;
    }

    public Boolean getFound() {
        return found;
    }

    public void setFound(Boolean found) {
        this.found = found;
    }



}
