package com.abhaybmicoctest.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.abhaybmicoctest.app.slidemenu.SlideMenu;

import com.abhaybmicoctest.app.base.ADInstructionActivity;
import com.abhaybmicoctest.app.entities.AndMedical_App_Global;
import com.abhaybmicoctest.app.entities.RegistrationInfoBean;
import com.abhaybmicoctest.app.utilities.ADSharedPreferences;
import com.abhaybmicoctest.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoctest.app.R;

public class DeviceSetUpActivityListDesign extends ADBaseActivity {
    AndMedical_App_Global app_global;
    ArrayAdapter<String> adap_list_devices;
    ListView list_devicesetup;
    private SlideMenu mSlideMenu;
    private String mUserName = "";
    private String mUserType = "";
    private TextView mHeaderTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.and_devicesetup_newlist);
        mSlideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        app_global = (AndMedical_App_Global) getApplication();
        list_devicesetup = (ListView) findViewById(R.id.list_newdevicesetup);

        mHeaderTextView = (TextView) findViewById(R.id.header);
        mHeaderTextView.setText(getString(R.string.device_set_up));

        String[] values;
        values = new String[]{"Blood Pressure Monitors", "UA-651BLE"};


        DeviceSetUpAdapter adapter = new DeviceSetUpAdapter(this, values);
        list_devicesetup.setAdapter(adapter);

		 /*
          * TODO : Implement region specific display of the listview based on Global or US app
		  */
        list_devicesetup.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RegistrationInfoBean infoBeanObj = new RegistrationInfoBean();
                if (position == 1) {
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_DEVICE_SET_UP_MODE, ADSharedPreferences.VALUE_DEVICE_SET_UP_MODE_BP);
                    Intent intent = new Intent(DeviceSetUpActivityListDesign.this, InstructionActivity.class);
                    intent.putExtra(ADInstructionActivity.DEVICE_SCAN_MODE_KEY, ADInstructionActivity.DEVICE_SCAN_MODE_BP);
                    startActivity(intent);
                } else if (position == 5) {
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_DEVICE_SET_UP_MODE, ADSharedPreferences.VALUE_DEVICE_SET_UP_MODE_AM);
                    Intent intent = new Intent(DeviceSetUpActivityListDesign.this, InstructionActivity.class);
                    intent.putExtra(ADInstructionActivity.DEVICE_SCAN_MODE_KEY, ADInstructionActivity.DEVICE_SCAN_MODE_AM_UW);
                    startActivity(intent);

                } else if (position == 3) {
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_DEVICE_SET_UP_MODE, ADSharedPreferences.VALUE_DEVICE_SET_UP_MODE_WS);
                    Intent intent = new Intent(DeviceSetUpActivityListDesign.this, InstructionActivity.class);
                    intent.putExtra(ADInstructionActivity.DEVICE_SCAN_MODE_KEY, ADInstructionActivity.DEVICE_SCAN_MODE_WS);
                    startActivity(intent);
                } else if (position == 7) { //Thermometer
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_DEVICE_SET_UP_MODE, ADSharedPreferences.VALUE_DEVICE_SET_UP_MODE_TM);
                    Intent intent = new Intent(DeviceSetUpActivityListDesign.this, InstructionActivity.class);
                    intent.putExtra(ADInstructionActivity.DEVICE_SCAN_MODE_KEY, ADInstructionActivity.DEVICE_SCAN_MODE_TM);
                    startActivity(intent);
                }


            }
        });
        // スライドメニュー
        mSlideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        String addnewUserVisiblity = ADSharedPreferences.getString(ADSharedPreferences.KEY_ADD_NEW_USER_VISIBLITY, "");
        String manageuservisibility = ADSharedPreferences.getString(ADSharedPreferences.KEY_MANAGER_USER_VISIBILITY, "");
        String frommanagevisibility = ADSharedPreferences.getString(ADSharedPreferences.KEY_FROM_MANAGER_VISIBILITY, "");
        mUserName = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
        String lastName = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_LAST_NAME, "");
        mSlideMenu.init(this, this, 333, mUserName, mUserType, addnewUserVisiblity,
                manageuservisibility, frommanagevisibility);

        mSlideMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideMenu.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (mSlideMenu != null) {
            if (!mSlideMenu.isShown()) {
                mSlideMenu.show();
            } else {
                mSlideMenu.hide();
            }
        }
    }

    @Override
    public SlideMenu getSlideMenu() {
        return mSlideMenu;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public class DeviceSetUpAdapter extends ArrayAdapter<String> {

        private final Context context;
        private final String[] values;

        public DeviceSetUpAdapter(Context context, String[] values) {
            super(context, R.layout.and_devicesetup_newdesign_device, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView;
            TextView textView;
            ImageView imageView;
            //Getting the zone
            RegistrationInfoBean infoBeanObj = new RegistrationInfoBean();

            if (position == 0) {
                rowView = inflater.inflate(R.layout.device_setup_header_row, parent, false);
                textView = (TextView) rowView.findViewById(R.id.label);
                imageView = (ImageView) rowView.findViewById(R.id.icon);

            } else if (position == 2) {
                rowView = inflater.inflate(R.layout.device_setup_header_row, parent, false);
                textView = (TextView) rowView.findViewById(R.id.label);
                imageView = (ImageView) rowView.findViewById(R.id.icon);
            } else if (position == 4) {
                rowView = inflater.inflate(R.layout.device_setup_header_row, parent, false);
                textView = (TextView) rowView.findViewById(R.id.label);
                imageView = (ImageView) rowView.findViewById(R.id.icon);
            } else if (position == 6) {
                rowView = inflater.inflate(R.layout.device_setup_header_row, parent, false);
                textView = (TextView) rowView.findViewById(R.id.label);
                imageView = (ImageView) rowView.findViewById(R.id.icon);
            } else {
                rowView = inflater.inflate(R.layout.and_devicesetup_newdesign_device, parent, false);
                textView = (TextView) rowView.findViewById(R.id.label);
                imageView = (ImageView) rowView.findViewById(R.id.icon);
            }


            String deviceName = values[position];

            if (deviceName.equalsIgnoreCase("UA-651BLE")) {
                if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
                    //Global app
                    imageView.setImageResource(R.drawable.ua_651ble_ec);
                } else {
                    //US app
                    imageView.setImageResource(R.drawable.ua_651ble_cd_new);
                }

                textView.setText("DeluxeCONNECT Upper Arm UA-651BLE");
            } else if (deviceName.equalsIgnoreCase("Blood Pressure Monitors")) {
                rowView.setBackgroundColor(getResources().getColor(R.color.bp_device_header));
                textView.setTextColor(Color.WHITE);
                textView.setText(getResources().getString(R.string.bloodPressure_heading));
            }
            return rowView;

        }

    }


}