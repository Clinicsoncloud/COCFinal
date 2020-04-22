package com.abhaybmicoc.app.screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.SplashActivity;
import com.abhaybmicoc.app.model.Clinic_Data;
import com.abhaybmicoc.app.model.Clinic_Data_Info;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.Utils;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivateScreen extends AppCompatActivity {

    private Context context = ActivateScreen.this;
    private EditText etPin;
    private Button btnSubmit;
    private SharedPreferences sp;
    private String activatorKey;
    List<Integer> imageList;
    RippleBackground rippleBackground;

    final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    android.app.Dialog confirmKioskDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_screen);
        init();
    }

    public void init() {
        try {
            sp = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
            activatorKey = sp.getString("pinLock", "");

            confirmKioskDialog = new android.app.Dialog(context);
            confirmKioskDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            if (activatorKey.equals("")) {
                Toast.makeText(getApplicationContext(), "Activate The Machine", Toast.LENGTH_SHORT).show();
            } else {
                Intent objIntent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(objIntent);
                finish();
            }

        } catch (Exception e) {
        }

        etPin = findViewById(R.id.etPin);
        btnSubmit = findViewById(R.id.procedurebtn);

        rippleBackground = findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        btnSubmit.setOnClickListener(view -> validateKiosk());

    }

    private void validateKiosk() {

        if (!etPin.getText().toString().trim().equals("")) {

            verifyKIOSK_ID();

        } else {
            etPin.requestFocus();
            etPin.setError("Please Enter KIOSK ID.");
        }
    }

    private void verifyKIOSK_ID() {
        try {
            Map<String, String> headerParams = new HashMap<>();
            Map<String, String> requestBodyParams = new HashMap<>();

            requestBodyParams.put("token", etPin.getText().toString().trim());

            HttpService.accessWebServices(
                    context,
                    ApiUtils.FIND_BY_TOKEN,
                    Request.Method.POST,
                    requestBodyParams,
                    headerParams,
                    (response, error, status) -> handleAPIResponse(response, error, status));
        } catch (Exception e) {
        }
    }

    private void handleAPIResponse(String response, VolleyError error, String status) {

        Log.e("response_KIOSKVerify", ":" + response);
        Log.e("status_KIOSKVerify", ":" + status);

        if (status.equals("response")) {
            try {
                Clinic_Data clinic_data = (Clinic_Data) Utils.parseResponse(response, Clinic_Data.class);

                Log.e("Found_Status_Log", ":" + clinic_data.getFound());

                if (clinic_data.getFound()) {
                    showConfirmationPopUp(clinic_data.getData());
                } else {
                    showFailuerPopUp();
                    Toast.makeText(context, "Please enter registered KIOSK ID...", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                // TODO: Handle exception
            }
        } else if (status.equals("error")) {
            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            // TODO: Handle error
        }
    }

    @SuppressLint("SetTextI18n")
    private void showConfirmationPopUp(Clinic_Data_Info clinic_data_info) {
        confirmKioskDialog.setContentView(R.layout.show_kiosk_activation_dilog);
        layoutParams.copyFrom(confirmKioskDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        confirmKioskDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        confirmKioskDialog.setCanceledOnTouchOutside(false);

        final TextView tv_msg = confirmKioskDialog.findViewById(R.id.tv_msg);
        final Button btn_Proceed = confirmKioskDialog.findViewById(R.id.btn_Proceed);
        final ImageView ic_CloseDialog = confirmKioskDialog.findViewById(R.id.ic_CloseDilog);

        tv_msg.setTextColor(context.getResources().getColor(R.color.white));

        try {
            tv_msg.setText("You are now connected to  \"" + clinic_data_info.getName() + "\"");

            btn_Proceed.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect2));

            btn_Proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        sp = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
                        try {
                            // Writing data to SharedPreferences
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("pinLock", clinic_data_info.getToken());
                            editor.putString("clinic_name", clinic_data_info.getName());
                            editor.putString("address", clinic_data_info.getAddress());
                            editor.putString("app_version", clinic_data_info.getAppVersion());
                            editor.putString("location_id", clinic_data_info.getLocationId());
                            editor.putString("total_tests_done", String.valueOf(clinic_data_info.getTotalTestsDone()));
                            editor.putString("allowed_trial_tests", String.valueOf(clinic_data_info.getAllowedTrialTests()));
                            editor.putString("installed_by", clinic_data_info.getInstalledBy());
                            editor.putString("assigned_user_id", String.valueOf(clinic_data_info.getAssignedUserId()));
                            editor.putString("installation_date", clinic_data_info.getInstallationDate());
                            editor.putString("machine_operator_name", clinic_data_info.getMachineOperatorName());
                            editor.putString("machine_operator_mobile_number", clinic_data_info.getMachineOperatorMobileNumber());
//                            editor.putString("client_name", clinicObject.getString("client_name"));
                            editor.putString("is_trial_mode", String.valueOf(clinic_data_info.getIsTrialMode()));
                            editor.putString("id", clinic_data_info.getId());
                            editor.commit();

                            confirmKioskDialog.dismiss();

                            Intent objIntent = new Intent(getApplicationContext(), SplashActivity.class);
                            startActivity(objIntent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                    }
                }
            });

            ic_CloseDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmKioskDialog.dismiss();
                }
            });


        } catch (Exception e) {
        }
        confirmKioskDialog.show();
        confirmKioskDialog.getWindow().setAttributes(layoutParams);
    }

    @SuppressLint("SetTextI18n")
    private void showFailuerPopUp() {
        confirmKioskDialog.setContentView(R.layout.show_kiosk_activation_dilog);
        layoutParams.copyFrom(confirmKioskDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        confirmKioskDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        confirmKioskDialog.setCanceledOnTouchOutside(false);


        final TextView tv_msg = confirmKioskDialog.findViewById(R.id.tv_msg);
        final TextView tv_title = confirmKioskDialog.findViewById(R.id.tv_title);
        final Button btn_Proceed = confirmKioskDialog.findViewById(R.id.btn_Proceed);
        final ImageView ic_CloseDilog = confirmKioskDialog.findViewById(R.id.ic_CloseDilog);

        tv_msg.setTextColor(context.getResources().getColor(R.color.white));


        try {
            tv_title.setText("Activation Failed!");
            tv_title.setTextColor(getResources().getColor(R.color.solid_red));
            tv_msg.setText("You have entered wrong KIOSK ID...");

            btn_Proceed.setText("Try Again");

            btn_Proceed.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect1));

            btn_Proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmKioskDialog.dismiss();
                }
            });

            ic_CloseDilog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmKioskDialog.dismiss();
                }
            });


        } catch (Exception e) {
        }
        confirmKioskDialog.show();
        confirmKioskDialog.getWindow().setAttributes(layoutParams);
    }


}
