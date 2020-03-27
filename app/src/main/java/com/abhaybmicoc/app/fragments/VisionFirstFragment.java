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
import com.abhaybmicoc.app.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by ketan 17-3-2020
 */
public class VisionFirstFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.tv_left_vision)
    TextView tvLeftVision;
    @BindView(R.id.rb_left_vision_1)
    RadioButton rbLeftVision1;
    @BindView(R.id.tv_right_vision)
    TextView tvRightVision;
    @BindView(R.id.rb_right_vision_1)
    RadioButton rbRightVision1;

    //    private SharedPreferences preferenceLeftVisionResult;
//    private SharedPreferences preferenceRightVisionResult;
    private SharedPreferences preferenceVisionResult;

    private String left_vision = "", right_vision = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VisionFirstFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VisionFirstFragment newInstance() {
        VisionFirstFragment fragment = new VisionFirstFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_vision_first, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("OnStartFirst", ":");

        setupEvents();
        initializeData();
    }


    /**
     *
     */
    private void initializeData() {
        /*preferenceLeftVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_LEFTVISION, Context.MODE_PRIVATE);
        preferenceRightVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_RIGHTVISION, Context.MODE_PRIVATE);*/
        preferenceVisionResult = getContext().getSharedPreferences(ApiUtils.PREFERENCE_VISION_RESULT, Context.MODE_PRIVATE);

        left_vision = preferenceVisionResult.getString("eye_left_vision", "");
        right_vision = preferenceVisionResult.getString("eye_right_vision", "");

        Log.e("left_vision_First", ":" + left_vision + "   : right_vision :  " + right_vision);

        if (!left_vision.equals("") && left_vision.equals("6/60")) {
            rbLeftVision1.setChecked(true);
        } else {
            rbLeftVision1.setChecked(false);
        }

        if (!right_vision.equals("") && right_vision.equals("6/60")) {
            rbRightVision1.setChecked(true);
        } else {
            rbRightVision1.setChecked(false);

        }

    }

    /**
     *
     */
    private void setupEvents() {
        rbLeftVision1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    saveLeftVisionResult("6/60");
            }
        });

        rbRightVision1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    saveRightVisionResult("6/60");
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
