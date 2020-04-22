package com.abhaybmicoc.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Offline_Sync_Response {

    @SerializedName("data")
    @Expose
    private Offline_Sync_Response_Data data;

    @SerializedName("success")
    @Expose
    private Boolean success;

    public Offline_Sync_Response_Data getData() {
        return data;
    }

    public void setData(Offline_Sync_Response_Data data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public class Offline_Sync_Response_Data {

        @SerializedName("error")
        @Expose
        private Boolean error;
        @SerializedName("patient_ids")
        @Expose
        private List<String> patientIds = null;
        @SerializedName("parameter_ids")
        @Expose
        private List<String> parameterIds = null;
        @SerializedName("errorPatient_ids")
        @Expose
        private List<String> errorPatientIds = null;
        @SerializedName("errorParameter_ids")
        @Expose
        private List<String> errorParameterIds = null;

        public Boolean getError() {
            return error;
        }

        public void setError(Boolean error) {
            this.error = error;
        }

        public List<String> getPatientIds() {
            return patientIds;
        }

        public void setPatientIds(List<String> patientIds) {
            this.patientIds = patientIds;
        }

        public List<String> getParameterIds() {
            return parameterIds;
        }

        public void setParameterIds(List<String> parameterIds) {
            this.parameterIds = parameterIds;
        }

        public List<String> getErrorPatientIds() {
            return errorPatientIds;
        }

        public void setErrorPatientIds(List<String> errorPatientIds) {
            this.errorPatientIds = errorPatientIds;
        }

        public List<String> getErrorParameterIds() {
            return errorParameterIds;
        }

        public void setErrorParameterIds(List<String> errorParameterIds) {
            this.errorParameterIds = errorParameterIds;
        }
    }
}
