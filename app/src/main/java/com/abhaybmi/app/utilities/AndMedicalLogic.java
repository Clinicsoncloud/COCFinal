package com.abhaybmi.app.utilities;

public class AndMedicalLogic {

	// For BP
	// Having Same Value in All Blood Pressure Classes
	public static boolean checkBPError(String systolic, String diastolic,
			String pulse) {

		if (systolic.equalsIgnoreCase(diastolic)
				&& diastolic.equalsIgnoreCase(pulse)) {
			return true;

		}

		return false;

	}

}
