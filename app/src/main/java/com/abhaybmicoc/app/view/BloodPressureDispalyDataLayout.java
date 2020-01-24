package com.abhaybmicoc.app.view;

import android.view.View;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.content.res.Resources;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.MeasuDataManager;
import com.abhaybmicoc.app.utilities.AndMedicalLogic;
import com.abhaybmicoc.app.activity.DashboardActivity;
import com.abhaybmicoc.app.entities.Lifetrack_infobean;
import com.abhaybmicoc.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoc.app.base.ADDisplayDataLinearLayout;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;

public class BloodPressureDispalyDataLayout extends ADDisplayDataLinearLayout {
	// region Variables

	private Context mContext;

	private TextView bpDateTextView;

	private TextView pulseUnitTextView;
	private TextView pulseValueTextView;
	private TextView pulseTitleTextView;
	private TextView systolicUnitTextView;
	private TextView systolicValueTextView;
	private TextView diastolicUnitTextView;
	private TextView systolicTitleTextView;
	private TextView diastolicValueTextView;
	private TextView diastolicTitleTextView;

	private ImageView leftArrowImageView;
	private ImageView rightArrowImageView;

	// endregion

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
		systolicValueTextView = (TextView)systolicLayout.findViewById(R.id.tv_bloodpressure_value_sys);
		systolicUnitTextView = (TextView)systolicLayout.findViewById(R.id.tv_bloodpressure_unit_sys);
		systolicTitleTextView = (TextView)systolicLayout.findViewById(R.id.tv_bloodpressure_title_sys);
		
		LinearLayout diastolicLayout = (LinearLayout) findViewById(R.id.value_layout_diastolic);
		diastolicValueTextView = (TextView)diastolicLayout.findViewById(R.id.tv_bloodpressure_value_diasltolic);
		diastolicUnitTextView = (TextView)diastolicLayout.findViewById(R.id.tv_bloodpressure_unit_diastolic);
		diastolicTitleTextView = (TextView)diastolicLayout.findViewById(R.id.tv_bloodpressure_title_diastolic);
		
		LinearLayout pulseLayout = (LinearLayout) findViewById(R.id.value_layout_pulse);
		pulseValueTextView = (TextView)pulseLayout.findViewById(R.id.tv_bloodpressure_value_pulse);
		pulseUnitTextView = (TextView)pulseLayout.findViewById(R.id.tv_bloodpressure_unit_pulse);
		pulseTitleTextView = (TextView)pulseLayout.findViewById(R.id.tv_bloodpressure_title_pulse);
		
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
