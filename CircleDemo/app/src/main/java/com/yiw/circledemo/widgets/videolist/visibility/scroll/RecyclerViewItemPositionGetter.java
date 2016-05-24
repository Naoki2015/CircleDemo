package com.yiw.circledemo.widgets.videolist.visibility.scroll;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * This class is an API for {@link com.waynell.videolist.visibility.calculator.ListItemsVisibilityCalculator}
 * Using this class is can access all the data from RecyclerView
 *
 * @author Wayne
 */
public class RecyclerViewItemPositionGetter implements ItemsPositionGetter {

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    public RecyclerViewItemPositionGetter(LinearLayoutManager layoutManager, RecyclerView recyclerView) {
        mLayoutManager = layoutManager;
        mRecyclerView = recyclerView;
    }

    @Override
    public View getChildAt(int position) {
        return mLayoutManager.getChildAt(position);
    }

    @Override
    public int indexOfChild(View view) {
        return mRecyclerView.indexOfChild(view);
    }

    @Override
    public int getChildCount() {
        return mRecyclerView.getChildCount();
    }

    @Override
    public int getLastVisiblePosition() {
        return mLayoutManager.findLastVisibleItemPosition();
    }

    @Override
    public int getFirstVisiblePosition() {
        return mLayoutManager.findFirstVisibleItemPosition();
    }
}
