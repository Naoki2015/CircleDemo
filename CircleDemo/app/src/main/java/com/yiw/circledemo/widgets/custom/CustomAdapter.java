package com.yiw.circledemo.widgets.custom;

import android.view.View;
import android.view.ViewGroup;

public class CustomAdapter {
	private String TAG = CustomAdapter.class.getSimpleName();
	private View myView;
	private ViewGroup myViewGroup;
	private CustomListView myCustomListView;
	private OnItemClickListener listener;
	private OnItemLongClickListener longListener;

	public int getCount() {
		return 0;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0L;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	private final void getAllViewAddSexangle() {
		this.myCustomListView.removeAllViews();
		for (int i = 0; i < getCount(); i++) {
			View viewItem = getView(i, this.myView, this.myViewGroup);
			this.myCustomListView.addView(viewItem, i);
		}
	}

	public void notifyDataSetChanged() {
		notifyCustomListView(this.myCustomListView);
	}

	public void notifyCustomListView(CustomListView formateList) {
		this.myCustomListView = formateList;
		this.myCustomListView.removeAllViews();
		getAllViewAddSexangle();
		setOnItemClickListener(this.listener);
		setOnItemLongClickListener(this.longListener);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
		/*for (int i = 0; i < this.myCustomListView.getChildCount(); i++) {
			int parame = i;
			View view = this.myCustomListView.getChildAt(i);
			view.setOnClickListener(new View.OnClickListener(parame, listener) {
				public void onClick(View v) {
					Log.i(CustomAdapter.this.TAG, "当前Item的值 : " + this.val$parame);
					this.val$listener.onItemClick(null, v, this.val$parame, CustomAdapter.this.getCount());
				}
			});
		}*/
	}

	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		this.longListener = listener;
		/*for (int i = 0; i < this.myCustomListView.getChildCount(); i++) {
			int parame = i;
			View view = this.myCustomListView.getChildAt(i);
			view.setOnLongClickListener(new View.OnLongClickListener(listener, parame) {
				public boolean onLongClick(View v) {
					this.val$listener.onItemLongClick(null, v, this.val$parame, CustomAdapter.this.getCount());
					return true;
				}
			});
		}*/
	}
}
