package com.abhaybmicoctest.app.printer.esys.pridedemoapp;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoctest.app.R;
import com.abhaybmicoctest.app.printer.evolute.bluetooth.BluetoothComm;
import com.prowesspride.api.Printer_ESC;

public class Act_EscBMPPrintActivity extends Activity implements OnClickListener {

	private Button btnSdcard, btnPrintBmp, btnOk;;
	private String sPicturePath = null;
	private ImageView imgBmp;
	Context context = this;
	public static int RESULT_LOAD_IMAGE = 10;
	public static int DEVICE_NOTCONNECTED = -100;
	private int iRetVal;
	boolean bpathgrayprint=false;
	Printer_ESC ptrEsc;
	Boolean bBmpprint=false;
	private LinearLayout llProg;
	public static Dialog dlgCustomdialog;
	public static ProgressBar pbProgress;
	private int iWidth;
	Bitmap bmpSelectImage;
	private boolean check = false;
	private static final String TAG = "EscBMPPrintActivity";

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_escbmpprint);

		Intent getintent=getIntent();
		String sCheck=getintent.getStringExtra("bmpprnt");
		Log.e("", "Sccheck..."+sCheck);
		
		if (sCheck.equalsIgnoreCase("BmpPrint")) {
			bBmpprint=true;
		}
		
		Display display = getWindowManager().getDefaultDisplay();
		iWidth = display.getWidth();
		
		llProg = (LinearLayout) findViewById(R.id.llProg);
		btnSdcard = (Button) findViewById(R.id.btnSdcard);
		btnSdcard.setOnClickListener(this);
		imgBmp = (ImageView) findViewById(R.id.imgBmp);
		if (!bBmpprint) {
			imgBmp.setImageResource(R.drawable.sampy1111);
		}
		btnPrintBmp = (Button) findViewById(R.id.btnPrintbmp);
		btnPrintBmp.setOnClickListener(this);
		// TODO Auto-generated method stub
		try {
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSdcard:
			Intent i = new Intent(
					Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
			break;

		case R.id.btnPrintbmp:
			Bitmapprint bmpprint = new Bitmapprint();
			bmpprint.execute(0);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE) {// && resultCode == RESULT_OK &&
												// null != data) {
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				sPicturePath = cursor.getString(columnIndex);
				cursor.close();
				try {
					bmpSelectImage = BitmapFactory.decodeFile(sPicturePath);
					imgBmp.setImageBitmap(bmpSelectImage);
					
				} catch (OutOfMemoryError e) {
					String str = "Image size is Large";
					dlgShow(str);
				}
			} catch (Exception e) {
				bmpEHandler.obtainMessage(3,
						"No Image Selected\nSelecting Default Image")
						.sendToTarget();
				e.printStackTrace();
			}
		}
	}

	public void dlgShowImageLength() { // TODO
		AlertDialog.Builder builder = new AlertDialog.Builder(
				Act_EscBMPPrintActivity.this);
		builder.setTitle("Pride Demo");
		builder.setMessage("Image size too large\nPlease select Proper image");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/* This method shows the ITestPrint AsynTask operation */
	public class Bitmapprint extends AsyncTask<Integer, Integer, Integer> {
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
				if ((sPicturePath == "") || (sPicturePath == null)) {
					//iRetVal=imgBmp.INVISIBLE;
					if (bBmpprint) {
					iRetVal = Act_EscListActivity.ptrEsc.iBmpPrint(context, R.drawable.logo1);
					
				}else{
					iRetVal = Act_EscListActivity.ptrEsc.iGrayscalePrint(context,R.drawable.sampy1111);
					 bpathgrayprint=true;
				}
				
			}else{
					if (bBmpprint) {
						iRetVal = Act_EscListActivity.ptrEsc.iBmpPrint(new File(sPicturePath));
							
					}else{
				iRetVal = Act_EscListActivity.ptrEsc.iGrayscalePrint(new File(sPicturePath));}
				}
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of ITestPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			//imgBmp.setVisibility(View.INVISIBLE);
			
			if (iRetVal == DEVICE_NOTCONNECTED) {
				bmpEHandler.obtainMessage(1, "Device not connected")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				sPicturePath = "";
				bmpEHandler.obtainMessage(1, "Bmp print successfull")
						.sendToTarget();
				if (bpathgrayprint) {
					imgBmp.setImageResource(R.drawable.sampy1111);
					bpathgrayprint=false;
				}else{
					imgBmp.setImageResource(R.drawable.evoluteogo);
				}
				
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				bmpEHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				bmpEHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				bmpEHandler.obtainMessage(1, "Printer at improper voltage")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				bmpEHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				bmpEHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				bmpEHandler.obtainMessage(1, "No response from Pride device")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.DEMO_VERSION) {
				bmpEHandler.obtainMessage(1, "Library in demo version")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				bmpEHandler.obtainMessage(1,
						"Connected  device is not authenticated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.NOT_ACTIVATED) {
				bmpEHandler.obtainMessage(1, "Library not activated")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.BMP_FILE_ERROR) {
				bmpEHandler.obtainMessage(1, "File size is large")
						.sendToTarget();
			} else if (iRetVal == Printer_ESC.INVALID_DEVICE_ID) {
				bmpEHandler.obtainMessage(1, "Invalid Device Id")
						.sendToTarget();
			} else {
				bmpEHandler.obtainMessage(1, "Unknown Response from Device")
						.sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/* Handler to display UI response messages */
	Handler bmpEHandler = new Handler() {
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
				dlgShow(str1);
				break;
			case 3:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}
		};
	};

	/* To show response messages */
	public void dlgShow(String str) {
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
		title_tv.setWidth(iWidth);
		TextView message_tv = (TextView) dlgCustomdialog
				.findViewById(R.id.tvMessage);
		message_tv.setText(Message);
		llProg = (LinearLayout) dlgCustomdialog.findViewById(R.id.llProg);
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
}
