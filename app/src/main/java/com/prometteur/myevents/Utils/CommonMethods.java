package com.prometteur.myevents.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;


import com.prometteur.myevents.Activity.EnterMobileNumberActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class CommonMethods {
    Context context;


    public CommonMethods(Context context) {
        this.context = context;
    }


    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,15}$");
        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
    }


    public void removeSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        editText.setFilters(new InputFilter[]{filter});


    }


    public void sessionExpired(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Login", "False");
        editor.putString("UserId", "");

        editor.putString("UserSession", "");
        editor.apply();
        Toast.makeText(context, "Session Expired", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, EnterMobileNumberActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public boolean isValidMobile(String phone) {
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 9 && phone.length() <= 13;
        }
        return false;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean returnVal = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

        if (!returnVal) {
            Toast.makeText(context, "Check your Internet connection and try again", Toast.LENGTH_SHORT).show();
        }

        return returnVal;
    }

    public String changeDateFormat(String strCurrentDate) {
        String date = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = format.parse(strCurrentDate);
            format = new SimpleDateFormat("yyyy-MM-dd  hh:mm a");
            date = format.format(newDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public String changeOnlyTimeFormat(String strCurrentDate) {
        String date = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date newDate = format.parse(strCurrentDate);
            format = new SimpleDateFormat("hh:mm a");
            date = format.format(newDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

}
