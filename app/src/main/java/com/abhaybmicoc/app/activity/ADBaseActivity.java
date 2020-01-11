package com.abhaybmicoc.app.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

import com.abhaybmicoc.app.activity.DashboardActivity;
import com.abhaybmicoc.app.DeviceSetUpActivityListDesign;
import main.java.com.abhaybmicoc.app.activity.LoginActivity;

import com.abhaybmicoc.app.screen.BpLoginScreen;
import com.abhaybmicoc.app.utils.Constant.SlideMenu;
import com.abhaybmicoc.app.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;

public abstract class ADBaseActivity extends Activity implements OnSlideMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSlideMenuItemClick(int itemId) { switchScreen(itemId); }

    /**
     * Unknown method
     */
    public void onAccount() {
        Intent intent = new Intent(this, BpLoginScreen.class);
        startActivity(intent);
        finish();
    }

    /**
     * Method to switch screen
     * @param itemId
     */
    private void switchScreen(int itemId){
        switch (itemId) {
            case 2:
                sliderScreens(SlideMenu.DASHBOARD, this);
                break;
            case 3:
                sliderScreens(SlideMenu.DEVICE_SETUP, this);
                break;
            case 4:
                sliderScreens(SlideMenu.LOGIN, this);
                break;
        }
    }

    /*
     * SliderScreens for Calling the Activity from Slider for each case
     */
    private void sliderScreens(SlideMenu screenNumber, Activity act) {
        Intent intent = null;

        switch (screenNumber) {
            case DASHBOARD:
                if (act.getClass() != DashboardActivity.class) {
                    intent = new Intent(act, DashboardActivity.class);
                    act.startActivity(intent);
                    finish();
                } else {
                    ((DashboardActivity) act).getSlideMenu().hide();
                }
                break;
            case DEVICE_SETUP:
                if (act.getClass() != DeviceSetUpActivityListDesign.class) {
                    intent = new Intent(act, DeviceSetUpActivityListDesign.class);
                    act.startActivity(intent);
                    finish();
                } else {
                    ((DeviceSetUpActivityListDesign) act).getSlideMenu().hide();
                }
                break;
            case LOGIN:
                intent = new Intent(act, LoginActivity.class);

                intent.setFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                act.startActivity(intent);
                finish();
                break;
        }

    }
}
