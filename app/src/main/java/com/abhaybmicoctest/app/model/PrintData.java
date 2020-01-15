package com.abhaybmicoctest.app.model;

import java.io.Serializable;

public class PrintData implements Serializable {

    private String parameter;
    private double curr_value;
    private double minRange;
    private double maxRange;
    private String unit;

    public PrintData(String parameter, double curr_value, double minRange, double maxRange, String unit) {
        this.parameter = parameter;
        this.curr_value = curr_value;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.unit = unit;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public double getCurr_value() {
        return curr_value;
    }

    public void setCurr_value(double curr_value) {
        this.curr_value = curr_value;
    }

    public double getMinRange() {
        return minRange;
    }

    public void setMinRange(double minRange) {
        this.minRange = minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
