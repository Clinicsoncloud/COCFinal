package com.abhaybmi.app.printer.esys.pridedemoapp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterViewFlipper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;


import com.abhaybmi.app.R;
import com.abhaybmi.app.printer.evolute.bluetooth.BluetoothComm;
import com.prowesspride.api.Printer_ESC;

public class Act_EscListActivity extends Activity implements OnChildClickListener {
	Context context = this;
	public static int iWidth;
	public static final int DEVICE_NOTCONNECTED = -100;
	private LinearLayout llprog;
	public static ProgressBar pbProgress;
	public static Dialog dlgCustomdialog;
	public static Printer_ESC ptrEsc;
	private int iRetVal;
	private Button btnOk;
	private ExpandableListView explvEsc;
	ListView listview;
	@SuppressWarnings("deprecation")
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.escexplistview);
		try {
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
			Log.e("", "printer static 1.................  is instantiated");
		} catch (Exception e) {
			Log.e("", "erron in prrntr static instantiating");
		}
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth(); 
		listview = (ListView) findViewById(R.id.listview);
		String[] values = new String[] { "[4] Test Print", "[5] Unicode Print", "[6] Reset",
		        "[7] Diagnostics", "[8] Paper Feed"  };
		 final ArrayList<String> list = new ArrayList<String>();
		    for (int i = 0; i < values.length; ++i) {
		      list.add(values[i]);
		    }
		   
		    final ArrayAdapter adapter = new ArrayAdapter(this,
		            R.layout.mytextview, list);
		        listview.setAdapter(adapter);
		    
		    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					switch (position) {
					case 0:
						//Toast.makeText(getApplicationContext(), "Onitemclick 0", Toast.LENGTH_LONG).show();
						ITestPrint itest = new ITestPrint();
						itest.execute(0);
						break;
					case 1:
						//Toast.makeText(getApplicationContext(), "Onitemclick 1", Toast.LENGTH_LONG).show();
						Act_UnicodePrinting unicodePrinting = new Act_UnicodePrinting(context, ptrEsc);
						unicodePrinting.unicode();
						break;
					case 2:
						//Toast.makeText(getApplicationContext(), "Onitemclick 3", Toast.LENGTH_LONG).show();
						ResetData data = new ResetData();
						data.execute(0);
						break;
					case 3:
						//Toast.makeText(getApplicationContext(), "Onitemclick 4", Toast.LENGTH_LONG).show();
						DiagnousESC diagonous = new DiagnousESC();
						diagonous.execute(0);
						break;
					case 4:
						//Toast.makeText(getApplicationContext(), "Onitemclick 5", Toast.LENGTH_LONG).show();
						Intent feed = new Intent(context,Act_EscPaperFeedActivity.class);
						context.startActivity(feed);
						break;
					default:
						break;
					}
					
				}
			
		    }); 
		explvEsc = (ExpandableListView)findViewById(R.id.explist_lv);
		explvEsc.setOnGroupExpandListener(new OnGroupExpandListener() {
			int previousGroup = -1;
			@Override
			public void onGroupExpand(int groupPosition) {
				if(groupPosition != previousGroup)
					explvEsc.collapseGroup(previousGroup);
				previousGroup = groupPosition;
			}
		});
		
		Button btnInfo = (Button)findViewById(R.id.infobut);
		btnInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgInformationBox();
			}
		});

		explvEsc.setDividerHeight(2);
		explvEsc.setGroupIndicator(null);
		explvEsc.setClickable(true);
		setGroupData();
		setChildGroupData();
		Act_EscAdapter mNewAdapter = new Act_EscAdapter(groupItem, childItem);
		mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
		explvEsc.setAdapter(mNewAdapter);
		explvEsc.setOnChildClickListener(this);	 
	}
	
	//Add data for ExpandableListView
	public void setGroupData() {
		groupItem.add(" [1]  Font Settings[Data Print]");
		groupItem.add(" [2]  Barcode Data Print");
		groupItem.add(" [3]  Image Print");
		}

	ArrayList<String> groupItem = new ArrayList<String>();
	ArrayList<Object> childItem = new ArrayList<Object>();

	public void setChildGroupData() {
		/** Add Data For [1] Font Settings */
		ArrayList<String> child = new ArrayList<String>();
		child.add("[i]Text Print");
		child.add("[ii]Set Font Properties");
		child.add("[iii]Set Font Styles");
		childItem.add(child);
		/** Add Data For [2] Brocade data print */
		child = new ArrayList<String>();
		child.add("[i]Barcode Data Print");
		child.add("[ii]QR Code");
		childItem.add(child);
		/** Add Data For [3] Bmp */
		child = new ArrayList<String>();
		child.add("[i]BMP Print");
		child.add("[ii]Grey Scale Image");
		childItem.add(child);
		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
		return true;
	}

	public void dlgInformationBox() { //TODO
		Dialog alert = new Dialog(context);
		alert.getWindow();
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// custom layout for information display
		alert.setContentView(R.layout.informationbox);
		TextView site_tv = (TextView) alert.findViewById(R.id.site_tv);
		String str_links = "<a href='http://www.evolute-sys.com'>www.evolute-sys.com</a>";
		site_tv.setLinksClickable(true);
		site_tv.setMovementMethod(LinkMovementMethod.getInstance());
		site_tv.setText(Html.fromHtml(str_links));
		alert.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dlgExit();
		}
		return super.onKeyDown(keyCode, event);
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
	/* This performs Progress dialog box to show the progress of operation */
	protected void dlgShowCustom(Context context1, String Message) {
		dlgCustomdialog = new Dialog(context1);
		dlgCustomdialog.setTitle("Pride Demo");
		dlgCustomdialog.setCancelable(false);
		dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustomdialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
				escHandler.obtainMessage(1, "Reset Successfull").sendToTarget();
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
	
	//Exit confirmation dialog box
	public void dlgExit() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set title
		alertDialogBuilder.setTitle("Pride Demo Application");
		//alertDialogBuilder.setIcon(R.drawable.icon);
		alertDialogBuilder.setMessage("Are you sure you want to exit Pride Demo application");
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				try {
                    //	BluetoothComm.mosOut = null;
                    //	BluetoothComm.misIn = null;
				} catch(NullPointerException e) { }
                //System.gc();
                //Act_EscListActivity.this.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
