package com.abhaybmicoc.app.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.SettingsActivity;
import com.abhaybmicoc.app.interfaces.RvClickListener;
import com.abhaybmicoc.app.utils.ErrorUtils;

import org.json.JSONArray;


/**
 * Created by Vaibhav .
 */

public class ConnectedDevicesListAdapter extends RecyclerView.Adapter<ConnectedDevicesListAdapter.ViewHolder> {
    private final JSONArray dataArray;
    Context context;
    View itemView;
    RvClickListener rvClickListener;


    public ConnectedDevicesListAdapter(Context context, JSONArray dataArray) {
        this.context = context;
        this.dataArray = dataArray;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDeviceName;


        public ViewHolder(View itemView) {
            super(itemView);

            tvDeviceName = itemView.findViewById(R.id.tv_DeviceName);

        }
    }

    public void setRvClickListener(SettingsActivity rvClickListener) {
        this.rvClickListener = rvClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        itemView = LayoutInflater.from(context).inflate(R.layout.row_connected_list_layout, parent, false);

        ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            holder.tvDeviceName.setText(dataArray.getJSONObject(position).getString("device_name"));

            if (dataArray.getJSONObject(position).getString("is_selected").equals("1")) {
                holder.tvDeviceName.setBackground(context.getResources().getDrawable(R.drawable.rectangle_green));
            } else {
                holder.tvDeviceName.setBackground(context.getResources().getDrawable(R.drawable.rectangle));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        rvClickListener.rv_click(position, 0, dataArray.getJSONObject(position).getString("device_name"));
                    } catch (Exception e) {
                        ErrorUtils.logErrors(context,e,"ConnectedDevicesListAdapter","onBindViewHolder",""+e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            ErrorUtils.logErrors(context,e,"ConnectedDevicesListAdapter","onBindViewHolder",""+e.getMessage());
        }

    }


    @Override
    public int getItemCount() {

        return dataArray.length();
    }

}
