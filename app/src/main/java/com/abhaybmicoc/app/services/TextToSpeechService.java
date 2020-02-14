package com.abhaybmicoc.app.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.abhaybmicoc.app.utils.ApiUtils;

import java.util.Locale;


import static android.content.Context.MODE_PRIVATE;

public class TextToSpeechService implements TextToSpeech.OnInitListener {

    private Context context;

    private TextToSpeech textToSpeech;

    private String msg;

    private boolean isInitialize = false;

    private SharedPreferences sharedPreferenceLanguage;

    public TextToSpeechService(Context context, String msg) {
        Log.e("constructo", " : ");
        this.context = context;

        this.textToSpeech = new TextToSpeech(context, this);

        this.msg = msg;
    }

    public void speakOut(String msg) {
        Log.e("speakOut", " : ");
        if (isInitialize)
            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stopTextToSpeech() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        Log.e("onInit", " : ");
        if (status == TextToSpeech.SUCCESS) {
            int result;

            sharedPreferenceLanguage = context.getSharedPreferences(ApiUtils.PREFERENCE_LANGUAGE, MODE_PRIVATE);

            Log.e("My_Selected_Lang", ":" + sharedPreferenceLanguage.getString("my_lan", ""));

            if (sharedPreferenceLanguage.getString("my_lan", "").equals("en")) {

                Log.e("inside_english"," : ");
                result = textToSpeech.setLanguage(Locale.US);
            } else {
                Log.e("inside_hindi"," : ");
                result = textToSpeech.setLanguage(Locale.forLanguageTag("hi"));
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("inside_langerror"," : ");
            } else {
                Log.e("inside_else"," : speakOut_isInitialize");
                isInitialize = true;
                speakOut(msg);
            }
        }
    }
}
