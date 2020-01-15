package com.abhaybmicoctest.app.printer.esys.pridedemoapp;

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

import com.abhaybmicoctest.app.R;
import com.abhaybmicoctest.app.printer.evolute.bluetooth.BluetoothComm;
import com.prowesspride.api.Printer_ESC;

public class Act_EscFontPropertesActivity extends Activity{
	private String[] barcode_style = { 
			"FONT_DEFAULT_16X16","FONT_DEFAULT_8X16",
			"FONT_SET_INVERSE_PRINT","FONT_SET_REVERSE_PRINT",
			"FONT_SET_DOUBLE_HEIGHT","FONT_SET_UNDERLINE"
	};
	private Printer_ESC ptrEsc;
	private int iRetVal,iWidth;
	public static int iFontproperty;
	private EditText edtFontproperty;
	private Button btnPrint,btnOk;
	private String sFontdata;
	public static Dialog dlgCustomdialog;
	Context context = this;
	private LinearLayout llprog;
	public static ProgressBar pbProgress;
	public static final int DEVICE_NOTCONNECTED = -100;
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_escfontproperty);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth();
		
		try{
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		edtFontproperty = (EditText)findViewById(R.id.edtFontproperty);
		Spinner spFontproperty = (Spinner)findViewById(R.id.spFontproperty);
		ArrayAdapter<String> arradBarcod = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, barcode_style);
		arradBarcod.setDropDownViewResource(R.layout.my_spinner);
		spFontproperty.setAdapter(arradBarcod);
		spFontproperty.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_16X16);
					break;
				case 1:
					ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_8X16);
					break;
				case 2:
					ptrEsc.iSetFontProperties(Printer_ESC.FONT_SET_INVERSE_PRINT);
					break;
				case 3:
					ptrEsc.iSetFontProperties(Printer_ESC.FONT_SET_REVERSE_PRINT);
					break;
				case 4:
					ptrEsc.iSetFontProperties(Printer_ESC.FONT_SET_DOUBLE_HEIGHT);
					break;
				case 5:
					ptrEsc.iSetFontProperties(Printer_ESC.FONT_SET_UNDERLINE);
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		btnPrint = (Button)findViewById(R.id.btnFonproPrint);
		btnPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sFontdata = edtFontproperty.getText().toString();
				if(sFontdata.length()==0){fontHandler.obtainMessage(2,"Enter Text").sendToTarget();
				}else if(sFontdata.length()>0){
					Fontstyle style = new Fontstyle();
					style.execute(0);
				}
			}

		});
	}
	
	
	/* Handler to display UI response messages   */
	Handler fontHandler = new Handler(){
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
	
	/*   This method shows the Fontstyle  AsynTask operation */
	public class Fontstyle extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		/* Task of Fontstyle performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				if('\n'==sFontdata.charAt(sFontdata.length()-1)||'\r'==sFontdata.charAt(sFontdata.length()-1))
					iRetVal = ptrEsc.iTextPrint(sFontdata);
				else 
					iRetVal = ptrEsc.iTextPrint(sFontdata+"\r");
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This displays the status messages of Fontstyle in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			edtFontproperty.setText("");
			if (iRetVal == DEVICE_NOTCONNECTED) {
				fontHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				fontHandler.obtainMessage(1, "Printing successfull").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				fontHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				fontHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				fontHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				fontHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				fontHandler.obtainMessage(1,"Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				fontHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				fontHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				fontHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				fontHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				fontHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
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
		llprog = (LinearLayout)dlgCustomdialog.findViewById(R.id.llProg);
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
	
}
