package com.yiw.httplib.callback;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * 返回类型是json的回调处理，做了自动对象转换
 * Created by yiw on 2016/7/6.
 */
public abstract class StringCallback<T> implements HttpCallback{

    public T parseNetworkResponse(Response response) throws Exception{

        String body = response.body().string();
        //获取T的类型
        Type mySuperClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType)mySuperClass).getActualTypeArguments()[0];
        T result = new Gson().fromJson(body, type);
        return result;
    }
}
