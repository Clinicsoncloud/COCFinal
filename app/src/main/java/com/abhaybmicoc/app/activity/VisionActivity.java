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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.fragments.VisionFirstFragment;
import com.abhaybmicoc.app.fragments.VisionFourFragment;
import com.abhaybmicoc.app.fragments.VisionSecondFragment;
import com.abhaybmicoc.app.fragments.VisionThirdFragment;
import com.abhaybmicoc.app.glucose.GlucoseActivity;
import com.abhaybmicoc.app.utils.ApiUtils;
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

    private SharedPreferences preferenceLeftVisionResult;
    private SharedPreferences preferenceRightVisionResul;

    private Context context;

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private static final int NUM_PAGES = 4;


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);
        ButterKnife.bind(this);

        setViewPager();
        setupEvents();
        initializeData();
    }

    private void initializeData() {
        preferenceLeftVisionResult = getSharedPreferences(ApiUtils.PREFERENCE_LEFTVISION,MODE_PRIVATE);
        preferenceRightVisionResul = getSharedPreferences(ApiUtils.PREFERENCE_RIGHTVISION,MODE_PRIVATE);
    }

    private void setupEvents() {
        btnFinish.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void setViewPager() {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(pagerAdapter);
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_finish:
                showResult();
                break;

            case R.id.btn_skip:
                context.startActivity(new Intent(getApplicationContext(), GlucoseActivity.class));
                break;

            case R.id.btn_next:
                context.startActivity(new Intent(getApplicationContext(), GlucoseActivity.class));
                break;
        }
    }

    private void showResult() {
        pager.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.GONE);
        tvResultLeftVision.setText(preferenceLeftVisionResult.getString("leftvision",""));
        tvResultRightVision.setText(preferenceRightVisionResul.getString("rightvision",""));
    }

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
                    return VisionFirstFragment.newInstance();
                case 1:
                    return VisionSecondFragment.newInstance();
                case 2:
                    return VisionThirdFragment.newInstance();
                case 3:
                    return VisionFourFragment.newInstance();
                default:
                    return VisionFirstFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}


