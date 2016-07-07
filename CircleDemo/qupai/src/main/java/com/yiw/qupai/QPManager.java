package com.yiw.qupai;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.duanqu.qupai.bean.QupaiUploadTask;
import com.duanqu.qupai.engine.session.MovieExportOptions;
import com.duanqu.qupai.engine.session.ProjectOptions;
import com.duanqu.qupai.engine.session.ThumbnailExportOptions;
import com.duanqu.qupai.engine.session.UISettings;
import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
import com.duanqu.qupai.sdk.android.QupaiManager;
import com.duanqu.qupai.sdk.android.QupaiService;
import com.duanqu.qupai.upload.QupaiUploadListener;
import com.duanqu.qupai.upload.UploadService;
import com.yiw.qupai.auth.AuthTest;
import com.yiw.qupai.common.Contant;
import com.yiw.qupai.listener.IUploadListener;
import com.yiw.qupai.utils.AppSharePreferences;

import java.io.File;
import java.util.UUID;

/**
 * Created by suneee on 2016/7/6.
 */
public class QPManager {

    private static final String TAG = QPManager.class.getSimpleName();
    private static QPManager instance;
    private Context context;
    private static final int REQUEST_CODE_VIDEO = 1000;

    private QPManager(Context context){
        this.context = context.getApplicationContext();

        String accessToken = AppSharePreferences.getInstance(this.context).get(Contant.SP_ACCESSTOKEN);
        String space = AppSharePreferences.getInstance(this.context).get(Contant.SP_SPACE);
        if(TextUtils.isEmpty(accessToken)){

            initAuth();
        }else {
            Contant.accessToken = accessToken;
            Contant.space = space;
        }
    }

    public static QPManager getInstance(Context context){
        if(instance == null){
            synchronized (QPManager.class){
                if(instance == null){
                    instance = new QPManager(context);
                }
            }
        }
        return instance;
    }

    public void initAuth(){
        AuthTest.getInstance().initAuth(context, Contant.APP_KEY, Contant.APP_SECRET);
    }

    /**
     * 趣拍初始化
     */
    public void initRecord() {

        String accessToken = AppSharePreferences.getInstance(context).get(Contant.SP_ACCESSTOKEN);
        if(TextUtils.isEmpty(accessToken)){
            initAuth();
        }else {
            Contant.accessToken = accessToken;
        }

        Log.e(TAG, "accessToken: " + accessToken);
        QupaiService qupaiService = QupaiManager.getQupaiService(context);
        if (qupaiService == null) {
            Toast.makeText(context, "插件没有初始化，无法获取 QupaiService",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //UI设置参数
        UISettings _UISettings = new UISettings() {

            @Override
            public boolean hasEditor() {
                return true;//是否需要编辑功能
            }

            @Override
            public boolean hasImporter() {
                return true;//是否需要导入功能
            }

            @Override
            public boolean hasGuide() {
                return false;//是否启动引导功能，建议用户第一次使用时设置为true
            }

            @Override
            public boolean hasSkinBeautifer() {
                return true;//是否显示美颜图标
            }
        };

        //压缩参数
        MovieExportOptions movie_options = new MovieExportOptions.Builder()
                .setVideoBitrate(Contant.DEFAULT_BITRATE)//码率
                .configureMuxer("movflags", "+faststart")
                .build();

        //输出视频的参数
        ProjectOptions projectOptions = new ProjectOptions.Builder()
                //输出视频宽高目前只能设置1：1的宽高，建议设置480*480.
                .setVideoSize(480, 480)
                //帧率
                .setVideoFrameRate(30)
                //时长区间
                .setDurationRange(Contant.DEFAULT_MIN_DURATION_LIMIT, Contant.DEFAULT_DURATION_LIMIT)
                .get();

        //缩略图参数,可设置取得缩略图的数量，默认10张
        ThumbnailExportOptions thumbnailExportOptions = new ThumbnailExportOptions.Builder().setCount(10).get();

        VideoSessionCreateInfo info = new VideoSessionCreateInfo.Builder()
                //水印地址，如"assets://Qupai/watermark/qupai-logo.png"
                .setWaterMarkPath(Contant.WATER_MARK_PATH)//水印地址
                //水印的位置
                .setWaterMarkPosition(1)
                //摄像头方向,可配置前置或后置摄像头
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
                //美颜百分比,设置之后内部会记住，多次设置无效
                .setBeautyProgress(80)
                //默认是否开启
                .setBeautySkinOn(true)
                .setMovieExportOptions(movie_options)
                .setThumbnailExportOptions(thumbnailExportOptions)
                .build();

        //初始化，建议在application里面做初始化，这里做是为了方便开发者认识参数的意义
        qupaiService.initRecord(info, projectOptions, _UISettings);
        qupaiService.addMusic(0, "szj", "assets://Qupai/music/szj");
        qupaiService.addMusic(1, "Athena", "assets://Qupai/music/Athena");
        qupaiService.addMusic(2, "Box Clever", "assets://Qupai/music/Box Clever");
        qupaiService.addMusic(3, "Byebye love", "assets://Qupai/music/Byebye love");
        qupaiService.addMusic(4, "chuangfeng", "assets://Qupai/music/chuangfeng");
        qupaiService.addMusic(5, "Early days", "assets://Qupai/music/Early days");
        qupaiService.addMusic(6, "Faraway", "assets://Qupai/music/Faraway");
    }


    /**
     * 调起拍摄界面
     * @param activity
     */
    public static void startRecordActivity(Activity activity) {
        QupaiService qupaiService = QupaiManager.getQupaiService(activity.getApplicationContext());

        if (qupaiService == null) {
            Toast.makeText(activity.getApplicationContext(), "插件没有初始化，无法获取 QupaiService",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //引导，只显示一次，这里用SharedPreferences记录
        Boolean isGuideShow = AppSharePreferences.getInstance(activity.getApplicationContext()).get(Contant.SP_SHOW_GUIDE, true);
        qupaiService.showRecordPage(activity, REQUEST_CODE_VIDEO, isGuideShow);
        AppSharePreferences.getInstance(activity.getApplicationContext()).set(Contant.SP_SHOW_GUIDE, false);
    }

    public void startUpload(String videoPath, String thumPath, final IUploadListener listener){
        if(listener==null){
            throw new IllegalArgumentException("IUploadListener is null...");
        }
        if(TextUtils.isEmpty(videoPath)){
            throw new IllegalArgumentException("videoPath is empty...");
        }
        if(TextUtils.isEmpty(thumPath)){
            throw new IllegalArgumentException("thumPath is empty...");
        }

        listener.preUpload();
        UploadService uploadService = UploadService.getInstance();
        uploadService.setQupaiUploadListener(new QupaiUploadListener() {
            @Override
            public void onUploadProgress(String uuid, long uploadedBytes, long totalBytes) {
                int percentsProgress = (int) (uploadedBytes * 100 / totalBytes);
                Log.e(TAG, "uuid: " + uuid + " &data:onUploadProgress: " + percentsProgress);
                listener.uploadProgress(percentsProgress);
            }

            @Override
            public void onUploadError(String uuid, int errorCode, String message) {
                Log.e(TAG, "uuid: " + uuid + " &onUploadError: " + errorCode + " &message: "+ message);
                listener.uploadError(errorCode, message);
            }

            @Override
            public void onUploadComplte(String uuid, int responseCode, String responseMessage) {
                //http://{DOMAIN}/v/{UUID}.mp4?token={ACCESS-TOKEN}
                //progress.setVisibility(View.GONE);
                //btn_open_video.setVisibility(View.VISIBLE);

                //这里返回的uuid是你创建上传任务时生成的uuid.开发者可以使用其他作为标识
                //videoUrl返回的是上传成功的视频地址,imageUrl是上传成功的图片地址
                String videoUrl = Contant.domain + "/v/" + responseMessage + ".mp4" + "?token=" + Contant.accessToken;
                String imageUrl = Contant.domain + "/v/" + responseMessage + ".jpg" + "?token=" + Contant.accessToken;

                listener.uploadComplet(videoUrl, imageUrl, responseMessage);
                Log.i("TAG", "data:onUploadComplte  " + "uuid: " + uuid + " &imgUrl: "+ Contant.domain +"/v/"+ responseMessage + ".jpg" + "?token=" + Contant.accessToken);
                Log.i("TAG", "data:onUploadComplte  " + "uuid: " + uuid + " &mp4Url: "+ Contant.domain +"/v/"+ responseMessage + ".mp4" + "?token=" + Contant.accessToken);
            }
        });
        String uuid = UUID.randomUUID().toString();
        Log.e("QupaiAuth",  "accessToken = " + Contant.accessToken +"  &space = "+ Contant.space);

        startUpload(createUploadTask(this.context, uuid, new File(videoPath), new File(thumPath),
                Contant.accessToken, Contant.space, Contant.shareType, Contant.tags, Contant.description));
    }

    /**
     * 开始上传
     * @param data 上传任务的task
     */
    private void startUpload(QupaiUploadTask data) {
        try {
            UploadService uploadService = UploadService.getInstance();
            uploadService.startUpload(data);
        } catch (IllegalArgumentException exc) {
            Log.d("upload", "Missing some arguments. " + exc.getMessage());
        }
    }

    /**
     * 创建一个上传任务
     * @param context
     * @param uuid        随机生成的UUID
     * @param _VideoFile  完整视频文件
     * @param _Thumbnail  缩略图
     * @param accessToken 通过调用鉴权得到token
     * @param space        开发者生成的Quid，必须要和token保持一致
     * @param share       是否公开 0公开分享 1私有(default) 公开类视频不需要AccessToken授权
     * @param tags        标签 多个标签用 "," 分隔符
     * @param description 视频描述
     * @return
     */
    private QupaiUploadTask createUploadTask(Context context, String uuid, File _VideoFile, File _Thumbnail, String accessToken,
                                             String space, int share, String tags, String description) {
        UploadService uploadService = UploadService.getInstance();
        return uploadService.createTask(context, uuid, _VideoFile, _Thumbnail,
                accessToken, space, share, tags, description);
    }


}
