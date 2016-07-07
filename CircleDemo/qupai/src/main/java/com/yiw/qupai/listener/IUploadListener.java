package com.yiw.qupai.listener;

/**
 * Created by yiw on 2016/7/7.
 */
public interface IUploadListener {

    public void preUpload();

    public void uploadComplet(String videoUrl, String imageUrl, String message);

    public void uploadError(int errorCode, String message);

    /**
     * 上传百分比
     * @param percentsProgress
     */
    public void uploadProgress(int percentsProgress);
}
