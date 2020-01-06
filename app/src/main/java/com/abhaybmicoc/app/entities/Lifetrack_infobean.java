package com.abhaybmicoc.app.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Lifetrack_infobean implements Comparable, Parcelable, Cloneable {

    private String date = "";
    private String time = "";
    private String dateTimeStamp = "0";
    private String deviceId = "";

    private String readingType = "";
    private String readingTakenTime = "";
    private String isSynced = "";
    String itemList;
    private boolean isVisible;

    private int keyidbp = 0; // DBのデータキーIDの格納場所
    private String pulse = "0";
    private String systolic = "0";
    private String diastolic = "0";
    private String pulseUnit = "bpm";
    private String systolicUnit = "mmhg";
    private String diastolicUnit = "mmhg";
    private String irregularPulseDetection = "false";

    private int keyidweight = 0; // DBのデータキーIDの格納場所
    private String weight = "";
    private String weightUnit = "kg";

    private int keyidlifetrack = 0; // DBのデータキーIDの格納場所
    private String steps = "0";
    private String cal = "0";
    private String distance = "0";
    private String sleep = "0";
    private String distanceUnit = "km";
    private String stepsUnits = "";
    private String calorieUnits = "";
    private String sleepUnit = "";
    private String distanceInMiles = "0";
    private String heartRate = "0";
    private String heartRateUnit = "";

    private int thermometerkeyId = 0;
    private String thermometerValue = null;
    private String thermometerUnit = null;
    private String thermometerName = null;

    public Lifetrack_infobean() {
    }

    public Lifetrack_infobean(Parcel in) {
        readFromParcel(in);
    }

    public void setReadingType(String readingType) {
        this.readingType = readingType;
    }

    public void setReadingTakenTime(String readingTakenTime) {
        this.readingTakenTime = readingTakenTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(String dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public void setIsSynced(String isSynced) {
        this.isSynced = isSynced;
    }

    public int getKeyidbp() {
        return keyidbp;
    }

    public void setKeyidbp(int keyidbp) {
        this.keyidbp = keyidbp;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getPulseUnit() {
        return pulseUnit;
    }

    public void setPulseUnit(String pulseUnit) {
        this.pulseUnit = pulseUnit;
    }

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getSystolicUnit() {
        return systolicUnit;
    }

    public void setSystolicUnit(String systolicUnit) {
        this.systolicUnit = systolicUnit;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getDiastolicUnit() {
        return diastolicUnit;
    }

    public void setDiastolicUnit(String diastolicUnit) {
        this.diastolicUnit = diastolicUnit;
    }

    public String getIrregularPulseDetection() {
        return irregularPulseDetection;
    }

    public void setIrregularPulseDetection(String irregularPulseDetection) {
        this.irregularPulseDetection = irregularPulseDetection;
    }

    public int getKeyidweight() {
        return keyidweight;
    }

    public void setKeyidweight(int keyidweight) {
        this.keyidweight = keyidweight;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }


    public void setKeyidLifeTrack(int keyidlifetrack) {
        this.keyidlifetrack = keyidlifetrack;
    }


    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getSleep() {
        return sleep;
    }

    public void setSleep(String sleep) {
        this.sleep = sleep;
    }

    public void setSleepUnit(String sleepUnit) {
        this.sleepUnit = sleepUnit;
    }

    public String getDistance() {
        return distance;
    }

    public double getDistanceValue() {
        String value = distance.replaceAll(",", ".");
        return Double.valueOf(value);
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistanceInMiles() {
        return distanceInMiles;
    }

    public void setDistanceInMiles(String distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getStepsUnits() {
        return stepsUnits;
    }

    public void setStepsUnits(String stepsUnits) {
        this.stepsUnits = stepsUnits;
    }

    public String getCal() {
        return cal;
    }

    public void setCal(String cal) {
        this.cal = cal;
    }

    public String getCalorieUnits() {
        return calorieUnits;
    }

    public void setCalorieUnits(String calorieUnits) {
        this.calorieUnits = calorieUnits;
    }

    public void setThermometerKeyId(int keyId) {
        thermometerkeyId = keyId;
    }
    public String getThermometerValue() {
        return thermometerValue;
    }

    public float getThermometer() {
        String value = thermometerValue.replaceAll(",", ".");

        return Float.valueOf(value);
    }
    public void setThermometerValue(String thermometerValue) {
        this.thermometerValue = thermometerValue;
    }

    public String getThermometerUnit() {
        return thermometerUnit;
    }

    public void setThermometerUnit(String thermometerUnit) {
        this.thermometerUnit = thermometerUnit;
    }

    public String getThermometerDeviceName() {
        return thermometerName;
    }

    public void setThermometerDeviceName(String thermometerName) {
        this.thermometerName = thermometerName;
    }

    @Override
    public String toString() {
        return getKeyidbp() + "" + getKeyidweight();
    }

    @Override
    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub

        if (!(arg0 instanceof Lifetrack_infobean))
            throw new ClassCastException();

        Lifetrack_infobean e = (Lifetrack_infobean) arg0;

        return date.compareTo(e.getDate());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(weight);
        dest.writeString(readingType);
        dest.writeString(readingTakenTime);
        dest.writeString(deviceId);
        dest.writeString(weightUnit);
        dest.writeString(steps);
        dest.writeString(cal);
        dest.writeString(distance);

        dest.writeString(sleep);
        dest.writeString(distanceUnit);
        dest.writeString(stepsUnits);
        dest.writeString(calorieUnits);
        dest.writeString(pulse);
        dest.writeString(systolic);
        dest.writeString(diastolic);

        dest.writeString(pulseUnit);
        dest.writeString(systolicUnit);
        dest.writeString(diastolicUnit);
        dest.writeString(sleepUnit);
        dest.writeString(isSynced);
        dest.writeString(distanceInMiles);

        dest.writeString(thermometerValue);
        dest.writeString(thermometerUnit);
        dest.writeString(thermometerName);

    }

    private void readFromParcel(Parcel in) {
        setDate(in.readString());
        setTime(in.readString());
        setWeight(in.readString());
        setReadingType(in.readString());
        setReadingTakenTime(in.readString());
        setDeviceId(in.readString());

        setWeightUnit(in.readString());
        setSteps(in.readString());
        setCal(in.readString());
        setDistance(in.readString());

        setSleep(in.readString());
        setDistanceUnit(in.readString());
        setStepsUnits(in.readString());
        setCalorieUnits(in.readString());
        setPulse(in.readString());
        setSystolic(in.readString());
        setDiastolic(in.readString());

        setPulseUnit(in.readString());
        setSystolicUnit(in.readString());
        setDiastolicUnit(in.readString());
        setSleepUnit(in.readString());
        setIsSynced(in.readString());
        setDistanceInMiles(in.readString());

        setThermometerValue(in.readString());
        setThermometerUnit(in.readString());
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
}
