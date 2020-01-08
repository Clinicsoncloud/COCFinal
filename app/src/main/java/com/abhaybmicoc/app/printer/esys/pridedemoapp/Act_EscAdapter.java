package com.abhaybmicoc.app.printer.esys.pridedemoapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.printer.evolute.bluetooth.BluetoothComm;
import com.prowesspride.api.Printer_ESC;

@SuppressWarnings("unchecked")
public class Act_EscAdapter extends BaseExpandableListAdapter {
	/* List of Return codes for the respective response */
	public static final int DEVICE_NOTCONNECTED = -100;
	public ArrayList<String> alGroup_Item, alTempChild;
	public ArrayList<Object> alChild_Item = new ArrayList<Object>();
	public LayoutInflater liInflater;
	public Activity activity;
	public Dialog dialog;
	Context context;
	public static ProgressBar pbProgress;
	private LinearLayout llprog;
	public static Dialog dlgCustomdialog;
	public static Printer_ESC ptrEsc;
	private int iRetVal;
	private Button btnOk;
	int nError = 0;
	String myStr;
	private static final String TAG = "Act_EscAdapter";
	public static boolean ESC = false;

	public Act_EscAdapter(ArrayList<String> grList, ArrayList<Object> childItem) {
		alGroup_Item = grList;
		this.alChild_Item = childItem;
	}

	public void setInflater(LayoutInflater mInflater, Activity act) {
		this.liInflater = mInflater;
		activity = act;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	
	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		alTempChild = (ArrayList<String>) alChild_Item.get(groupPosition);
		try {
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
			Log.e(TAG, "printer static 1.................  is instantiated");
		} catch (Exception e) {
			Log.e(TAG, "erron in prrntr static instantiating");
		}
		TextView text = null;
		if (convertView == null) {
			convertView = liInflater.inflate(R.layout.childrow, null);
		}
		text = (TextView) convertView.findViewById(R.id.tvTitel);
		text.setText(alTempChild.get(childPosition));

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (alTempChild.get(childPosition).equals("[i]Text Print")) {
					context = v.getContext();
					Intent i = new Intent(context, Act_EscTextDataActivity.class);
					context.startActivity(i);
					
				} else if (alTempChild.get(childPosition).equals(
						"[ii]Set Font Properties")) {
					context = v.getContext();
					Intent font = new Intent(context,
							Act_EscFontPropertesActivity.class);
					context.startActivity(font);
					
				} else if (alTempChild.get(childPosition).equals(
						"[iii]Set Font Styles")) {
					context = v.getContext();
					Intent font = new Intent(context,
							Act_EscChangeFontActivity.class);
					context.startActivity(font);
					
				} else if (alTempChild.get(childPosition).equals(
						"[i]Barcode Data Print")) {
					context = v.getContext();
					Intent heiht = new Intent(context,
							Act_EscBarcodeHeightActivity.class);
					context.startActivity(heiht);
					
				} 
				else if (alTempChild.get(childPosition).equals(
						"[ii]QR Code")) {
					Log.e(TAG, "Esc QR code");
					context = v.getContext();
					ESC = true;
					
					Act_QRCode qrcodeasyc=new Act_QRCode(context, ptrEsc);
					qrcodeasyc.QrCode();
				}else if (alTempChild.get(childPosition).equals(
						"[i]BMP Print")) {
					context = v.getContext();
					Intent bmpprint = new Intent(context,
							Act_EscBMPPrintActivity.class);
					bmpprint.putExtra("bmpprnt","BmpPrint" );
					context.startActivity(bmpprint);
					
				} else if (alTempChild.get(childPosition).equals(
						"[ii]Grey Scale Image")) {
					context = v.getContext();
					Intent bmpprint = new Intent(context,
							Act_EscBMPPrintActivity.class);
					bmpprint.putExtra("bmpprnt","GrayscalePrint" );
					context.startActivity(bmpprint);
					
				} else {
					Log.d(TAG, "Deprecated.........!");
				}

			}
		});
		return convertView;
	}

	
	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) alChild_Item.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return alGroup_Item.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = liInflater.inflate(R.layout.grouprow, null);
			CheckedTextView trview = (CheckedTextView) convertView
					.findViewById(R.id.tvTitel);
			if (getChildrenCount(groupPosition) == 0 ) {
				convertView.setVisibility( View.INVISIBLE );
		    } 
		    else {
		    	convertView.setVisibility( View.VISIBLE );
		    } 
			
		}
		((CheckedTextView) convertView).setText(alGroup_Item.get(groupPosition));
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/* This performs Progress dialog box to show the progress of operation */
	protected void dlgShowCustom(Context context1, String Message) {
		dlgCustomdialog = new Dialog(context1);
		dlgCustomdialog.setTitle("Pride Demo");
		dlgCustomdialog.setCancelable(false);
		dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustomdialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dlgCustomdialog.setContentView(R.layout.progressdialog);
		TextView title_tv = (TextView) dlgCustomdialog
				.findViewById(R.id.tvTitle);
		title_tv.setWidth(Act_EscListActivity.iWidth);
		TextView message_tv = (TextView) dlgCustomdialog
				.findViewById(R.id.tvMessage);
		message_tv.setText(Message);
		llprog = (LinearLayout) dlgCustomdialog.findViewById(R.id.llProg);
		pbProgress = (ProgressBar) dlgCustomdialog.findViewById(R.id.pbDialog);
		btnOk = (Button) dlgCustomdialog.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgCustomdialog.dismiss();
			}
		});
		dlgCustomdialog.show();
	}

	/* Handler to display UI response messages */
	Handler escHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				try {
					TextView message_tv = (TextView) dlgCustomdialog
							.findViewById(R.id.tvMessage);
					message_tv.setText("" + msg.obj);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 2:
				String str1 = (String) msg.obj;
				dlgDhowdialog(str1);
				break;

			default:
				break;
			}
		};
	};

	/* This method shows the StartPrint AsynTask operation */
	public class StartPrint1 extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed */
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}

		/* Task of StartPrint performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = 0;
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of StartPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Printing Successfull")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1, "No response from Pride device")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,
						"Connected  device is not authenticated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1, "Library not Activated")
						.sendToTarget();
			} else {
				escHandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* This method shows the ResetData AsynTask operation */
	public class ResetData extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed */
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}

		/* Task of ResetData performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrEsc.iReset();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of ResetData in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Reset Successful").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1, "No response from Pride device")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,
						"Connected  device is not authenticated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1, "Library not Activated")
						.sendToTarget();
			} else {
				escHandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}

			super.onPostExecute(result);
		}
	}

	/* This method shows the ITestPrint AsynTask operation */
	public class ITestPrint extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed */
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}

		/* Task of ITestPrint performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrEsc.iTestPrint();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of ITestPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Test printing successful")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1, "No response from Pride device")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,
						"Connected  device is not authenticated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1, "Library not Activated")
						.sendToTarget();
			} else {
				escHandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* This method shows the Diagnous AsynTask operation */
	public class DiagnousESC extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed */
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}

		/* Task of Diagnous performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrEsc.iDiagnose();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/*
		 * This sends message to handler to display the status messages of
		 * Diagnose in the dialog box
		 */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Printer is at good condition")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1, "No response from Pride device")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,
						"Connected  device is not authenticated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1, "Library not Activated")
						.sendToTarget();
			} else {
				escHandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* This method shows the ClearBuffer AsynTask operation */
	class ClearBuffer1 extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}

		/* Task of ClearBuffer performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = 0;// ptrEsc.iClearBuffer_PM();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/*
		 * This sends message to handler to display the status messages of
		 * ClearBuffer in the dialog box
		 */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Buffer Cleared Successfully")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1, "No response from Pride device")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,
						"Connected  device is not authenticated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1, "Library not Activated")
						.sendToTarget();
			} else {
				escHandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* This method shows the ClearBuffer AsynTask operation */
	class ClearBuffer2 extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}

		/* Task of ClearBuffer performing in the background */
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				try {
					InputStream input = BluetoothComm.misIn;
					OutputStream outstream = BluetoothComm.mosOut;
					ptrEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
				} catch (Exception e) {
					e.printStackTrace();
				}

				read("test1.txt");
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_8X16);
				SystemClock.sleep(1000);
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_SET_DOUBLE_HEIGHT);

				iRetVal = ptrEsc.iTextPrint(myStr);
				read("test2.txt");
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_8X16);

				iRetVal = ptrEsc.iTextPrint(myStr);
				read("test3.txt");
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_8X16);
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_SET_DOUBLE_HEIGHT);

				iRetVal = ptrEsc.iTextPrint(myStr);
				read("test4.txt");
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_8X16);

				iRetVal = ptrEsc.iTextPrint(myStr);
				read("test5.txt");
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_8X16);
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_SET_DOUBLE_HEIGHT);

				iRetVal = ptrEsc.iTextPrint(myStr);

				read("test6.txt");
				ptrEsc.iSetFontProperties(Printer_ESC.FONT_DEFAULT_8X16);
				iRetVal = ptrEsc.iTextPrint(myStr);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/*
		 * This sends message to handler to display the status messages of
		 * ClearBuffer in the dialog box
		 */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Print Successfull").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1, "No response from Pride device")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,
						"Connected  device is not authenticated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1, "Library not Activated")
						.sendToTarget();
			} else {
				escHandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* To show response messages */
	public void dlgDhowdialog(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle("Pride Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		/* create alert dialog */
		AlertDialog alertDialog = alertDialogBuilder.create();
		/* show alert dialog */
		alertDialog.show();
	}

	public String read(String filename) {

		String line = null;
		File dir = Environment.getExternalStorageDirectory();
		Log.e(TAG, "File path----->" + dir);
		
		File file = new File(dir, filename);
		Log.e(TAG, "File path----->" + file.getAbsolutePath());
		if (file.exists()) // check if file exist
		{
			Log.e(TAG, "File path----exists->" + file.getAbsolutePath());

			// Read text from file
			StringBuilder text = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				// String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}
			} catch (IOException e) {
				nError = 1;
				// You'll need to add proper error handling here
			}

			Log.e(TAG, "DATA------------>" + text);
			myStr = text.toString();
		} else {
			Log.e(TAG, "File path---not-->" + file.getAbsolutePath());
			nError = 1;
		}

		return line;
	}
	

}
