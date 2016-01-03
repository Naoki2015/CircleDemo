package com.yiw.circledemo.adapter;

/*
 Suneee Android Client, NoticeItemAdapter
 Copyright (c) 2014 Suneee Tech Company Limited
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yiw.circledemo.R;
import com.yiw.circledemo.bean.CommentItem;
import com.yiw.circledemo.spannable.CircleMovementMethod;
import com.yiw.circledemo.spannable.NameClickListener;
import com.yiw.circledemo.spannable.NameClickable;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CommentAdapter
 * @Description: 评论的adapter
 * @author yiw
 * @date 2015-12-28 下午3:40:29
 */
public class CommentAdapter extends BaseAdapter {

    private List<CommentItem> datasource = new ArrayList<CommentItem>();
    private Context mContext;
    private ICommentItemClickListener commentItemClickListener;// 评论点击事件

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
        CircleMovementMethod circleMovementMethod;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext,
                    R.layout.im_social_item_comment, null);
            holder.commentTv = (TextView) convertView.findViewById(R.id.commentTv);
            holder.circleMovementMethod = new CircleMovementMethod(R.color.name_selector_color,
                    R.color.name_selector_color);
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

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, 0));

        if (!TextUtils.isEmpty(toReplyName)) {

            builder.append(" 回复 ");
            builder.append(setClickableSpan(toReplyName, 1));
        }
        builder.append(": ");
        //转换表情字符
        String contentBodyStr = bean.getContent();
        //SpannableString contentSpanText = new SpannableString(contentBodyStr);
        //contentSpanText.setSpan(new UnderlineSpan(), 0, contentSpanText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(contentBodyStr);
        holder.commentTv.setText(builder);

        final CircleMovementMethod circleMovementMethod = holder.circleMovementMethod;
        holder.commentTv.setMovementMethod(circleMovementMethod);
        holder.commentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    commentItemClickListener.onItemClick(position);
                }
            }
        });
        return convertView;
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new NameClickable(new NameClickListener(
                        subjectSpanText, ""), position), 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
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

    public interface ICommentItemClickListener {

        public void onItemClick(int position);
    }

}
