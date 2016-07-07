package com.yiw.httplib;

import android.os.Handler;
import android.os.Looper;

import com.yiw.httplib.callback.HttpCallback;
import com.yiw.httplib.config.HttpConfig;
import com.yiw.httplib.log.LoggerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 一个简易的基于okhttp的网络请求封装
 * 网络请求类，提供get/post请求方式
 *
 * Created by yiw on 2016/7/6.
 */
public class YHttpClient {
    /**
     * 网络请求timeout时间
     * 以毫秒为单位
     */
    private static long DEFAULT_CONNECTTIMEOUT = 10*1000;

    private long connectTimeout = DEFAULT_CONNECTTIMEOUT;

    private static YHttpClient instance;
    private OkHttpClient okHttpClient;
    private Handler handler;

    private YHttpClient(){
        handler = new Handler(Looper.getMainLooper());

        if(HttpConfig.getInstance().getHttpConnectTimeout()>0){
            connectTimeout = HttpConfig.getInstance().getHttpConnectTimeout();
        }

        okHttpClient = createOKHttpClicent();
    }

    public static YHttpClient getInstance(){
        if(instance == null){
            synchronized (YHttpClient.class){
                if(instance == null){
                    instance = new YHttpClient();
                }
            }
        }

        return instance;
    }

    public void httpGet(String url, final HttpCallback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureResponse(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.isSuccessful()){
                        sendSuccessResponse(callback, callback.parseNetworkResponse(response));
                    }else{
                        sendFailureResponse(callback, new Exception(response.message()));
                    }
                }catch (Exception e){
                    sendFailureResponse(callback, e);
                }
            }
        });
    }

    public void httpPost(String url, String reqParamsJson, final HttpCallback callback){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, reqParamsJson);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureResponse(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.isSuccessful()){

                        sendSuccessResponse(callback, callback.parseNetworkResponse(response));
                    }else{
                        sendFailureResponse(callback, new Exception(response.message()));
                    }
                }catch (Exception e){
                    sendFailureResponse(callback, e);
                }
            }
        });
    }

    private void sendSuccessResponse(final HttpCallback callback, final Object object){
        if (callback == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(object);
            }
        });
    }

    private void sendFailureResponse(final HttpCallback callback, final Exception e){
        if (callback == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(e);
            }
        });
    }


    public OkHttpClient createOKHttpClicent(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                //.cookieJar()
                //.hostnameVerifier()
                .build();

        boolean isDebug = HttpConfig.getInstance().isDebug();
        if(isDebug){
            String tag = HttpConfig.getInstance().getDebugTag();
            client = client.newBuilder().addInterceptor(new LoggerInterceptor(tag, true)).build();
        }

        return client;
    }
}
