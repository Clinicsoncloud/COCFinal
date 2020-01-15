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
import android.util.Log;
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

public class Act_EscChangeFontActivity extends Activity{
	
	Context context = this;
	private EditText edtFont;
	//static ProgressDialog pdDialog;
	private Button btnSetmode,btnOk;
	private int iRetVal,iFont_Reverse,iFont_Inverse,iFont_Underline;
	private String sAddData;
	private String[] entertext_font = { 
								"STYLE_UNDERLINE ON","STYLE_UNDERLINE OFF", 
								"STYLE_INVERSE ON","STYLE_INVERSE OFF",
								"STYLE_REVERSE_180 ON", "STYLE_REVERSE_180 OFF" 
								};
	
	public static final int DEVICE_NOTCONNECTED = -100;
	private Printer_ESC Ptresc;
	//private Printer_ESC ptrEsc;
	Dialog dlgCustomdialog;
	private LinearLayout llProg;
	private int iWidth;
	public static ProgressBar pbProgress;
	public static String TAG= "Act_EscChangeFontActivity";
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_escchangefont);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth();
		
		try{
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			Ptresc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		edtFont = (EditText)findViewById(R.id.edtFont);
		Spinner spChngfontSty = (Spinner)findViewById(R.id.spChngfontSty);
		ArrayAdapter<String> arradFontSty = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, entertext_font);
		arradFontSty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spChngfontSty.setAdapter(arradFontSty);
		spChngfontSty.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					int i1 = Ptresc.iChangeFontStyle(Printer_ESC.STYLE_UNDERLINE, true);
					//Ptresc.iChangeFontStyle(Printer_ESC.STYLE_UNDERLINE,true);
					Log.e(TAG,"Underline on 1"+i1);
					break;
				case 1:
					//Ptresc.iChangeFontStyle(Printer_ESC.STYLE_UNDERLINE, false);
					int i2 = Ptresc.iChangeFontStyle(Printer_ESC.STYLE_UNDERLINE, false);
					Log.e(TAG,"Underline 0ff 2"+i2);
					break;
				case 2:
					int i3 = Ptresc.iChangeFontStyle(Printer_ESC.STYLE_INVERSE, true);
					Log.e(TAG,"Inverse on 3"+i3);
					break;
				case 3:
					int i4 = Ptresc.iChangeFontStyle(Printer_ESC.STYLE_INVERSE, false);		
					Log.e(TAG,"Inverse off 4"+i4);
					break;
				case 4:
					int i5 = Ptresc.iChangeFontStyle(Printer_ESC.STYLE_REVERSE_180, true);
					Log.e(TAG,"Reverse on 5"+i5);
					break;
				case 5:
					int i6 = Ptresc.iChangeFontStyle(Printer_ESC.STYLE_REVERSE_180, false);
					Log.e(TAG,"Reverse on 6"+i6);
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		btnSetmode = (Button)findViewById(R.id.btntTextPrint);
		btnSetmode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String sData = edtFont.getText().toString();
				Log.e(TAG, "value is   "+sData);
				if (sData.length() == 0) {
					fontHandler.obtainMessage(2, "Enter Text").sendToTarget();
					//Toast.makeText(getApplicationContext(), "Please enter text to print", Toast.LENGTH_LONG).show();
				} else if (sData.length() > 0) {
					EnterTextAsyc asynctask = new EnterTextAsyc();
					asynctask.execute(0);
				}
			}
		});
	}
	
	/*   This method shows the Reverse  AsynTask operation */
	public class Reverse extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait...");
			super.onPreExecute();
		}
		/* Task of Reverse performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iFont_Reverse= Ptresc.iChangeFontStyle(Printer_ESC.STYLE_REVERSE_180, true);
				Log.e(TAG,"STYLE_REVERSE_180      "+iFont_Reverse);
			} catch (NullPointerException e) {
				iFont_Reverse = DEVICE_NOTCONNECTED;
				return iFont_Reverse;
			}
			return iFont_Reverse;
			
		}
		
		/* This displays the status messages of Reverse in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			edtFont.setText("");
			if (iFont_Reverse == DEVICE_NOTCONNECTED) {
				fontHandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.SUCCESS) {
				fontHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PLATEN_OPEN) {
				fontHandler.obtainMessage(1,"Printer platen open").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PAPER_OUT) {
				fontHandler.obtainMessage(1,"Printer paper out").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.IMPROPER_VOLTAGE) {
				fontHandler.obtainMessage(1,"Printer improper voltage").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.FAILURE) {
				fontHandler.obtainMessage(1,"Printer failed").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PARAM_ERROR) {
				fontHandler.obtainMessage(1,"Printer param error").sendToTarget();
			}else if (iFont_Reverse == Printer_ESC.NO_RESPONSE) {
				fontHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iFont_Reverse== Printer_ESC.DEMO_VERSION) {
				fontHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iFont_Reverse==Printer_ESC.INVALID_DEVICE_ID) {
				fontHandler.obtainMessage(1,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iFont_Reverse==Printer_ESC.NOT_ACTIVATED) {
				fontHandler.obtainMessage(1,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the InverseAsyc  AsynTask operation */
	public class Inverse extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait...");
			super.onPreExecute();
		}
		
		/* Task of InverseAsyc performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iFont_Inverse= Ptresc.iChangeFontStyle(Printer_ESC.STYLE_INVERSE, true);
				Log.e(TAG,"STYLE_INVERSE      "+iFont_Inverse);
			} catch (NullPointerException e) {
				iFont_Inverse = DEVICE_NOTCONNECTED;
				return iFont_Inverse;
			}
			return iFont_Inverse;
		}
		
		/* This displays the status messages of InverseAsyc in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			edtFont.setText("");
			if (iFont_Reverse == DEVICE_NOTCONNECTED) {
				fontHandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.SUCCESS) {
				fontHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PLATEN_OPEN) {
				fontHandler.obtainMessage(1,"Printer platen open").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PAPER_OUT) {
				fontHandler.obtainMessage(1,"Printer paper out").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.IMPROPER_VOLTAGE) {
				fontHandler.obtainMessage(1,"Printer improper voltage").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.FAILURE) {
				fontHandler.obtainMessage(1,"Printer failed").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PARAM_ERROR) {
				fontHandler.obtainMessage(1,"Printer param error").sendToTarget();
			}else if (iFont_Reverse == Printer_ESC.NO_RESPONSE) {
				fontHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iFont_Reverse== Printer_ESC.DEMO_VERSION) {
				fontHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iFont_Reverse==Printer_ESC.INVALID_DEVICE_ID) {
				fontHandler.obtainMessage(1,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iFont_Reverse==Printer_ESC.NOT_ACTIVATED) {
				fontHandler.obtainMessage(1,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the UnderLineAsyc  AsynTask operation */
	public class UnderLine extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait...");
			super.onPreExecute();
		}
		
		/* Task of UnderLineAsyc performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iFont_Underline= Ptresc.iChangeFontStyle(Printer_ESC.STYLE_UNDERLINE, true);
				Log.e(TAG,"STYLE_UNDERLINE      "+iFont_Reverse);
			} catch (NullPointerException e) {
				iFont_Underline = DEVICE_NOTCONNECTED;
				return iFont_Underline;
			}
			return iFont_Underline;
		}
		
		/* This displays the status messages of UnderLineAsyc in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			edtFont.setText("");
			if (iFont_Reverse == DEVICE_NOTCONNECTED) {
				fontHandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.SUCCESS) {
				fontHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PLATEN_OPEN) {
				fontHandler.obtainMessage(1,"Printer platen open").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PAPER_OUT) {
				fontHandler.obtainMessage(1,"Printer paper out").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.IMPROPER_VOLTAGE) {
				fontHandler.obtainMessage(1,"Printer improper voltage").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.FAILURE) {
				fontHandler.obtainMessage(1,"Printer failed").sendToTarget();
			} else if (iFont_Reverse == Printer_ESC.PARAM_ERROR) {
				fontHandler.obtainMessage(1,"Printer param error").sendToTarget();
			}else if (iFont_Reverse == Printer_ESC.NO_RESPONSE) {
				fontHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iFont_Reverse== Printer_ESC.DEMO_VERSION) {
				fontHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iFont_Reverse==Printer_ESC.INVALID_DEVICE_ID) {
				fontHandler.obtainMessage(1,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iFont_Reverse==Printer_ESC.NOT_ACTIVATED) {
				fontHandler.obtainMessage(1,"Library not valid").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the EnterTextAsyc  AsynTask operation */
	public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait...");
			super.onPreExecute();
		}
		/* Task of EnterTextAsyc performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				sAddData = edtFont.getText().toString();
				if('\n'==sAddData.charAt(sAddData.length()-1)||'\r'==sAddData.charAt(sAddData.length()-1)){
					iRetVal = Ptresc.iTextPrint(sAddData);
					Log.e("Print","Containg \\n or \\r "+sAddData);
					Log.e("Data","<<<if condition>>>"+iRetVal);
				}else{ 
					iRetVal = Ptresc.iTextPrint(sAddData+"\r");
					Log.e(TAG,"Not Containg \\n or \\r "+sAddData);
					Log.e(TAG,"<<<else condition>>>"+iRetVal);
				}
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of EnterTextAsyc in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			edtFont.setText("");
			if (iRetVal == DEVICE_NOTCONNECTED) {
				fontHandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				fontHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				fontHandler.obtainMessage(1,"Printer platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				fontHandler.obtainMessage(1,"Printer paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				fontHandler.obtainMessage(1,"Printer improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				fontHandler.obtainMessage(1,"Printer failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				fontHandler.obtainMessage(1,"Printer param error").sendToTarget();
			}else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				fontHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				fontHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				fontHandler.obtainMessage(1,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				fontHandler.obtainMessage(1,"Library not valid").sendToTarget();
			}
			
			super.onPostExecute(result);
		}
	}
	
	/* Handler to display UI response messages   */
	Handler fontHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				try{
					TextView message_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvMessage); 
					message_tv.setText(""+msg.obj);
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
		alertDialogBuilder.setTitle("Pride Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
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
		pbProgress = (ProgressBar)dlgCustomdialog.findViewById(R.id.pbDialog);
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
}
