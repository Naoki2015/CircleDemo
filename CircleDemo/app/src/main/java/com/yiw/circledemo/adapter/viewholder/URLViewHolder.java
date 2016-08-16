package com.yiw.circledemo.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiw.circledemo.R;

/**
 * Created by suneee on 2016/8/16.
 */
public class URLViewHolder extends CircleViewHolder{
    public LinearLayout urlBody;
    /** 链接的图片 */
    public ImageView urlImageIv;
    /** 链接的标题 */
    public TextView urlContentTv;

    public URLViewHolder(View itemView){
        super(itemView, TYPE_URL);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }

        viewStub.setLayoutResource(R.layout.viewstub_urlbody);
        View subViw  = viewStub.inflate();
        LinearLayout urlBodyView = (LinearLayout) subViw.findViewById(R.id.urlBody);
        if(urlBodyView != null){
            urlBody = urlBodyView;
            urlImageIv = (ImageView) subViw.findViewById(R.id.urlImageIv);
            urlContentTv = (TextView) subViw.findViewById(R.id.urlContentTv);
        }
    }
}
