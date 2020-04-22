package com.abhaybmicoc.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Visit_Upload_Response {

    @SerializedName("data")
    @Expose
    private Visit_Upload_Response_Data data;

    @SerializedName("success")
    @Expose
    private Boolean success;

    public Visit_Upload_Response_Data getData() {
        return data;
    }

    public void setData(Visit_Upload_Response_Data data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class Visit_Upload_Response_Data {

        @SerializedName("patient")
        @Expose
        private Patient_Data patient;

        @SerializedName("parameter")
        @Expose
        private Parameter_Data parameter;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("file")
        @Expose
        private String file;

        public Patient_Data getPatient() {
            return patient;
        }

        public void setPatient(Patient_Data patient) {
            this.patient = patient;
        }

        public Parameter_Data getParameter() {
            return parameter;
        }

        public void setParameter(Parameter_Data parameter) {
            this.parameter = parameter;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }
}
