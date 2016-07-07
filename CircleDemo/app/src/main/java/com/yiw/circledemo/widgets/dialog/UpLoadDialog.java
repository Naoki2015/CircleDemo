package com.yiw.circledemo.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.yiw.circledemo.R;

public class UpLoadDialog extends Dialog {

	private TextView dialogLoadingText;
	private CircularProgressBar progressBar;

	public UpLoadDialog(Context context) {
		super(context, R.style.progress_style);
		setCancelable(true);
		setContentView(R.layout.layout_upload);
		dialogLoadingText = (TextView) findViewById(R.id.dialog_loading_text);
		progressBar = (CircularProgressBar) findViewById(R.id.video_progress);
	}

	public UpLoadDialog(Context context, int resid) {
		this(context);
		dialogLoadingText.setText(resid);
	}

	public UpLoadDialog(Context context, String message) {
		this(context);
		if (!TextUtils.isEmpty(message)) {
			dialogLoadingText.setText(message);
		}
	}

	public final void setMessage(String message) {
		if (!TextUtils.isEmpty(message)) {
			dialogLoadingText.setText(message);
		}
	}

    public void setPercentsProgress(int percentsProgress){
        progressBar.setProgress(percentsProgress);
    }

	public final void dismiss() {
		super.dismiss();
	}

	public final void show() {
		super.show();
	}

	public void canceable(boolean value) {
		setCancelable(value);
	}

}
