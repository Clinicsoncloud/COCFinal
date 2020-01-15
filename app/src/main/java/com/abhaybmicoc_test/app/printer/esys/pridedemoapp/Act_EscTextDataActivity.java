package com.abhaybmicoc_test.app.printer.esys.pridedemoapp;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abhaybmicoc_test.app.R;
import com.prowesspride.api.Printer_ESC;

public class Act_EscTextDataActivity extends Activity{
	
	Context context = this;
	private EditText edtTextData;
	//static ProgressDialog pdDialog;
	private Button btntTextPrint,btnOk;
	private int iRetVal,iWidth;
	private String sAddDatastr;
	public Dialog dlgCustom;
	private LinearLayout llProg;
	public static ProgressBar pbDialog;
	public static final int DEVICE_NOTCONNECTED = -100;
	//private Printer_ESC ptrEsc;
	public static String TAG = "Act_EscTextDataActivity";
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_esctextdata);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth();
		edtTextData = (EditText)findViewById(R.id.textdata_edt);
		btntTextPrint = (Button)findViewById(R.id.btntTextPrint);
		btntTextPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sData = edtTextData.getText().toString();
				if (sData.length() == 0) {
					dataHandler.obtainMessage(2, "Enter Text").sendToTarget();
				} else if (sData.length() > 0) {
					TextAsyc asynctask = new TextAsyc();
					asynctask.execute(0);
				}
			}
		});
	}
	
	/*   This method shows the TextAsyc operation */
	public class TextAsyc extends AsyncTask<Integer, Integer, Integer> {
		

		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of TextAsyc performing in the background*/	
		@SuppressLint("WrongThread")
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				sAddDatastr = edtTextData.getText().toString();
				if('\n'==sAddDatastr.charAt(sAddDatastr.length()-1)||'\r'==sAddDatastr.charAt(sAddDatastr.length()-1))
				iRetVal = Act_EscAdapter.ptrEsc.iTextPrint(sAddDatastr);
				else 
				iRetVal	=Act_EscAdapter. ptrEsc.iTextPrint(sAddDatastr+"\r");
				Log.e(TAG,"iretval value is"+iRetVal);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of TextAsyc in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			edtTextData.setText("");
			if (iRetVal == DEVICE_NOTCONNECTED) {
				dataHandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				dataHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				dataHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				dataHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				dataHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				dataHandler.obtainMessage(1, "Printering failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				dataHandler.obtainMessage(1,"Pparameter error").sendToTarget();
			}else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				dataHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				dataHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				dataHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				dataHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				dataHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* Handler to display UI response messages   */
	Handler dataHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				try{
					TextView tvMessage = (TextView)dlgCustom.findViewById(R.id.tvMessage); 
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
		dlgCustom = new Dialog(context1);
		dlgCustom.setTitle("Pride Demo");
		dlgCustom.setCancelable(false);
		dlgCustom.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustom.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dlgCustom.setContentView(R.layout.progressdialog);
		TextView tvTitle = (TextView)dlgCustom.findViewById(R.id.tvTitle);
		tvTitle.setWidth(iWidth);
		TextView tvMessage = (TextView)dlgCustom.findViewById(R.id.tvMessage); 
		tvMessage.setText(Message);
		llProg = (LinearLayout)dlgCustom.findViewById(R.id.llProg);
		pbDialog = (ProgressBar)dlgCustom.findViewById(R.id.pbDialog);
		btnOk = (Button)dlgCustom.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgCustom.dismiss();
			}
		});
		dlgCustom.show();
	}
}
