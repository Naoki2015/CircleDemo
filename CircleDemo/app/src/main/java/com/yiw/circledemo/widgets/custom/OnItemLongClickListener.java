package com.yiw.circledemo.widgets.custom;

import android.view.View;
import android.widget.AdapterView;

public abstract interface OnItemLongClickListener {
	public abstract boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
}
