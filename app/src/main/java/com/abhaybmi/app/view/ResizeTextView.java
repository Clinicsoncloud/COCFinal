package com.abhaybmi.app.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class ResizeTextView extends TextView {
	/**
	 * @param context
	 */
	public ResizeTextView(Context context)
	{
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ResizeTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		resize();
	}

	/**
	 */
	private void resize()
	{
		final float MIN_TEXT_SIZE = 10f;

		int viewHeight = this.getHeight();
		int viewWidth = this.getWidth();

		float textSize = getTextSize();

		Paint paint = new Paint();
		paint.setTextSize(textSize);

		FontMetrics fm = paint.getFontMetrics();
		float textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

		float textWidth = paint.measureText(this.getText().toString());

		while (viewHeight < textHeight | viewWidth < textWidth)
		{
			if (MIN_TEXT_SIZE >= textSize)
			{
				textSize = MIN_TEXT_SIZE;
				break;
			}

			textSize--;

			paint.setTextSize(textSize);

			fm = paint.getFontMetrics();
			textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

			textWidth = paint.measureText(this.getText().toString());
		}

		setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	}
}
