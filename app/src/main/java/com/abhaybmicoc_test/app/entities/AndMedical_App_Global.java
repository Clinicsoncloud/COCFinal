package com.abhaybmicoc_test.app.entities;

import android.app.Application;
import android.text.TextUtils;

import com.abhaybmicoc_test.app.MeasuDataManager;
import com.abhaybmicoc_test.app.printer.evolute.bluetooth.BluetoothComm;
import com.abhaybmicoc_test.app.utilities.ADSharedPreferences;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.prowesspride.api.Setup;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class AndMedical_App_Global extends Application {
	
	private static AndMedical_App_Global app;
	private MeasuDataManager measuDataManager;

	/**Bluetooth communication connection object*/
	public static BluetoothComm mBTcomm = null;
	public boolean connection = false;
	public static Setup setup=null ;
    public static final String TAG = AndMedical_App_Global.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AndMedical_App_Global mInstance;
	@Override
	public void onCreate() {
		super.onCreate();
        Fabric.with(this, new Crashlytics());
		app = this;
        mInstance = this;
		ADSharedPreferences.SharedInstance(this);
		if (this.measuDataManager == null) {
			this.measuDataManager = new MeasuDataManager(this);
		}
	}

	/**
	 * Set up a Bluetooth connection
	 * @param String sMac Bluetooth hardware address
	 * @return Boolean
	 * */

    public static boolean activityVisible; // Variable that will check the
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }

    public static synchronized AndMedical_App_Global getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

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
	@Override
	public void onTerminate() {
		super.onTerminate();
		 
		ADSharedPreferences.releaseInstance();
	}
	
	public static AndMedical_App_Global getApp() {
		return app;
	}

	public MeasuDataManager getMeasuDataManager() {
		return measuDataManager;
	}

	public void setMeasuDataManager(MeasuDataManager measuDataManager) {
		this.measuDataManager = measuDataManager;
	}

}
