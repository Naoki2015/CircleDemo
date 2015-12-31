package com.yiw.circledemo.widgets;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class AppNoScrollerListView extends ListView {

	private static final int INVALID_POINTER = -1;
	private int mActivePointerId;
	private float mInitialMotionY;
	private int mTouchSlop;
	private boolean mIsBeingVerticalScrooled;

	private boolean mIsBeingHorizonalScrooled;
	private float mInitialMotionX;
	private int vertical = 0;
	private int horizonal = 1;
	private int whichVerticalScrollFirst = -1;
	private boolean hasDispatched = false;

	public AppNoScrollerListView(Context context) {
		this(context, null);

	}

	public AppNoScrollerListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AppNoScrollerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isEnabled()) {
			return false;
		}
		final int action = MotionEventCompat.getActionMasked(ev);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			hasDispatched = false;
			mIsBeingVerticalScrooled = false;
			mIsBeingHorizonalScrooled = false;
			whichVerticalScrollFirst = -1;
			final float initialMotionX = getMotionEventX(ev, mActivePointerId);
			final float initialMotionY = getMotionEventY(ev, mActivePointerId);
			if (initialMotionY == -1) {
				return false;
			}
			mInitialMotionX = initialMotionX;
			mInitialMotionY = initialMotionY;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mActivePointerId == INVALID_POINTER) {
				return false;
			}

			final float x = getMotionEventX(ev, mActivePointerId);
			final float xDiff = x - mInitialMotionX;
			if (Math.abs(xDiff) > mTouchSlop && !mIsBeingHorizonalScrooled && whichVerticalScrollFirst == -1) {
				whichVerticalScrollFirst = horizonal;
				mIsBeingHorizonalScrooled = true;
			}

			final float y = getMotionEventY(ev, mActivePointerId);
			if (y == -1) {
				return false;
			}
			final float yDiff = y - mInitialMotionY;
			if (yDiff > mTouchSlop && !mIsBeingVerticalScrooled && whichVerticalScrollFirst == -1) {
				whichVerticalScrollFirst = vertical;
				mIsBeingVerticalScrooled = true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			whichVerticalScrollFirst = -1;
			mIsBeingHorizonalScrooled = false;
			mIsBeingVerticalScrooled = false;
			break;
		}

		if (action == MotionEvent.ACTION_MOVE && !mIsBeingVerticalScrooled && mIsBeingHorizonalScrooled && !hasDispatched
				&& null != onHorizonalTouchEventListener) {
			onHorizonalTouchEventListener.onHorizonalTouchEvent();
			hasDispatched = true;
		}
		return super.onTouchEvent(ev);
	}

	private float getMotionEventX(MotionEvent ev, int activePointerId) {
		final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
		if (index < 0) {
			return -1;
		}
		return MotionEventCompat.getX(ev, index);
	}

	private float getMotionEventY(MotionEvent ev, int activePointerId) {
		final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
		if (index < 0) {
			return -1;
		}
		return MotionEventCompat.getY(ev, index);
	}

	public OnHorizonalTouchEventListener onHorizonalTouchEventListener;

	public void setOnHorizonalTouchEventListener(OnHorizonalTouchEventListener onHorizonalTouchEventListener) {
		this.onHorizonalTouchEventListener = onHorizonalTouchEventListener;
	}

	public interface OnHorizonalTouchEventListener {
		public void onHorizonalTouchEvent();
	}
}
