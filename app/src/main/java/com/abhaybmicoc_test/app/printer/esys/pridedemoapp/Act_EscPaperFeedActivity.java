package com.abhaybmicoc_test.app.printer.esys.pridedemoapp;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.abhaybmicoc_test.app.R;
import com.abhaybmicoc_test.app.printer.evolute.bluetooth.BluetoothComm;
import com.prowesspride.api.Printer_ESC;

public class Act_EscPaperFeedActivity extends Activity{
	private String[] sPaperFeed_Types = { "FEED_IN_MILLIMETER", "FEED_IN_LINES"};
	private Button btnPaperfeedPrint,btnOk;
	private int iPaperFeedType,iRetVal;
	public static final int DEVICE_NOTCONNECTED = -100;
	private int iStrToIntVal,iWidth;
	public Dialog dlgCustomdialog;
	public static ProgressBar pgProgress;
	private LinearLayout llProg;
	Context context = this;
	private Printer_ESC ptrEsc;
	private EditText edtPaperfeed;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_escpaperfeed);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth();
		
		try{
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
		}catch(Exception e){}
		
		edtPaperfeed = (EditText)findViewById(R.id.edtPaperfeed);
		Spinner spPaperfeed = (Spinner)findViewById(R.id.spPaperfeed);
		ArrayAdapter<String> arradPaperfeed = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, sPaperFeed_Types);
		arradPaperfeed.setDropDownViewResource(R.layout.my_spinner);
		spPaperfeed.setAdapter(arradPaperfeed);
		spPaperfeed.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					edtPaperfeed.setText("");
					iPaperFeedType = Printer_ESC.FEED_IN_MILLIMETER;//-36;
					break;
				case 1:
					edtPaperfeed.setText("");
					iPaperFeedType = Printer_ESC.FEED_IN_LINES;//-37;
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		btnPaperfeedPrint = (Button)findViewById(R.id.btnPaperfeedPrint);
		btnPaperfeedPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sValue = edtPaperfeed.getText().toString();
				if (sValue.length() == 0) {
					paperHandler.obtainMessage(2, "Enter Text").sendToTarget();
				} else if (sValue.length() > 0) {
					iStrToIntVal=Integer.parseInt(sValue);
					PaperFeed paperfeed = new PaperFeed();
					paperfeed.execute(0);
				}
			}
		});
	}

		
	// This method shows the PaperFeed AsynTask operation 
	public class PaperFeed extends AsyncTask<Integer, Integer, Integer> {
		// displays the progress dialog untill background task is completed
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		// Task of PaperFeed performing in the background
		@Override
		protected Integer doInBackground(Integer... params) {
			try {			
				iRetVal = ptrEsc.iPaperFeed(iPaperFeedType, iStrToIntVal);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		//This sends message to handler to display the status messages of PaperFeed in the dialog box 
		@Override
		protected void onPostExecute(Integer result) {	
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			edtPaperfeed.setText("");
			if (iRetVal == DEVICE_NOTCONNECTED) {
				paperHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				paperHandler.obtainMessage(1,"Paper Feed Successfull").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				paperHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				paperHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				paperHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				paperHandler.obtainMessage(1, "Printering  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				paperHandler.obtainMessage(1,"Invalie input").sendToTarget();
			}else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				paperHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				paperHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				paperHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				paperHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				paperHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/* This performs Progress dialog box to show the progress of operation */
	protected void dlgShowCustom(Context context1,String Message) {
		dlgCustomdialog = new Dialog(context1);
		dlgCustomdialog.setTitle("Pride Demo");
		dlgCustomdialog.setCancelable(false);
		dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustomdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dlgCustomdialog.setContentView(R.layout.progressdialog);
		TextView title_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvTitle);
		title_tv.setWidth(iWidth);
		TextView message_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvMessage); 
		message_tv.setText(Message);
		llProg = (LinearLayout)dlgCustomdialog.findViewById(R.id.llProg);
		pgProgress = (ProgressBar)dlgCustomdialog.findViewById(R.id.pbDialog);
		btnOk = (Button)dlgCustomdialog.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgCustomdialog.dismiss();
			}
		});
		dlgCustomdialog.show();
	}
	/* Handler to display UI response messages   */
	Handler paperHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				try{
					TextView tvMessage = (TextView)dlgCustomdialog.findViewById(R.id.tvMessage); 
					tvMessage.setText(""+msg.obj);
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 2:
				String str1 = (String) msg.obj;
				dlgShow(str1);
				break;
			default:
				break;
			}
		};
	};
	
	/*  To show response messages  */
	public void dlgShow(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Pride Demo Application")
		.setMessage(str).setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		/* create alert dialog*/
		AlertDialog alertDialog = alertDialogBuilder.create();
		/* show alert dialog*/
		alertDialog.show();
	}
}