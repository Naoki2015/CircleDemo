package com.yiw.circledemo.spannable;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.yiw.circledemo.MyApplication;
import com.yiw.circledemo.R;

/**
 * @author yiw
 * @Description:
 * @date 16/1/2 16:54
 */
public class CircleMovementMethod extends BaseMovementMethod {
    public final String TAG = CircleMovementMethod.class.getSimpleName();
    private final static int DEFAULT_COLOR_ID = R.color.transparent;
    private final static int DEFAULT_CLICKABLEA_COLOR_ID = R.color.default_clickable_color;
    /**整个textView的背景色*/
    private int textViewBgColor;
    /**点击部分文字时部分文字的背景色*/
    private int clickableSpanBgClor;

    private BackgroundColorSpan mBgSpan;
    private ClickableSpan[] mClickLinks;
    private boolean isPassToTv = true;
    /**
     * true：响应textview的点击事件， false：响应设置的clickableSpan事件
     */
    public boolean isPassToTv() {
        return isPassToTv;
    }
    private void setPassToTv(boolean isPassToTv){
        this.isPassToTv = isPassToTv;
    }

    public CircleMovementMethod(){
        this.textViewBgColor = MyApplication.getContext().getResources().getColor(DEFAULT_COLOR_ID);
        this.clickableSpanBgClor = MyApplication.getContext().getResources().getColor(DEFAULT_CLICKABLEA_COLOR_ID);
    }

    /**
     *
     * @param clickableSpanBgClor  点击选中部分时的背景色
     */
    public CircleMovementMethod(int clickableSpanBgClor){
        this.clickableSpanBgClor = clickableSpanBgClor;
        this.textViewBgColor = MyApplication.getContext().getResources().getColor(DEFAULT_COLOR_ID);
    }

    /**
     *
     * @param clickableSpanBgClor 点击选中部分时的背景色
     * @param textViewBgColor 整个textView点击时的背景色
     */
    public CircleMovementMethod(int clickableSpanBgClor, int textViewBgColor){
        this.textViewBgColor = textViewBgColor;
        this.clickableSpanBgClor = clickableSpanBgClor;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            mClickLinks = buffer.getSpans(off, off, ClickableSpan.class);
            if(mClickLinks.length > 0){
                // 点击的是Span区域，不要把点击事件传递
                setPassToTv(false);
                Selection.setSelection(buffer,
                        buffer.getSpanStart(mClickLinks[0]),
                        buffer.getSpanEnd(mClickLinks[0]));
                //设置点击区域的背景色
                mBgSpan = new BackgroundColorSpan(clickableSpanBgClor);
                buffer.setSpan(mBgSpan,
                        buffer.getSpanStart(mClickLinks[0]),
                        buffer.getSpanEnd(mClickLinks[0]),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                setPassToTv(true);
                // textview选中效果
                widget.setBackgroundColor(textViewBgColor);
            }

        }else if(action == MotionEvent.ACTION_UP){
            if(mClickLinks.length > 0){
                mClickLinks[0].onClick(widget);
                if(mBgSpan != null){//移除点击时设置的背景span
                    buffer.removeSpan(mBgSpan);
                }
            }else{

            }
            Selection.removeSelection(buffer);
            widget.setBackgroundResource(R.color.transparent);
        }else if(action == MotionEvent.ACTION_MOVE){
            //这种情况不用做处理
        }else{
            if(mBgSpan != null){//移除点击时设置的背景span
                buffer.removeSpan(mBgSpan);
            }
            widget.setBackgroundResource(R.color.transparent);
        }
        return Touch.onTouchEvent(widget, buffer, event);
    }
}
