package com.yiw.circledemo.widgets.videolist.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

/**
 * This is player implementation based on {@link TextureView}
 * It encapsulates {@link MediaPlayer}.
 *
 * @author Wayne
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureVideoView extends ScalableTextureView
        implements TextureView.SurfaceTextureListener,
        Handler.Callback,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = "TextureVideoView";
    private static final boolean SHOW_LOGS = true;

    private volatile int mCurrentState = STATE_IDLE;
    private volatile int mTargetState  = STATE_IDLE;

    private static final int STATE_ERROR              = -1;
    private static final int STATE_IDLE               = 0;
    private static final int STATE_PREPARING          = 1;
    private static final int STATE_PREPARED           = 2;
    private static final int STATE_PLAYING            = 3;
    private static final int STATE_PAUSED             = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private static final int MSG_START = 0x0001;
    private static final int MSG_PAUSE = 0x0004;
    private static final int MSG_STOP = 0x0006;

    private Uri mUri;
    private Context mContext;
    private Surface mSurface;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    private MediaPlayerCallback mMediaPlayerCallback;
    private Handler mHandler;
    private Handler mVideoHandler;

    private boolean mSoundMute;
    private boolean mHasAudio;

    private static final HandlerThread sThread = new HandlerThread("VideoPlayThread");
    static {
        sThread.start();
    }

    public interface MediaPlayerCallback {
        void onPrepared(MediaPlayer mp);
        void onStoped(MediaPlayer mp);
        void onCompletion(MediaPlayer mp);
        void onBufferingUpdate(MediaPlayer mp, int percent);
        void onVideoSizeChanged(MediaPlayer mp, int width, int height);

        boolean onInfo(MediaPlayer mp, int what, int extra);
        boolean onError(MediaPlayer mp, int what, int extra);
    }

    public TextureVideoView(Context context) {
        super(context);
        init();
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setMediaPlayerCallback(MediaPlayerCallback mediaPlayerCallback) {
        mMediaPlayerCallback = mediaPlayerCallback;
        if (mediaPlayerCallback == null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        synchronized (TextureVideoView.class) {
            switch (msg.what) {

                case MSG_START:
                    if(SHOW_LOGS) Log.i(TAG, "<< handleMessage init");
                    openVideo();
                    if(SHOW_LOGS) Log.i(TAG, ">> handleMessage init");
                    break;


                case MSG_PAUSE:
                    if(SHOW_LOGS) Log.i(TAG, "<< handleMessage pause");
                    if(mMediaPlayer != null) {
                        mMediaPlayer.pause();
                    }
                    mCurrentState = STATE_PAUSED;
                    if(SHOW_LOGS) Log.i(TAG, ">> handleMessage pause");
                    break;

                case MSG_STOP:
                    if(SHOW_LOGS) Log.i(TAG, "<< handleMessage stop");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mMediaPlayerCallback != null) {
                                mMediaPlayerCallback.onStoped(mMediaPlayer);
                            }
                        }
                    });
                    release(true);
                    if(SHOW_LOGS) Log.i(TAG, ">> handleMessage stop");
                    break;

            }
        }
        return true;
    }

    private void init() {
        mContext = getContext();
        mCurrentState = STATE_IDLE;
        mTargetState  = STATE_IDLE;
        mHandler = new Handler();
        mVideoHandler = new Handler(sThread.getLooper(), this);
        setSurfaceTextureListener(this);
    }


    // release the media player in any state
    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState  = STATE_IDLE;
            }
//            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
        }
    }

    private void openVideo() {
        if (mUri == null || mSurface == null || mTargetState != STATE_PLAYING) {
            // not ready for playback just yet, will try again later
            return;
        }

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepareAsync();

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            mTargetState = STATE_PREPARING;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                MediaExtractor mediaExtractor = new MediaExtractor();
                mediaExtractor.setDataSource(mContext, mUri, null);
                MediaFormat format;
                mHasAudio = false;
                for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
                    format = mediaExtractor.getTrackFormat(i);
                    String mime = format.getString(MediaFormat.KEY_MIME);
                    if (mime.startsWith("audio/")) {
                        mHasAudio = true;
                        break;
                    }
                }
            }
            else {
                mHasAudio = true;
            }

        } catch (IOException ex) {
            if(SHOW_LOGS) Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaPlayerCallback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mMediaPlayerCallback != null) {
                            mMediaPlayerCallback.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
                        }
                    }
                });
            }
        } catch (IllegalArgumentException ex) {
            if(SHOW_LOGS) Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaPlayerCallback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mMediaPlayerCallback != null) {
                            mMediaPlayerCallback.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        if(mTargetState == STATE_PLAYING) {
            if(SHOW_LOGS) Log.i(TAG, "onSurfaceTextureAvailable start");
            start();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurface = null;
        stop();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        if(SHOW_LOGS) Log.i(TAG, "setVideoURI " + uri.toString());
        mUri = uri;
    }

    public void start() {
        mTargetState = STATE_PLAYING;

        if (isInPlaybackState()) {
            mVideoHandler.obtainMessage(MSG_STOP).sendToTarget();
        }

        if(mUri != null && mSurface != null) {
            mVideoHandler.obtainMessage(MSG_START).sendToTarget();
        }
    }

    public void pause() {
        mTargetState = STATE_PAUSED;

        if (isPlaying()) {
            mVideoHandler.obtainMessage(MSG_PAUSE).sendToTarget();
        }
    }

    public void resume() {
        mTargetState = STATE_PLAYING;

        if (!isPlaying()) {
            mVideoHandler.obtainMessage(MSG_START).sendToTarget();
        }
    }

    public void stop() {
        mTargetState = STATE_PLAYBACK_COMPLETED;

        if(isInPlaybackState()) {
            mVideoHandler.obtainMessage(MSG_STOP).sendToTarget();
        }
    }

    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    public void mute() {
        if(mMediaPlayer != null) {
            mMediaPlayer.setVolume(0, 0);
            mSoundMute = true;
        }
    }

    public void unMute() {
        if (mAudioManager != null && mMediaPlayer != null) {
            int max = 100;
            int audioVolume = 100;
            double numerator = max - audioVolume > 0 ? Math.log(max - audioVolume) : 0;
            float volume = (float) (1 - (numerator / Math.log(max)));
            mMediaPlayer.setVolume(volume, volume);
            mSoundMute = false;
        }
    }

    public boolean isMute() {
        return mSoundMute;
    }

    public boolean isHasAudio() {
        return mHasAudio;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        mTargetState = STATE_PLAYBACK_COMPLETED;
        if (mMediaPlayerCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayerCallback != null) {
                        mMediaPlayerCallback.onCompletion(mp);
                    }
                }
            });
        }
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        if(SHOW_LOGS) Log.e(TAG, "onError() called with " + "mp = [" + mp + "], what = [" + what + "], extra = [" + extra + "]");
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if (mMediaPlayerCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayerCallback != null) {
                        mMediaPlayerCallback.onError(mp, what, extra);
                    }
                }
            });
        }
        return true ;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        if(SHOW_LOGS) Log.i(TAG, "onPrepared " + mUri.toString());
        if (mTargetState != STATE_PREPARING || mCurrentState != STATE_PREPARING) {
            return;
        }

        mCurrentState = STATE_PREPARED;

        if (isInPlaybackState()) {
//            mute();
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mTargetState = STATE_PLAYING;
        }

        if (mMediaPlayerCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayerCallback != null) {
                        mMediaPlayerCallback.onPrepared(mp);
                    }
                }
            });
        }
    }

    @Override
    public void onVideoSizeChanged(final MediaPlayer mp, final int width, final int height) {
        if (mMediaPlayerCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayerCallback != null) {
                        mMediaPlayerCallback.onVideoSizeChanged(mp, width, height);
                    }
                }
            });
        }
    }

    @Override
    public void onBufferingUpdate(final MediaPlayer mp, final int percent) {
        if (mMediaPlayerCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayerCallback != null) {
                        mMediaPlayerCallback.onBufferingUpdate(mp, percent);
                    }
                }
            });
        }
    }

    @Override
    public boolean onInfo(final MediaPlayer mp, final int what, final int extra) {
        if (mMediaPlayerCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayerCallback != null) {
                        mMediaPlayerCallback.onInfo(mp, what, extra);
                    }
                }
            });
        }
        return true;
    }
}
