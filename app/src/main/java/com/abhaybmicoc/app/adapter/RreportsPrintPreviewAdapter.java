package com.abhaybmicoc.app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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

import java.text.DecimalFormat;
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

//        calculatePrintData(convertView, viewHolder, position);

        return convertView;
    }

    // endregion

    /*private void calculatePrintData(View convertView, ViewHolder viewHolder, int position) {
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
    }*/

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
/*
    private void showWeight(ViewHolder viewHolder) {
        double height = getHeight();

        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.valueTV.setText("NA");
            viewHolder.resultTV.setText("");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            viewHolder.rangeTV.setText("");
        } else {
            double standardWeightMen = ((height - 80) * 0.7);
            standardWeightMen = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightMen));
            double standardWeightFemale = (((height * 1.37) - 110) * 0.45);
            standardWeightFemale = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightFemale));
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            SharedPreferences.Editor objectShared = sharedPreferencesPersonal.edit();

            Log.e("standardWeightMen", " : " + standardWeightMen);
            Log.e("standardWeightFemale", " : " + standardWeightFemale);

            if (sharedPreferencesPersonal.getString("gender", "").equals("male")) {

                Log.e("standardWeightMenFF", " : " + (0.90 * standardWeightMen));
                Log.e("standardWeightFemaleFF", " : " + (1.09 * standardWeightMen));

                double standardWeighRangeFrom = (0.90 * standardWeightMen);
                standardWeighRangeFrom = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeFrom));
                double standardWeighRangeTo = (1.09 * standardWeightMen);
                standardWeighRangeTo = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeTo));

                double standarWeightHighFrom = (1.10 * standardWeightMen);
                standarWeightHighFrom = Double.parseDouble(new DecimalFormat("#.##").format(standarWeightHighFrom));
                double standarWeightHighTo = (1.20 * standardWeightMen);
                standarWeightHighTo = Double.parseDouble(new DecimalFormat("#.##").format(standarWeightHighTo));
                double standarWeightLowFrom = (0.80 * standardWeightMen);
                standarWeightLowFrom = Double.parseDouble(new DecimalFormat("#.##").format(standarWeightLowFrom));
                double standarWeightLowTo = (0.89 * standardWeightMen);
                standarWeightLowTo = Double.parseDouble(new DecimalFormat("#.##").format(standarWeightLowTo));

                viewHolder.rangeTV.setText("" + standardWeighRangeFrom + " - " + standardWeighRangeTo + "kg");

                objectShared.putString("standardRangeMaleFrom", "" + (0.90 * standardWeightMen));
                objectShared.putString("standardRangeMaleTo", "" + (1.09 * standardWeightMen));
                objectShared.commit();

                if (Double.parseDouble(printData.getCurr_value()) > standarWeightHighTo) {
                    viewHolder.resultTV.setText("Seriously High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= standarWeightHighTo && Double.parseDouble(printData.getCurr_value()) >= standarWeightHighFrom) {

                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                } else if (Double.parseDouble(printData.getCurr_value()) <= standardWeighRangeTo && Double.parseDouble(printData.getCurr_value()) >= standardWeighRangeFrom) {

                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                } else if (Double.parseDouble(printData.getCurr_value()) <= standarWeightLowTo && Double.parseDouble(printData.getCurr_value()) >= standarWeightLowFrom) {

                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                } else if (Double.parseDouble(printData.getCurr_value()) < standarWeightLowFrom) {

                    viewHolder.resultTV.setText("Seriously Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

            }
        }
    }

    private void showBmi(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.rangeTV.setText("18.5 - 25");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            if (Double.parseDouble(printData.getCurr_value()) > 25) {
                viewHolder.resultTV.setText("High");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
            } else if (Double.parseDouble(printData.getCurr_value()) <= 25 && Double.parseDouble(printData.getCurr_value()) >= 18.5) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else if (Double.parseDouble(printData.getCurr_value()) < 18.5) {
                viewHolder.resultTV.setText("Low");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
            }
        }
    }

    private void showBodyFat(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (SharedPreferenceService.isMalePatient(context)) {
                if (Double.parseDouble(printData.getCurr_value()) > 26) {
                    viewHolder.resultTV.setText("Seriously High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 26 && Double.parseDouble(printData.getCurr_value()) >= 21) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 21 && Double.parseDouble(printData.getCurr_value()) >= 11) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 11) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

                viewHolder.rangeTV.setText("11 - 21" + "(%)");

            } else { //female bofy fat

                viewHolder.rangeTV.setText("21 - 30" + "(%)");

                if (Double.parseDouble(printData.getCurr_value()) > 36) {
                    viewHolder.resultTV.setText("Seriously High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 36 && Double.parseDouble(printData.getCurr_value()) > 30) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 30 && Double.parseDouble(printData.getCurr_value()) >= 21) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 21) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }
            }
        }
    }

    private void showFatFreeWeight(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
    }

    private void showSubcutaneousFat(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            viewHolder.parameterTV.setText("" + printData.getParameter());

        } else {
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (sharedPreferencesPersonal.getString("gender", "").equals("male")) {
                viewHolder.rangeTV.setText("8.6 - 16.7");

                if (Double.parseDouble(printData.getCurr_value()) > 16.7) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 16.7 && Double.parseDouble(printData.getCurr_value()) >= 8.6) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 8.6) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

            } else { // female subcutaneous fat
                viewHolder.rangeTV.setText("18.5 - 26.7");

                if (Double.parseDouble(printData.getCurr_value()) > 26.7) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 26.7 && Double.parseDouble(printData.getCurr_value()) >= 18.5) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 18.5) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }
            }
        }
    }

    private void showVisceralFat(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.rangeTV.setText(" < = 9");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            if (Double.parseDouble(printData.getCurr_value()) > 14) {
                viewHolder.resultTV.setText("Seriously High");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
            } else if (Double.parseDouble(printData.getCurr_value()) <= 14 && Double.parseDouble(printData.getCurr_value()) >= 10) {
                viewHolder.resultTV.setText("High");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
            } else if (Double.parseDouble(printData.getCurr_value()) <= 9) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
            }
        }
    }

    private void showBodyWater(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (SharedPreferenceService.isMalePatient(context)) {
                viewHolder.rangeTV.setText("55 - 65 %");

                if (Double.parseDouble(printData.getCurr_value()) > 65) {
                    viewHolder.resultTV.setText("Adequate");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 65 && Double.parseDouble(printData.getCurr_value()) >= 55) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 55) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

            } else {
                *//* Female body water *//*
                viewHolder.rangeTV.setText("45 - 60 %");

                if (Double.parseDouble(printData.getCurr_value()) > 60) {
                    viewHolder.resultTV.setText("Adequate");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 60 && Double.parseDouble(printData.getCurr_value()) >= 45) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 45) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }
            }
        }
    }

    private void showHemoglobin(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (sharedPreferencesPersonal.getString("gender", "").equals("male")) {
                if (Double.parseDouble(printData.getCurr_value()) > 17) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 17 && Double.parseDouble(printData.getCurr_value()) >= 13) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 13) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

                viewHolder.rangeTV.setText("13 - 17 g/dl");
            } else {
                if (Double.parseDouble(printData.getCurr_value()) > 15) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 15 && Double.parseDouble(printData.getCurr_value()) >= 12) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 12) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

                viewHolder.rangeTV.setText("12 - 15 g/dl");
            }
        }
    }

    private void showEyeLeftVision(ViewHolder viewHolder) {
        if (printData.getCurr_value().equals("")) {
            viewHolder.rangeTV.setText("6/6");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.rangeTV.setText("6/6");

            if (printData.getCurr_value().equals("6/6")) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                viewHolder.resultTV.setText("Not upto Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
            }
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
        }
    }

    private void showEyeRightVision(ViewHolder viewHolder) {
        if (printData.getCurr_value().equals("")) {
            viewHolder.rangeTV.setText("6/6");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            viewHolder.rangeTV.setText("6/6");

            if (printData.getCurr_value().equals("6/6")) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                viewHolder.resultTV.setText("Not upto Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
            }

            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
        }
    }


    private void showBloodGlucose(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (sharedPreferencesPersonalPreferencesGlucose.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Fasting (Before Meal)")) {
                if (Double.parseDouble(printData.getCurr_value()) > 100) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 100 && Double.parseDouble(printData.getCurr_value()) >= 70) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 70) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

                viewHolder.rangeTV.setText("70 - 100 mg/dl");

                SharedPreferences.Editor glucoseEditor = sharedPreferencesPersonalPreferencesGlucose.edit();
                glucoseEditor.putString("standardGlucose", "70-100 mg/dl");
                glucoseEditor.commit();

            } else if (sharedPreferencesPersonalPreferencesGlucose.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Post Prandial (After Meal)")) {
                if (Double.parseDouble(printData.getCurr_value()) > 140) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                } else if (Double.parseDouble(printData.getCurr_value()) <= 140 && Double.parseDouble(printData.getCurr_value()) >= 70) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                } else if (Double.parseDouble(printData.getCurr_value()) < 70) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

                viewHolder.rangeTV.setText("70 - 140 mg/dl");

                SharedPreferences.Editor glucoseEditor = sharedPreferencesPersonalPreferencesGlucose.edit();
                glucoseEditor.putString("standardGlucose", "70-140 mg/dl");
                glucoseEditor.commit();

            } else if (sharedPreferencesPersonalPreferencesGlucose.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Random (Not Sure)")) {
                if (Double.parseDouble(printData.getCurr_value()) > 160) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                } else if (Double.parseDouble(printData.getCurr_value()) <= 160 && Double.parseDouble(printData.getCurr_value()) >= 79) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                } else if (Double.parseDouble(printData.getCurr_value()) < 79) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

                viewHolder.rangeTV.setText("79 - 160 mg/dl");

                SharedPreferences.Editor glucoseEditor = sharedPreferencesPersonalPreferencesGlucose.edit();
                glucoseEditor.putString("standardGlucose", "79-160 mg/dl");
                glucoseEditor.commit();
            }
        }
    }

    private void showPulseRate(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (Double.parseDouble(printData.getCurr_value()) > 100) {

                viewHolder.resultTV.setText("High");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

            } else if (Double.parseDouble(printData.getCurr_value()) <= 100 && Double.parseDouble(printData.getCurr_value()) >= 60) {

                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else if (Double.parseDouble(printData.getCurr_value()) < 60) {

                viewHolder.resultTV.setText("Low");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            }

            viewHolder.rangeTV.setText("60 - 100 bpm");
        }
    }

    private void showBodyOxygen(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (Double.parseDouble(printData.getCurr_value()) >= 94) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else if (Double.parseDouble(printData.getCurr_value()) < 94) {
                viewHolder.resultTV.setText("Low");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            }

            viewHolder.rangeTV.setText(" > = 94 %");
        }
    }

    private void showDiastolicBloodPressure(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (Double.parseDouble(printData.getCurr_value()) > 89) {
                viewHolder.resultTV.setText("High");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

            } else if (Double.parseDouble(printData.getCurr_value()) <= 89 && Double.parseDouble(printData.getCurr_value()) >= 60) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else if (Double.parseDouble(printData.getCurr_value()) < 60) {
                viewHolder.resultTV.setText("Low");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            }

            viewHolder.rangeTV.setText("60 - 89 mmHg");
        }
    }

    private void showSystolicBloodPressure(ViewHolder viewHolder) {

        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (Double.parseDouble(printData.getCurr_value()) > 139) {
                viewHolder.resultTV.setText("High");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

            } else if (Double.parseDouble(printData.getCurr_value()) <= 139 && Double.parseDouble(printData.getCurr_value()) >= 90) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else if (Double.parseDouble(printData.getCurr_value()) < 90) {
                viewHolder.resultTV.setText("Low");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            }

            viewHolder.rangeTV.setText("90 - 139 mmHg");
        }
    }

    private void showBodyTemperature(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (Double.parseDouble(printData.getCurr_value()) > 99) {
                viewHolder.resultTV.setText("High");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

            } else if (Double.parseDouble(printData.getCurr_value()) <= 99 && Double.parseDouble(printData.getCurr_value()) >= 97) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else if (Double.parseDouble(printData.getCurr_value()) < 97) {
                viewHolder.resultTV.setText("Low");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            }

            viewHolder.rangeTV.setText("97-99F");
        }
    }

    private void showBoneMass(ViewHolder viewHolder) {
        double weight = getWeight();

        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            SharedPreferences.Editor editor = sharedPreferencesPersonalPreferencesActofit.edit();

            if (SharedPreferenceService.isMalePatient(context)) {
                if (weight > 75) {
                    viewHolder.rangeTV.setText("3.0 - 3.4 kg");
                    editor.putString("standardBoneMass", "3.0-3.4 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 3.4) {
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (Double.parseDouble(printData.getCurr_value()) <= 3.4 && Double.parseDouble(printData.getCurr_value()) >= 3.0) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 3.0) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (weight <= 75 && weight >= 60) {
                    viewHolder.rangeTV.setText("2.7 - 3.1 kg");
                    editor.putString("standardBoneMass", "2.7-3.1 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 3.1) {
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (Double.parseDouble(printData.getCurr_value()) >= 2.7 && Double.parseDouble(printData.getCurr_value()) <= 3.1) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 2.7) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (weight < 60) {
                    if (Double.parseDouble(printData.getCurr_value()) > 2.7) {
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (Double.parseDouble(printData.getCurr_value()) >= 2.3 && Double.parseDouble(printData.getCurr_value()) <= 2.7) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 2.3) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }

                    viewHolder.rangeTV.setText("2.3 - 2.7 kg");
                    editor.putString("standardBoneMass", "2.3-2.7 kg");
                    editor.commit();
                }
            } else {
                *//* Female bone mass *//*

                if (weight > 60) {
                    viewHolder.rangeTV.setText("2.3 - 2.7 kg");
                    editor.putString("standardBoneMass", "2.3-2.7 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 2.7) {
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (Double.parseDouble(printData.getCurr_value()) >= 2.3 && Double.parseDouble(printData.getCurr_value()) <= 2.7) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 2.3) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (weight >= 45 && weight <= 60) {
                    viewHolder.rangeTV.setText("2.0- 2.4 kg");
                    editor.putString("standardBoneMass", "2.0-2.4 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 2.4) {
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (Double.parseDouble(printData.getCurr_value()) >= 2.0 && Double.parseDouble(printData.getCurr_value()) <= 2.4) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 2.0) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (weight < 45) {
                    viewHolder.rangeTV.setText("1.6 - 2.0 kg");
                    editor.putString("standardBoneMass", "1.6-2.0 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 2.0) {
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (Double.parseDouble(printData.getCurr_value()) >= 1.6 && Double.parseDouble(printData.getCurr_value()) <= 2.0) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 1.6) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                }
            }
        }
    }

    private void showMuscleMass(ViewHolder viewHolder) {
        double height = getHeight();

        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            viewHolder.resultTV.setText("");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            SharedPreferences.Editor editor = sharedPreferencesPersonalPreferencesActofit.edit();

            if (sharedPreferencesPersonal.getString("gender", "").equals("male")) {
                if (height > 170) {
                    viewHolder.rangeTV.setText("49.4 - 59.5 kg");

                    editor.putString("standardMuscleMass", "49.4-59.5 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 59.4) {
                        viewHolder.resultTV.setText("Adequate");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) <= 59.4 && Double.parseDouble(printData.getCurr_value()) >= 49.4) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 49.4) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (height <= 170 && height >= 160) {
                    viewHolder.rangeTV.setText("44 - 52.4 kg");

                    editor.putString("standardMuscleMass", "44-52.4 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 52.4) {
                        viewHolder.resultTV.setText("Adequate");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) <= 52.4 && Double.parseDouble(printData.getCurr_value()) >= 44) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 44) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (height < 160) {
                    viewHolder.rangeTV.setText("38.5 - 46.5 kg");

                    editor.putString("standardMuscleMass", "38.5-46.5 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 46.5) {
                        viewHolder.resultTV.setText("Adequate");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                    } else if (Double.parseDouble(printData.getCurr_value()) <= 46.5 && Double.parseDouble(printData.getCurr_value()) >= 38.5) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                    } else if (Double.parseDouble(printData.getCurr_value()) < 38.5) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                }
            } else {
                *//* Female muscle mass *//*

                if (height > 160) {
                    viewHolder.rangeTV.setText("36.5 - 42.5 kg");

                    editor.putString("standardMuscleMass", "36.4-42.5 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 42.5) {
                        viewHolder.resultTV.setText("Adequate");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) <= 42.5 && Double.parseDouble(printData.getCurr_value()) >= 36.5) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 36.5) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (height <= 160 && height >= 150) {
                    viewHolder.rangeTV.setText("32.9 - 37.5 kg");
                    editor.putString("standardMuscleMass", "32.9-37.5 kg");
                    editor.commit();

                    if (Double.parseDouble(printData.getCurr_value()) > 37.5) {
                        viewHolder.resultTV.setText("Adequate");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) <= 37.5 && Double.parseDouble(printData.getCurr_value()) >= 32.9) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 32.9) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                } else if (height < 150) {
                    if (Double.parseDouble(printData.getCurr_value()) > 34.7) {
                        editor.putString("standardMuscleMass", "29.1-34.7 kg");
                        editor.commit();

                        viewHolder.resultTV.setText("Adequate");
                        viewHolder.rangeTV.setText("29.1 - 34.7 kg");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) <= 34.7 && Double.parseDouble(printData.getCurr_value()) >= 29.1) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (Double.parseDouble(printData.getCurr_value()) < 29.1) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }
                }
            }
        }
    }

    private void showPhysique(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.valueTV.setText("");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            viewHolder.resultTV.setText("" + sharedPreferencesPersonalPreferencesActofit.getString("physique", ""));
        }
    }

    private void showBmr(ViewHolder viewHolder) {
        int age = getAge();
        double weight = getWeight();

        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            SharedPreferences.Editor bmrEditor = sharedPreferencesPersonalPreferencesActofit.edit();

            double standardMetabolism = 0.0;

            if (SharedPreferenceService.isMalePatient(context)) {
                if (age >= 70)
                    standardMetabolism = 21.5 * weight;
                else if (age >= 50 && age <= 69)
                    standardMetabolism = 21.5 * weight;
                else if (age >= 30 && age <= 49)
                    standardMetabolism = 22.3 * weight;
                else if (age >= 18 && age <= 29)
                    standardMetabolism = 24 * weight;

                if (Double.parseDouble(printData.getCurr_value()) >= standardMetabolism) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                } else if (Double.parseDouble(printData.getCurr_value()) < standardMetabolism) {
                    viewHolder.resultTV.setText("Not upto Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                }

                standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));

                viewHolder.rangeTV.setText(" > = " + standardMetabolism + "Kcal");

                bmrEditor.putString("standardBMR", "" + standardMetabolism);
                bmrEditor.commit();
            } else {
                if (age >= 70)
                    standardMetabolism = 20.7 * weight;
                else if (age >= 50 && age <= 69)
                    standardMetabolism = 20.7 * weight;
                else if (age >= 30 && age <= 49)
                    standardMetabolism = 21.7 * weight;
                else if (age >= 18 && age <= 29)
                    standardMetabolism = 23.6 * weight;

                if (Double.parseDouble(printData.getCurr_value()) >= standardMetabolism) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                } else if (Double.parseDouble(printData.getCurr_value()) < standardMetabolism) {
                    viewHolder.resultTV.setText("Not upto Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                }

                standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));

                viewHolder.rangeTV.setText(" > = " + standardMetabolism + "Kcal");

                bmrEditor.putString("standardBMR", "" + standardMetabolism);
                bmrEditor.commit();
            }
        }
    }

    private void showMetabolicAge(ViewHolder viewHolder) {
        int age = getAge();

        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("<= " + age + " yrs");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (Double.parseDouble(printData.getCurr_value()) <= age) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else if (Double.parseDouble(printData.getCurr_value()) > age) {
                viewHolder.resultTV.setText("Not up to Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
            }
        }
    }

    private void showHealthScore(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
    }

    private void showProtein(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.rangeTV.setText("16 - 18 %");
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            if (Double.parseDouble(printData.getCurr_value()) > 18) {
                viewHolder.resultTV.setText("Adequate");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else if (Double.parseDouble(printData.getCurr_value()) <= 18 && Double.parseDouble(printData.getCurr_value()) > 16) {
                viewHolder.resultTV.setText("Standard");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else if (Double.parseDouble(printData.getCurr_value()) < 16) {
                viewHolder.resultTV.setText("Low");
                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            }
        }
    }

    private void showSkeletalMuscle(ViewHolder viewHolder) {
        if (Double.parseDouble(printData.getCurr_value()) == 0.0) {
            viewHolder.rangeTV.setText("");
            viewHolder.resultTV.setText("");
            viewHolder.valueTV.setText("NA");
            viewHolder.parameterTV.setText("" + printData.getParameter());
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        } else {
            viewHolder.valueTV.setText("" + printData.getCurr_value());
            viewHolder.parameterTV.setText("" + printData.getParameter());

            if (SharedPreferenceService.isMalePatient(context)) {
                *//* Male skeleton muscle *//*

                if (Double.parseDouble(printData.getCurr_value()) > 59) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                } else if (Double.parseDouble(printData.getCurr_value()) <= 59 && Double.parseDouble(printData.getCurr_value()) >= 49) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (Double.parseDouble(printData.getCurr_value()) < 49) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }

                viewHolder.rangeTV.setText("49 - 59 %");
            } else {
                *//* Female skeleton muscle *//*

                viewHolder.rangeTV.setText("40 - 50 %");

                if (Double.parseDouble(printData.getCurr_value()) > 50) {
                    viewHolder.resultTV.setText("High");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                } else if (Double.parseDouble(printData.getCurr_value()) <= 50 && Double.parseDouble(printData.getCurr_value()) >= 40) {
                    viewHolder.resultTV.setText("Standard");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                } else if (Double.parseDouble(printData.getCurr_value()) < 40) {
                    viewHolder.resultTV.setText("Low");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }
            }
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
    }*/

    // endregion
}
