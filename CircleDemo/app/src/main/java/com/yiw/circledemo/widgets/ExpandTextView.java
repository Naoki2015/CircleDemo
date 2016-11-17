package com.yiw.circledemo.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiw.circledemo.R;
import com.yiw.circledemo.spannable.CircleMovementMethod;

/**
 * Created by yiwei on 16/7/10.
 */
public class ExpandTextView extends LinearLayout {
    public static final int DEFAULT_MAX_LINES = 3;
    private TextView contentText;
    private TextView textPlus;

    private int showLines;

    private ExpandStatusListener expandStatusListener;
    private boolean isExpand;

    public ExpandTextView(Context context) {
        super(context);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_magic_text, this);
        contentText = (TextView) findViewById(R.id.contentText);
        if(showLines > 0){
            contentText.setMaxLines(showLines);
        }

        textPlus = (TextView) findViewById(R.id.textPlus);
        textPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textStr = textPlus.getText().toString().trim();
                if("全文".equals(textStr)){
                    contentText.setMaxLines(Integer.MAX_VALUE);
                    textPlus.setText("收起");
                    setExpand(true);
                }else{
                    contentText.setMaxLines(showLines);
                    textPlus.setText("全文");
                    setExpand(false);
                }
                //通知外部状态已变更
                if(expandStatusListener != null){
                    expandStatusListener.statusChange(isExpand());
                }
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandTextView, 0, 0);
        try {
            showLines = typedArray.getInt(R.styleable.ExpandTextView_showLines, DEFAULT_MAX_LINES);
        }finally {
            typedArray.recycle();
        }
    }

    public void setText(final CharSequence content){
        contentText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                // 避免重复监听
                contentText.getViewTreeObserver().removeOnPreDrawListener(this);

                int linCount = contentText.getLineCount();
                if(linCount > showLines){

                    if(isExpand){
                        contentText.setMaxLines(Integer.MAX_VALUE);
                        textPlus.setText("收起");
                    }else{
                        contentText.setMaxLines(showLines);
                        textPlus.setText("全文");
                    }
                    textPlus.setVisibility(View.VISIBLE);
                }else{
                    textPlus.setVisibility(View.GONE);
                }

                //Log.d("onPreDraw", "onPreDraw...");
                //Log.d("onPreDraw", linCount + "");
                return true;
            }


        });
        contentText.setText(content);
        contentText.setMovementMethod(new CircleMovementMethod(getResources().getColor(R.color.name_selector_color)));
    }

    public void setExpand(boolean isExpand){
        this.isExpand = isExpand;
    }

    public boolean isExpand(){
        return this.isExpand;
    }

    public void setExpandStatusListener(ExpandStatusListener listener){
        this.expandStatusListener = listener;
    }

    public static interface ExpandStatusListener{

        void statusChange(boolean isExpand);
    }

}
