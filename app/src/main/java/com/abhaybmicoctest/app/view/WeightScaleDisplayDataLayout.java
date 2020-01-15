package com.abhaybmicoctest.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhaybmicoctest.app.MeasuDataManager;
import com.abhaybmicoctest.app.R;
import com.abhaybmicoctest.app.base.ADDisplayDataLinearLayout;
import com.abhaybmicoctest.app.entities.AndMedical_App_Global;
import com.abhaybmicoctest.app.entities.DataBase;
import com.abhaybmicoctest.app.entities.Lifetrack_infobean;
import com.abhaybmicoctest.app.entities.RegistrationInfoBean;
import com.abhaybmicoctest.app.utilities.ADSharedPreferences;
import com.abhaybmicoctest.app.utilities.ANDMedicalUtilities;


public class WeightScaleDisplayDataLayout extends ADDisplayDataLinearLayout {
	
	private TextView weight_scale_value;
	private TextView txt_weightScale_Date;
	private TextView weight_unit;
	private TextView bmi;
	private TextView bmi_unit;
	private Context mContext;

	private ImageView leftArrowImageView;
	private ImageView rightArrowImageView;

	public WeightScaleDisplayDataLayout(Context context) {
		super(context);
		mContext = context;
	}
	
	public WeightScaleDisplayDataLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public WeightScaleDisplayDataLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	@Override
	protected void init() {
		super.init();
		
		txt_weightScale_Date = (TextView) findViewById(R.id.text_date);
		leftArrowImageView = (ImageView)findViewById(R.id.image_left_arrow);
		rightArrowImageView = (ImageView)findViewById(R.id.image_right_arrow);

		LinearLayout weightLayout = (LinearLayout) findViewById(R.id.value_layout_weight);
		weight_scale_value = (TextView)weightLayout.findViewById(R.id.disp_data_ws_value_textview);
		weight_unit = (TextView)weightLayout.findViewById(R.id.disp_data_ws_unit_textview);
		
		LinearLayout bmiLayout = (LinearLayout) findViewById(R.id.value_layout_bmi);
		bmi = (TextView)bmiLayout.findViewById(R.id.disp_data_ws_value_textview);
		bmi_unit = (TextView)bmiLayout.findViewById(R.id.disp_data_ws_unit_textview);
		bmi_unit.setText("BMI");		
	}

	@Override
	public void setData(Lifetrack_infobean data) {
		super.setData(data);
		
		AndMedical_App_Global appGlobal = (AndMedical_App_Global)getContext().getApplicationContext();
		String weightString = data.getWeight();
		String weightUnit = data.getWeightUnit();


		
		String userName = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
		DataBase db;
		if(ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
			db = new DataBase(mContext);
		} else {
			db = new DataBase(mContext, userName);
		}


		RegistrationInfoBean registerInfo = db.getUserDetailAccount("guest@gmail.com");
		String height;
		String heightUnit;
		
		if (registerInfo != null) {
			height = registerInfo.getUserHeight();
			if (height == null) {
				height = "0";
			}
			heightUnit = registerInfo.getUserHeightUnit();
		} else {
			height = "0";
			heightUnit = "cm";
		}
		
		String currentUnitType = ADSharedPreferences.getString(ADSharedPreferences.KEY_WEIGHT_SCALE_UNITS, ADSharedPreferences.DEFAULT_WEIGHT_SCALE_UNITS);
		float weight = Float.valueOf(weightString);

		if (currentUnitType.equalsIgnoreCase(ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_LBS)) {
			if (weightUnit.equalsIgnoreCase(ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_KG)) {
				setBMIUSKG(heightUnit, height, weight);
				weight = (float) (weight / 0.45359);
			} else {
				setBMIUSKG(heightUnit, height, (float)(weight * 0.45359));
			}

			weightUnit = ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_LBS;

		} else if (currentUnitType.equalsIgnoreCase(ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_KG)) {
			if (weightUnit.equalsIgnoreCase(ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_LBS)) {
				weight = (float) (weight * 0.45359);
				setBMIUSKG(heightUnit, height, weight);
			} else {
				setBMIUSKG(heightUnit, height, weight);
			}

			weightUnit = ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_KG;
		}
		weight_scale_value.setText(String.format("%.1f", weight));
		
		try {
			txt_weightScale_Date.setText(ANDMedicalUtilities
					.FormatDashboardDispDate(mContext, data.getDate() + "T"
							+ data.getTime()));
		} catch (Exception e) {
		}
		weight_unit.setText(weightUnit);
		
		MeasuDataManager manager = appGlobal.getMeasuDataManager();
		int index = manager.getCurrentIndex(data, MeasuDataManager.MEASU_DATA_TYPE_WS);
		int max = (manager.getCountList(MeasuDataManager.MEASU_DATA_TYPE_WS)-1);
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
	
	private void setBMIUSKG(String heightUnit, String height, float weight) {
		if (height == null || height.isEmpty()) {
			this.bmi.setText("0");
			return;
		}
		float heightFloat = Float.valueOf(height);
		
		if (heightUnit.equalsIgnoreCase(getResources().getString(R.string.height_unit_us))) {
			heightFloat = (float) (heightFloat * 0.0254);
		} else {
			heightFloat = (float) (heightFloat / 100);
		}
		
		if (heightFloat != 0) {
			String bmi = String.format("%.1f",((weight) / (heightFloat * heightFloat)));
			this.bmi.setText(bmi);
			this.bmi.setVisibility(View.VISIBLE);
		} else {
			this.bmi.setText("0");
		}
	}
}
