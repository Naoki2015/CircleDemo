package com.yiw.circledemo.listener;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
/**
 * 
* @ClassName: SwpipeListViewOnScrollListener 
* @Description: 解决SwipeRefreshLayout和listview的滑动冲突事件
* @author yiw
* @date 2015-12-28 下午4:17:48 
*
 */
public class SwpipeListViewOnScrollListener implements AbsListView.OnScrollListener {
	 
    private SwipeRefreshLayout mSwipeView;
    private AbsListView.OnScrollListener mOnScrollListener;
 
    public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView) {
        mSwipeView = swipeView;
    }
 
    public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView,
            OnScrollListener onScrollListener) {
        mSwipeView = swipeView;
        mOnScrollListener = onScrollListener;
    }
 
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
    	System.out.println("onScrollStateChanged: " + i);
    }
 
    @SuppressLint("NewApi")
	@Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        View firstView = absListView.getChildAt(0);
        // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
        if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {
            mSwipeView.setEnabled(true);
        } else {
            mSwipeView.setEnabled(false);
        }
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(absListView, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }
}