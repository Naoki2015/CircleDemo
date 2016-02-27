package com.yiw.circledemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yiw.circledemo.adapter.CircleAdapter;
import com.yiw.circledemo.bean.CircleItem;
import com.yiw.circledemo.contral.CirclePublicCommentContral;
import com.yiw.circledemo.listener.SwpipeListViewOnScrollListener;
import com.yiw.circledemo.utils.CommonUtils;
import com.yiw.circledemo.utils.DatasUtil;

import java.util.List;

/**
 * @author yiw
 * @ClassName: MainActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2015-12-28 下午4:21:18
 */
public class MainActivity extends Activity implements OnRefreshListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    private ListView mCircleLv;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CircleAdapter mAdapter;
    private LinearLayout mEditTextBody;
    private EditText mEditText;
    private TextView sendTv;

    private int mScreenHeight;
    private int mEditTextBodyHeight;
    private CirclePublicCommentContral mCirclePublicCommentContral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadData();
    }

    @SuppressLint({"ClickableViewAccessibility", "InlinedApi"})
    private void initView() {
        LinearLayout v_top = (LinearLayout) findViewById(R.id.v_top);
        v_top.setPadding(0, getStatusBarHeight(), 0, 0);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mRefreshLayout);
        mCircleLv = (ListView) findViewById(R.id.circleLv);
        mCircleLv.setOnScrollListener(new SwpipeListViewOnScrollListener(mSwipeRefreshLayout));
        mCircleLv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEditTextBody.getVisibility() == View.VISIBLE) {
                    mEditTextBody.setVisibility(View.GONE);
                    CommonUtils.hideSoftInput(MainActivity.this, mEditText);
                    return true;
                }
                return false;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mAdapter = new CircleAdapter(this);
        mCircleLv.setAdapter(mAdapter);

        mEditTextBody = (LinearLayout) findViewById(R.id.editTextBodyLl);
        mEditText = (EditText) findViewById(R.id.circleEt);
        sendTv = (TextView) findViewById(R.id.sendTv);

        mCirclePublicCommentContral = new CirclePublicCommentContral(this, mEditTextBody, mEditText, sendTv);
        mCirclePublicCommentContral.setmListView(mCircleLv);
        mAdapter.setmCirclePublicCommentContral(mCirclePublicCommentContral);

        setViewTreeObserver();
    }


    private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = mSwipeRefreshLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                mSwipeRefreshLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = mSwipeRefreshLayout.getRootView().getHeight();
                if (r.top != statusBarH) {
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
                Log.d(TAG, "keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);
                if (keyboardH == MyApplication.mKeyBoardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }

                MyApplication.mKeyBoardH = keyboardH;
                mScreenHeight = screenH;//应用屏幕的高度
                mEditTextBodyHeight = mEditTextBody.getHeight();
                if (mCirclePublicCommentContral != null) {
                    mCirclePublicCommentContral.handleListViewScroll();
                }
            }
        });
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);

    }

    private void loadData() {
        List<CircleItem> datas = DatasUtil.createCircleDatas();
        mAdapter.setDatas(datas);
        mAdapter.notifyDataSetChanged();
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public int getEditTextBodyHeight() {
        return mEditTextBodyHeight;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mEditTextBody != null && mEditTextBody.getVisibility() == View.VISIBLE) {
                mEditTextBody.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
