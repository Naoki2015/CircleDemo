package com.yiw.circledemo.widgets.videolist.visibility.scroll;

import android.view.View;
import android.widget.ListView;

/**
 * This class is an API for {@link com.waynell.videolist.visibility.calculator.ListItemsVisibilityCalculator}
 * Using this class is can access all the data from ListView
 *
 * @author Wayne
 */
public class ListViewItemPositionGetter implements ItemsPositionGetter {

    private final ListView mListView;

    public ListViewItemPositionGetter(ListView listView) {
        mListView = listView;
    }

    @Override
    public View getChildAt(int position) {
        return mListView.getChildAt(position);
    }

    @Override
    public int indexOfChild(View view) {
        return mListView.indexOfChild(view);
    }

    @Override
    public int getChildCount() {
        return mListView.getChildCount();
    }

    @Override
    public int getLastVisiblePosition() {
        return mListView.getLastVisiblePosition();
    }

    @Override
    public int getFirstVisiblePosition() {
        return mListView.getFirstVisiblePosition();
    }
}
