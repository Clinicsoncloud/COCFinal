package com.abhaybmicoc.app.activity;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.abhaybmicoc.app.R;

public class DialogActivity extends Activity {
	public static int REQUEST_CODE = 200 ;
	public static String INTENT_KEY_TITLE = "com.andmedical.intent.key.title";
	public static String INTENT_KEY_MESSAGE = "com.andmedical.intent.key.message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_dialog);
		
		Intent intent = getIntent();
		if(intent == null) {
			finish();
		}

		setupUI(intent);
		setupEvents();
	}

	/**
	 *
	 */
	private void setupUI(Intent intent){
		Bundle bundle = intent.getExtras();

		String title = null;
		String message = null;
		if(bundle != null) {
			title = bundle.getString(INTENT_KEY_TITLE);
			message = bundle.getString(INTENT_KEY_MESSAGE);

			if(title == null)
				title = "";

			if(message == null)
				message = "";
		}

		TextView titleTextView = findViewById(R.id.dialog_title_textview);
		TextView messageTextView = findViewById(R.id.dialog_message_textview);

		titleTextView.setText(title);
		messageTextView.setText(message);
	}

	/**
	 *
	 */
	private void setupEvents(){
		TextView positiveTextureView = findViewById(R.id.positive_button_textview);
		positiveTextureView.setOnClickListener(view -> {
			setResult(RESULT_OK);
			finish();
		});
	}
}