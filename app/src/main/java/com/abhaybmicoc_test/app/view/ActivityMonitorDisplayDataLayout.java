package com.abhaybmicoc_test.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhaybmicoc_test.app.DashboardActivity;
import com.abhaybmicoc_test.app.MeasuDataManager;
import com.abhaybmicoc_test.app.R;
import com.abhaybmicoc_test.app.base.ADDisplayDataLinearLayout;
import com.abhaybmicoc_test.app.entities.AndMedical_App_Global;
import com.abhaybmicoc_test.app.entities.DataBase;
import com.abhaybmicoc_test.app.entities.Lifetrack_infobean;
import com.abhaybmicoc_test.app.entities.RegistrationInfoBean;
import com.abhaybmicoc_test.app.utilities.ADSharedPreferences;
import com.abhaybmicoc_test.app.utilities.ANDMedicalUtilities;

import java.text.DecimalFormat;

public class ActivityMonitorDisplayDataLayout extends ADDisplayDataLinearLayout {
	
	private final static int MODE_STEP = 100;
	private final static int MODE_DISTANCE = 200;
	
	private int mButtonMode = MODE_STEP;
	
	private TextView txt_steps;
	private TextView txt_steps_unit;
	private TextView txt_steps_button;
	private TextView txt_activity_date;
	
	private TextView txt_miles_value;
	private TextView txt_bpm_value;
	private TextView txt_cal_value;
	
	private TextView txt_miles_title;
	private TextView txt_bpm_title;
	private TextView txt_cal_title;
	private ImageView activity_icon;

	private Lifetrack_infobean mData;
	
	private final DecimalFormat mFormat = new DecimalFormat("0.0#"); 
	
	private ImageView leftArrowImageView;
	private ImageView rightArrowImageView;

	private Context mContext;

	public ActivityMonitorDisplayDataLayout(Context context) {
		super(context);
		mContext = context;
	}

	public ActivityMonitorDisplayDataLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public ActivityMonitorDisplayDataLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	@Override
	protected void init() {
		super.init();
		txt_steps = (TextView) findViewById(R.id.txt_totalsteps);
		txt_steps_unit = (TextView) findViewById(R.id.txt_total_step_unit);
		txt_steps_button = (TextView) findViewById(R.id.txt_total_step_button);

		txt_activity_date = (TextView) findViewById(R.id.text_date);
		leftArrowImageView = (ImageView)findViewById(R.id.image_left_arrow);
		rightArrowImageView = (ImageView)findViewById(R.id.image_right_arrow);

		LinearLayout milesLayout = (LinearLayout) findViewById(R.id.value_layout_distance);
		txt_miles_value = (TextView)milesLayout.findViewById(R.id.disp_data_am_value_textview);
		txt_miles_title = (TextView)milesLayout.findViewById(R.id.disp_data_am_title_textview);
		
		LinearLayout bpmLayout = (LinearLayout) findViewById(R.id.value_layout_bpm);
		txt_bpm_value = (TextView)bpmLayout.findViewById(R.id.disp_data_am_value_textview);
		txt_bpm_title = (TextView)bpmLayout.findViewById(R.id.disp_data_am_title_textview);
		
		LinearLayout caloryLayout = (LinearLayout) findViewById(R.id.value_layout_calory);
		txt_cal_value = (TextView)caloryLayout.findViewById(R.id.disp_data_am_value_textview);
		txt_cal_title = (TextView)caloryLayout.findViewById(R.id.disp_data_am_title_textview);

		//ACGS-10
		activity_icon = (ImageView) findViewById(R.id.dashboard_icon_display);
		setListiner();

	}

	// リスナーのセット
	public void setListiner(){
		txt_steps_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txt_steps_button.getText() == getResources().getString(R.string.activitymonitor_title_steps) && mButtonMode == MODE_STEP) {
					mButtonMode = MODE_DISTANCE;
					txt_steps_button.setText(R.string.activitymonitor_title_distance);
					DataBase dataBase;
					RegistrationInfoBean registerInfo;
					if (!ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
						String userName = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
						dataBase = new DataBase(mContext, userName);
						registerInfo = dataBase.getUserDetailAccount(ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_EMAIL, ""));
					} else {
						dataBase = new DataBase(mContext);
						registerInfo = dataBase.getGuestInfo();
					}
					
					String unitType = registerInfo.getUserHeightUnit();
					double distance = mData.getDistanceValue();
					txt_steps_unit.setVisibility(View.VISIBLE);
					if (unitType.equalsIgnoreCase(getResources().getString(R.string.height_unit_us))) {
						Double va = Double.valueOf(distance) * 0.621371;
						txt_steps.setText(mFormat.format(va));
						txt_steps_unit.setText(R.string.activitymonitor_unit_mi);
					} else {
						txt_steps.setText(mFormat.format(distance));
						txt_steps_unit.setText(R.string.activitymonitor_unit_km);
					}
				} else {
					mButtonMode = MODE_STEP;
					txt_steps_button.setText(R.string.activitymonitor_title_steps);
					String steps = String.valueOf(mData.getSteps());
					txt_steps_unit.setText("");
					txt_steps_unit.setVisibility(View.GONE);
					txt_steps.setText(steps);
				}
			}
		});
	}


	
	@Override
	public void setData(Lifetrack_infobean data) {
		super.setData(data);
		mData = data;

		// 歩数表示部
		if (mButtonMode == MODE_DISTANCE) {
			txt_steps_button.setText(R.string.activitymonitor_title_distance);
			DataBase dataBase;
			RegistrationInfoBean registerInfo;
			if (!ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
				String userName = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
				dataBase = new DataBase(mContext, userName);
				registerInfo = dataBase.getUserDetailAccount(ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_EMAIL, ""));
			} else {
				dataBase = new DataBase(mContext);
				registerInfo = dataBase.getGuestInfo();
			}
			
			String unitType = registerInfo.getUserHeightUnit();
			double distance = Double.valueOf(mData.getDistance());
			if (unitType.equalsIgnoreCase(getResources().getString(R.string.height_unit_us))) {
				Double va = Double.valueOf(distance) * 0.621371;
				txt_steps.setText(mFormat.format(va));
				txt_steps_unit.setText(R.string.activitymonitor_unit_mi);
			} else {
				txt_steps.setText(mFormat.format(distance));
				txt_steps_unit.setText(R.string.activitymonitor_unit_km);
			}
		} else {
			txt_steps_button.setText(R.string.activitymonitor_title_steps);
			String steps = String.valueOf(mData.getSteps());
			txt_steps_unit.setText("");
			txt_steps.setText(steps);
		} 
		
		// 脈拍表示部
		if (data.getHeartRate() == null) {
			txt_bpm_value.setText("0");
		} else {
			txt_bpm_value.setText(data.getHeartRate());
		}
		txt_bpm_title.setText(R.string.activitymonitor_unit_bpm);
		
		// カロリー表示部
		//ACGS-10
		String devId  = mData.getDeviceId();

		if (devId.contains("UW-302")) {
			int calValue = Integer.valueOf(data.getCal());
			float calFloat = calValue / 10;
			calFloat = Math.round(calFloat);
			int calINT = (int) calFloat;
			String display_calories = String.valueOf(calINT);
			txt_cal_value.setText(display_calories);
			txt_cal_title.setText(R.string.activitymonitor_unit_kcal);
		} else {

			float calFloat = Float.valueOf(data.getCal());
			calFloat = Math.round(calFloat);
			int calINT = (int) calFloat;
			String display_calories = String.valueOf(calINT);
			txt_cal_value.setText(display_calories);
			txt_cal_title.setText(R.string.activitymonitor_unit_cal);
		}

		
		int sleepTime = Integer.valueOf(data.getSleep());
		int hour = sleepTime / 60;
		int minute = sleepTime % 60;
		
		if (hour != 0) {
			txt_miles_value.setText(hour + "h " + minute + "m");
		} else {
			txt_miles_value.setText(minute + "m");
		}
		txt_miles_title.setText(R.string.activitymonitor_unit_sleep);
		
		txt_activity_date.setText(ANDMedicalUtilities
				.FormatDashboardDispDate(mContext, data.getDate() + "T"
						+ data.getTime()));
		
		DashboardActivity dashboard = (DashboardActivity)mContext;
		AndMedical_App_Global appGlobal = (AndMedical_App_Global)dashboard.getApplication();
		MeasuDataManager manager = appGlobal.getMeasuDataManager();
		int index = manager.getCurrentIndex(data, MeasuDataManager.MEASU_DATA_TYPE_AM);
		int max = (manager.getCountList(MeasuDataManager.MEASU_DATA_TYPE_AM)-1);
		if ((index > 0) && (index <= max))
		{
			leftArrowImageView.setVisibility(View.VISIBLE);
		}
		else
		{
			leftArrowImageView.setVisibility(View.INVISIBLE);
		}
		if ((index >= 0) && (index < max))
		{
			rightArrowImageView.setVisibility(View.VISIBLE);
		}
		else
		{
			rightArrowImageView.setVisibility(View.INVISIBLE);
		}
	}

	//Function to populate default Activity data layout when paired
	public void setDataNull() {

		init();
		txt_steps_button.setText(R.string.activitymonitor_title_steps);
		txt_steps_unit.setText("");
		txt_steps.setText("");
		txt_bpm_value.setText("");
		txt_bpm_title.setText(R.string.activitymonitor_unit_bpm);
		txt_cal_value.setText("");
		txt_cal_title.setText(R.string.activitymonitor_unit_cal);
		txt_miles_value.setText("");
		txt_miles_title.setText(R.string.activitymonitor_unit_sleep);
		rightArrowImageView.setVisibility(View.INVISIBLE);
		leftArrowImageView.setVisibility(View.INVISIBLE);

	}
}
