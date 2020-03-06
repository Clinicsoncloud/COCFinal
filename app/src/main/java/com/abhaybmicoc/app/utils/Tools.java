package com.abhaybmicoc.app.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;


import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tools {
    private static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", "erro ao recuperar a versão da API" + e.getMessage());
        }

        return f.floatValue();
    }

    public static ProgressDialog progressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Please Wait......");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        return progressDialog;
    }

    public static ProgressDialog progressDialog1(Context context){

        ProgressDialog pd = new ProgressDialog(context);
        pd.setIndeterminate(false);
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        return pd;
    }
    public static KProgressHUD kHudDialog(Context context){

        KProgressHUD pd = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setDetailsLabel("Downloading data")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        return pd;
    }



    public static Snackbar showSnackbar(View view, String message) {

        Snackbar snackbar = Snackbar.make(view
                , message, Snackbar.LENGTH_LONG);
//        View sbSnackbar=snackbar.getView();
//        TextView textView=(TextView)sbSnackbar.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(Color.YELLOW);
        snackbar.show();
        return snackbar;
    }

    public final static boolean isValidEmailId(CharSequence target) {

        if (target == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }

    }

    public static boolean isValidatePassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static Toast showToast(Context mContext, String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        return toast;

    }

}
