package com.abhaybmicoc.app.model;

import java.io.Serializable;

public class ReportsPrintData implements Serializable {

    private String parameter;
    private String result;
    private String value;
    private String range;

    public ReportsPrintData(String parameter, String result, String value, String range) {
        this.parameter = parameter;
        this.result = result;
        this.value = value;
        this.range = range;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
}
