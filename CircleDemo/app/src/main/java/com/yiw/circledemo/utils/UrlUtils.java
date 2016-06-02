package com.yiw.circledemo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import com.yiw.circledemo.spannable.SpannableClickable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by suneee on 2016/6/2.
 */
public class UrlUtils {

    public static SpannableStringBuilder formatUrlString(String contentStr){

        SpannableStringBuilder sp;
        if(!TextUtils.isEmpty(contentStr)){
            sp = new SpannableStringBuilder(contentStr);
            Pattern pattern = Pattern.compile("(http|https|ftp|svn)://([a-zA-Z0-9]+[/?.?])" +
                    "+[a-zA-Z0-9]*\\??([a-zA-Z0-9]*=[a-zA-Z0-9]*&?)*");
            Matcher matcher = pattern.matcher(contentStr);

            while (matcher.find()) {
                final String url = matcher.group();
                int start = contentStr.indexOf(url);
                if (start >= 0) {
                    int end = start + url.length();
                    //sp.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(new SpannableClickable(){
                        @Override
                        public void onClick(View widget) {
                            Uri uri = Uri.parse(url);
                            Context context = widget.getContext();
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                            context.startActivity(intent);
                        }
                    }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }else{
            sp = new SpannableStringBuilder();
        }
        return sp;
    }
}
