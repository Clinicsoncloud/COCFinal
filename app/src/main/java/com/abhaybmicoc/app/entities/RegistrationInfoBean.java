package com.abhaybmicoc.app.entities;

public class RegistrationInfoBean {

	private String userHeight;
	private String userHeightunit;
	private String userDateBirth;
	private String uwTrackerName;


	public String getUserHeight() {
		return userHeight;
	}

	public void setUserHeight(String userHeight) {
		this.userHeight = userHeight;
	}

	public String getUserHeightUnit() {
		return userHeightunit;
	}

	public void setUserHeightUnit(String userHeightunit) {
		this.userHeightunit = userHeightunit;
	}

	public String getUserDateBirth() {
		return userDateBirth;
	}

	public void setUserDateBirth(String userDateBirth) {
		this.userDateBirth = userDateBirth;
	}

	public void setUWTrackerName(String name) {this.uwTrackerName = name;}
	public String getUWTrackerName() {
		return uwTrackerName;
	}
}
