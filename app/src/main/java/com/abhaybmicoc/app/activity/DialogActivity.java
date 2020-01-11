package com.abhaybmicoc.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DialogActivity extends Activity {
	
	public static String INTENT_KEY_TITLE = "com.andmedical.intent.key.title";
	public static String INTENT_KEY_MESSAGE = "com.andmedical.intent.key.message";
	public static int REQUEST_CODE = 200 ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_dialog);
		
		Intent intent = getIntent();
		if(intent == null) {
			finish();
		}
		
		Bundle bundle = intent.getExtras();
		String title = null;
		String message = null;
		if(bundle != null) {
			title = bundle.getString(INTENT_KEY_TITLE);
			message = bundle.getString(INTENT_KEY_MESSAGE);

			if(title ==null)title = "";
			if(message == null)message = "";
		}
		
		TextView titleTextView = (TextView)findViewById(R.id.dialog_title_textview);
		titleTextView.setText(title);
		TextView messageTextView = (TextView)findViewById(R.id.dialog_message_textview);
		messageTextView.setText(message);
		
		TextView positiveTextureView = (TextView)findViewById(R.id.positive_button_textview);
		positiveTextureView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}
}