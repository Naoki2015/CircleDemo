package com.yiw.circledemo.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.yiw.circledemo.R;
import com.yiw.circledemo.widgets.videolist.VideoListGlideModule;
import com.yiw.circledemo.widgets.videolist.model.VideoLoadMvpView;
import com.yiw.circledemo.widgets.videolist.target.VideoLoadTarget;
import com.yiw.circledemo.widgets.videolist.target.VideoProgressTarget;
import com.yiw.circledemo.widgets.videolist.visibility.items.ListItem;
import com.yiw.circledemo.widgets.videolist.widget.TextureVideoView;

import java.io.File;
import java.io.InputStream;

/**
 * Created by yiwei on 16/5/23.
 */
public class CircleVideoView extends LinearLayout implements VideoLoadMvpView, ListItem {

    public TextureVideoView videoPlayer;
    public ImageView videoFrame;
    public CircularProgressBar videoProgress;
    public ImageView videoButton;

    public VideoLoadTarget videoTarget;
    public VideoProgressTarget progressTarget;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;

    private int postion;;
    private String videoUrl;

    private OnPlayClickListener onPlayClickListener;
    private String videoLocalPath;

    public CircleVideoView(Context context) {
        super(context);
        init();
    }

    public CircleVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPostion(int pos) {
        postion = pos;
    }

    public void setVideoUrl(String url){
        videoUrl = url;
    }

    public void setVideoImgUrl(String imgUrl){

        Glide.with(getContext())
                .load(imgUrl)
                .placeholder(new ColorDrawable(0xffdcdcdc))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(videoFrame);

        if(videoState == STATE_IDLE){
            videoButton.setVisibility(View.VISIBLE);
            videoFrame.setVisibility(View.VISIBLE);
        }else if(videoState == STATE_ACTIVED){
            videoButton.setVisibility(View.GONE);
            videoFrame.setVisibility(View.GONE);
        }else{
            videoButton.setVisibility(View.VISIBLE);
            videoFrame.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.layout_video, this);
        videoPlayer = (TextureVideoView) findViewById(R.id.video_player);
        videoFrame = (ImageView) findViewById(R.id.iv_video_frame);
        videoProgress = (CircularProgressBar) findViewById(R.id.video_progress);
        videoButton = (ImageView) findViewById(R.id.iv_video_play);

        videoTarget = new VideoLoadTarget(this);
        progressTarget = new VideoProgressTarget(videoTarget, videoProgress);


        videoPlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayer.isPlaying()) {
                    videoPlayer.stop();
                }
            }
        });

        videoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(videoUrl)){
                    Toast.makeText(getContext(), "video url is empty...", Toast.LENGTH_LONG).show();
                    return;
                }
                // load video file
                videoState = STATE_ACTIVED;
                progressTarget.start();
                Glide.with(getContext())
                        .using(VideoListGlideModule.getOkHttpUrlLoader(), InputStream.class)
                        .load(new GlideUrl(videoUrl))
                        .as(File.class)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(progressTarget);

                videoButton.setVisibility(View.INVISIBLE);
                if(onPlayClickListener!=null){
                    onPlayClickListener.onPlayClick(postion);
                }
            }
        });

    }

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {

    }

    @Override
    public void deactivate(View currentView, int position) {
        if(this.postion==position){
            videoState = STATE_DEACTIVED;
            if(!TextUtils.isEmpty(videoUrl)){
                videoPlayer.stop();
                videoStopped();
            }
        }
    }

    @Override
    public TextureVideoView getVideoView() {
        return videoPlayer;
    }

    @Override
    public void videoBeginning() {
        videoPlayer.setAlpha(1.f);
        cancelAlphaAnimate(videoFrame);
        startAlphaAnimate(videoFrame);
    }

    @Override
    public void videoStopped() {
        cancelAlphaAnimate(videoFrame);
        videoPlayer.setAlpha(0);
        videoFrame.setAlpha(1.f);
        videoButton.setVisibility(View.VISIBLE);
        videoProgress.setVisibility(View.GONE);
        videoFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void videoPrepared(MediaPlayer player) {

    }

    @Override
    public void videoResourceReady(String videoPath) {
        videoLocalPath = videoPath;
        if(videoLocalPath != null) {
            videoPlayer.setVideoPath(videoPath);
            if(videoState == STATE_ACTIVED) {
                videoPlayer.start();
            }
        }
    }

    public static interface OnPlayClickListener{
        void onPlayClick(int pos);
    }

    public void resetVideo() {
        if(!TextUtils.isEmpty(videoUrl)){
            videoState = STATE_IDLE;
            videoPlayer.stop();
            videoLocalPath = null;
            videoStopped();
        }
    }

    private void cancelAlphaAnimate(View v) {
        ViewCompat.animate(v).cancel();
    }

    private void startAlphaAnimate(View v) {
        ViewCompat.animate(v).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {

            }

            @Override
            public void onAnimationEnd(View view) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).alpha(0f);
    }
}
