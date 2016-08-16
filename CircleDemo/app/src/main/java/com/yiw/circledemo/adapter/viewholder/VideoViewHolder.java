package com.yiw.circledemo.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;

import com.yiw.circledemo.R;
import com.yiw.circledemo.widgets.CircleVideoView;

/**
 * Created by suneee on 2016/8/16.
 */
public class VideoViewHolder extends CircleViewHolder {

    public CircleVideoView videoView;

    public VideoViewHolder(View itemView){
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        
        viewStub.setLayoutResource(R.layout.viewstub_videobody);
        View subView = viewStub.inflate();

        CircleVideoView videoBody = (CircleVideoView) subView.findViewById(R.id.videoView);
        if(videoBody!=null){
            this.videoView = videoBody;
        }
    }
}
