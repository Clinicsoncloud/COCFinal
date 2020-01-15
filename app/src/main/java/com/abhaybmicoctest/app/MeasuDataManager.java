package com.abhaybmicoctest.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.abhaybmicoctest.app.entities.DataBase;
import com.abhaybmicoctest.app.entities.Lifetrack_infobean;
import com.abhaybmicoctest.app.entities.RegistrationInfoBean;
import com.abhaybmicoctest.app.utilities.ADSharedPreferences;
import com.abhaybmicoctest.app.utilities.ANDMedicalUtilities;


public class MeasuDataManager {

    public static final int MEASU_DATA_TYPE_UNKNOW = -1;
    public static final int MEASU_DATA_TYPE_AM = 0;
    public static final int MEASU_DATA_TYPE_BP = 1;
    public static final int MEASU_DATA_TYPE_WS = 2;
    public static final int MEASU_DATA_TYPE_TH = 3;

    public static final String ACTION_AM_DATA_UPDATE = "com.andmedical.action_am_data_update";
    public static final String ACTION_BP_DATA_UPDATE = "com.andmedical.action_bp_data_update";
    public static final String ACTION_WS_DATA_UPDATE = "com.andmedical.action_ws_data_update";
    public static final String ACTION_TH_DATA_UPDATE = "com.andmedical.action_tm_data_update";

    public static IntentFilter MeasuDataUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_AM_DATA_UPDATE);
        intentFilter.addAction(ACTION_BP_DATA_UPDATE);
        intentFilter.addAction(ACTION_WS_DATA_UPDATE);
        intentFilter.addAction(ACTION_TH_DATA_UPDATE);
        return intentFilter;
    }

    private int[] dataTypes = {
            MEASU_DATA_TYPE_AM,
            MEASU_DATA_TYPE_BP,
            MEASU_DATA_TYPE_WS,
            MEASU_DATA_TYPE_TH,
    };

    private Context mContext;
    private DataBase mDataBase;
    private ArrayList<Lifetrack_infobean> mAmDataList;
    private ArrayList<Lifetrack_infobean> mBpDataList;
    private ArrayList<Lifetrack_infobean> mWsDataList;
    private ArrayList<Lifetrack_infobean> mTmDataList;
    private ArrayList<Lifetrack_infobean> mGmDataList;
    private ArrayList<Lifetrack_infobean> mAmDispDataList;
    private Lifetrack_infobean dispDataAM;
    private Lifetrack_infobean dispDataBP;
    private Lifetrack_infobean dispDataWS;
    private Lifetrack_infobean dispDataTM;


    public MeasuDataManager(Context context) {
        super();
        mContext = context;

        String userName = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
        changeUser(userName);
    }

    private void changeUser(String userName) {
        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            mDataBase = new DataBase(mContext);

            if (mDataBase.getGuestInfo() == null) {
                RegistrationInfoBean registerInfo = new RegistrationInfoBean();
                if (Locale.getDefault().equals(Locale.JAPAN)) {
                    registerInfo.setUserHeightUnit("cm");
                } else {
                    registerInfo.setUserHeightUnit("in");
                }
                mDataBase.entryGuestInfo(registerInfo);
            }
        } else {
            mDataBase = new DataBase(mContext, userName);
        }
    }

    public void syncAllMeasuDatas(boolean isSendBroadcast) {
        if (mDataBase == null) {
            return;
        }

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            setDispMeasuData(MEASU_DATA_TYPE_AM, null);
            new SelectDataFromDatabase(MEASU_DATA_TYPE_AM, isSendBroadcast, null).execute();
            setDispMeasuData(MEASU_DATA_TYPE_BP, null);
            new SelectDataFromDatabase(MEASU_DATA_TYPE_BP, isSendBroadcast, null).execute();
            setDispMeasuData(MEASU_DATA_TYPE_WS, null);
            new SelectDataFromDatabase(MEASU_DATA_TYPE_WS, isSendBroadcast, null).execute();
            setDispMeasuData(MEASU_DATA_TYPE_TH, null);
            new SelectDataFromDatabase(MEASU_DATA_TYPE_TH, isSendBroadcast, null).execute();
        } else {
            // to Future
        }
    }

    public void syncMeasudata(int dataType, boolean isSendBroadcast) {
        syncMeasudata(dataType, isSendBroadcast, null);
    }

    public void syncMeasudata(int dataType, boolean isSendBroadcast, MeasureDataSyncListener listener) {
        if (mDataBase == null) {
            return;
        }

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            new SelectDataFromDatabase(dataType, isSendBroadcast, listener).execute();
        } else {
            // to Future
        }
    }

    private static final Object LOCK = new Object();

    public static ArrayList<Lifetrack_infobean> sortDate(ArrayList<Lifetrack_infobean> list, boolean isAsc) {
        synchronized (LOCK) {
            if (list == null || list.size() < 0) {
                return new ArrayList<Lifetrack_infobean>();
            }

            Collections.sort(list, (isAsc) ? comparatorListAcs : comparatorListDesc);
        }

        return list;
    }

    private static Comparator<Lifetrack_infobean> comparatorListAcs = new Comparator<Lifetrack_infobean>() {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

        @Override
        public int compare(Lifetrack_infobean lhs, Lifetrack_infobean rhs) {
            int j = 0;
            try {
                j = df.parse(lhs.getDate() + "T" + lhs.getTime())
                        .compareTo(df.parse(rhs.getDate() + "T" + rhs.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return j;
        }
    };

    private static Comparator<Lifetrack_infobean> comparatorListDesc = new Comparator<Lifetrack_infobean>() {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

        @Override
        public int compare(Lifetrack_infobean lhs, Lifetrack_infobean rhs) {
            int j = 0;
            try {
                j = df.parse(rhs.getDate() + "T" + rhs.getTime())
                        .compareTo(df.parse(lhs.getDate() + "T" + lhs.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return j;
        }
    };

    private class SelectDataFromDatabase extends AsyncTask<Void, Void, Void> {

        private int mSelectDataType = MEASU_DATA_TYPE_UNKNOW;
        private boolean mIsExecuteBroadcast = true;
        private ArrayList<Lifetrack_infobean> mList;
        private MeasureDataSyncListener mSyncListener;

        public SelectDataFromDatabase(int dataType, boolean isExecuteBroadcast, MeasureDataSyncListener listener) {
            super();
            mSelectDataType = dataType;
            mIsExecuteBroadcast = isExecuteBroadcast;
            mSyncListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mSyncListener != null) {
                mSyncListener.onSyncStart(mSelectDataType);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mSelectDataType == MEASU_DATA_TYPE_UNKNOW) {
                // to do nothing
            } else if (mSelectDataType == MEASU_DATA_TYPE_AM) {
                mList = sortDate(mDataBase.getAllActivityDetails(), true);
            } else if (mSelectDataType == MEASU_DATA_TYPE_BP) {
                mList = sortDate(mDataBase.getbpDetails(), true);
            } else if (mSelectDataType == MEASU_DATA_TYPE_WS) {
                mList = sortDate(mDataBase.getAllWeightDetails(), true);
            } else if (mSelectDataType == MEASU_DATA_TYPE_TH) {
                mList = sortDate(mDataBase.getAllThermometerDetails(), true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            String action = null;

            if (mSelectDataType == MEASU_DATA_TYPE_UNKNOW) {
                // to do nothing
                return;
            } else if (mSelectDataType == MEASU_DATA_TYPE_AM) {
                setAmDataList(mList);
                setAmDispDataList(mList);
                action = ACTION_AM_DATA_UPDATE;
            } else if (mSelectDataType == MEASU_DATA_TYPE_BP) {
                setBpDataList(mList);
                action = ACTION_BP_DATA_UPDATE;
            } else if (mSelectDataType == MEASU_DATA_TYPE_WS) {
                setWsDataList(mList);
                action = ACTION_WS_DATA_UPDATE;
            } else if (mSelectDataType == MEASU_DATA_TYPE_TH) {
                setTmDataList(mList);
                action = ACTION_TH_DATA_UPDATE;
            }

            if (mList != null && mList.size() > 0) {
                if (mSelectDataType != MEASU_DATA_TYPE_AM) {
                    setDispMeasuData(mSelectDataType, mList.get(mList.size() - 1));
                } else {
                    setDispMeasuData(mSelectDataType, mAmDispDataList.get(mAmDispDataList.size() - 1));
                }
            }

            if (mIsExecuteBroadcast) {
                sendBroadcast(action);
            }

            if (mSyncListener != null) {
                mSyncListener.onSyncEnd(mSelectDataType, (mList != null));
            }
        }
    }

    public interface MeasureDataSyncListener {
        public void onSyncStart(int type);

        public void onSyncEnd(int type, boolean result);
    }

    private void sendBroadcast(String action) {
        if (action == null) {
            return;
        }
        Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    public ArrayList<Lifetrack_infobean> getBpDataList() {
        if (mBpDataList == null) {
            mBpDataList = new ArrayList<Lifetrack_infobean>();
        }
        return mBpDataList;
    }

    private void setBpDataList(ArrayList<Lifetrack_infobean> mBpDataList) {
        this.mBpDataList = mBpDataList;
    }

    public ArrayList<Lifetrack_infobean> getWsDataList() {
        if (mWsDataList == null) {
            mWsDataList = new ArrayList<Lifetrack_infobean>();
        }
        return mWsDataList;
    }

    private void setWsDataList(ArrayList<Lifetrack_infobean> mWsDataList) {
        this.mWsDataList = mWsDataList;
    }

    public ArrayList<Lifetrack_infobean> getAmDataList() {
        if (mAmDataList == null) {
            mAmDataList = new ArrayList<Lifetrack_infobean>();
        }
        return mAmDataList;
    }


    private void setAmDataList(ArrayList<Lifetrack_infobean> mAmDataList) {
        this.mAmDataList = mAmDataList;
    }

    public ArrayList<Lifetrack_infobean> getTmDataList() {
        if (mTmDataList == null) {
            mTmDataList = new ArrayList<Lifetrack_infobean>();
        }
        return mTmDataList;
    }

    private void setTmDataList(ArrayList<Lifetrack_infobean> mTmDataList) {
        this.mTmDataList = mTmDataList;
    }

    public ArrayList<Lifetrack_infobean> getGmDataList() {
        if (mGmDataList == null) {
            mGmDataList = new ArrayList<Lifetrack_infobean>();
        }
        return mGmDataList;
    }

    /**
     * Get Measure Data List
     *
     * @param dataType
     * @return List
     */
    private synchronized ArrayList<Lifetrack_infobean> getMeasuDataList(int dataType) {
        if (dataType == MEASU_DATA_TYPE_AM) {
            return getAmDataList();
        } else if (dataType == MEASU_DATA_TYPE_BP) {
            return getBpDataList();
        } else if (dataType == MEASU_DATA_TYPE_WS) {
            return getWsDataList();
        } else if (dataType == MEASU_DATA_TYPE_TH) {
            return getTmDataList();
        }

        return null;
    }

    /**
     * Set Display MainDashboard Data
     *
     * @param dataType
     * @param data
     */
    private synchronized void setDispMeasuData(int dataType, Lifetrack_infobean data) {
        if (dataType == MEASU_DATA_TYPE_AM) {
            dispDataAM = data;
        } else if (dataType == MEASU_DATA_TYPE_BP) {
            dispDataBP = data;
        } else if (dataType == MEASU_DATA_TYPE_WS) {
            dispDataWS = data;
        } else if (dataType == MEASU_DATA_TYPE_TH) {
            dispDataTM = data;
        }
    }

    /**
     * Get Display MainDashboard Data
     *
     * @param dataType
     * @return data
     */
    private Lifetrack_infobean getDispMeasuData(int dataType) {
        if (dataType == MEASU_DATA_TYPE_AM) {
            return dispDataAM;
        } else if (dataType == MEASU_DATA_TYPE_BP) {
            return dispDataBP;
        } else if (dataType == MEASU_DATA_TYPE_WS) {
            return dispDataWS;
        } else if (dataType == MEASU_DATA_TYPE_TH) {
            return dispDataTM;
        }

        return null;
    }

    /**
     * Get Current Display Data
     *
     * @param dataType
     * @return
     */
    public Lifetrack_infobean getCurrentDispData(int dataType) {

        if (dataType == MEASU_DATA_TYPE_UNKNOW) {
            return null;
        }

        ArrayList<Lifetrack_infobean> list;
        if (dataType != MEASU_DATA_TYPE_AM) {
            list = getMeasuDataList(dataType);
        } else {
            list = getAmDispDataList();
        }
        if (list == null || list.isEmpty()) {
            return null;
        }

        Lifetrack_infobean data = getDispMeasuData(dataType);
        if (data != null) {
            return data;
        } else {
            data = list.get(list.size() - 1);
            setDispMeasuData(dataType, data);
            return data;
        }
    }

    /**
     * Datas is Exist FutureData
     *
     * @return true Exist Future Data. false Not Exist Future Data
     */
    public boolean isExistFutureDatas() {

        boolean isShow = false;

        for (int index = 0; index < dataTypes.length; index++) {
            int dataType = dataTypes[index];
            Lifetrack_infobean data = getFutureData(dataType);
            if (data != null) {
                isShow = true;
            }
        }
        return isShow;
    }

    /**
     * Datas is Exist PastData
     *
     * @return true Exist Past Data. false Not Exist Past Data
     */
    public boolean isExistPastDatas() {

        boolean isShow = false;

        for (int index = 0; index < dataTypes.length; index++) {
            int dataType = dataTypes[index];
            Lifetrack_infobean data = getPastData(dataType);
            if (data != null) {
                isShow = true;
            }
        }

        return isShow;
    }

    /**
     * Get Future Data(than CurrentDispData)
     *
     * @param dataType
     * @return Future Data
     */
    private Lifetrack_infobean getFutureData(int dataType) {
        Lifetrack_infobean currentData = getCurrentDispData(dataType);

        if (currentData == null) {
            return null;
        }

        List<Lifetrack_infobean> list;
        if (dataType != MEASU_DATA_TYPE_AM) {
            list = getMeasuDataList(dataType);
        } else {
            list = getAmDispDataList();
        }
        if (list == null) {
            return null;
        }

        int index = list.indexOf(currentData);
        // to the future
        index++;
        if (list.size() <= index || !list.contains(currentData)) {
            return null;
        }

        return list.get(index);
    }

    /**
     * Get Past Data(than CurrentDispData)
     *
     * @param dataType dataType
     * @return Past Data
     */
    private Lifetrack_infobean getPastData(int dataType) {

        Lifetrack_infobean currentData = getCurrentDispData(dataType);
        if (currentData == null) {
            return null;
        }

        ArrayList<Lifetrack_infobean> list;
        if (dataType != MEASU_DATA_TYPE_AM) {
            list = getMeasuDataList(dataType);
        } else {
            list = getAmDispDataList();
            sortDate(list, true);
        }
        if (list == null) {
            return null;
        }
        int index = list.indexOf(currentData);
        MeasuDataManager.sortDate(list, true);

        if (index == -1) {
            for (Lifetrack_infobean item : list) {
                if (item.toString().equals(currentData.toString())) {
                    index = list.indexOf(item);
                    break;
                }
            }
        }

        // to the Past
        index--;
        if (index < 0 || !list.contains(currentData)) {
            return null;
        }

        return list.get(index);
    }

    public int getCurrentIndex(Lifetrack_infobean data, int dataType) {
        int index = -1;

        List<Lifetrack_infobean> list;
        if (dataType != MEASU_DATA_TYPE_AM) {
            list = getMeasuDataList(dataType);
        } else {
            list = getAmDispDataList();
        }
        if (list == null) {
            return index;
        }

        index = list.indexOf(data);

        return index;
    }

    public int getCountList(int dataType) {
        List<Lifetrack_infobean> list;
        if (dataType != MEASU_DATA_TYPE_AM) {
            list = getMeasuDataList(dataType);
        } else {
            list = getAmDispDataList();
        }
        if (list == null) {
            return 0;
        }

        return list.size();
    }

    /**
     * Move DisplayDatas Future
     */
    public void moveDatasToTheFuture() {
        for (int index = 0; index < dataTypes.length; index++) {
            int dataType = dataTypes[index];
            Lifetrack_infobean data = getFutureData(dataType);
            if (data != null) {
                setDispMeasuData(dataType, data);
            }
        }
    }

    /**
     * Move DisplayDatas Future
     */
    public void moveDatasToTheFuture(int dataType) {
        Lifetrack_infobean data = getFutureData(dataType);
        if (data != null) {
            setDispMeasuData(dataType, data);
        }
    }

    /**
     * Move DisplayDatas Past
     */
    public void moveDatasToThePast() {
        for (int index = 0; index < dataTypes.length; index++) {
            int dataType = dataTypes[index];
            Lifetrack_infobean data = getPastData(dataType);
            if (data != null) {
                setDispMeasuData(dataType, data);
            }
        }
    }

    /**
     * Move DisplayDatas Past
     */
    public void moveDatasToThePast(int dataType) {
        Lifetrack_infobean data = getPastData(dataType);
        if (data != null) {
            setDispMeasuData(dataType, data);
        }
    }


    private final DecimalFormat mDecimalFormat = new DecimalFormat("0.0#");
    private final DecimalFormat mIntegerFormat = new DecimalFormat("0");

    private ArrayList<Lifetrack_infobean> getAmDispDataList() {
        if (mAmDispDataList == null) {
            mAmDispDataList = new ArrayList<Lifetrack_infobean>();
        }
        return mAmDispDataList;
    }


    private void setAmDispDataList(ArrayList<Lifetrack_infobean> dataList) {
        if (mAmDispDataList == null) {
            mAmDispDataList = new ArrayList<Lifetrack_infobean>();
        } else {
            mAmDispDataList.clear();
        }

        MeasuDataManager.sortDate(dataList, true);


        Calendar preCalendar = null;
        Lifetrack_infobean preData = null;

        float sumStep = 0;
        float sumDistance = 0;
        float sumDistanceInMiles = 0;
        float sumCalorie = 0;
        float sumSleep = 0;
        float sumHeartRate = 0;


        String devId;

        for (int i = 0; i < dataList.size(); i++) {
            Lifetrack_infobean data = dataList.get(i);
            if (data.getDate() == null) {
                continue;
            }
            devId = data.getDeviceId();

            String dateArray[] = data.getDate().split("-");

            if (dateArray.length != 3) {
                continue;
            }

            int y = Integer.valueOf(dateArray[0]);
            int m = Integer.valueOf(dateArray[1]) - 1;
            int d = Integer.valueOf(dateArray[2]);

            Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
            currentCalendar.clear();
            currentCalendar.set(y, m, d);

            if (preCalendar == null) {
                preCalendar = (Calendar) currentCalendar.clone();
            }

            if (preCalendar.compareTo(currentCalendar) == 0) {
                preData = data;
                float step = Float.valueOf(data.getSteps());
                float distance = Float.valueOf(data.getDistance());
                float distanceInMiles = Float.valueOf(data.getDistanceInMiles());
                float calorie = Float.valueOf(data.getCal());
                float sleep = Float.valueOf(data.getSleep());
                float heartRate = Float.valueOf(data.getHeartRate());


                sumStep = sumStep + step;
                sumDistance = sumDistance + distance;
                sumDistanceInMiles = sumDistanceInMiles + distanceInMiles;
                sumCalorie = sumCalorie + calorie;
                if (sleep >= sumSleep) {
                    sumSleep = sleep;
                }

                if (sumHeartRate < heartRate) {
                    sumHeartRate = heartRate;
                }


                if (i >= dataList.size() - 1) {
                    Lifetrack_infobean item = new Lifetrack_infobean();
                    item.setSteps(mIntegerFormat.format(sumStep));
                    item.setDistance(mDecimalFormat.format(sumDistance));
                    item.setDistanceInMiles(mDecimalFormat.format(sumDistanceInMiles));
                    item.setCal(mIntegerFormat.format(sumCalorie));
                    item.setSleep(mIntegerFormat.format(sumSleep));
                    item.setHeartRate(mIntegerFormat.format(sumHeartRate));
                    item.setDate(data.getDate());
                    item.setTime(data.getTime());
                    item.setDeviceId(devId); //Added this to differentiate between the UW and other activity
                    mAmDispDataList.add(item);
                    return;
                }
            } else {
                if (i >= dataList.size() - 1) {
                    {
                        //Have to add 2 days of data, the last and the last but one.
                        Lifetrack_infobean item = new Lifetrack_infobean();
                        item.setSteps(mIntegerFormat.format(sumStep));
                        item.setDistance(mDecimalFormat.format(sumDistance));
                        item.setDistanceInMiles(mDecimalFormat.format(sumDistanceInMiles));
                        item.setCal(mIntegerFormat.format(sumCalorie));
                        item.setSleep(mIntegerFormat.format(sumSleep));
                        item.setHeartRate(mIntegerFormat.format(sumHeartRate));
                        item.setDate(preData.getDate());
                        item.setTime(preData.getTime());
                        item.setDeviceId(devId); //Added this to differentiate between the UW and other activity
                        mAmDispDataList.add(item);

                        //Adding the last day data
                        Lifetrack_infobean item_last = new Lifetrack_infobean();
                        item_last.setSteps(mIntegerFormat.format(Float.valueOf(data.getSteps())));
                        item_last.setDistance(mDecimalFormat.format(Float.valueOf(data.getDistance())));
                        item_last.setDistanceInMiles(mDecimalFormat.format(sumDistanceInMiles));
                        item_last.setCal(mIntegerFormat.format(Float.valueOf(data.getCal())));
                        item_last.setSleep(mIntegerFormat.format(Float.valueOf(data.getSleep())));
                        item_last.setHeartRate(mIntegerFormat.format(Float.valueOf(data.getHeartRate())));
                        item_last.setDate(data.getDate());
                        item_last.setTime(data.getTime());
                        item_last.setDeviceId(devId); //Added this to differentiate between the UW and other activity
                        mAmDispDataList.add(item_last);
                        return;

                    }

                } else {
                    Lifetrack_infobean item = new Lifetrack_infobean();
                    item.setSteps(mIntegerFormat.format(sumStep));
                    item.setDistance(mDecimalFormat.format(sumDistance));
                    item.setDistanceInMiles(mDecimalFormat.format(sumDistanceInMiles));
                    item.setCal(mIntegerFormat.format(sumCalorie));
                    item.setSleep(mIntegerFormat.format(sumSleep));
                    item.setHeartRate(mIntegerFormat.format(sumHeartRate));
                    item.setDate(preData.getDate());
                    item.setTime(preData.getTime());
                    item.setDeviceId(devId); //Added this to differentiate between the UW and other activity
                    mAmDispDataList.add(item);

                    sumStep = 0;
                    sumDistance = 0;
                    sumDistanceInMiles = 0;
                    sumCalorie = 0;
                    sumSleep = 0;
                    sumHeartRate = 0;

                    preData = data;
                    preCalendar = (Calendar) currentCalendar.clone();

                    float step = Float.valueOf(data.getSteps());
                    float distance = Float.valueOf(data.getDistance());
                    float distanceInMiles = Float.valueOf(data.getDistanceInMiles());
                    float calorie = Float.valueOf(data.getCal());
                    float sleep = Float.valueOf(data.getSleep());
                    float heartRate = Float.valueOf(data.getHeartRate());

                    sumStep = sumStep + step;
                    sumDistance = sumDistance + distance;
                    sumDistanceInMiles = sumDistanceInMiles + distanceInMiles;
                    sumCalorie = sumCalorie + calorie;
                    if (sleep > sumSleep) {
                        sumSleep = sleep;
                    }

                    if (sumHeartRate < heartRate) {
                        sumHeartRate = heartRate;
                    }
                }
            }
        }
    }
}
