package com.abhaybmicoc.app.model;

import java.io.Serializable;

public class PrintData implements Serializable {

    private String parameter;
    private double curr_value;

    public PrintData(String parameter, double curr_value) {
        this.parameter = parameter;
        this.curr_value = curr_value;
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
}
