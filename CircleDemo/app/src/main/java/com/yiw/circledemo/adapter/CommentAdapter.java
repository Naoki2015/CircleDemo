package com.yiw.circledemo.adapter;

/*
 Suneee Android Client, NoticeItemAdapter
 Copyright (c) 2014 Suneee Tech Company Limited
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.BaseMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yiw.circledemo.R;
import com.yiw.circledemo.bean.CommentItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiw
 * @ClassName: CommentAdapter
 * @Description: 评论的adapter
 * @date 2015-12-28 下午3:40:29
 */
public class CommentAdapter extends BaseAdapter {

    private ViewHolder holder;
    private List<CommentItem> datasource = new ArrayList<CommentItem>();
    private Context mContext;
    private ICommentItemClickListener commentItemClickListener;// 评论点击事件
    private boolean isPassToTv = true;

    public boolean isPassToTv() {
        return isPassToTv;
    }

    public void setPassToTv(boolean isPassToTv) {
        this.isPassToTv = isPassToTv;
    }

    public void setCommentClickListener(
            ICommentItemClickListener commentItemClickListener) {
        this.commentItemClickListener = commentItemClickListener;
    }

    public CommentAdapter(Context context) {
        mContext = context;
    }

    /**
     * @param context
     */
    public CommentAdapter(Activity context, List<CommentItem> datasource) {
        this.datasource = datasource;
    }

    public void setDatasource(List<CommentItem> datasource) {
        if (datasource != null) {
            this.datasource = datasource;
        }
    }

    class ViewHolder {
        TextView commentTv;
        MyMovementMethod myMovementMethod;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext,
                    R.layout.im_social_item_comment, null);
            holder.commentTv = (TextView) convertView.findViewById(R.id.commentTv);
            holder.myMovementMethod = new MyMovementMethod();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommentItem bean = datasource.get(position);
        String name = bean.getUser().getName();
        String id = bean.getId();
        String toReplyName = "";
        if (bean.getToReplyUser() != null) {
            toReplyName = bean.getToReplyUser().getName();
        }

        SpannableString subjectSpanText = new SpannableString(name);
        subjectSpanText.setSpan(new NameClickable(new NameClickListener(
                        subjectSpanText, "")), 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(subjectSpanText);

        if (!TextUtils.isEmpty(toReplyName)) {

            builder.append(" 回复 ");
            SpannableString objectSpanText = new SpannableString(toReplyName);
            objectSpanText.setSpan(new NameClickable(new NameClickListener(
                            objectSpanText, "")), 0, objectSpanText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(objectSpanText);
        }
        builder.append(": ");
        //转换表情字符
        String contentBodyStr = bean.getContent();
        //SpannableString contentSpanText = new SpannableString(contentBodyStr);
        //contentSpanText.setSpan(new UnderlineSpan(), 0, contentSpanText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(contentBodyStr);
        holder.commentTv.setText(builder);

        holder.commentTv.setMovementMethod(holder.myMovementMethod);
        holder.commentTv.setOnClickListener(null);
        holder.commentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPassToTv) {
                    commentItemClickListener.onItemClick(position);
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return datasource.size();
    }

    @Override
    public Object getItem(int arg0) {
        return datasource.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public List<CommentItem> getDatasource() {
        return datasource;
    }

    // TextView中的名字Span区域点击事件
    class NameClickable extends ClickableSpan implements OnClickListener {
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

            int colorValue = mContext.getResources().getColor(
                    R.color.color_8290AF);
            ds.bgColor = mContext.getResources().getColor(R.color.name_selector_text);
            ds.setColor(colorValue);
            ds.setUnderlineText(false);
            ds.clearShadowLayer();
        }
    }

    // 评论中名字的点击事件
    class NameClickListener implements View.OnClickListener {
        private SpannableString userName;
        private String userId;

        public NameClickListener(SpannableString name, String userid) {
            this.userName = name;
            this.userId = userid;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, userName, Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyMovementMethod extends BaseMovementMethod {
        public final String TAG = MyMovementMethod.class.getSimpleName();
        private BackgroundColorSpan mBgSpan;
        private ClickableSpan[] mClickLinks;

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
                    // 点击的是Span区域，不要把点击事件传递CommentItemView
                    setPassToTv(false);
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(mClickLinks[0]),
                            buffer.getSpanEnd(mClickLinks[0]));
                    mBgSpan = new BackgroundColorSpan(mContext.getResources().getColor(R.color.im_line_color));
                    buffer.setSpan(mBgSpan,
                            buffer.getSpanStart(mClickLinks[0]), buffer.getSpanEnd(mClickLinks[0]), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    setPassToTv(true);
                    // 点击选中效果
                    ((View) widget.getParent())
                            .setBackgroundResource(R.color.adapter_item_phone_selector_color);
                }

            }else if(action == MotionEvent.ACTION_UP){
                if(mClickLinks.length > 0){
                    mClickLinks[0].onClick(widget);
                    if(mBgSpan!=null){
                        buffer.removeSpan(mBgSpan);
                    }
                }
                Selection.removeSelection(buffer);
                ((View) widget.getParent()).setBackgroundResource(R.color.transparent);
            }else if(action == MotionEvent.ACTION_MOVE){

            }else{
                if(mBgSpan!=null){
                    buffer.removeSpan(mBgSpan);
                }
                ((View) widget.getParent()).setBackgroundResource(R.color.transparent);
            }
            return Touch.onTouchEvent(widget, buffer, event);
        }
    }

    public interface ICommentItemClickListener {

        public void onItemClick(int position);
    }

}
