package com.abhaybmicoc.app.services;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextToSpeechService implements TextToSpeech.OnInitListener {

    private Context context;

    private TextToSpeech textToSpeech;

    private String msg;

    private boolean isInitialize = false;

    public TextToSpeechService(Context context, String msg) {
        this.context = context;

        this.textToSpeech = new TextToSpeech(context, this);

        this.msg = msg;
    }

    public void speakOut(String msg) {
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                isInitialize = true;
                speakOut(msg);
            }
        }
    }
}
