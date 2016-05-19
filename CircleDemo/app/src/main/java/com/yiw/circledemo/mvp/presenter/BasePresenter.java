package com.yiw.circledemo.mvp.presenter;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.view.View;

import com.yiw.circledemo.mvp.view.BaseView;

import java.lang.ref.WeakReference;

/**
 * Created by yiwei on 16/4/1.
 */
public class BasePresenter<V extends BaseView> {
    protected String tag;
    private WeakReference<V> viewRef;
    protected Context mContext;

    public void cancleRequest(){
        cancleRequest(tag);
    }

    public void cancleRequest(String tag){
    }

    @UiThread
    public void attachView(V view) {
        viewRef = new WeakReference<V>(view);
        mContext = getContext();
    }

    /**
     * Get the attached view. You should always call {@link #isViewAttached()} to check if the view
     * is
     * attached to avoid NullPointerExceptions.
     *
     * @return <code>null</code>, if view is not attached, otherwise the concrete view instance
     */
    @UiThread
    @Nullable
    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    /**
     * Checks if a view is attached to this presenter. You should always call this method before
     * calling {@link #getView()} to get the view instance.
     */
    @UiThread
    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    @UiThread
    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    protected Context getContext(){
        if(getView() instanceof Fragment){
            return ((Fragment)getView()).getActivity();
        }else if(getView() instanceof Activity){
            return (Context) getView();
        }else if(getView() instanceof View){
            return ((View)getView()).getContext();
        }
        return null;
    }
}
