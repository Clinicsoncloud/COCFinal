package com.abhaybmicoc.app.services;

import android.util.Log;
import android.os.Build;
import android.content.Context;
import android.annotation.TargetApi;
import android.speech.tts.TextToSpeech;
import android.content.SharedPreferences;

import com.abhaybmicoc.app.utils.ApiUtils;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class TextToSpeechService implements TextToSpeech.OnInitListener {

    private Context context;

    private TextToSpeech textToSpeech;

    private String message;

    private boolean isInitialize = false;

    private SharedPreferences sharedPreferenceLanguage;

    public TextToSpeechService(Context context, String msg) {
        this.context = context;

        this.textToSpeech = new TextToSpeech(context, this);
        this.message = msg;
        Log.e("constructor_TxtSpeach", " : " + msg);
    }

    public void speakOut(String message) {
        if (isInitialize) {
            Log.e("speakOut_message", " : " + message);
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void stopTextToSpeech() {

        Log.e("Stopped_TextSpeach", ":" + textToSpeech);
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

            isInitialize = true;

            sharedPreferenceLanguage = context.getSharedPreferences(ApiUtils.PREFERENCE_LANGUAGE, MODE_PRIVATE);

            Log.e("My_Selected_Lang", ":" + sharedPreferenceLanguage.getString("my_lan", ""));

            if (sharedPreferenceLanguage.getString("my_lan", "").equals("en")) {

                Log.e("inside_english", " : ");
                result = textToSpeech.setLanguage(Locale.US);
            } else if (sharedPreferenceLanguage.getString("my_lan","").equals("hi")) {
                Log.e("inside_hindi", " : ");
                result = textToSpeech.setLanguage(Locale.forLanguageTag("hi"));
            }else {
                result = textToSpeech.setLanguage(Locale.forLanguageTag("mar"));
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("inside_langerror", " : ");
            } else {
                Log.e("inside_else", " : speakOut_isInitialize" + isInitialize);
                speakOut(message);
            }
        }
    }
}
