package com.abhaybmi.app.printer.esys.pridedemoapp;

import java.io.ByteArrayInputStream;


import com.abhaybmi.app.R;
import com.evolute.qrimage.QRCodeGenerator;
import com.evolute.textimage.TextGenerator;
import com.evolute.textimage.TextGenerator.ImageWidth;
import com.evolute.textimage.TextGenerator.Justify;
import com.prowesspride.api.HexString;
import com.prowesspride.api.Printer_ESC;
import com.prowesspride.api.Printer_GEN;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Act_QRCode {
	Context contextGlb;
	private Boolean bGeneral = false;
	private Boolean bEsc = false;
	Printer_ESC ptrEsc;
	Printer_GEN ptrGen;
	private String TAG = "QRCode";
	int iRetVal;
	
	EditText edit_qrData;
	Button btn_qrPrn;
	public Dialog dlgCustomDialog;
	
	public ProgressBar pbProgress;
	private LinearLayout llprog;
	private Button btnOk;

	Act_QRCode(Context context, Printer_ESC ptrEsc) {
		Log.e(TAG, "consturctor ptresc");
		contextGlb = context;
		bEsc = true;
		this.ptrEsc = ptrEsc;
	}

	Act_QRCode(Context context, Printer_GEN ptrGen) {
		Log.e(TAG, "consturctor ptrgen");
		contextGlb = context;
		bGeneral = true;
		this.ptrGen = ptrGen;
	}

	private static int selectPostion;
	LinearLayout linrlayout;
	LinearLayout linrtxtvw;
	EditText cust_edt;
	public Boolean bConfirm = false;
	int iVal = 0;
	Button btn_cust, btnconfirm;
	TextView tv_selectLang;
	String str;
	public static Justify justfyPostion;
	int counter = 0;
	Boolean bEditPrint = false;
	private final static int MESSAGE_BOX = 1;
	public static final int DEVICE_NOTCONNECTED = -100;
	int xx1;

	public void QrCode() {
		// TODO Auto-generated method stub
		
		Log.e(TAG, "QrCode");
		final Dialog dlgQRText = new Dialog(contextGlb);
		dlgQRText.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgQRText.setContentView(R.layout.act_qrcode);
		
		edit_qrData = (EditText) dlgQRText.findViewById(R.id.edit_qrcode);
		edit_qrData.setTextColor(Color.parseColor("#FFFFFF"));
		btn_qrPrn = (Button) dlgQRText.findViewById(R.id.btn_qrcodePrn);
		
		btn_qrPrn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgQRText.dismiss();
				str = edit_qrData.getText().toString();
				if(str.length()==0){
					Toast.makeText(contextGlb, "Please Enter Data", Toast.LENGTH_LONG).show();
					dlgQRText.show();
				}
				else if(str.length()>0){
					Qrcode qrcode = new Qrcode();
					qrcode.execute(0);
				}
							
			}
		});
		dlgQRText.show();
	}
	class Qrcode extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			// progressDialog(context, "Please Wait...");
			dlgShowCustom(contextGlb, "Please wait..");
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			Log.e(TAG, "in method...>>");
			Bitmap bmpDrawQRCode = null;
			try {
				bmpDrawQRCode = QRCodeGenerator.bmpDrawQRCode(ImageWidth.Inch_2, str);
				Log.e(TAG, "before api");
				byte[] bBmpFileData = TextGenerator.bGetBmpFileData(bmpDrawQRCode);
				Log.d(TAG, "byte data...." + HexString.bufferToHex(bBmpFileData));// bBmpFileData);
				ByteArrayInputStream bis = new ByteArrayInputStream(bBmpFileData);
				Log.e(TAG, " >>> 1" );
				if(bGeneral){
					Log.e(TAG, " >>> 2" );
				iRetVal = ptrGen.iBmpPrint(bis);
				Log.e(TAG, " Gen result" + iRetVal);
				}else {
					iRetVal = ptrEsc.iBmpPrint(bis);
					Log.e(TAG, " Esc Result "+iRetVal);
				}
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (Exception e) {
				Log.e(TAG, "Exception Device not ");
				iRetVal = DEVICE_NOTCONNECTED;
				e.printStackTrace();
			}
			return iRetVal;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// tv_selectLang.setText("");
			edit_qrData.setText("");
			bConfirm = false;
			Log.e(TAG, "+++++TIMER STOPED+++++++");
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			}else if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrHandler.obtainMessage(1, "Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1, "Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1, "Parameter error").sendToTarget();
			} else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1, "No response from Legend device").sendToTarget();
			} else if (iRetVal == Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1, "Library in demo version").sendToTarget();
			} else if (iRetVal == Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1, "Connected  device is not authenticated.").sendToTarget();
			} else if (iRetVal == Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1, "Library not activated").sendToTarget();
			} else if (iRetVal == Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1, "Not Supported").sendToTarget();
			} else {
				ptrHandler.obtainMessage(1, "Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
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
					TextView tvMessage = (TextView) dlgCustomDialog
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
		dlgCustomDialog = new Dialog(con);
		dlgCustomDialog.setTitle("Pride Demo ");
		dlgCustomDialog.setCancelable(false);
		dlgCustomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustomDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dlgCustomDialog.setContentView(R.layout.progressdialog);
		TextView title_tv = (TextView) dlgCustomDialog.findViewById(R.id.tvTitle);
		title_tv.setWidth(500);
		TextView message_tv = (TextView) dlgCustomDialog.findViewById(R.id.tvMessage);
		message_tv.setText(Message);
		llprog = (LinearLayout) dlgCustomDialog.findViewById(R.id.llProg);
		pbProgress = (ProgressBar) dlgCustomDialog.findViewById(R.id.pbDialog);
		btnOk = (Button) dlgCustomDialog.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgCustomDialog.dismiss();
			}
		});
		dlgCustomDialog.show();
	}
	}
	




