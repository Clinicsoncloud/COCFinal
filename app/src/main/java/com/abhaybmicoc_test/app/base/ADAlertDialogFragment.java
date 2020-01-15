package com.abhaybmicoc_test.app.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ADAlertDialogFragment extends DialogFragment {
	// Title
	private String title;
	// Message
	private String message;
	// Positive Button Title
	private String positive;
	// Negative Button Title
	private String negative;
	
	/**
	 * コンストラクタ
	 * @param title Title
	 * @param message Message
	 * @param positive Positive Button Title
	 * @param negative Negative Button Title
	 */
	public ADAlertDialogFragment(String title,String message, String positive , String negative) {
		this.title = title;
		this.message = message;
		this.positive = positive;
		this.negative = negative;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    if(title != null)builder.setTitle(title) ;
	    if(message != null)builder.setMessage(message);
	    if(positive != null)builder.setPositiveButton(positive, onPositiveListener);
	    if(negative != null)builder.setNegativeButton(negative, onNegativeListener);
	    return builder.create();
	}
	
	/**
	 * Positive Button Click Listener
	 */
	private DialogInterface.OnClickListener onPositiveListener;
	public void setOnPositiveListener(DialogInterface.OnClickListener listener) {
		onPositiveListener = listener ;
	}
	
	/**
	 * Negative Button Click Listener
	 */
	private DialogInterface.OnClickListener onNegativeListener;
}
