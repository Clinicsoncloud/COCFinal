package com.abhaybmicoc.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clinic_Data_Info {

    @SerializedName("location_id")
    @Expose
    private String locationId;
    @SerializedName("assigned_user_id")
    @Expose
    private Object assignedUserId;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("gmail_id")
    @Expose
    private Object gmailId;
    @SerializedName("actofit_id")
    @Expose
    private Object actofitId;
    @SerializedName("image_file")
    @Expose
    private Object imageFile;
    @SerializedName("app_version")
    @Expose
    private String appVersion;
    @SerializedName("installed_by")
    @Expose
    private String installedBy;
    @SerializedName("gmail_password")
    @Expose
    private Object gmailPassword;
    @SerializedName("actofit_password")
    @Expose
    private Object actofitPassword;
    @SerializedName("installation_step")
    @Expose
    private String installationStep;
    @SerializedName("machine_operator_name")
    @Expose
    private String machineOperatorName;
    @SerializedName("machine_operator_mobile_number")
    @Expose
    private String machineOperatorMobileNumber;
    @SerializedName("last_sync_date")
    @Expose
    private Object lastSyncDate;
    @SerializedName("actofit_end_date")
    @Expose
    private Object actofitEndDate;
    @SerializedName("installation_date")
    @Expose
    private String installationDate;
    @SerializedName("license_expiry_date")
    @Expose
    private Object licenseExpiryDate;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("is_trial_mode")
    @Expose
    private int isTrialMode;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("deleted_by")
    @Expose
    private Object deletedBy;
    @SerializedName("updated_by")
    @Expose
    private String updatedBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("total_tests_done")
    @Expose
    private Integer totalTestsDone;
    @SerializedName("allowed_trial_tests")
    @Expose
    private Integer allowedTrialTests;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Object getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Object assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getGmailId() {
        return gmailId;
    }

    public void setGmailId(Object gmailId) {
        this.gmailId = gmailId;
    }

    public Object getActofitId() {
        return actofitId;
    }

    public void setActofitId(Object actofitId) {
        this.actofitId = actofitId;
    }

    public Object getImageFile() {
        return imageFile;
    }

    public void setImageFile(Object imageFile) {
        this.imageFile = imageFile;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getInstalledBy() {
        return installedBy;
    }

    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }

    public Object getGmailPassword() {
        return gmailPassword;
    }

    public void setGmailPassword(Object gmailPassword) {
        this.gmailPassword = gmailPassword;
    }

    public Object getActofitPassword() {
        return actofitPassword;
    }

    public void setActofitPassword(Object actofitPassword) {
        this.actofitPassword = actofitPassword;
    }

    public String getInstallationStep() {
        return installationStep;
    }

    public void setInstallationStep(String installationStep) {
        this.installationStep = installationStep;
    }

    public String getMachineOperatorName() {
        return machineOperatorName;
    }

    public void setMachineOperatorName(String machineOperatorName) {
        this.machineOperatorName = machineOperatorName;
    }

    public String getMachineOperatorMobileNumber() {
        return machineOperatorMobileNumber;
    }

    public void setMachineOperatorMobileNumber(String machineOperatorMobileNumber) {
        this.machineOperatorMobileNumber = machineOperatorMobileNumber;
    }

    public Object getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Object lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public Object getActofitEndDate() {
        return actofitEndDate;
    }

    public void setActofitEndDate(Object actofitEndDate) {
        this.actofitEndDate = actofitEndDate;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public Object getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(Object licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getIsTrialMode() {
        return isTrialMode;
    }

    public void setIsTrialMode(int isTrialMode) {
        this.isTrialMode = isTrialMode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Object getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Object deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTotalTestsDone() {
        return totalTestsDone;
    }

    public void setTotalTestsDone(Integer totalTestsDone) {
        this.totalTestsDone = totalTestsDone;
    }

    public Integer getAllowedTrialTests() {
        return allowedTrialTests;
    }

    public void setAllowedTrialTests(Integer allowedTrialTests) {
        this.allowedTrialTests = allowedTrialTests;
    }
}
