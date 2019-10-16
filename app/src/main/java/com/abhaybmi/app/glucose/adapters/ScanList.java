package com.abhaybmi.app.glucose.adapters;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhaybmi.app.R;
import com.abhaybmi.app.glucose.models.ResultsModel;

import org.maniteja.com.synclib.helper.HelperC;
import org.maniteja.com.synclib.helper.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sreyas V Pariyath on 4/28/16.
 */
public class ScanList extends RecyclerView.Adapter<ScanList.MyViewHolder>
    {
        private LayoutInflater layoutInflater;
        private ArrayList<BluetoothDevice> mLeDevices;
        boolean uuidflag = true;
        private int activityFlag = 0;
        Context ctx;
        List<ResultsModel> data = Collections.emptyList();
        Util util;

        public ScanList(Context context, List<ResultsModel> data, boolean uuidF, int flag, Activity activity)
        {

            layoutInflater = LayoutInflater.from(context);
            mLeDevices = new ArrayList<BluetoothDevice>();
            this.data = data;
            uuidflag = uuidF;
            activityFlag = flag;
            ctx=context;
            util = new Util(ctx,activity);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = layoutInflater.inflate(R.layout.blelistrow, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            // ResultsModel current = data.get(position);
            BluetoothDevice device = mLeDevices.get(position);
            String deviceName = device.getName();
            if(!util.readString(HelperC.key_devname,"").equals(""))
            {
                System.out.println("Scan List : 1 " + device.getAddress() + " :: " + device.getName() + " :: " + util.readString(HelperC.key_devname,""));
                if(util.readString(HelperC.key_autoconnectaddress, "").equals(device.getAddress()))
                {
                    System.out.println("Scan List : 2 " + device.getAddress() + " :: " + device.getName() + " :: " + util.readString(HelperC.key_autoconnectaddress, ""));
                    if(!util.readString(HelperC.key_devname, "").equals(device.getName()))
                    {
                        System.out.println("Scan List : 3 before" + device.getAddress() + " :: " + device.getName() + " :: " + util.readString(HelperC.key_devname,""));
                        deviceName = util.readString(HelperC.key_devname, "");
                        System.out.println("Scan List : 3 " + device.getAddress() + " :: " + device.getName() + " :: " + util.readString(HelperC.key_devname,""));
                    }
                    else
                    {
                        System.out.println("Scan List : 4 " + device.getAddress() + " :: " + device.getName());
                    }
                }
                else
                {
//                    util.putString(HelperC.key_devname,device.getOperatorName());
                    System.out.println("Scan List : 5 " + device.getAddress() + " :: " + device.getName());
                }
            }
            else
            {
                System.out.println("Scan List : 6 " + device.getAddress() + " :: " + device.getName());
            }
            if (deviceName != null && deviceName.length() > 0)
                holder.txtName.setText(deviceName);//new Util(ctx).readString(device.getAddress(),device.getOperatorName())
            else
                holder.txtName.setText(R.string.unknown_device);
            // viewHolder.deviceAddress.setText(device.getAddress());
           /* holder.txtAge.setText(current.getAge());
            holder.txtSex.setText(current.getSex());
            holder.txtDate.setText(current.getDate());
            holder.txtSync.setText(current.getSyncStatus());
            holder.txtoperator.setText(current.getOperatorid());
            holder.txthb.setText(current.getResult());
            holder.txtuuid.setText(current.getUuid());
            holder.txtpagenumber.setText(current.getItemnumber());
            holder.txtTime.setText(current.getTime());*/
           /* if (!uuidflag)
                {
                    holder.txtuuid.setVisibility(View.GONE);
                    holder.txtuuidh.setVisibility(View.GONE);
                }
            if(activityFlag== ActivityClassTracker.loadImage)
                {
                    holder.thumbnail.setImageBitmap(current.getThumbnail());
                    holder.thumbnail.setVisibility(View.GONE);
                }
            else
                {
                    holder.thumbnail.setVisibility(View.GONE);
                }*/

        }

        @Override
        public int getItemCount()
        {
            return mLeDevices.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            ImageView thumbnail;
            TextView txtName, txtAge, txtSex, txtDate, txtoperator, txtSync, txthb, txtuuid, txtuuidh, txtpagenumber, txtTime;

            public MyViewHolder(View itemView)
            {
                super(itemView);
                txtName = (TextView) itemView.findViewById(R.id.btname);
               /* txtAge = (TextView) itemView.findViewById(R.id.txtAge);
                txtSex = (TextView) itemView.findViewById(R.id.txtSex);
                txtDate = (TextView) itemView.findViewById(R.id.txtDate);
                txtoperator = (TextView) itemView.findViewById(R.id.txtOperator);
                txtSync = (TextView) itemView.findViewById(R.id.txtSyncStatus);
                txthb = (TextView) itemView.findViewById(R.id.txtHb);
                txtuuid = (TextView) itemView.findViewById(R.id.txtUUID);
                txtuuidh = (TextView) itemView.findViewById(R.id.txtUUIDH);
                txtpagenumber = (TextView) itemView.findViewById(R.id.txtitemnumber);
                txtTime = (TextView) itemView.findViewById(R.id.txtTime);*/
                thumbnail = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }

        public void setFilter(List<ResultsModel> countryModels)
        {
            data = new ArrayList<>();
            data.addAll(countryModels);
            notifyDataSetChanged();
        }
        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        public void clear() {
            mLeDevices.clear();
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

    }
