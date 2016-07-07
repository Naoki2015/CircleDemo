package com.yiw.httplib.callback;

import okhttp3.Response;

/**
 * 网络接口结果处理
 * 如果返回是String类型的Json,你可以使用StringCallback，其封装了解析
 * 如果返回是其他类型，请实现该接口自己在parseNetworkResponse方法中解析
 *
 * Created by suneee on 2016/6/24.
 */
public interface  HttpCallback<T> {

    public void onFailure(Exception e);

    public void onSuccess(T result);

    public T parseNetworkResponse(Response response) throws Exception;

}
