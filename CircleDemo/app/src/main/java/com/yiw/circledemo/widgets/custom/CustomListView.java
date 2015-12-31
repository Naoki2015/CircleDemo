package com.yiw.circledemo.widgets.custom;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class CustomListView extends RelativeLayout {
	private String TAG = CustomListView.class.getSimpleName();
	private CustomAdapter myCustomAdapter;
//	private static boolean addChildType;
	private int dividerHeight = 0;

	private int dividerWidth = 0;

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onLayout(boolean arg0, int argLeft, int argTop, int argRight, int argBottom) {
		Log.i(this.TAG, "L:" + argLeft + " T:" + argTop + " R:" + argRight + " B:" + argBottom);
		int count = getChildCount();
		int row = 0;
		int lengthX = 0;
		int lengthY = 0;
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();

			if (lengthX == 0)
				lengthX += width;
			else {
				lengthX += width + getDividerWidth();
			}

			if ((i == 0) && (lengthX <= argRight)) {
				lengthY += height;
			}

			if (lengthX > argRight) {
				lengthX = width;
				lengthY += getDividerHeight() + height;
				row++;
				child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
			} else {
				child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
			}
		}
		ViewGroup.LayoutParams lp = getLayoutParams();
		lp.height = lengthY;
		setLayoutParams(lp);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		int height = View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(0, 0);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	final int getDividerHeight() {
		return this.dividerHeight;
	}

	public void setDividerHeight(int dividerHeight) {
		this.dividerHeight = dividerHeight;
	}

	final int getDividerWidth() {
		return this.dividerWidth;
	}

	public void setDividerWidth(int dividerWidth) {
		this.dividerWidth = dividerWidth;
	}

	public void setAdapter(CustomAdapter adapter) {
		this.myCustomAdapter = adapter;
		adapter.notifyCustomListView(this);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.myCustomAdapter.setOnItemClickListener(listener);
	}

	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		this.myCustomAdapter.setOnItemLongClickListener(listener);
	}

}
