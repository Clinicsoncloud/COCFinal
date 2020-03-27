package com.abhaybmicoc.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.hemoglobin.util.AppUtils;
import com.abhaybmicoc.app.utils.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by ketan 16-3-2020
 */

public class VisionThirdFragment extends Fragment {
    @BindView(R.id.tv_left_vision_three)
    TextView tvLeftVisionThree;
    @BindView(R.id.rb_left_vision_3)
    RadioButton rbLeftVision3;
    @BindView(R.id.rb_left_vision_4)
    RadioButton rbLeftVision4;
    @BindView(R.id.rg_left_vision_three)
    RadioGroup rgLeftVisionThree;
    @BindView(R.id.tv_right_vision_three)
    TextView tvRightVisionThree;
    @BindView(R.id.rb_right_vision_3)
    RadioButton rbRightVision3;
    @BindView(R.id.rb_right_vision_4)
    RadioButton rbRightVision4;
    @BindView(R.id.rg_right_vision_three)
    RadioGroup rgRightVisionThree;
    Unbinder unbinder;
    //    private SharedPreferences sharedPreferenceLeftVision;
//    private SharedPreferences sharedPreferenceRightVision;
    private SharedPreferences preferenceVisionResult;


    private String left_vision = "", right_vision = "";

    public VisionThirdFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VisionThirdFragment newInstance() {
        VisionThirdFragment fragment = new VisionThirdFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_vision_third, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("OnStartThird", ":");

        setupEvents();
        initializeData();
    }

    /**
     *
     */
    private void initializeData() {
        /*sharedPreferenceLeftVision = getContext().getSharedPreferences(ApiUtils.PREFERENCE_LEFTVISION, Context.MODE_PRIVATE);
        sharedPreferenceRightVision = getContext().getSharedPreferences(ApiUtils.PREFERENCE_RIGHTVISION, Context.MODE_PRIVATE);*/
        preferenceVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_VISION_RESULT, Context.MODE_PRIVATE);

        left_vision = preferenceVisionResult.getString("eye_left_vision", "");
        right_vision = preferenceVisionResult.getString("eye_right_vision", "");

        Log.e("left_vision_Third", ":" + left_vision + "   : right_vision :  " + right_vision);


        if (!left_vision.equals("") && left_vision.equals("6/24")) {
            rbLeftVision3.setChecked(true);
        } else {
            rbLeftVision3.setChecked(false);

        }

        if (!left_vision.equals("") && left_vision.equals("6/18")) {
            rbLeftVision4.setChecked(true);
        } else {
            rbLeftVision4.setChecked(false);

        }

        if (!right_vision.equals("") && right_vision.equals("6/24")) {
            rbRightVision3.setChecked(true);
        } else {
            rbRightVision3.setChecked(false);
        }

        if (!right_vision.equals("") && right_vision.equals("6/18")) {
            rbRightVision4.setChecked(true);
        } else {
            rbRightVision4.setChecked(false);
        }

    }

    /**
     *
     */
    private void setupEvents() {
        rgLeftVisionThree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {

                    case R.id.rb_left_vision_3:
                        saveLeftVisionResult("6/24");
                        break;

                    case R.id.rb_left_vision_4:
                        saveLeftVisionResult("6/18");
                        break;
                }
            }
        });


        rgRightVisionThree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_right_vision_3:
                        saveRightVisionResult("6/24");
                        break;

                    case R.id.rb_right_vision_4:
                        saveRightVisionResult("6/18");
                        break;
                }
            }
        });
    }

    /**
     * @param rightVisionResult
     */
    private void saveRightVisionResult(String rightVisionResult) {
        SharedPreferences.Editor editor = preferenceVisionResult.edit();
        editor.putString("eye_right_vision", rightVisionResult);
        editor.commit();
    }

    /**
     * @param leftVisionResult
     */
    private void saveLeftVisionResult(String leftVisionResult) {
        SharedPreferences.Editor editor = preferenceVisionResult.edit();
        editor.putString("eye_left_vision", leftVisionResult);
        editor.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event

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
