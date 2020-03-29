package com.abhaybmicoc.app.activity;

/**
 * created by ketan 16-3-2020
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.fragments.VisionFirstFragment;
import com.abhaybmicoc.app.fragments.VisionFourFragment;
import com.abhaybmicoc.app.fragments.VisionSecondFragment;
import com.abhaybmicoc.app.fragments.VisionThirdFragment;
import com.abhaybmicoc.app.glucose.GlucoseScanListActivity;
import com.abhaybmicoc.app.glucose.GlucoseScanListActivity;
import com.abhaybmicoc.app.oximeter.MainActivity;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ZoomOutPageTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VisionActivity extends AppCompatActivity implements View.OnClickListener {

    //region variables

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.tv_result_left_vision)
    TextView tvResultLeftVision;
    @BindView(R.id.tv_result_right_vision)
    TextView tvResultRightVision;
    @BindView(R.id.layout_result)
    LinearLayout layoutResult;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_mobile_number)
    TextView tvMobileNumber;
    @BindView(R.id.layout_btn_finish)
    LinearLayout layoutBtnFinish;
    @BindView(R.id.layout_btn_skip)
    LinearLayout layoutBtnSkip;
    @BindView(R.id.layout_buttons)
    LinearLayout layoutButtons;
    @BindView(R.id.tv_header_height)
    TextView tvHeaderHeight;
    @BindView(R.id.tv_header_weight)
    TextView tvHeaderWeight;
    @BindView(R.id.tv_header_pulseoximeter)
    TextView tvHeaderPulseoximeter;
    @BindView(R.id.tv_header_bloodpressure)
    TextView tvHeaderBloodpressure;
    @BindView(R.id.tv_header_tempreture)
    TextView tvHeaderTempreture;
    @BindView(R.id.iv_left_arrow)
    ImageView ivLeftArrow;
    @BindView(R.id.iv_right_arrow)
    ImageView ivRightArrow;

    private SharedPreferences preferenceVisionResult;
    private SharedPreferences preferencePersonalData;

    private Context context = VisionActivity.this;

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private static final int NUM_PAGES = 4;

    //endregion

    // region initialization methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);
        ButterKnife.bind(this);

        setViewPager();
        setupEvents();
        initializeData();
    }


    /**
     *
     */
    private void initializeData() {
        preferenceVisionResult = getSharedPreferences(ApiUtils.PREFERENCE_VISION_RESULT, MODE_PRIVATE);
        preferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        setUserData();
    }

    /**
     *
     */
    private void setUserData() {
        tvName.setText("Name : " + preferencePersonalData.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + preferencePersonalData.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + preferencePersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobileNumber.setText("Phone : " + preferencePersonalData.getString(Constant.Fields.MOBILE_NUMBER, ""));
    }

    /**
     *
     */
    private void setupEvents() {
        btnFinish.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvHeaderHeight.setOnClickListener(this);
        tvHeaderWeight.setOnClickListener(this);
        tvHeaderPulseoximeter.setOnClickListener(this);
        tvHeaderBloodpressure.setOnClickListener(this);
        tvHeaderTempreture.setOnClickListener(this);


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int CurrentPossition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                switch (position) {

                    case 0:
                        layoutBtnSkip.setVisibility(View.VISIBLE);
                        layoutBtnFinish.setVisibility(View.VISIBLE);
                        ivLeftArrow.setVisibility(View.GONE);
                        ivRightArrow.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        layoutBtnSkip.setVisibility(View.GONE);
                        layoutBtnFinish.setVisibility(View.VISIBLE);
                        ivRightArrow.setVisibility(View.VISIBLE);
                        ivLeftArrow.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        layoutBtnSkip.setVisibility(View.GONE);
                        layoutBtnFinish.setVisibility(View.VISIBLE);
                        ivRightArrow.setVisibility(View.VISIBLE);
                        ivLeftArrow.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        layoutBtnSkip.setVisibility(View.GONE);
                        layoutBtnFinish.setVisibility(View.VISIBLE);
                        ivRightArrow.setVisibility(View.GONE);
                        ivLeftArrow.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        ivLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        ivRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });
    }

    /**
     *
     */
    private void setViewPager() {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(pagerAdapter);

        layoutBtnSkip.setVisibility(View.VISIBLE);
        layoutBtnFinish.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
//       super.onBackPressed();
        //Disabled the back button
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_finish:
                showVisionResult();
                break;

            case R.id.btn_skip:

                tvResultLeftVision.setText("");
                tvResultRightVision.setText("");

                startActivity(new Intent(getApplicationContext(), GlucoseScanListActivity.class));
                //shared preferences  cleared
                preferenceVisionResult.edit().clear().apply();
                finish();
                break;

            case R.id.btn_next:
                startActivity(new Intent(getApplicationContext(), GlucoseScanListActivity.class));
                finish();
                break;

            case R.id.tv_header_height:
                startActivity(new Intent(getApplicationContext(), HeightActivity.class));
                finish();
                break;

            case R.id.tv_header_weight:
                startActivity(new Intent(getApplicationContext(), ActofitMainActivity.class));
                finish();
                break;

            case R.id.tv_header_pulseoximeter:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;

            case R.id.tv_header_bloodpressure:
                startActivity(new Intent(getApplicationContext(), BloodPressureActivity.class));
                finish();
                break;

            case R.id.tv_header_tempreture:
                startActivity(new Intent(getApplicationContext(), ThermometerScreen.class));
                finish();
                break;
        }
    }

    /**
     *
     */
    private void showVisionResult() {
        pager.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.GONE);
        tvResultLeftVision.setText(preferenceVisionResult.getString("eye_left_vision", ""));
        tvResultRightVision.setText(preferenceVisionResult.getString("eye_right_vision", ""));
        btnSkip.setVisibility(View.GONE);
        ivRightArrow.setVisibility(View.GONE);
        ivLeftArrow.setVisibility(View.GONE);

    }


    //endregion

    //region adapter class

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    Log.e("secondFragement", " : Log");
                    return VisionFirstFragment.newInstance();
                case 1:
                    Log.e("secondFragement", " : Log");
                    return VisionSecondFragment.newInstance();

                case 2:
                    Log.e("ThirdFragement", " : Log");
                    return VisionThirdFragment.newInstance();
                case 3:
                    Log.e("fourFragment", " : Log");
                    return VisionFourFragment.newInstance();
                default:
                    Log.e("defaultFragment", " : Log");
                    return VisionFirstFragment.newInstance();
            }
        }

        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    //endregion
}


