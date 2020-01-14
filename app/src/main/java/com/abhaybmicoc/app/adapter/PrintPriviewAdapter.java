package com.abhaybmicoc.app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.model.PrintData;
import com.abhaybmicoc.app.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;


public class PrintPriviewAdapter extends ArrayAdapter<PrintData> {


    Context context;
    List<PrintData> printDataList = new ArrayList<>();
    SharedPreferences shared;
    PrintData printData;



    public PrintPriviewAdapter(Context context, int resource, List<PrintData> objects) {
        super(context, resource, objects);
        shared = context.getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
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

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.printlist_item, null);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

    /*    boolean isMale = false;
        if(shared.getString("gendar","").equals("male"))
           isMale = true;
        switch (position){

            case 0:

                if(isMale){



                }

                break;

            case 1:
                break;

            case 2:
                if(isMale){

                    if(printData.getCurr_value() > 26){
                        viewHolder.resultTV.setText("Seriously High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    }else if(printData.getCurr_value() < 26 && printData.getCurr_value() > 22){
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.yellow1));
                    }else if(printData.getCurr_value() < 21 && printData.getCurr_value() > 11){
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                    }else if(printData.getCurr_value() < 11){
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    }

                    viewHolder.rangeTV.setText();

                }else {

                    if(printData.getCurr_value() > 36){
                        viewHolder.resultTV.setText("Seriously High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    }else if(printData.getCurr_value() < 36 && printData.getCurr_value() > 31){
                        viewHolder.resultTV.setText("High");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.yellow1));
                    }else if(printData.getCurr_value() < 30 && printData.getCurr_value() > 21){
                        viewHolder.resultTV.setText("Standard");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
                    }else if(printData.getCurr_value() < 21){
                        viewHolder.resultTV.setText("Low");
                        viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
                    }


                }
                break;


        }
*/


        viewHolder.parameterTV.setText(printData.getParameter());
        viewHolder.valueTV.setText("" + printData.getCurr_value());
        if (printData.getCurr_value() >= printData.getMinRange()&&printData.getCurr_value()<=printData.getMaxRange()) {
            viewHolder.resultTV.setText("Standard");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.green));
        }if (printData.getCurr_value() < printData.getMinRange()) {
            viewHolder.resultTV.setText("Low");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.solid_red));
        }if (printData.getCurr_value() > printData.getMaxRange()) {
            viewHolder.resultTV.setText("High");
            viewHolder.resultTV.setBackgroundColor(context.getResources().getColor(R.color.yellow1));
        }
        viewHolder.rangeTV.setText(printData.getMinRange()+" - "+printData.getMaxRange()+" "+printData.getUnit());

        return convertView;
    }





    static
    class ViewHolder {
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
