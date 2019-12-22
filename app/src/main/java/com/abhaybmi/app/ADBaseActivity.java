package com.abhaybmi.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.abhaybmi.app.slidemenu.SlideMenu;
import com.abhaybmi.app.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;

public abstract class ADBaseActivity extends Activity implements
		OnSlideMenuItemClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSlideMenuItemClick(int itemId) {
		switch (itemId) {
		case 1:
			sliderScreens(1, this);
			break;
		case 2:
			sliderScreens(2, this);
			break;
		case 3:
			sliderScreens(3, this);
			break;
		case 4:
			sliderScreens(4, this);
			break;
		case 5:
			sliderScreens(5, this);
			break;
		case 6:
			sliderScreens(6, this);
			break;
		case 7:
			sliderScreens(7, this);
			break;
		case 8:
			sliderScreens(8, this);
			break;
		default:
			sliderScreens(8, this);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener#onAccount()
	 */
	public void onAccount() {
        Intent intent = new Intent(this, BpLoginScreen.class);
		startActivity(intent);
		finish();
	}

	/*
	 * SliderScreens for Calling the Activity from Slider for each case
	 */

	private void sliderScreens(int screenNumber, Activity act) {
		Intent intent = null;
		switch (screenNumber) {
		case 2:
			if (act.getClass() != DashboardActivity.class) {
				intent = new Intent(act, DashboardActivity.class);
				act.startActivity(intent);
				finish();
			} else {
				((DashboardActivity)act).getSlideMenu().hide();
			}
			break;
		case 3:
			if (act.getClass() != DeviceSetUpActivityListDesign.class) {
				intent = new Intent(act, DeviceSetUpActivityListDesign.class);
				act.startActivity(intent);
				finish();
			} else {
				((DeviceSetUpActivityListDesign)act).getSlideMenu().hide();
			}
			break;
		case 8:
			intent = new Intent(act, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			act.startActivity(intent);
			finish();
			break;
		}

	}

	abstract public SlideMenu getSlideMenu();
}
