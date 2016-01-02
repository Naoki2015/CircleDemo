package com.yiw.circledemo.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.yiw.circledemo.MyApplication;
import com.yiw.circledemo.R;

/**
 * @author yiw
 * @Description:
 * @date 16/1/2 16:32
 */
public class NameClickable extends ClickableSpan implements View.OnClickListener {
    private final View.OnClickListener mListener;

    public NameClickable(View.OnClickListener l) {
        mListener = l;
    }

    @Override
    public void onClick(View widget) {
        mListener.onClick(widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        int colorValue = MyApplication.getContext().getResources().getColor(
                R.color.color_8290AF);
        ds.setColor(colorValue);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
