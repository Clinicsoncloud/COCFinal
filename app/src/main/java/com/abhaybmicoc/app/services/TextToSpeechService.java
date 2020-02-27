package com.abhaybmicoc.app.services;

import android.util.Log;
import android.os.Build;
import android.content.Context;
import android.annotation.TargetApi;
import android.speech.tts.TextToSpeech;
import android.content.SharedPreferences;
import android.support.annotation.RequiresApi;

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
    }

    public void speakOut(String message) {
        if (isInitialize) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
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
        if (status == TextToSpeech.SUCCESS) {
            initializeLanguage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initializeLanguage(){
        /**
         * 1. Get preferred language
         * 2. Check if language is initialized
         * 3.a. If language initialized, speak default message
         * 3.b. If language not initialized, log error
         */
        isInitialize = true;
        sharedPreferenceLanguage = context.getSharedPreferences(ApiUtils.PREFERENCE_LANGUAGE, MODE_PRIVATE);

        int result = setLanguage();

        if (isLanguageSelected(result)) {
            speakOut(message);
        }
    }

    private boolean isLanguageSelected(int result){
        return result != -1 && result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int setLanguage(){
        int result = -1;

        if (sharedPreferenceLanguage.getString("my_language", "").equals("en")) {
            result = textToSpeech.setLanguage(Locale.US);
        } else if (sharedPreferenceLanguage.getString("my_language", "").equals("hi")) {
            result = textToSpeech.setLanguage(Locale.forLanguageTag("hi"));
            textToSpeech.setPitch(1f);
            textToSpeech.setSpeechRate(1.8f);
        } else if (sharedPreferenceLanguage.getString("my_language", "").equals("mar")) {
            result = textToSpeech.setLanguage(Locale.forLanguageTag("mar"));
            textToSpeech.setPitch(1f);
            textToSpeech.setSpeechRate(1.8f);
        }
        return result;
    }
}
