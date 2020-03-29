package com.abhaybmicoc.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * created by ketan 16-3-2020
 */
public class VisionSecondFragment extends Fragment {

    //region variables
    View rootView;

    @BindView(R.id.tv_left_vision)
    TextView tvLeftVision;

    public static RadioButton rbLeftVision2;
    public static RadioButton rbRightVision2;
    @BindView(R.id.tv_right_vision)
    TextView tvRightVision;


    /**
     * sharedPreferences Declaration
     */
//    private SharedPreferences sharedPrefVision;
//    private SharedPreferences sharedPreferenceRightVision;
    private SharedPreferences preferenceVisionResult;

    private String left_vision = "";
    private String right_vision = "";


    //endregion


    public VisionSecondFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VisionSecondFragment newInstance() {
        VisionSecondFragment fragment = new VisionSecondFragment();
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
        rootView = inflater.inflate(R.layout.fragment_vision_second, container, false);
        ButterKnife.bind(this, rootView);


        /*setupUI(rootView);
        setupEvents();
        initializeData();*/

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("OnStartSecond", ":");
        setupUI();
        setupEvents();
        initializeData();
    }

    private void setupUI() {
        rbLeftVision2 = rootView.findViewById(R.id.rb_left_vision_2);
        rbRightVision2 = rootView.findViewById(R.id.rb_right_vision_2);
    }

    /**
     * initialization method
     * declared the sharedpreferences here
     */
    private void initializeData() {
        preferenceVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_VISION_RESULT, MODE_PRIVATE);

        left_vision = preferenceVisionResult.getString("eye_left_vision", "");
        right_vision = preferenceVisionResult.getString("eye_right_vision", "");

        Log.e("left_vision_Second", ":" + left_vision + "   : right_vision :  " + right_vision);

        if (!left_vision.equals("") && left_vision.equals("6/36")) {
            rbLeftVision2.setChecked(true);
        } else {
            rbLeftVision2.setChecked(false);
        }

        if (!right_vision.equals("") && right_vision.equals("6/36")) {
            rbRightVision2.setChecked(true);
        } else {
            rbRightVision2.setChecked(false);
        }

    }

    /**
     * set the onchecked listeners for the radiobuttons for result left and right vision
     */
    private void setupEvents() {

        rbLeftVision2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    saveleftVisionResult("6/36");
            }
        });

        rbRightVision2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    saveRightVisionResult("6/36");
            }
        });
    }

    private void clearFirstRadioButton() {
        VisionFirstFragment.rbLeftVision1.setChecked(false);
    }

    /**
     * save the right vision result
     * if the right radiobutton is checked
     */
    private void saveRightVisionResult(String rightVisionResult) {
        SharedPreferences.Editor editor = preferenceVisionResult.edit();
        editor.putString("eye_right_vision", rightVisionResult);
        editor.commit();
    }

    /**
     * save the left vision result
     * if the left radiobutton is checked
     */
    private void saveleftVisionResult(String leftVisionResult) {
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
