package com.yiw.httplib.config;

/**
 * 配置一些全局参数
 * 在使用支付前配置
 * Created by yiw on 2016/6/22.
 */
public class HttpConfig {

    private static HttpConfig instance;

    private HttpConfig(){}
    public static HttpConfig getInstance(){

        if(instance == null){
            synchronized (HttpConfig.class){
                if(instance == null){
                    instance = new HttpConfig();
                }
            }
        }
        return instance;
    }

    private long http_connect_timeout ;

    public void setHttpConnectTimeout(long connectTimeout){
        http_connect_timeout = connectTimeout;
    }

    public long getHttpConnectTimeout(){
        return http_connect_timeout;
    }


    private String debugTag = "debug";
    private boolean isDebug = false;

    public String getDebugTag() {
        return debugTag;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(String debugTag, boolean isDebug){
        this.debugTag = debugTag;
        this.isDebug = isDebug;
    }


}
