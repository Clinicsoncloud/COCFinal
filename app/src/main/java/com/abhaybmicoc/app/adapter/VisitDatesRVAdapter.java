package com.abhaybmicoc.app.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.interfaces.RvClickListener;
import com.abhaybmicoc.app.model.Patient_Visit_Response;
import com.abhaybmicoc.app.utils.DTU;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Vaibhav 27-02-2019.
 */

public class VisitDatesRVAdapter extends RecyclerView.Adapter<VisitDatesRVAdapter.ViewHolder> {
    private final List<Patient_Visit_Response.Patient_Visit_Data> dataList;
    Context context;
    View itemView;
    RvClickListener rvClickListener;


    int pos = 0;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    public VisitDatesRVAdapter(Context context, List<Patient_Visit_Response.Patient_Visit_Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_VisitDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_VisitDate = itemView.findViewById(R.id.tv_VisitDate);

        }
    }

    public void setRvClickListener(RvClickListener rvClickListener) {
        this.rvClickListener = rvClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_visit_dates_layout, parent, false);

        ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public Object getItem(int position) {
        return dataList.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        pos = position;

        holder.tv_VisitDate.setText(DTU.get_DateMonthOnlyFromTimeZoneDate(dataList.get(position).getCreatedAt()));

        if (dataList.get(position).getIsSelectedDate() != null && dataList.get(position).getIsSelectedDate()) {
            holder.tv_VisitDate.setTextColor(context.getColor(R.color.green));
        } else {
            holder.tv_VisitDate.setTextColor(context.getColor(R.color.white_1000));

        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dmafdavbndvndvczv", "sdfhfdhfd");
                rvClickListener.rv_click(position, 0, "selected_date");
            }
        });
    }

    @Override
    public int getItemCount() {

        return dataList.size();
    }

}
