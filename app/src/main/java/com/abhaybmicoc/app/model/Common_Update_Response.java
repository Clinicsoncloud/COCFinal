package com.abhaybmicoc.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Common_Update_Response {

    @SerializedName("success")
    @Expose
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
