package com.abhaybmi.app.printer.esys.pridedemoapp;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.lang.Object;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.abhaybmi.app.R;
import com.evolute.textimage.TextGenerator;
import com.evolute.textimage.TextGenerator.ImageWidth;
import com.evolute.textimage.TextGenerator.Justify;
import com.prowesspride.api.HexString;
import com.prowesspride.api.Printer_ESC;
import com.prowesspride.api.Printer_GEN;

public class Act_UnicodePrinting {

	Context contextGlb;
	Act_GeneralPrinterActivity genprnact;
	private Boolean bGeneral = false;
	private Boolean bEsc = false;
	Printer_ESC ptrEsc;
	Printer_GEN ptrGen;
	private String TAG = "UnicodePrinting";
	int iRetVal;
	public Dialog dlgCustomdialog;
	public ProgressBar pbProgress;
	private LinearLayout llprog;
	private Button btnOk;

	Act_UnicodePrinting(Context context, Printer_ESC ptrEsc) {
		Log.e(TAG, "consturctor ptresc");
		contextGlb = context;
		bEsc = true;
		this.ptrEsc = ptrEsc;
	}

	Act_UnicodePrinting(Context context, Printer_GEN ptrGen) {
		Log.e(TAG, "consturctor ptrgen");
		contextGlb = context;
		bGeneral = true;
		this.ptrGen = ptrGen;
	}

	private static int selecPostion;
	LinearLayout linrlayout;
	LinearLayout linrtxtvw;
	EditText cust_edt;
	public Boolean bConfirm = false;
	int iVal = 0;
	Button btn_cust, btn_Confirm;
	TextView tv_selectLang;
	String str;
	public static Justify justfyPostion;
	int counter = 0;
	public static Display display;
	Boolean beditprint = false;
	private final static int MESSAGE_BOX = 1;
	public static final int DEVICE_NOTCONNECTED = -100;

	public void unicode() {
		// TODO Auto-generated method stub
		final Dialog dlgUnicodeText = new Dialog(contextGlb);
		dlgUnicodeText.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgUnicodeText.setContentView(R.layout.act_unicode);
		TextView tvTitel = (TextView) dlgUnicodeText.findViewById(R.id.tv_unicodecode);
		btn_cust = (Button) dlgUnicodeText.findViewById(R.id.custom_btn);
		cust_edt = (EditText) dlgUnicodeText.findViewById(R.id.custom_edt);
		cust_edt.setTextColor(Color.parseColor("#FFFFFF"));
		linrlayout = (LinearLayout) dlgUnicodeText.findViewById(R.id.linearLayout123);
		linrtxtvw = (LinearLayout) dlgUnicodeText.findViewById(R.id.linearLayout456);
		tvTitel.setWidth(500);
		tv_selectLang = (TextView) dlgUnicodeText.findViewById(R.id.tv_selectLang);
		Spinner sp_SelectLanguage = (Spinner) dlgUnicodeText.findViewById(R.id.sp_SelectLanguage);
		List<String> lang = new ArrayList<String>();
		
		lang.add(" Hindi ");
		lang.add(" Kannada ");
		lang.add(" Telugu ");
		lang.add(" Tamil ");
		lang.add(" Edit ");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(contextGlb,
				android.R.layout.simple_spinner_item, lang);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_SelectLanguage.setAdapter(dataAdapter);
		sp_SelectLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub

				/*if (position == 0) {
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("");

				}*/ if (position == 0) {
					Log.e(TAG, "hindiii");
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("एवोलुते सिस्टम प्राइवेट लिमिटेड");
					Log.e(TAG, "<<<unicode1>>>>" + str);
					str = "एवोलुते सिस्टम प्राइवेट लिमिटेड " + "\n" + "\n" + "\n" + "\n" + "\n"
							+ "\n" + "\n" + "\n";
					Log.e(TAG, "<<<unicode2>>>>" + str);
					beditprint = false;

				} else if (position == 1) {
					Log.e(TAG, "kanada>>>>>>");
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("ಎವೊಲ್ಯುತ್ ಸಿಸ್ಟಮ್ಸ್ ಪ್ರೈವೇಟ್ ಲಿಮಿಟೆಡ್ ");
					Log.e(TAG, "<<<unicode1>>>>" + str);
					str = "ಎವೊಲುತೆ ಸಿಸ್ಟಮ್ ಪ್ರೈವೇಟ್ ಲಿಮಿಟೆಡ್ " + "\n" + "\n" + "\n" + "\n" + "\n"
							+ "\n" + "\n" + "\n";
					Log.e(TAG, "<<<unicode2>>>>" + str);
					beditprint = false;
				} else if (position == 2) {
					Log.e(TAG, "telugu");
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("ఎవోలుటే సిస్టం ప్రైవేటు లిమిటెడ్");
					str = "ఎవోలుటే సిస్టం ప్రైవేటు లిమిటెడ్" + "\n" + "\n" + "\n" + "\n" + "\n"
							+ "\n" + "\n" + "\n";
					beditprint = false;

				} else if (position == 3) {
					linrlayout.setVisibility(View.INVISIBLE);
					linrtxtvw.setVisibility(View.VISIBLE);
					tv_selectLang.setText("எவோளுடே சிஸ்டம்ஸ் பவத் ல்த்து");
					str = "எவோளுடே சிஸ்டம்ஸ் பவத் ல்த்து" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n";
					beditprint = false;

				} else if (position == 4) {
					str="";
					tv_selectLang.setText("");
					linrlayout.setVisibility(View.VISIBLE);
					linrtxtvw.setVisibility(View.INVISIBLE);
					Log.e("custom cassee", "spinner 4");
					beditprint = true;
					custom();
				}
			}

			private void custom() {
				// TODO Auto-generated method stub
				final Dialog dlgUnicodeText = new Dialog(contextGlb);
				dlgUnicodeText.requestWindowFeature(Window.FEATURE_NO_TITLE);
				Log.e(TAG, "in try");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		Spinner sp_SelectPosition = (Spinner) dlgUnicodeText.findViewById(R.id.sp_SelectPosition);
		List<String> list = new ArrayList<String>();
		list.add(" Left");
		list.add(" Center");
		list.add(" Right");
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(contextGlb,
				android.R.layout.simple_spinner_item, list);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_SelectPosition.setAdapter(dataAdapter1);
		sp_SelectPosition.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				if (position == 0) {
					justfyPostion = Justify.ALIGN_LEFT;
				} else if (position == 1) {
					justfyPostion = Justify.ALIGN_CENTER;
				} else if (position == 2) {
					justfyPostion = Justify.ALIGN_RIGHT;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		Log.e(TAG, "in try>>>> 0");
		cust_edt.getText().toString();
		Log.e(TAG, "in try>>>> 1");
		tv_selectLang.setText(cust_edt.getText().toString());
		Log.e(TAG, "in try>>> 2");
		btn_Confirm = (Button) dlgUnicodeText.findViewById(R.id.custom_btn);
		Log.e(TAG, "in try>>> 3");
		btn_cust.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(TAG, "in try>>> 4");
				bConfirm = true;
				if (cust_edt.length() == 0 || cust_edt.equals("") || cust_edt == null) {
					// EditText is empty
					cust_edt.setError("Input Text is required");
				}
				else{
					cust_edt.setError(null);
				}

				linrtxtvw.setVisibility(View.VISIBLE);
				tv_selectLang.setText(cust_edt.getText().toString());
			}
		});

		Button btn_unicode = (Button) dlgUnicodeText.findViewById(R.id.btn_unicodeprnt);

		btn_unicode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//dlgShowCustom(context, "Please Wait....");
				dlgUnicodeText.dismiss();
				String ss = tv_selectLang.getText().toString();
				String sedit = cust_edt.getText().toString();
				if (ss.length() == 0 && sedit.length() == 0) {
					Toast.makeText(contextGlb, "No Input's selected", Toast.LENGTH_LONG).show();
					dlgUnicodeText.show();
				} else if (!bConfirm && beditprint) {
					Toast.makeText(contextGlb, "Confirm Data once & print", Toast.LENGTH_LONG)
							.show();
					dlgUnicodeText.show();
				} else if (sedit.length() == 0 && ss.length() == 0) {
					Log.e(TAG, "counter");
					Toast.makeText(contextGlb, "Confirm Data once & print", Toast.LENGTH_LONG)
							.show();
					if (counter > 0) {
						Log.e(TAG, "counter");
						UnicodeASync uniasynctask = new UnicodeASync();
						uniasynctask.execute(0);
					}
					dlgUnicodeText.show();
				} else {
					Log.e(TAG, "else");
					UnicodeASync uniasynctask = new UnicodeASync();
					uniasynctask.execute(0);
				}
				// counter++;
			}
		});
		dlgUnicodeText.show();
	}
	
	class UnicodeASync extends AsyncTask<Integer, Integer, Integer> {
		// displays the progress dialog until background task is completed
		@Override
		protected void onPreExecute() {
			// progressDialog(context, "Please Wait...");
			dlgShowCustom(contextGlb, "Please wait..");
			super.onPreExecute();
		}

		// Task of EnterTextAsyc performing in the background
		@Override
		protected Integer doInBackground(Integer... params) {

			try {
				String newString = cust_edt.getText().toString();

				if (newString.length() == 0) {
					Log.e(TAG, "inside if>>>>>");
					// newString=(String) tv_selectLang.getText();
					newString = tv_selectLang.getText().toString();
					Log.e(TAG, "value in newstring................" + newString);
				} else {
					Log.e(TAG, "else block" + newString);
				}

				String sdcardBmpPath = Environment.getExternalStorageDirectory().getAbsolutePath()
						+ "/myImage.bmp";

				Bitmap bmpImg1 = TextGenerator.bmpDrawText(ImageWidth.Inch_2, newString, 30,
						justfyPostion);
				Log.e(TAG, "Str value <<<unicode 3>>>>" + str);
				Log.e(TAG, "Str value <<<unicode 3>>>>" + justfyPostion);

				Bitmap bmpfinal = TextGenerator.bmpConvertTo_24Bit(bmpImg1);

				byte[] bBmpFileData = TextGenerator.bGetBmpFileData(bmpfinal);
				ByteArrayInputStream bis = new ByteArrayInputStream(bBmpFileData);

				if (bEsc) {
					Log.e(TAG, "in if block using esc protocol");
					iVal = ptrEsc.iBmpPrint(bis);

				} else {
					Log.e("", "in else block using general protocol");
					iVal = ptrGen.iBmpPrint(bis);
				}
				Log.e(TAG, "xx value>>>>>>>>>" + iVal);
				Log.d("bmp", "Result : " + iVal);

			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;

				return iRetVal;
			}
			return iRetVal;
		}

		// This displays the status messages of EnterTextAsyc in the dialog box
		@Override
		protected void onPostExecute(Integer result) {
			cust_edt.setText("");
			bConfirm = false;
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			Log.e(TAG, "+++++TIMER STOPED+++++++");
			if (iVal == DEVICE_NOTCONNECTED) {
				ptrHandler.obtainMessage(1, "Device not connected").sendToTarget();
			} else if (iVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1, "Printer platen is open").sendToTarget();
			} else if (iVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1, "Printer paper is out").sendToTarget();
			} else if (iVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1, "Printer improper voltage").sendToTarget();
			} else if (iVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printer failed").sendToTarget();
			} else if (iVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1, "Printer param error").sendToTarget();
			} else if (iVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1, "No response from device").sendToTarget();
			} else if (iVal == Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1, "Library is in demo version").sendToTarget();
			} else if (iVal == Printer_ESC.SUCCESS) {
				ptrHandler.obtainMessage(1, "Print Success").sendToTarget();
			} else if (iVal == Printer_ESC.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1, "Printer platen is open").sendToTarget();
			} else if (iVal == Printer_ESC.PAPER_OUT) {
				ptrHandler.obtainMessage(1, "Printer paper is out").sendToTarget();
			} else if (iVal == Printer_ESC.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1, "Printer improper voltage").sendToTarget();
			} else if (iVal == Printer_ESC.FAILURE) {
				ptrHandler.obtainMessage(1, "Printer failed").sendToTarget();
			} else if (iVal == Printer_ESC.PARAM_ERROR) {
				ptrHandler.obtainMessage(1, "Printer param error").sendToTarget();
			} else if (iVal == Printer_ESC.NO_RESPONSE) {
				ptrHandler.obtainMessage(1, "No response from device").sendToTarget();
			} else if (iVal == Printer_ESC.DEMO_VERSION) {
				ptrHandler.obtainMessage(1, "Library is in demo version").sendToTarget();
			} else if (iVal == Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1, "Library is in demo version").sendToTarget();
			} else if (iVal == Printer_ESC.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1, "Connected  device is not license authenticated.")
						.sendToTarget();
				super.onPostExecute(result);
			}
		}
	}
		public void dlgShow(String str) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextGlb);
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
		
		/* Handler to display UI response messages */
		Handler ptrHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 1:
					try {
						TextView tvMessage = (TextView) dlgCustomdialog
								.findViewById(R.id.tvMessage);
						tvMessage.setText("" + msg.obj);
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				case 2:
					String str1 = (String) msg.obj;
					dlgShow(str1);
					break;
				case 3:
					Toast.makeText(contextGlb, (String) msg.obj, Toast.LENGTH_LONG)
							.show();
					break;
				default:
					break;
				}
			};
		};
		protected void dlgShowCustom(Context con, String Message) {
			dlgCustomdialog = new Dialog(con);
			dlgCustomdialog.setTitle("Pride Demo");
			dlgCustomdialog.setCancelable(false);
			dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dlgCustomdialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			dlgCustomdialog.setContentView(R.layout.progressdialog);
			TextView title_tv = (TextView) dlgCustomdialog.findViewById(R.id.tvTitle);
			title_tv.setWidth(500);
			TextView message_tv = (TextView) dlgCustomdialog.findViewById(R.id.tvMessage);
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
	}
