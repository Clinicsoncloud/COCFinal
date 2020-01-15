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
import com.abhaybmicoc_test.app.entities.Lifetrack_infobean;
import com.abhaybmicoc_test.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoc_test.app.utilities.ThermometerUtilites;

import java.text.DecimalFormat;


public class ThermometerDisplayDataLayout extends ADDisplayDataLinearLayout {

	private TextView text_thermometer;
	private TextView text_thermometer_unit;
	private TextView text_date;
	
	private ImageView leftArrowImageView;
	private ImageView rightArrowImageView;

	private Context mContext;
	
	public ThermometerDisplayDataLayout(Context context) {
		super(context);
		mContext = context ;
	}
	
	public ThermometerDisplayDataLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public ThermometerDisplayDataLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	@Override
	protected void init() {
		super.init();
		text_date = (TextView)findViewById(R.id.text_date);
		leftArrowImageView = (ImageView)findViewById(R.id.image_left_arrow);
		rightArrowImageView = (ImageView)findViewById(R.id.image_right_arrow);
		
		LinearLayout thermometerLayout = (LinearLayout)findViewById(R.id.value_layout_thermometer);
		text_thermometer = (TextView)thermometerLayout.findViewById(R.id.thermometer_value_textview);
		text_thermometer_unit = (TextView)thermometerLayout.findViewById(R.id.disp_data_thermometer_unit_textview);		
	}
	
	public void setData(Lifetrack_infobean data) {
		super.setData(data);
		try {
			text_date.setText(ANDMedicalUtilities
					.FormatDashboardDispDate(mContext, data.getDate() + "T"
							+ data.getTime()));
		} catch (Exception e) {
		}

		boolean isEnable = ThermometerUtilites.checkErrData(data);
		
		if (isEnable)
		{
			DecimalFormat format = ThermometerUtilites.getDegreeUnitFromThermometerName(data);
			float value = ThermometerUtilites.convertValueFromDegreeUnit(data);
			text_thermometer.setText(format.format(value));
		}
		else
		{
			text_thermometer.setText(getResources().getString(R.string.value_error));
		}
		text_thermometer_unit.setText(ThermometerUtilites.getCurrentUnit(mContext));
		
		DashboardActivity dashboard = (DashboardActivity)mContext;
		AndMedical_App_Global appGlobal = (AndMedical_App_Global)dashboard.getApplication();
		MeasuDataManager manager = appGlobal.getMeasuDataManager();
		int index = manager.getCurrentIndex(data, MeasuDataManager.MEASU_DATA_TYPE_TH);
		int max = (manager.getCountList(MeasuDataManager.MEASU_DATA_TYPE_TH)-1);
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
}
