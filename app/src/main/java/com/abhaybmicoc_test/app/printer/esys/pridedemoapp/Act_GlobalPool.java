package com.abhaybmicoc_test.app.printer.esys.pridedemoapp;

import android.app.Application;

import com.abhaybmicoc_test.app.printer.evolute.bluetooth.BluetoothComm;
import com.prowesspride.api.Setup;

public class Act_GlobalPool extends Application{
	
	/**Bluetooth communication connection object*/
	public BluetoothComm mBTcomm = null;
	public boolean connection = false; 
	public static Setup setup=null ;
 
	@Override
	public void onCreate(){
		super.onCreate();
	}
	/**
	 * Set up a Bluetooth connection 
	 * @param String sMac Bluetooth hardware address 
	 * @return Boolean
	 * */
	public boolean createConn(String sMac){
		if (null == this.mBTcomm)
		{
			this.mBTcomm = new BluetoothComm(sMac);
			if (this.mBTcomm.createConn()){
				connection = true;
				return true;
			}
			else{
				this.mBTcomm = null;
				connection = false;
				return false;
			}
		}
		else
			return true;
	}

	/**
	 * Close and release the connection
	 * @return void
	 * */
	public void closeConn(){
		if (null != this.mBTcomm){
			this.mBTcomm.closeConn();
			this.mBTcomm = null;
		}
	}
}
