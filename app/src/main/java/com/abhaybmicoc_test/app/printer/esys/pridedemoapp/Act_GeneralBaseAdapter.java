package com.abhaybmicoc_test.app.printer.esys.pridedemoapp;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abhaybmicoc_test.app.R;

public class Act_GeneralBaseAdapter extends BaseAdapter {
    Context context;
    List<Act_GenRowItem> rowItems;
     
    public Act_GeneralBaseAdapter(Context context, List<Act_GenRowItem> items) {
        this.context = context;
        this.rowItems = items;
    }
     
    /*private view holder class*/
    private class ViewHolder {
        TextView tvTitle;
        TextView tvDesc;
    	}
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) 
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.tvDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Act_GenRowItem rowItem = (Act_GenRowItem) getItem(position);
        holder.tvDesc.setText(rowItem.getDesc());
        holder.tvTitle.setText(rowItem.getTitle());
        return convertView;
    }
 
    @Override
    public int getCount() {     
        return rowItems.size();
    }
 
    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

}
