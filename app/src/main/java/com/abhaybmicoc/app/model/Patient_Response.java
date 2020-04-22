package com.abhaybmicoc.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Patient_Response {

    @SerializedName("data")
    @Expose
    private Patient_Data data;

    @SerializedName("found")
    @Expose
    private Boolean found;

    public Patient_Data getData() {
        return data;
    }

    public void setData(Patient_Data data) {
        this.data = data;
    }

    public Boolean getFound() {
        return found;
    }

    public void setFound(Boolean found) {
        this.found = found;
    }
}
