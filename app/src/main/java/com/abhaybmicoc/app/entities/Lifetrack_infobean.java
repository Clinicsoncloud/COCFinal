package com.abhaybmicoc.app.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Lifetrack_infobean implements Comparable, Parcelable, Cloneable {
    // region Variables

    private int keyidbp = 0;
    private int keyidweight = 0;
    private int keyidlifetrack = 0;
    private int thermometerkeyId = 0;

    private String date = "";
    private String time = "";
    private String cal = "0";
    private String steps = "0";
    private String pulse = "0";
    private String sleep = "0";
    private String weight = "";
    private String deviceId = "";
    private String isSynced = "";
    private String sleepUnit = "";
    private String distance = "0";
    private String systolic = "0";
    private String diastolic = "0";
    private String stepsUnits = "";
    private String heartRate = "0";
    private String readingType = "";
    private String weightUnit = "kg";
    private String pulseUnit = "bpm";
    private String calorieUnits = "";
    private String dateTimeStamp = "0";
    private String distanceUnit = "km";
    private String readingTakenTime = "";
    private String systolicUnit = "mmhg";
    private String distanceInMiles = "0";
    private String diastolicUnit = "mmhg";
    private String thermometerUnit = null;
    private String thermometerName = null;
    private String thermometerValue = null;
    private String irregularPulseDetection = "false";

    // endregion

    public Lifetrack_infobean() {
    }

    public Lifetrack_infobean(Parcel parcel) {
        readFromParcel(parcel);
    }


    public int getKeyidbp() {
        return keyidbp;
    }
    public int getKeyidweight() {
        return keyidweight;
    }

    public double getDistanceValue() { return Double.valueOf(distance.replaceAll(",", ".")); }
    public float getThermometer() { return Float.valueOf(thermometerValue.replaceAll(",", ".")); }

    public String getCal() {
        return cal;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getPulse() {
        return pulse;
    }
    public String getSleep() {
        return sleep;
    }
    public String getSteps() {
        return steps;
    }
    public String getWeight() {
        return weight;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public String getSystolic() {
        return systolic;
    }
    public String getDistance() {
        return distance;
    }
    public String getPulseUnit() {
        return pulseUnit;
    }
    public String getDiastolic() {
        return diastolic;
    }
    public String getHeartRate() {
        return heartRate;
    }
    public String getWeightUnit() {
        return weightUnit;
    }
    public String getStepsUnits() {
        return stepsUnits;
    }
    public String getSystolicUnit() {
        return systolicUnit;
    }
    public String getDistanceUnit() {
        return distanceUnit;
    }
    public String getCalorieUnits() {
        return calorieUnits;
    }
    public String getDateTimeStamp() {
        return dateTimeStamp;
    }
    public String getDiastolicUnit() {
        return diastolicUnit;
    }
    public String getDistanceInMiles() {
        return distanceInMiles;
    }
    public String getThermometerUnit() {
        return thermometerUnit;
    }
    public String getThermometerValue() {
        return thermometerValue;
    }
    public String getThermometerDeviceName() {
        return thermometerName;
    }
    public String getIrregularPulseDetection() {
        return irregularPulseDetection;
    }

    public void setCal(String cal) {
        this.cal = cal;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setPulse(String pulse) {
        this.pulse = pulse;
    }
    public void setSleep(String sleep) { this.sleep = sleep; }
    public void setSteps(String steps) {
        this.steps = steps;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public void setKeyidbp(int keyidbp) { this.keyidbp = keyidbp; }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public void setIsSynced(String isSynced) {
        this.isSynced = isSynced;
    }
    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public void setThermometerKeyId(int keyId) {
        thermometerkeyId = keyId;
    }
    public void setPulseUnit(String pulseUnit) {
        this.pulseUnit = pulseUnit;
    }
    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }
    public void setSleepUnit(String sleepUnit) {
        this.sleepUnit = sleepUnit;
    }
    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }
    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }
    public void setStepsUnits(String stepsUnits) {
        this.stepsUnits = stepsUnits;
    }
    public void setKeyidweight(int keyidweight) { this.keyidweight = keyidweight; }
    public void setReadingType(String readingType) {
        this.readingType = readingType;
    }
    public void setSystolicUnit(String systolicUnit) {
        this.systolicUnit = systolicUnit;
    }
    public void setCalorieUnits(String calorieUnits) {
        this.calorieUnits = calorieUnits;
    }
    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }
    public void setDateTimeStamp(String dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }
    public void setDiastolicUnit(String diastolicUnit) {
        this.diastolicUnit = diastolicUnit;
    }
    public void setKeyidLifeTrack(int keyidlifetrack) {
        this.keyidlifetrack = keyidlifetrack;
    }
    public void setDistanceInMiles(String distanceInMiles) { this.distanceInMiles = distanceInMiles; }
    public void setThermometerUnit(String thermometerUnit) { this.thermometerUnit = thermometerUnit; }
    public void setThermometerValue(String thermometerValue) { this.thermometerValue = thermometerValue; }
    public void setReadingTakenTime(String readingTakenTime) { this.readingTakenTime = readingTakenTime; }
    public void setThermometerDeviceName(String thermometerName) { this.thermometerName = thermometerName; }
    public void setIrregularPulseDetection(String irregularPulseDetection) { this.irregularPulseDetection = irregularPulseDetection; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cal);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(sleep);
        dest.writeString(steps);
        dest.writeString(pulse);
        dest.writeString(weight);
        dest.writeString(deviceId);
        dest.writeString(distance);
        dest.writeString(systolic);
        dest.writeString(isSynced);
        dest.writeString(diastolic);
        dest.writeString(pulseUnit);
        dest.writeString(sleepUnit);
        dest.writeString(weightUnit);
        dest.writeString(stepsUnits);
        dest.writeString(readingType);
        dest.writeString(distanceUnit);
        dest.writeString(calorieUnits);
        dest.writeString(systolicUnit);
        dest.writeString(diastolicUnit);
        dest.writeString(distanceInMiles);
        dest.writeString(thermometerUnit);
        dest.writeString(thermometerName);
        dest.writeString(thermometerValue);
        dest.writeString(readingTakenTime);

    }

    private void readFromParcel(Parcel in) {
        setCal(in.readString());
        setDate(in.readString());
        setTime(in.readString());
        setSteps(in.readString());
        setSleep(in.readString());
        setPulse(in.readString());
        setWeight(in.readString());
        setIsSynced(in.readString());
        setDistance(in.readString());
        setSystolic(in.readString());
        setDeviceId(in.readString());
        setSleepUnit(in.readString());
        setPulseUnit(in.readString());
        setDiastolic(in.readString());
        setWeightUnit(in.readString());
        setStepsUnits(in.readString());
        setReadingType(in.readString());
        setDistanceUnit(in.readString());
        setCalorieUnits(in.readString());
        setSystolicUnit(in.readString());
        setDiastolicUnit(in.readString());
        setDistanceInMiles(in.readString());
        setThermometerUnit(in.readString());
        setThermometerValue(in.readString());
        setReadingTakenTime(in.readString());
        setThermometerDeviceName(in.readString());
    }

    public static final Creator CREATOR = new Creator() {
        public Lifetrack_infobean createFromParcel(Parcel in) {
            return new Lifetrack_infobean(in);
        }

        public Lifetrack_infobean[] newArray(int size) {
            return new Lifetrack_infobean[size];
        }
    };

    @Override
    public String toString() {
        return getKeyidbp() + "" + getKeyidweight();
    }

    @Override
    public int compareTo(Object arg) {
        if (!(arg instanceof Lifetrack_infobean))
            throw new ClassCastException();

        Lifetrack_infobean e = (Lifetrack_infobean) arg;

        return date.compareTo(e.getDate());

    }

    @Override
    public int describeContents() {
        return 0;
    }
}
