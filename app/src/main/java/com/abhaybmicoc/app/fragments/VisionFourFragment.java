package com.abhaybmicoc.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by ketan 17-3-2020
 */

public class VisionFourFragment extends Fragment {

    @BindView(R.id.tv_left_vision_last)
    TextView tvLeftVisionLast;
    @BindView(R.id.rb_left_vision_5)
    RadioButton rbLeftVision5;
    @BindView(R.id.rb_left_vision_6)
    RadioButton rbLeftVision6;
    @BindView(R.id.rb_left_vision_7)
    RadioButton rbLeftVision7;
    @BindView(R.id.rg_left_vision_last)
    RadioGroup rgLeftVisionLast;
    @BindView(R.id.tv_right_vision_last)
    TextView tvRightVisionLast;
    @BindView(R.id.rb_right_vision_5)
    RadioButton rbRightVision5;
    @BindView(R.id.rb_right_vision_6)
    RadioButton rbRightVision6;
    @BindView(R.id.rb_right_vision_7)
    RadioButton rbRightVision7;
    @BindView(R.id.rg_right_vision_last)
    RadioGroup rgRightVisionLast;

    private SharedPreferences preferenceLeftVisionResult;
    private SharedPreferences preferenceRightVisionResult;

    public VisionFourFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VisionFourFragment newInstance() {
        VisionFourFragment fragment = new VisionFourFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vision_four, container, false);
        ButterKnife.bind(this, rootView);

        setupEvents();
        initializeData();

        return rootView;
    }

    /**
     * initialization of data
     * initialized sharedpreferences
     */
    private void initializeData() {
        preferenceLeftVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_LEFTVISION,Context.MODE_PRIVATE);
        preferenceRightVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_RIGHTVISION,Context.MODE_PRIVATE);
    }

    /**
     * events
     * radiogroup events
     */
    private void setupEvents() {
        rgLeftVisionLast.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){

                    case R.id.rb_left_vision_5:
                        saveLeftVisionResult("6/12");
                        break;

                    case R.id.rb_left_vision_6:
                        saveLeftVisionResult("6/9");
                        break;

                    case R.id.rb_left_vision_7:
                        saveLeftVisionResult("6/6");
                        break;
                }
            }
        });

        rgRightVisionLast.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){

                    case R.id.rb_right_vision_5:
                        saveRightVisionResult("6/12");
                        break;

                    case R.id.rb_right_vision_6:
                        saveRightVisionResult("6/9");
                        break;

                    case R.id.rb_right_vision_7:
                        saveRightVisionResult("6/6");
                        break;
                }
            }
        });
    }

    /**
     * save the left vision data
     * @param leftVisionResult
     */
    private void saveLeftVisionResult(String leftVisionResult) {
        SharedPreferences.Editor editor = preferenceLeftVisionResult.edit();
        editor.putString("leftvision", leftVisionResult);
        editor.commit();
    }

    /**
     * save the right vision data
     * @param rightVisionResult
     */
    private void saveRightVisionResult(String rightVisionResult) {
        SharedPreferences.Editor editor = preferenceRightVisionResult.edit();
        editor.putString("rightvision", rightVisionResult);
        editor.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
