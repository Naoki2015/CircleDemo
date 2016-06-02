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
public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

    private int DEFAULT_COLOR_ID = R.color.color_8290AF;
    private int textColorId = DEFAULT_COLOR_ID;

    public SpannableClickable() {

    }

    public SpannableClickable(int textColorId){
        this.textColorId = textColorId;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        int colorValue = MyApplication.getContext().getResources().getColor(
                textColorId);
        ds.setColor(colorValue);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
