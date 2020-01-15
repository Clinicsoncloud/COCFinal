package com.abhaybmicoctest.app.printer.esys.pridedemoapp;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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

public class Act_EscBarcodeHeightActivity extends Activity{
	private String[] barcode_style = { 
							"2of5 Compressed","2Of5 Uncompressed",
							"3Of9 Compressed","3Of9 Uncompressed", 
							"Ean13/Upc-A" 
							};
	private int iSelectpostion;
	static int iBarcodestyle;
	private String sBarcodestr,sBarheight;
	private EditText edtEBarcode,edtEbarheight;
	public Context context = this;
	private int iRetVal;
	private int iBarheight = 120; // default minimum height value
	private Dialog dlgCustomdialog;
	private LinearLayout llProg;
	public static ProgressBar pbProgress;
	public static ProgressDialog pdDialog;
	public static final int DEVICE_NOTCONNECTED = -100;
	private Printer_ESC ptrEsc;
	private int iWidth;
	private Button btnOk;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_escbarcodeheight);
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth();
		
		try{
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		edtEBarcode = (EditText)findViewById(R.id.edtEBarcode);
		edtEbarheight = (EditText)findViewById(R.id.edtEbarheight);
		
		
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtEBarcode.getWindowToken(), 0);
		//spinner select items
		Spinner spBarcode = (Spinner)findViewById(R.id.spBarcode);
		ArrayAdapter<String> arradBarcode = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, barcode_style);
		arradBarcode.setDropDownViewResource(R.layout.my_spinner);
		spBarcode.setAdapter(arradBarcode);
		spBarcode.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				InputMethodManager hight = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				hight.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
				switch (position) {
				case 0:
					edtEBarcode.setText("");
					iSelectpostion = 1;
					iBarcodestyle=Printer_ESC.BARCODE_2OF5_COMPRESSED;//-21;
					break;
				case 1:
					iSelectpostion = 2;
					edtEBarcode.setText("");
					iBarcodestyle=Printer_ESC.BARCODE_2OF5_UNCOMPRESSED;//-22;
					break;
				case 2:
					edtEBarcode.setText("");
					iSelectpostion = 3;
					iBarcodestyle=Printer_ESC.BARCODE_3OF9_COMPRESSED;//-24;
					break;
				case 3:
					edtEBarcode.setText("");
					iSelectpostion = 4;
					iBarcodestyle=Printer_ESC.BARCODE_3OF9_UNCOMPRESSED;//-25;
					break;
				case 4:
					edtEBarcode.setText("");
					iSelectpostion = 5;
					iBarcodestyle=Printer_ESC.BARCODE_EAN13_STANDARD;//-26;
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		edtEBarcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (iSelectpostion) {
				case 1:
					edtEBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edtEBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25)});
					break;
				case 2:
					edtEBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edtEBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				case 3:
					edtEBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
					edtEBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25)});
					break;
				case 4:
					edtEBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
					edtEBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				case 5:
					edtEBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edtEBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				default:
					break;
				}
			}
		});
		
		Button btnBarheiPrint = (Button)findViewById(R.id.btnBarheiPrint);
		btnBarheiPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sBarcodestr = edtEBarcode.getText().toString();
				sBarheight = edtEbarheight.getText().toString();
				
				Log.e("EDIT TEXT", "VALUE"+sBarheight);
				try{
					iBarheight = Integer.parseInt(sBarheight);
					
				}catch(NumberFormatException e){}
				
				Log.e("EDIT TEXT", "VALUE Convertion"+iBarheight);
				char[] ccc = sBarcodestr.toCharArray();
				/*if(iBarheight>=256 || iBarheight<120){
					Toast.makeText(getApplicationContext(), "Enter Height greater than 119 and less than 256", Toast.LENGTH_LONG).show();
				}else{*/
				if ((sBarcodestr.length() == 0)) {
					barcodeHandler.obtainMessage(2, "Enter text").sendToTarget();
				} else if ((sBarcodestr.length() > 0)) {
					switch (iSelectpostion) {
					case 3:
						BarCodePrint barcode = new BarCodePrint();
						barcode.execute(0);
						break;
					case 1:
						int i;
						for (i = 0; i < sBarcodestr.length(); i++) {
							if (!(ccc[i] >= '0' && ccc[i] <= '9')){
								break;
							}
						}
						if (i != sBarcodestr.length()) {
							barcodeHandler.obtainMessage(2,"Please enter numeric characters").sendToTarget();
						} else {
							BarCodePrint barcode1 = new BarCodePrint();
							barcode1.execute(0);
						}
						break;
					case 2:
						sBarcodestr = edtEBarcode.getText().toString();
						if (sBarcodestr.length() > 12) {
							barcodeHandler.obtainMessage(2,"Enter data less than 13 characters").sendToTarget();
						} else {
							for (i = 0; i < sBarcodestr.length(); i++) {
								if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
									break;
								}
							}
							if (i != sBarcodestr.length()) {
								barcodeHandler.obtainMessage(2,"Please enter numeric characters").sendToTarget();
							} else {
								BarCodePrint barcode1 = new BarCodePrint();
								barcode1.execute(0);
							}
						}
						break;
					case 4:
						sBarcodestr = edtEBarcode.getText().toString();
						if (sBarcodestr.length() > 12) {
							barcodeHandler.obtainMessage(2,"Enter data less than 13 characters").sendToTarget();
						} else {
							BarCodePrint barcode1 = new BarCodePrint();
							barcode1.execute(0);
						}
						break;
					case 5:
						sBarcodestr = edtEBarcode.getText().toString();
						if (sBarcodestr.length() > 13) {
							barcodeHandler.obtainMessage(2,"Enter data less than 13").sendToTarget();
						} else {
							for (i = 0; i < sBarcodestr.length(); i++) {
								if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
									break;
								}
							}
							if (i != sBarcodestr.length()) {
								barcodeHandler.obtainMessage(2,"Please enter numerics only").sendToTarget();
							} else {
								BarCodePrint barcode1 = new BarCodePrint();
								barcode1.execute(0);
							}
						}
						break;
					}
				}
			}
			//}
		
		});
	}


	/* Handler to display UI response messages   */
	Handler barcodeHandler = new Handler() {
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
	public void dlgShow(String str){
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
	
	/*   This method shows the BarCodePrint  AsynTask operation */
	public class BarCodePrint extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		/* Task of BarCodePrint performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrEsc.iSetBarcodeHeight(iBarheight);
				if (iRetVal == Printer_ESC.SUCCESS) {
					byte[] b = sBarcodestr.getBytes();
					iRetVal = ptrEsc.iBarcodePrint(iBarcodestyle, b);
				} 
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This displays the status messages of BarCodePrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			//dlgCustomdialog.dismiss();
			edtEBarcode.setText("");
			edtEbarheight.setText("");
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				barcodeHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				barcodeHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				barcodeHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				barcodeHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				barcodeHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				barcodeHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				barcodeHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				barcodeHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				barcodeHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				barcodeHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				barcodeHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_SUPPORTED) {
				barcodeHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				barcodeHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* This performs Progress dialog box to show the progress of operation */
	public static void dlgProgress(Context context, String msg) {
		pdDialog = new ProgressDialog(context);
		pdDialog.setMessage(msg);
		pdDialog.setIndeterminate(true);
		pdDialog.setCancelable(false);
		pdDialog.show();
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
