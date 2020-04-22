package com.abhaybmicoc.app.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VU {
    public static final String MOBILE_PATTERN = "[0][1][2]{10}";
    public static final String PHONE_PATTERN = "[0-9]{11}";
    public static final String LAND_LINE_PATTERN = "[0-9]{11}";
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";
    public static final String VEHICLE_NO_PATTERN = "[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)?[0-9]{4}";
    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z-a-z]+\\.+[a-z]+");
    public static Pattern pattern;
    public static String contactstring;
    static Matcher matcher;

    public static boolean isEmpty(EditText editText) {
        // TODO method to check edit text is fill or no
        // return true when edit text is empty
        if (editText.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(TextView textView) {
        // TODO method to check edit text is fill or no
        // return true when edit text is empty
        if (textView.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }


    public static boolean isDotOnly(EditText editText) {
        // TODO method to check edit text is fill or no
        if (editText.getText().toString().length() == 1 && editText.getText().toString().charAt(0) == '.') {
            return true;
        }
        if (editText.getText().toString().trim().equals(".")) {
            return true;
        }


        return false;
    }

    public static boolean isDot(EditText editText) {
        // TODO method to check edit text is fill or no
        if (editText.getText().toString().length() == 1 && editText.getText().toString().trim().equals(".")) {
            return true;
        }

        if (editText.getText().toString().length() > 1 && editText.getText().toString().charAt(1) == '.') {
            return true;
        }
        return false;
    }

    public static boolean isAutocompleteEmpty(AutoCompleteTextView editText) {
        // TODO method to check edit text is fill or no
        if (editText.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    public static boolean checkEmail(EditText edittext) {
        return EMAIL_ADDRESS_PATTERN.matcher(edittext.getText().toString().trim()).matches();
    }

    public static boolean isEmailId(EditText editText) {
        // method to check edit text is fill or no
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(editText.getText().toString().trim());
        if (matcher.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isPhoneNo(EditText edittext) {
        //method to check Phone no string
        if (edittext.getText().toString().trim().length() != 0 && edittext.getText().toString().trim().length() < 10 || edittext.getText().toString().trim().length() > 11) {
            return true;
        }
        return false;
    }

    public static boolean isConfirPassWord(EditText edtPassword,
                                           EditText edtConfirPassword) {
        if (edtPassword.getText().toString()
                .equals(edtConfirPassword.getText().toString())) {
            return true;
        }
        return false;
    }

    public static boolean isVehicleNo(EditText editText) {
        if (editText.getText().toString().matches(VEHICLE_NO_PATTERN)) {
            return true;
        }
        return false;
    }

    public static boolean isValidNo(EditText editText) {
        if (editText.getText().toString().matches(MOBILE_PATTERN)) {
            if (editText.getText().toString().trim().charAt(0) != '0') {
                return true;
            }
        }
        return false;
    }

    public static boolean isContactNo(EditText editText) {
        if (editText.getText().toString().trim().length() != 0 && editText.getText().toString().trim().length() != 10) {
            if (editText.getText().toString().trim().length() == 10) {
                if (editText.getText().toString().trim().charAt(0) != '0') {
                    return true;
                }
                return false;
            } else {
                return true;
            }

        }


        return false;
    }


    public static boolean isMobile(EditText edt) {
        String str = edt.getText().toString().trim();
        String MOBILE = "^[0-9]{10}";
        Matcher m;
        Pattern p = Pattern.compile(MOBILE);
        m = p.matcher(str);
        if (!m.matches()) {
            return true;
        } else if (str.length() < 10 || str.length() > 10) {
            return true;
        } else {
            //Log.e("validation","else");
            for (int i = 0; i < 7; i++) {
                //  Log.e("validation","for");
                if (Character.getNumericValue(str.charAt(0)) == i) {
                    //      Log.e("validation","if");
                    return true;
                }
            }
        }
        return false;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

        listView.requestLayout();
    }

    public static boolean isPasswordValid(String passwordEd) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!#$%^&+=])(?=\\S+$).{4,}$";
        //final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(passwordEd);

        return matcher.matches();
      /*  Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(passwordEd);
        //if (!matcher.matches()) {
            return matcher.matches();
        //}
        //return false;*/
    }


}
