package com.abhaybmicoc.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parameter_Data {

    @SerializedName("clinic_id")
    @Expose
    private String clinicId;
    @SerializedName("patient_id")
    @Expose
    private String patientId;
    @SerializedName("bmi")
    @Expose
    private String bmi;
    @SerializedName("bmr")
    @Expose
    private String bmr;
    @SerializedName("pulse")
    @Expose
    private String pulse;
    @SerializedName("sugar")
    @Expose
    private String sugar;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("oxygen")
    @Expose
    private String oxygen;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("protein")
    @Expose
    private String protein;
    @SerializedName("body_fat")
    @Expose
    private String bodyFat;
    @SerializedName("meta_age")
    @Expose
    private String metaAge;
    @SerializedName("physique")
    @Expose
    private String physique;
    @SerializedName("bone_mass")
    @Expose
    private String boneMass;
    @SerializedName("dialostic")
    @Expose
    private String dialostic;
    @SerializedName("body_water")
    @Expose
    private String bodyWater;
    @SerializedName("hemoglobin")
    @Expose
    private String hemoglobin;
    @SerializedName("muscle_mass")
    @Expose
    private String muscleMass;
    @SerializedName("temperature")
    @Expose
    private String temperature;
    @SerializedName("health_score")
    @Expose
    private String healthScore;
    @SerializedName("visceral_fat")
    @Expose
    private String visceralFat;
    @SerializedName("subcutaneous")
    @Expose
    private String subcutaneous;
    @SerializedName("blood_pressure")
    @Expose
    private String bloodPressure;
    @SerializedName("skeleton_muscle")
    @Expose
    private String skeletonMuscle;
    @SerializedName("fat_free_weight")
    @Expose
    private String fatFreeWeight;
    @SerializedName("eye_left_vision")
    @Expose
    private String eyeLeftVision;
    @SerializedName("eye_right_vision")
    @Expose
    private String eyeRightVision;
    @SerializedName("bmirange")
    @Expose
    private String bmirange;
    @SerializedName("bmrrange")
    @Expose
    private String bmrrange;
    @SerializedName("eyerange")
    @Expose
    private String eyerange;
    @SerializedName("pulserange")
    @Expose
    private String pulserange;
    @SerializedName("subfatrange")
    @Expose
    private String subfatrange;
    @SerializedName("weightrange")
    @Expose
    private String weightrange;
    @SerializedName("proteinrange")
    @Expose
    private String proteinrange;
    @SerializedName("bodyfatrange")
    @Expose
    private String bodyfatrange;
    @SerializedName("metaagerange")
    @Expose
    private String metaagerange;
    @SerializedName("fatfreerange")
    @Expose
    private Object fatfreerange;
    @SerializedName("bodytemprange")
    @Expose
    private String bodytemprange;
    @SerializedName("bonemassrange")
    @Expose
    private Object bonemassrange;
    @SerializedName("physiquerange")
    @Expose
    private Object physiquerange;
    @SerializedName("systolicrange")
    @Expose
    private String systolicrange;
    @SerializedName("bodywaterrange")
    @Expose
    private String bodywaterrange;
    @SerializedName("dialosticrange")
    @Expose
    private String dialosticrange;
    @SerializedName("bloodsugarrange")
    @Expose
    private String bloodsugarrange;
    @SerializedName("hemoglobinrange")
    @Expose
    private String hemoglobinrange;
    @SerializedName("musclemassrange")
    @Expose
    private String musclemassrange;
    @SerializedName("healthscorerange")
    @Expose
    private Object healthscorerange;
    @SerializedName("visceralfatrange")
    @Expose
    private String visceralfatrange;
    @SerializedName("pulseoximeterrange")
    @Expose
    private String pulseoximeterrange;
    @SerializedName("skeletanmusclerange")
    @Expose
    private String skeletanmusclerange;
    @SerializedName("bmiresult")
    @Expose
    private String bmiresult;
    @SerializedName("bmrresult")
    @Expose
    private String bmrresult;
    @SerializedName("pulseresult")
    @Expose
    private String pulseresult;
    @SerializedName("sugarresult")
    @Expose
    private String sugarresult;
    @SerializedName("heightresult")
    @Expose
    private Object heightresult;
    @SerializedName("oxygenresult")
    @Expose
    private String oxygenresult;
    @SerializedName("weightresult")
    @Expose
    private String weightresult;
    @SerializedName("bodyfatresult")
    @Expose
    private String bodyfatresult;
    @SerializedName("eyeleftresult")
    @Expose
    private String eyeleftresult;
    @SerializedName("metaageresult")
    @Expose
    private String metaageresult;
    @SerializedName("proteinresult")
    @Expose
    private String proteinresult;
    @SerializedName("bonemassresult")
    @Expose
    private String bonemassresult;
    @SerializedName("eyerightresult")
    @Expose
    private String eyerightresult;
    @SerializedName("systolicresult")
    @Expose
    private String systolicresult;
    @SerializedName("bodywaterresult")
    @Expose
    private String bodywaterresult;
    @SerializedName("hemoglobinresult")
    @Expose
    private String hemoglobinresult;
    @SerializedName("musclemassresult")
    @Expose
    private String musclemassresult;
    @SerializedName("temperatureresult")
    @Expose
    private String temperatureresult;
    @SerializedName("visceralfatresult")
    @Expose
    private String visceralfatresult;
    @SerializedName("subcutaneousresult")
    @Expose
    private String subcutaneousresult;
    @SerializedName("bloodpressureresult")
    @Expose
    private String bloodpressureresult;
    @SerializedName("fatfreeweightresult")
    @Expose
    private Object fatfreeweightresult;
    @SerializedName("skeletonmuscleresult")
    @Expose
    private String skeletonmuscleresult;
    @SerializedName("feedback")
    @Expose
    private Integer feedback;
    @SerializedName("app_version")
    @Expose
    private String appVersion;
    @SerializedName("is_sms_sent")
    @Expose
    private Object isSmsSent;
    @SerializedName("is_email_sent")
    @Expose
    private Object isEmailSent;
    @SerializedName("is_pdf_created")
    @Expose
    private Object isPdfCreated;
    @SerializedName("created_by")
    @Expose
    private Object createdBy;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;
    @SerializedName("deleted_by")
    @Expose
    private Object deletedBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("id")
    @Expose
    private String id;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getBmr() {
        return bmr;
    }

    public void setBmr(String bmr) {
        this.bmr = bmr;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getOxygen() {
        return oxygen;
    }

    public void setOxygen(String oxygen) {
        this.oxygen = oxygen;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(String bodyFat) {
        this.bodyFat = bodyFat;
    }

    public String getMetaAge() {
        return metaAge;
    }

    public void setMetaAge(String metaAge) {
        this.metaAge = metaAge;
    }

    public String getPhysique() {
        return physique;
    }

    public void setPhysique(String physique) {
        this.physique = physique;
    }

    public String getBoneMass() {
        return boneMass;
    }

    public void setBoneMass(String boneMass) {
        this.boneMass = boneMass;
    }

    public String getDialostic() {
        return dialostic;
    }

    public void setDialostic(String dialostic) {
        this.dialostic = dialostic;
    }

    public String getBodyWater() {
        return bodyWater;
    }

    public void setBodyWater(String bodyWater) {
        this.bodyWater = bodyWater;
    }

    public String getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(String hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public String getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(String muscleMass) {
        this.muscleMass = muscleMass;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(String healthScore) {
        this.healthScore = healthScore;
    }

    public String getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(String visceralFat) {
        this.visceralFat = visceralFat;
    }

    public String getSubcutaneous() {
        return subcutaneous;
    }

    public void setSubcutaneous(String subcutaneous) {
        this.subcutaneous = subcutaneous;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getSkeletonMuscle() {
        return skeletonMuscle;
    }

    public void setSkeletonMuscle(String skeletonMuscle) {
        this.skeletonMuscle = skeletonMuscle;
    }

    public String getFatFreeWeight() {
        return fatFreeWeight;
    }

    public void setFatFreeWeight(String fatFreeWeight) {
        this.fatFreeWeight = fatFreeWeight;
    }

    public String getEyeLeftVision() {
        return eyeLeftVision;
    }

    public void setEyeLeftVision(String eyeLeftVision) {
        this.eyeLeftVision = eyeLeftVision;
    }

    public String getEyeRightVision() {
        return eyeRightVision;
    }

    public void setEyeRightVision(String eyeRightVision) {
        this.eyeRightVision = eyeRightVision;
    }

    public String getBmirange() {
        return bmirange;
    }

    public void setBmirange(String bmirange) {
        this.bmirange = bmirange;
    }

    public String getBmrrange() {
        return bmrrange;
    }

    public void setBmrrange(String bmrrange) {
        this.bmrrange = bmrrange;
    }

    public String getEyerange() {
        return eyerange;
    }

    public void setEyerange(String eyerange) {
        this.eyerange = eyerange;
    }

    public String getPulserange() {
        return pulserange;
    }

    public void setPulserange(String pulserange) {
        this.pulserange = pulserange;
    }

    public String getSubfatrange() {
        return subfatrange;
    }

    public void setSubfatrange(String subfatrange) {
        this.subfatrange = subfatrange;
    }

    public String getWeightrange() {
        return weightrange;
    }

    public void setWeightrange(String weightrange) {
        this.weightrange = weightrange;
    }

    public String getProteinrange() {
        return proteinrange;
    }

    public void setProteinrange(String proteinrange) {
        this.proteinrange = proteinrange;
    }

    public String getBodyfatrange() {
        return bodyfatrange;
    }

    public void setBodyfatrange(String bodyfatrange) {
        this.bodyfatrange = bodyfatrange;
    }

    public String getMetaagerange() {
        return metaagerange;
    }

    public void setMetaagerange(String metaagerange) {
        this.metaagerange = metaagerange;
    }

    public Object getFatfreerange() {
        return fatfreerange;
    }

    public void setFatfreerange(Object fatfreerange) {
        this.fatfreerange = fatfreerange;
    }

    public String getBodytemprange() {
        return bodytemprange;
    }

    public void setBodytemprange(String bodytemprange) {
        this.bodytemprange = bodytemprange;
    }

    public Object getBonemassrange() {
        return bonemassrange;
    }

    public void setBonemassrange(Object bonemassrange) {
        this.bonemassrange = bonemassrange;
    }

    public Object getPhysiquerange() {
        return physiquerange;
    }

    public void setPhysiquerange(Object physiquerange) {
        this.physiquerange = physiquerange;
    }

    public String getSystolicrange() {
        return systolicrange;
    }

    public void setSystolicrange(String systolicrange) {
        this.systolicrange = systolicrange;
    }

    public String getBodywaterrange() {
        return bodywaterrange;
    }

    public void setBodywaterrange(String bodywaterrange) {
        this.bodywaterrange = bodywaterrange;
    }

    public String getDialosticrange() {
        return dialosticrange;
    }

    public void setDialosticrange(String dialosticrange) {
        this.dialosticrange = dialosticrange;
    }

    public String getBloodsugarrange() {
        return bloodsugarrange;
    }

    public void setBloodsugarrange(String bloodsugarrange) {
        this.bloodsugarrange = bloodsugarrange;
    }

    public String getHemoglobinrange() {
        return hemoglobinrange;
    }

    public void setHemoglobinrange(String hemoglobinrange) {
        this.hemoglobinrange = hemoglobinrange;
    }

    public String getMusclemassrange() {
        return musclemassrange;
    }

    public void setMusclemassrange(String musclemassrange) {
        this.musclemassrange = musclemassrange;
    }

    public Object getHealthscorerange() {
        return healthscorerange;
    }

    public void setHealthscorerange(Object healthscorerange) {
        this.healthscorerange = healthscorerange;
    }

    public String getVisceralfatrange() {
        return visceralfatrange;
    }

    public void setVisceralfatrange(String visceralfatrange) {
        this.visceralfatrange = visceralfatrange;
    }

    public String getPulseoximeterrange() {
        return pulseoximeterrange;
    }

    public void setPulseoximeterrange(String pulseoximeterrange) {
        this.pulseoximeterrange = pulseoximeterrange;
    }

    public String getSkeletanmusclerange() {
        return skeletanmusclerange;
    }

    public void setSkeletanmusclerange(String skeletanmusclerange) {
        this.skeletanmusclerange = skeletanmusclerange;
    }

    public String getBmiresult() {
        return bmiresult;
    }

    public void setBmiresult(String bmiresult) {
        this.bmiresult = bmiresult;
    }

    public String getBmrresult() {
        return bmrresult;
    }

    public void setBmrresult(String bmrresult) {
        this.bmrresult = bmrresult;
    }

    public String getPulseresult() {
        return pulseresult;
    }

    public void setPulseresult(String pulseresult) {
        this.pulseresult = pulseresult;
    }

    public String getSugarresult() {
        return sugarresult;
    }

    public void setSugarresult(String sugarresult) {
        this.sugarresult = sugarresult;
    }

    public Object getHeightresult() {
        return heightresult;
    }

    public void setHeightresult(Object heightresult) {
        this.heightresult = heightresult;
    }

    public String getOxygenresult() {
        return oxygenresult;
    }

    public void setOxygenresult(String oxygenresult) {
        this.oxygenresult = oxygenresult;
    }

    public String getWeightresult() {
        return weightresult;
    }

    public void setWeightresult(String weightresult) {
        this.weightresult = weightresult;
    }

    public String getBodyfatresult() {
        return bodyfatresult;
    }

    public void setBodyfatresult(String bodyfatresult) {
        this.bodyfatresult = bodyfatresult;
    }

    public String getEyeleftresult() {
        return eyeleftresult;
    }

    public void setEyeleftresult(String eyeleftresult) {
        this.eyeleftresult = eyeleftresult;
    }

    public String getMetaageresult() {
        return metaageresult;
    }

    public void setMetaageresult(String metaageresult) {
        this.metaageresult = metaageresult;
    }

    public String getProteinresult() {
        return proteinresult;
    }

    public void setProteinresult(String proteinresult) {
        this.proteinresult = proteinresult;
    }

    public String getBonemassresult() {
        return bonemassresult;
    }

    public void setBonemassresult(String bonemassresult) {
        this.bonemassresult = bonemassresult;
    }

    public String getEyerightresult() {
        return eyerightresult;
    }

    public void setEyerightresult(String eyerightresult) {
        this.eyerightresult = eyerightresult;
    }

    public String getSystolicresult() {
        return systolicresult;
    }

    public void setSystolicresult(String systolicresult) {
        this.systolicresult = systolicresult;
    }

    public String getBodywaterresult() {
        return bodywaterresult;
    }

    public void setBodywaterresult(String bodywaterresult) {
        this.bodywaterresult = bodywaterresult;
    }

    public String getHemoglobinresult() {
        return hemoglobinresult;
    }

    public void setHemoglobinresult(String hemoglobinresult) {
        this.hemoglobinresult = hemoglobinresult;
    }

    public String getMusclemassresult() {
        return musclemassresult;
    }

    public void setMusclemassresult(String musclemassresult) {
        this.musclemassresult = musclemassresult;
    }

    public String getTemperatureresult() {
        return temperatureresult;
    }

    public void setTemperatureresult(String temperatureresult) {
        this.temperatureresult = temperatureresult;
    }

    public String getVisceralfatresult() {
        return visceralfatresult;
    }

    public void setVisceralfatresult(String visceralfatresult) {
        this.visceralfatresult = visceralfatresult;
    }

    public String getSubcutaneousresult() {
        return subcutaneousresult;
    }

    public void setSubcutaneousresult(String subcutaneousresult) {
        this.subcutaneousresult = subcutaneousresult;
    }

    public String getBloodpressureresult() {
        return bloodpressureresult;
    }

    public void setBloodpressureresult(String bloodpressureresult) {
        this.bloodpressureresult = bloodpressureresult;
    }

    public Object getFatfreeweightresult() {
        return fatfreeweightresult;
    }

    public void setFatfreeweightresult(Object fatfreeweightresult) {
        this.fatfreeweightresult = fatfreeweightresult;
    }

    public String getSkeletonmuscleresult() {
        return skeletonmuscleresult;
    }

    public void setSkeletonmuscleresult(String skeletonmuscleresult) {
        this.skeletonmuscleresult = skeletonmuscleresult;
    }

    public Integer getFeedback() {
        return feedback;
    }

    public void setFeedback(Integer feedback) {
        this.feedback = feedback;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Object getIsSmsSent() {
        return isSmsSent;
    }

    public void setIsSmsSent(Object isSmsSent) {
        this.isSmsSent = isSmsSent;
    }

    public Object getIsEmailSent() {
        return isEmailSent;
    }

    public void setIsEmailSent(Object isEmailSent) {
        this.isEmailSent = isEmailSent;
    }

    public Object getIsPdfCreated() {
        return isPdfCreated;
    }

    public void setIsPdfCreated(Object isPdfCreated) {
        this.isPdfCreated = isPdfCreated;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Object deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
