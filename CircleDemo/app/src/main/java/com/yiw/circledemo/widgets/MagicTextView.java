package com.yiw.circledemo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiw.circledemo.R;
import com.yiw.circledemo.spannable.CircleMovementMethod;

/**
 * Created by yiw on 2016/6/3.
 */
public class MagicTextView extends LinearLayout {

    public static final int DEFAULT_MAX_LINES = 3;
    private TextView contentText;
    private TextView textPlus;

    public MagicTextView(Context context) {
        super(context);
        init(context);
    }

    public MagicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MagicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_magic_text, this);
        contentText = (TextView) findViewById(R.id.contentText);

        textPlus = (TextView) findViewById(R.id.textPlus);
        textPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textStr = textPlus.getText().toString().trim();
                if("全文".equals(textStr)){
                    contentText.setMaxLines(Integer.MAX_VALUE);
                    textPlus.setText("收起");
                }else{
                    contentText.setMaxLines(DEFAULT_MAX_LINES);
                    textPlus.setText("全文");
                }
            }
        });
    }

    public void setText(CharSequence content){
        contentText.setText(content);
        contentText.post(new Runnable() {
            @Override
            public void run() {
                int linCount = contentText.getLineCount();
                if(linCount>3){
                    contentText.setMaxLines(DEFAULT_MAX_LINES);
                    textPlus.setVisibility(View.VISIBLE);
                    textPlus.setText("全文");
                }else{
                    textPlus.setVisibility(View.GONE);
                }
            }
        });

        contentText.setMovementMethod(new CircleMovementMethod(R.color.name_selector_color,
                R.color.name_selector_color));
    }

}
