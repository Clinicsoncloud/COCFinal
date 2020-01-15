package com.abhaybmicoctest.app.printer.esys.pridedemoapp;

public class Act_GenRowItem {
	private int iImageId;
    private String sTitle;
    private String sDesc;
     
    public Act_GenRowItem(String title, String desc) {
        this.sTitle = title;
        this.sDesc = desc;
    }
    public int getImageId() {
        return iImageId;
    }
    public void setImageId(int imageId) {
        this.iImageId = imageId;
    }
    public String getDesc() {
        return sDesc;
    }
    public void setDesc(String desc) {
        this.sDesc = desc;
    }
    public String getTitle() {
        return sTitle;
    }
    public void setTitle(String title) {
        this.sTitle = title;
    }
    @Override
    public String toString() {
        return sTitle + "\n" + sDesc;
    }   
}
