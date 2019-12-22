package com.abhaybmi.app.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhaybmi.app.DashboardActivity;
import com.abhaybmi.app.MeasuDataManager;
import com.abhaybmi.app.R;
import com.abhaybmi.app.base.ADDisplayDataLinearLayout;
import com.abhaybmi.app.entities.AndMedical_App_Global;
import com.abhaybmi.app.entities.Lifetrack_infobean;
import com.abhaybmi.app.utilities.ANDMedicalUtilities;
import com.abhaybmi.app.utilities.AndMedicalLogic;


public class BloodPressureDispalyDataLayout extends ADDisplayDataLinearLayout {
	
	private TextView bpDateTextView;
	
	private TextView systolicValueTextView;
	private TextView systolicUnitTextView;
	private TextView systolicTitleTextView;

	private TextView diastolicValueTextView;
	private TextView diastolicUnitTextView;
	private TextView diastolicTitleTextView;
	
	private TextView pulseValueTextView;
	private TextView pulseUnitTextView;
	private TextView pulseTitleTextView;
	
	private ImageView leftArrowImageView;
	private ImageView rightArrowImageView;
	
	private Context mContext;

	public BloodPressureDispalyDataLayout(Context context) {
		super(context);
		mContext = context;
	}

	public BloodPressureDispalyDataLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public BloodPressureDispalyDataLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	@Override
	protected void init() {
		super.init();
		
		bpDateTextView = (TextView) findViewById(R.id.text_date);
		leftArrowImageView = (ImageView)findViewById(R.id.image_left_arrow);
		rightArrowImageView = (ImageView)findViewById(R.id.image_right_arrow);
		
		LinearLayout systolicLayout = (LinearLayout) findViewById(R.id.value_layout_systolic);
		systolicValueTextView = (TextView)systolicLayout.findViewById(R.id.disp_data_bp_value_textview);
		systolicUnitTextView = (TextView)systolicLayout.findViewById(R.id.disp_data_bp_unit_textview);
		systolicTitleTextView = (TextView)systolicLayout.findViewById(R.id.disp_data_bp_title_textview);
		
		LinearLayout diastolicLayout = (LinearLayout) findViewById(R.id.value_layout_diastolic);
		diastolicValueTextView = (TextView)diastolicLayout.findViewById(R.id.disp_data_bp_value_textview);
		diastolicUnitTextView = (TextView)diastolicLayout.findViewById(R.id.disp_data_bp_unit_textview);
		diastolicTitleTextView = (TextView)diastolicLayout.findViewById(R.id.disp_data_bp_title_textview);
		
		LinearLayout pulseLayout = (LinearLayout) findViewById(R.id.value_layout_pulse);
		pulseValueTextView = (TextView)pulseLayout.findViewById(R.id.disp_data_bp_value_textview);
		pulseUnitTextView = (TextView)pulseLayout.findViewById(R.id.disp_data_bp_unit_textview);
		pulseTitleTextView = (TextView)pulseLayout.findViewById(R.id.disp_data_bp_title_textview);
		
		Resources res = getContext().getResources();
		systolicUnitTextView.setText("mmHg");
		systolicTitleTextView.setText(res.getString(R.string.bloodpressure_title_systolic));

		diastolicUnitTextView.setText("mmHg");
		diastolicTitleTextView.setText(res.getString(R.string.bloodpressure_title_diastolic));
		
		pulseUnitTextView.setText("bpm");
		pulseTitleTextView.setText(res.getString(R.string.bloodpressure_title_pulse));		
	}

	@Override
	public void setData(Lifetrack_infobean data) {
		super.setData(data);
		
		init();
		
		String bpDate = data.getDate();
		String bpTime = data.getTime();
		String sysVal = data.getSystolic();
		String diaVal = data.getDiastolic();
		String pulVal = data.getPulse();
		
		bpDateTextView.setText(ANDMedicalUtilities.FormatDashboardDispDate(mContext, bpDate + "T" + bpTime));

		boolean isError = AndMedicalLogic.checkBPError(data.getSystolic(),
		data.getDiastolic(), data.getPulse());
		if (isError) {
			String err = getResources().getString(R.string.value_error) ;
			systolicValueTextView.setText(err);
			diastolicValueTextView.setText(err);
			pulseValueTextView.setText(err);
		} 
		else {
			systolicValueTextView.setText(sysVal);
			diastolicValueTextView.setText(diaVal);
			pulseValueTextView.setText(pulVal);
		}
		
		DashboardActivity dashboard = (DashboardActivity)mContext;
		AndMedical_App_Global appGlobal = (AndMedical_App_Global)dashboard.getApplication();
		MeasuDataManager manager = appGlobal.getMeasuDataManager();
		int index = manager.getCurrentIndex(data, MeasuDataManager.MEASU_DATA_TYPE_BP);
		int max = (manager.getCountList(MeasuDataManager.MEASU_DATA_TYPE_BP)-1);
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
