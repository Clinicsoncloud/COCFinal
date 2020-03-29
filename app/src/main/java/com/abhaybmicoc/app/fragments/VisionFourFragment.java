package com.abhaybmicoc.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
    @BindView(R.id.tv_right_vision_last)
    TextView tvRightVisionLast;

    public static RadioButton rbLeftVision5;
    public static RadioButton rbLeftVision6;
    public static RadioButton rbLeftVision7;
    public static RadioButton rbRightVision5;
    public static RadioButton rbRightVision6;
    public static RadioButton rbRightVision7;

    public static RadioGroup rgRightVisionLast;
    public static RadioGroup rgLeftVisionLast;

    private SharedPreferences preferenceVisionResult;

    private String left_vision = "";
    private String right_vision = "";

    View rootView;

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
        rootView = inflater.inflate(R.layout.fragment_vision_four, container, false);
        ButterKnife.bind(this, rootView);

        setupUI();
        /*setupEvents();
        initializeData();*/

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e("OnStartFourth", ":");

        setupEvents();
        initializeData();
    }

    private void setupUI() {

        rbLeftVision5 = rootView.findViewById(R.id.rb_left_vision_5);
        rbLeftVision6 = rootView.findViewById(R.id.rb_left_vision_6);
        rbLeftVision7 = rootView.findViewById(R.id.rb_left_vision_7);

        rbRightVision5 = rootView.findViewById(R.id.rb_right_vision_5);
        rbRightVision6 = rootView.findViewById(R.id.rb_right_vision_6);
        rbRightVision7 = rootView.findViewById(R.id.rb_right_vision_7);

        rgLeftVisionLast = rootView.findViewById(R.id.rg_left_vision_last);
        rgRightVisionLast = rootView.findViewById(R.id.rg_right_vision_last);
    }

    /**
     * initialization of data
     * initialized sharedpreferences
     */
    private void initializeData() {

        preferenceVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_VISION_RESULT, Context.MODE_PRIVATE);

        left_vision = preferenceVisionResult.getString("eye_left_vision", "");
        right_vision = preferenceVisionResult.getString("eye_right_vision", "");

        Log.e("left_vision_Fourth", ":" + left_vision + "   : right_vision :  " + right_vision);

        if (!left_vision.equals("") && left_vision.equals("6/12")) {
            rbLeftVision5.setChecked(true);
        } else {
            rbLeftVision5.setChecked(false);
        }

        if (!left_vision.equals("") && left_vision.equals("6/9")) {
            rbLeftVision6.setChecked(true);
        } else {
            rbLeftVision6.setChecked(false);
        }

        if (!left_vision.equals("") && left_vision.equals("6/6")) {
            rbLeftVision7.setChecked(true);
        } else {
            rbLeftVision7.setChecked(false);
        }

        if (!right_vision.equals("") && right_vision.equals("6/12")) {
            rbRightVision5.setChecked(true);
        } else {
            rbRightVision5.setChecked(false);
        }

        if (!right_vision.equals("") && right_vision.equals("6/9")) {
            rbRightVision6.setChecked(true);
        } else {
            rbRightVision6.setChecked(false);
        }

        if (!right_vision.equals("") && right_vision.equals("6/6")) {
            rbRightVision6.setChecked(true);
        } else {
            rbRightVision6.setChecked(false);
        }
    }

    /**
     * events
     * radiogroup events
     */
    private void setupEvents() {
        rgLeftVisionLast.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {

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
                switch (checkedId) {

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
     *
     * @param leftVisionResult
     */
    private void saveLeftVisionResult(String leftVisionResult) {
        SharedPreferences.Editor editor = preferenceVisionResult.edit();
        editor.putString("eye_left_vision", leftVisionResult);
        editor.commit();
    }

    /**
     * save the right vision data
     *
     * @param rightVisionResult
     */
    private void saveRightVisionResult(String rightVisionResult) {
        SharedPreferences.Editor editor = preferenceVisionResult.edit();
        editor.putString("eye_right_vision", rightVisionResult);
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
