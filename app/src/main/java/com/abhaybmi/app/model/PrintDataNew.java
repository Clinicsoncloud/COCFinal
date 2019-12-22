package com.abhaybmi.app.model;

import java.io.Serializable;

public class PrintDataNew implements Serializable {

    private String parameter;
    private double curr_value;

    public PrintDataNew(String parameter, double curr_value) {
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
