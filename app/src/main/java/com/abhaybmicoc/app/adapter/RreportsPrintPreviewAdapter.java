package com.abhaybmicoc.app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.model.ReportsPrintData;
import com.abhaybmicoc.app.services.DateService;
import com.abhaybmicoc.app.services.SharedPreferenceService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.DTU;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class RreportsPrintPreviewAdapter extends ArrayAdapter<ReportsPrintData> {
    // region Variables

    private Context context;

    private List<ReportsPrintData> listData = new ArrayList<>();

    private SharedPreferences sharedPreferencesPersonal;
    private SharedPreferences sharedPreferencesPersonalPreferencesActofit;
    private SharedPreferences sharedPreferencesPersonalPreferencesGlucose;
    private SharedPreferences sharedPreferencesPersonalPreferencesHemoglobin;
    private SharedPreferences sharedPreferencesPersonalPreferencesBloodPressure;

    private ReportsPrintData printData;

    // endregion

    // region Events

    public RreportsPrintPreviewAdapter(Context context, int resource, List<ReportsPrintData> objects) {
        super(context, resource, objects);

        this.context = context;

        initializeData(objects);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        printData = listData.get(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.printlist_item, null);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();

        calculatePrintData(convertView, viewHolder, position);

        return convertView;
    }

    // endregion

    private void calculatePrintData(View convertView, ViewHolder viewHolder, int position) {
        switch (position) {
            case 0:
                showWeight(viewHolder);
                break;

            case 1:
                showBmi(viewHolder);
                break;

            case 2:
                showBodyFat(viewHolder);
                break;

            case 3:
                showFatFreeWeight(viewHolder);
                break;

            case 4:
                showSubcutaneousFat(viewHolder);
                break;

            case 5:
                showVisceralFat(viewHolder);
                break;

            case 6:
                showBodyWater(viewHolder);
                break;

            case 7:
                showSkeletalMuscle(viewHolder);
                break;

            case 8:
                showProtein(viewHolder);
                break;

            case 9:
                showMetabolicAge(viewHolder);
                break;

            case 10:
                showHealthScore(viewHolder);
                break;

            case 11:
                showBmr(viewHolder);
                break;

            case 12:
                showPhysique(viewHolder);
                break;

            case 13:
                showMuscleMass(viewHolder);
                break;

            case 14:
                showBoneMass(viewHolder);
                break;

            case 15:
                showBodyTemperature(viewHolder);
                break;

            case 16:
                showSystolicBloodPressure(viewHolder);
                break;

            case 17:
                showDiastolicBloodPressure(viewHolder);
                break;

            case 18:
                showBodyOxygen(viewHolder);
                break;

            case 19:
                showPulseRate(viewHolder);
                break;

            case 20:
                showBloodGlucose(viewHolder);
                break;

            case 21:
                showHemoglobin(viewHolder);
                break;

            case 22:
                showEyeLeftVision(viewHolder);
                break;

            case 23:
                showEyeRightVision(viewHolder);
                break;
        }
    }

    // endregion

    // region Initialization methods

    private void initializeData(List<ReportsPrintData> objects) {
        this.listData = objects;

        sharedPreferencesPersonal = context.getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        sharedPreferencesPersonalPreferencesActofit = context.getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        sharedPreferencesPersonalPreferencesGlucose = context.getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
        sharedPreferencesPersonalPreferencesHemoglobin = context.getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        sharedPreferencesPersonalPreferencesBloodPressure = context.getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
    }

    // endregion

    // region Nested classes

    static class ViewHolder {
        @BindView(R.id.rangeTV)
        TextView rangeTV;
        @BindView(R.id.valueTV)
        TextView valueTV;
        @BindView(R.id.resultTV)
        TextView resultTV;
        @BindView(R.id.parameterTV)
        TextView parameterTV;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    // endregion

    // region Data display methods
    private void showWeight(ViewHolder viewHolder) {
        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Seriously High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        } else if (printData.getResult().equals("Seriously Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showBmi(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        }
    }

    private void showBodyFat(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Seriously High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setText("High");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setText("Standard");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setText("Low");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showFatFreeWeight(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

    }

    private void showSubcutaneousFat(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showVisceralFat(ViewHolder viewHolder) {
        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Seriously High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
    }

    private void showBodyWater(ViewHolder viewHolder) {
        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Adequate")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showHemoglobin(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showEyeLeftVision(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Not upto Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        }
    }

    private void showEyeRightVision(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Not upto Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        }
    }


    private void showBloodGlucose(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showPulseRate(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }

    }

    private void showBodyOxygen(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showDiastolicBloodPressure(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showSystolicBloodPressure(ViewHolder viewHolder) {


        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showBodyTemperature(ViewHolder viewHolder) {
        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showBoneMass(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showMuscleMass(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Adequate")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showPhysique(ViewHolder viewHolder) {
        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
    }

    private void showBmr(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));


        if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Not upto Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        }
    }

    private void showMetabolicAge(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setText("Standard");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Not up to Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        }
    }

    private void showHealthScore(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
    }

    private void showProtein(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("Adequate")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }
    }

    private void showSkeletalMuscle(ViewHolder viewHolder) {

        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText(printData.getValue());
        viewHolder.rangeTV.setText(printData.getRange());
        viewHolder.resultTV.setText(printData.getResult());
        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if (printData.getResult().equals("High")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        } else if (printData.getResult().equals("Standard")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (printData.getResult().equals("Low")) {
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
        }

    }

    // endregion

    // region Logical methods

    private double getWeight() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT))
            return SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT);
        else
            return 0;
    }

    private int getHeight() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.HEIGHT))
            return SharedPreferenceService.getInteger(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.HEIGHT);
        else
            return 0;
    }

    private int getAge() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_PERSONALDATA, Constant.Fields.DATE_OF_BIRTH)) {
            String dateOfBirth = SharedPreferenceService.getString(context, ApiUtils.PREFERENCE_PERSONALDATA, Constant.Fields.DATE_OF_BIRTH);
            return DateService.getAgeFromStringDate(DTU.getYYYYMD(dateOfBirth));
        } else
            return 0;
    }

    // endregion
}
