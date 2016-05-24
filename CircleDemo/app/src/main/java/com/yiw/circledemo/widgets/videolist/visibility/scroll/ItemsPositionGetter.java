package com.yiw.circledemo.widgets.videolist.visibility.scroll;

import android.view.View;

/**
 * This class is an API for {@link com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator}
 * Using this class is can access all the data from RecyclerView / ListView
 *
 * There is two different implementations for ListView and for RecyclerView.
 * RecyclerView introduced LayoutManager that's why some of data moved there
 *
 * @author Wayne
 */
public interface ItemsPositionGetter {
    View getChildAt(int position);

    int indexOfChild(View view);

    int getChildCount();

    int getLastVisiblePosition();

    int getFirstVisiblePosition();
}
