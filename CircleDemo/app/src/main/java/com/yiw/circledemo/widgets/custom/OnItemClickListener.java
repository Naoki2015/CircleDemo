package com.yiw.circledemo.widgets.custom;

import android.view.View;
import android.widget.AdapterView;

public abstract interface OnItemClickListener
{
  public abstract void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
}
