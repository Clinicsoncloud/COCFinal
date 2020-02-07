package com.abhaybmicoc.app.services;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechService implements TextToSpeech.OnInitListener {

    Context context;

    TextToSpeech textToSpeech;

    String msg;

    public TextToSpeechService(Context context, String msg) {
        Log.e("TextToSpeechService"," : ");
        this.context = context;

        this.textToSpeech = new TextToSpeech(context,this);

        this.msg = msg;
    }

    public void speakOut(String msg) {
        Log.e("speakOut"," : ");
        Log.e("msg",""+msg);
        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stopTextToSpeech() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) { }
    }

    @Override
    public void onInit(int status) {
        Log.e("onInit"," : ");
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // TODO: Handle this instead of logging
            } else {
                speakOut(msg);
            }
        }
    }
}
