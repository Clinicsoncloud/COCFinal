package com.abhaybmicoc.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.model.PrintDataNew;
import com.abhaybmicoc.app.utils.ApiUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class PrintPreviewActivityNew extends ArrayAdapter<PrintDataNew> {


    Context context;
    List<PrintDataNew> printDataList = new ArrayList<>();
    SharedPreferences shared;
    SharedPreferences actofitData;
    SharedPreferences bpObject;
    SharedPreferences glucoseData;
    SharedPreferences HemoglobinObject;
    PrintDataNew printData;

    int age;

    Double weight;

    int height;

    String parsedDate;

    public PrintPreviewActivityNew(Context context, int resource, List<PrintDataNew> objects) {
        super(context, resource, objects);
        shared = context.getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        actofitData = context.getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        bpObject = context.getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
        glucoseData = context.getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
        HemoglobinObject = context.getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        this.printDataList = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return printDataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        printData = printDataList.get(position);

        PrintpriviewAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.printlist_item, null);

            viewHolder = new PrintpriviewAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (PrintpriviewAdapter.ViewHolder) convertView.getTag();
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String inputText = shared.getString("dob", "");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = inputDateFormat.parse(inputText);
            parsedDate = formatter.format(date);
            System.out.println("Date---------" + parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        age = getAge(parsedDate);

        if (!actofitData.getString("weight", "").isEmpty())
            weight = Double.valueOf(actofitData.getString("weight", ""));
            height = Integer.parseInt(actofitData.getString("height", ""));

        String isGender = shared.getString("gender", "");


        Log.e("height", "" + height);
        Log.e("age", "" + age);
        Log.e("gender", "" + isGender);
        Log.e("subcutaneous fat", "" + actofitData.getString("subfat", ""));


        switch (position) {

            case 0: //weight

                if (printData.getCurr_value() == 0.0) {
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
                    SharedPreferences.Editor objectShared = shared.edit();

                    Log.e("standardWeightMen", " : " + standardWeightMen);
                    Log.e("standardWeightFemale", " : " + standardWeightFemale);

                    if (shared.getString("gender", "").equals("male")) {

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

                        if (printData.getCurr_value() > standarWeightHighTo) {
                            viewHolder.resultTV.setText("Seriously High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                        } else if (printData.getCurr_value() <= standarWeightHighTo && printData.getCurr_value() >= standarWeightHighFrom) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= standardWeighRangeTo && printData.getCurr_value() >= standardWeighRangeFrom) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() <= standarWeightLowTo && printData.getCurr_value() >= standarWeightLowFrom) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        } else if (printData.getCurr_value() < standarWeightLowFrom) {

                            viewHolder.resultTV.setText("Seriously Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                        }


                    } else { //femle weight


                        double standardWeighRangeFrom = (0.90 * standardWeightFemale);
                        standardWeighRangeFrom = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeFrom));
                        double standardWeighRangeTo = (1.09 * standardWeightFemale);
                        standardWeighRangeTo = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeTo));

                        viewHolder.rangeTV.setText("" + standardWeighRangeFrom + " - " + standardWeighRangeTo + "kg");
                        objectShared.putString("standardRangeFemaleFrom", "" + (0.90 * standardWeightFemale));
                        objectShared.putString("standardRangeFemaleTo", "" + (1.09 * standardWeightFemale));
                        objectShared.commit();

                        if (printData.getCurr_value() > (1.20 * standardWeightFemale)) {

                            viewHolder.resultTV.setText("Seriously High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() >= (1.10 * standardWeightFemale) && printData.getCurr_value() <= (1.20 * standardWeightFemale)) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() >= (0.90 * standardWeightFemale) && printData.getCurr_value() <= (1.09 * standardWeightFemale)) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() >= (0.80 * standardWeightFemale) && printData.getCurr_value() <= (0.89 * standardWeightFemale)) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        } else if (printData.getCurr_value() < (0.80 * standardWeightFemale)) {

                            viewHolder.resultTV.setText("Seriously Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        }
                    }
                }
                break;

            case 1: //bmi
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("18.5 - 25");

                    if (printData.getCurr_value() > 25) {
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    } else if (printData.getCurr_value() <= 25 && printData.getCurr_value() >= 18.5) {
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                    } else if (printData.getCurr_value() < 18.5) {
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    }
                }
                break;

            case 2: //Body Fat
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());

                    if (shared.getString("gender", "").equals("male")) {

                        if (printData.getCurr_value() > 26) {
                            viewHolder.resultTV.setText("Seriously High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                        } else if (printData.getCurr_value() <= 26 && printData.getCurr_value() >= 21) {
                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                        } else if (printData.getCurr_value() <= 21 && printData.getCurr_value() >= 11) {
                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                        } else if (printData.getCurr_value() < 11) {
                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                        }
                        viewHolder.rangeTV.setText("11 - 21" + "(%)");


                    } else { //female bofy fat

                        viewHolder.rangeTV.setText("21 - 30" + "(%)");

                        if (printData.getCurr_value() > 36) {
                            viewHolder.resultTV.setText("Seriously High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                        } else if (printData.getCurr_value() <= 36 && printData.getCurr_value() > 30) {
                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                        } else if (printData.getCurr_value() <= 30 && printData.getCurr_value() >= 21) {
                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                        } else if (printData.getCurr_value() < 21) {
                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                        }
                    }
                }
                break;

            case 3: //Fat Free Weight
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                }
                break;

            case 4: //Subcutaneous Fat (%)
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");

                }else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText(""+printData.getCurr_value());
                    if (shared.getString("gender", "").equals("male")) {

                        viewHolder.rangeTV.setText("8.6 - 16.7");

                        if (printData.getCurr_value() > 16.7) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= 16.7 && printData.getCurr_value() >= 8.6) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 8.6) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        }


                    } else { // female subcutaneous fat

                        viewHolder.rangeTV.setText("18.5 - 26.7");

                        if (printData.getCurr_value() > 26.7) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= 26.7 && printData.getCurr_value() >= 18.5) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 18.5) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                        }
                    }
                }
                break;

            case 5: //Visceral Fat
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {

                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText(" < = 9");

                    if (printData.getCurr_value() > 14) {

                        viewHolder.resultTV.setText("Seriously High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (printData.getCurr_value() <= 14 && printData.getCurr_value() >= 10) {

                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    } else if (printData.getCurr_value() <= 9) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                    }
                }

                break;

            case 6: //Body water (%)

                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());

                    if (shared.getString("gender", "").equals("male")) {

                        viewHolder.rangeTV.setText("55 - 65 %");

                        if (printData.getCurr_value() > 65) {

                            viewHolder.resultTV.setText("Adequate");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() <= 65 && printData.getCurr_value() >= 55) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 55) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        }

                    } else { //female body water

                        viewHolder.rangeTV.setText("45 - 60 %");

                        if (printData.getCurr_value() > 60) {

                            viewHolder.resultTV.setText("Adequate");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() <= 60 && printData.getCurr_value() >= 45) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 45) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        }
                    }
                }

                break;

            case 7: //Skeleton muscle (%)
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.parameterTV.setText("" + printData.getParameter());

                    if (shared.getString("gender", "").equals("male")) {

                        if (printData.getCurr_value() > 59) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= 59 && printData.getCurr_value() >= 49) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 49) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        }

                        viewHolder.rangeTV.setText("49 - 59 %");

                    } else {  //female skeleton muscle

                        viewHolder.rangeTV.setText("40 - 50 %");

                        if (printData.getCurr_value() > 50) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= 50 && printData.getCurr_value() >= 40) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 40) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        }
                    }
                }
                break;


            case 8: //Protein
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("16 - 18 %");

                    if (printData.getCurr_value() > 18) {

                        viewHolder.resultTV.setText("Adequate");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() <= 18 && printData.getCurr_value() > 16) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() < 16) {

                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                    }
                }

                break;

            case 9: //Metabolic age

                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    ///viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("<="+age+"yrs");
                    if (printData.getCurr_value() <= age) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() > age) {

                        viewHolder.resultTV.setText("Not upto Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    }
                }
                break;

            case 10: //Health Score
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {

                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                }
                break;

            /*case 11: //phisique

                viewHolder.parameterTV.setText(""+printData.getParameter());
                viewHolder.valueTV.setText("" +printData.getCurr_value());

                break;*/

            case 11:  //BMR
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {

                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");

                    SharedPreferences.Editor bmrEditor = actofitData.edit();

                    double standardMetabolism = 0.0;

                    if (shared.getString("gender", "").equals("male")) {

                        if (age >= 70) {

                            standardMetabolism = 21.5 * weight;

                        } else if (age >= 50 && age <= 69) {

                            standardMetabolism = 21.5 * weight;


                        } else if (age >= 30 && age <= 49) {

                            standardMetabolism = 22.3 * weight;


                        } else if (age >= 18 && age <= 29) {

                            standardMetabolism = 24 * weight;

                        }

                        if (printData.getCurr_value() >= standardMetabolism) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < standardMetabolism) {

                            viewHolder.resultTV.setText("Not upto Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        }

                        standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));

                        viewHolder.rangeTV.setText(" > = " + standardMetabolism + "Kcal");
                        bmrEditor.putString("standardBMR", "" + standardMetabolism);
                        bmrEditor.commit();


                    } else {


                        if (age >= 70) {

                            standardMetabolism = 20.7 * weight;

                        } else if (age >= 50 && age <= 69) {

                            standardMetabolism = 20.7 * weight;


                        } else if (age >= 30 && age <= 49) {

                            standardMetabolism = 21.7 * weight;


                        } else if (age >= 18 && age <= 29) {

                            standardMetabolism = 23.6 * weight;

                        }

                        if (printData.getCurr_value() >= standardMetabolism) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                        } else if (printData.getCurr_value() < standardMetabolism) {

                            viewHolder.resultTV.setText("Not upto Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        }
                        standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
                        viewHolder.rangeTV.setText(" > = " + standardMetabolism + "Kcal");
                        bmrEditor.putString("standardBMR", "" + standardMetabolism);
                        bmrEditor.commit();

                    }
                }
                break;


            case 12://phisique
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("");
                    viewHolder.resultTV.setText("" + actofitData.getString("physique", ""));
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                }
                break;

            case 13: //Muscle mass
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");

                    SharedPreferences.Editor editor = actofitData.edit();

                    if (shared.getString("gender", "").equals("male")) {

                        if (height > 170) {

                            viewHolder.rangeTV.setText("49.4 - 59.5 kg");
                            editor.putString("standardMuscleMass", "49.4-59.5 kg");
                            editor.commit();

                            if (printData.getCurr_value() > 59.4) {

                                viewHolder.resultTV.setText("Adequate");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                            } else if (printData.getCurr_value() <= 59.4 && printData.getCurr_value() >= 49.4) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 49.4) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }


                        } else if (height <= 170 && height >= 160) {

                            viewHolder.rangeTV.setText("44 - 52.4 kg");
                            editor.putString("standardMuscleMass", "44-52.4 kg");
                            editor.commit();

                            if (printData.getCurr_value() > 52.4) {

                                viewHolder.resultTV.setText("Adequate");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                            } else if (printData.getCurr_value() <= 52.4 && printData.getCurr_value() >= 44) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 44) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }


                        } else if (height < 160) {

                            viewHolder.rangeTV.setText("38.5 - 46.5 kg");
                            editor.putString("standardMuscleMass", "38.5-46.5 kg");
                            editor.commit();

                            if (printData.getCurr_value() > 46.5) {

                                viewHolder.resultTV.setText("Adequate");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                            } else if (printData.getCurr_value() <= 46.5 && printData.getCurr_value() >= 38.5) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 38.5) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }


                        }
                    } else { // female muscle mass

                        if (height > 160) {

                            viewHolder.rangeTV.setText("36.5 - 42.5 kg");
                            editor.putString("standardMuscleMass", "36.4-42.5 kg");
                            editor.commit();

                            if (printData.getCurr_value() > 42.5) {

                                viewHolder.resultTV.setText("Adequate");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                            } else if (printData.getCurr_value() <= 42.5 && printData.getCurr_value() >= 36.5) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 36.5) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }

                        } else if (height <= 160 && height >= 150) {

                            viewHolder.rangeTV.setText("32.9 - 37.5 kg");
                            editor.putString("standardMuscleMass", "32.9-37.5 kg");
                            editor.commit();

                            if (printData.getCurr_value() > 37.5) {

                                viewHolder.resultTV.setText("Adequate");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                            } else if (printData.getCurr_value() <= 37.5 && printData.getCurr_value() >= 32.9) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 32.9) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }


                        } else if (height < 150) {

                            if (printData.getCurr_value() > 34.7) {

                                viewHolder.rangeTV.setText("29.1 - 34.7 kg");
                                editor.putString("standardMuscleMass", "29.1-34.7 kg");
                                editor.commit();

                                viewHolder.resultTV.setText("Adequate");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                            } else if (printData.getCurr_value() <= 34.7 && printData.getCurr_value() >= 29.1) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 29.1) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }


                        }
                    }
                }
                break;

            case 14: // Bone mass
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");

                    SharedPreferences.Editor editor1 = actofitData.edit();


                    if (shared.getString("gender", "").equals("male")) {

                        if (weight > 75) {

                            viewHolder.rangeTV.setText("3.0 - 3.4 kg");
                            editor1.putString("standardBoneMass", "3.0-3.4 kg");
                            editor1.commit();

                            if (printData.getCurr_value() > 3.4) {

                                viewHolder.resultTV.setText("High");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                            } else if (printData.getCurr_value() <= 3.4 && printData.getCurr_value() >= 3.0) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 3.0) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }


                        } else if (weight <= 75 && weight >= 60) {

                            viewHolder.rangeTV.setText("2.7 - 3.1 kg");
                            editor1.putString("standardBoneMass", "2.7-3.1 kg");
                            editor1.commit();

                            if (printData.getCurr_value() > 3.1) {

                                viewHolder.resultTV.setText("High");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                            } else if (printData.getCurr_value() >= 2.7 && printData.getCurr_value() <= 3.1) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 2.7) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }


                        } else if (weight < 60) {

                            if (printData.getCurr_value() > 2.7) {

                                viewHolder.resultTV.setText("High");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                            } else if (printData.getCurr_value() >= 2.3 && printData.getCurr_value() <= 2.7) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 2.3) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }

                            viewHolder.rangeTV.setText("2.3 - 2.7 kg");
                            editor1.putString("standardBoneMass", "2.3-2.7 kg");
                            editor1.commit();


                        }
                    } else { // female bone mass

                        if (weight > 60) {

                            viewHolder.rangeTV.setText("2.3 - 2.7 kg");
                            editor1.putString("standardBoneMass", "2.3-2.7 kg");
                            editor1.commit();

                            if (printData.getCurr_value() > 2.7) {

                                viewHolder.resultTV.setText("High");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                            } else if (printData.getCurr_value() >= 2.3 && printData.getCurr_value() <= 2.7) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 2.3) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));


                            }


                        } else if (weight >= 45 && weight <= 60) {

                            viewHolder.rangeTV.setText("2.0- 2.4 kg");
                            editor1.putString("standardBoneMass", "2.0-2.4 kg");
                            editor1.commit();

                            if (printData.getCurr_value() > 2.4) {

                                viewHolder.resultTV.setText("High");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));


                            } else if (printData.getCurr_value() >= 2.0 && printData.getCurr_value() <= 2.4) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                            } else if (printData.getCurr_value() < 2.0) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }

                        } else if (weight < 45) {

                            viewHolder.rangeTV.setText("1.6 - 2.0 kg");
                            editor1.putString("standardBoneMass", "1.6-2.0 kg");
                            editor1.commit();

                            if (printData.getCurr_value() > 2.0) {

                                viewHolder.resultTV.setText("High");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                            } else if (printData.getCurr_value() >= 1.6 && printData.getCurr_value() <= 2.0) {

                                viewHolder.resultTV.setText("Standard");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                            } else if (printData.getCurr_value() < 1.6) {

                                viewHolder.resultTV.setText("Low");
                                viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                            }
                        }
                    }
                }
                break;
            case 15: //Body temp
                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("");
                    if (printData.getCurr_value() > 99) {

                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (printData.getCurr_value() <= 99 && printData.getCurr_value() >= 97) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() < 97) {

                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }

                    viewHolder.rangeTV.setText("97-99F");
                }

                break;
            case 16: // Blood Pressure systolic

                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {

                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("");
                    if (printData.getCurr_value() > 139) {

                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (printData.getCurr_value() <= 139 && printData.getCurr_value() >= 90) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() < 90) {

                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }

                    viewHolder.rangeTV.setText("90 - 139 mmHg");
                }
                break;

            case 17: //Blood pressure diastolic

                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {

                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("");
                    if (printData.getCurr_value() > 89) {

                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (printData.getCurr_value() <= 89 && printData.getCurr_value() >= 60) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() < 60) {

                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                    }

                    viewHolder.rangeTV.setText("60 - 89 mmHg");

                }

                break;

            case 18:// pulse oximeter

                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {

                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("");
                    if (printData.getCurr_value() >= 94) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() < 94) {

                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                    }

                    viewHolder.rangeTV.setText(" > = 94 %");

                }
                break;

            case 19: //pulse

                if (printData.getCurr_value() == 0.0) {

                    viewHolder.valueTV.setText("NA");
                    viewHolder.parameterTV.setText(""+printData.getParameter());
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");

                } else {

                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("");

                    if (printData.getCurr_value() > 100) {

                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                    } else if (printData.getCurr_value() <= 100 && printData.getCurr_value() >= 60) {

                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                    } else if (printData.getCurr_value() < 60) {

                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                    }

                    viewHolder.rangeTV.setText("60 - 100 bpm");
                }
                break;
            case 20: // blood glucose

                if (printData.getCurr_value() == 0.0) {
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.valueTV.setText("NA");
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");
                } else {

                    viewHolder.valueTV.setText("" +printData.getCurr_value());
                    viewHolder.parameterTV.setText("" +printData.getParameter());
                    viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("");

                    SharedPreferences.Editor glucoseEditor = glucoseData.edit();

                    if (glucoseData.getString("glucosetype", "").equals("Fasting (Before Meal)")) {

                        if (printData.getCurr_value() > 100) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));


                        } else if (printData.getCurr_value() <= 100 && printData.getCurr_value() >= 70) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                        } else if (printData.getCurr_value() < 70) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));


                        }

                        viewHolder.rangeTV.setText("70 - 100 mg/dl");
                        glucoseEditor.putString("standardGlucose", "70-100 mg/dl");
                        glucoseEditor.commit();


                    } else if (glucoseData.getString("glucosetype", "").equals("Post Prandial (After Meal)")) {

                        if (printData.getCurr_value() > 140) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= 140 && printData.getCurr_value() >= 70) {


                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 70) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));


                        }

                        viewHolder.rangeTV.setText("70 - 140 mg/dl");
                        glucoseEditor.putString("standardGlucose", "70-140 mg/dl");
                        glucoseEditor.commit();

                    } else if (glucoseData.getString("glucosetype", "").equals("Random (Not Sure)")) {

                        if (printData.getCurr_value() > 160) {
                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));


                        } else if (printData.getCurr_value() <= 160 && printData.getCurr_value() >= 79) {
                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));


                        } else if (printData.getCurr_value() < 79) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                        }

                        viewHolder.rangeTV.setText("79 - 160 mg/dl");
                        glucoseEditor.putString("standardGlucose", "79-160 mg/dl");
                        glucoseEditor.commit();
                    }
                }
                break;
            case 21: // hemoglobin

                if (printData.getCurr_value() == 0.0) {

                    viewHolder.valueTV.setText("NA");
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.resultTV.setText("");
                    viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    viewHolder.rangeTV.setText("");


                } else {
                    viewHolder.valueTV.setText("" + printData.getCurr_value());
                    viewHolder.parameterTV.setText("" + printData.getParameter());
                    viewHolder.resultTV.setText("");
                    viewHolder.rangeTV.setText("");

                    if (shared.getString("gender", "").equals("male")) {

                        if (printData.getCurr_value() > 17.2) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= 17.2 && printData.getCurr_value() >= 13.8) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 13.8) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                        }

                        viewHolder.rangeTV.setText("13.8 - 17.2 g/dl");


                    } else {

                        if (printData.getCurr_value() > 15.1) {

                            viewHolder.resultTV.setText("High");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));

                        } else if (printData.getCurr_value() <= 15.1 && printData.getCurr_value() >= 12.1) {

                            viewHolder.resultTV.setText("Standard");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));

                        } else if (printData.getCurr_value() < 12.1) {

                            viewHolder.resultTV.setText("Low");
                            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));

                        }
                        viewHolder.rangeTV.setText("12.1 - 15.1 g/dl");
                    }
                }
                break;
        }

        return convertView;
    }


    private int getAge(String dobString) {

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null)
            return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }


        return age;
    }


    static class ViewHolder {
        @BindView(R.id.parameterTV)
        TextView parameterTV;
        @BindView(R.id.resultTV)
        TextView resultTV;
        @BindView(R.id.valueTV)
        TextView valueTV;
        @BindView(R.id.rangeTV)
        TextView rangeTV;


        ViewHolder(View view) {

            ButterKnife.bind(this, view);
        }
    }


}
