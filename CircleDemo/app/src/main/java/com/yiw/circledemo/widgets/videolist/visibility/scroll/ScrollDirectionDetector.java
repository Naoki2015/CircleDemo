package com.yiw.circledemo.widgets.videolist.visibility.scroll;

import android.view.View;


/**
 * This class detects a {@link ScrollDirection} ListView is scrolled to.
 * And then call {@link OnDetectScrollListener#onScrollDirectionChanged(ScrollDirection)}
 *
 * @author Wayne
 */
public class ScrollDirectionDetector {

    private final OnDetectScrollListener mOnDetectScrollListener;

    private int mOldTop;
    private int mOldFirstVisibleItem;

    private ScrollDirection mOldScrollDirection = null;

    public ScrollDirectionDetector(OnDetectScrollListener onDetectScrollListener) {
        mOnDetectScrollListener = onDetectScrollListener;
    }

    public interface OnDetectScrollListener {
        void onScrollDirectionChanged(ScrollDirection scrollDirection);
    }

    public enum ScrollDirection {
        UP, DOWN
    }

    public void onDetectedListScroll(ItemsPositionGetter itemsPositionGetter, int firstVisibleItem) {

        View view = itemsPositionGetter.getChildAt(0);
        int top = (view == null) ? 0 : view.getTop();

        if (firstVisibleItem == mOldFirstVisibleItem) {
            if (top > mOldTop) {
                onScrollUp();
            } else if (top < mOldTop) {
                onScrollDown();
            }
        } else {
            if (firstVisibleItem < mOldFirstVisibleItem) {
                onScrollUp();
            } else {
                onScrollDown();
            }
        }

        mOldTop = top;
        mOldFirstVisibleItem = firstVisibleItem;
    }

    private void onScrollDown() {
        if(mOldScrollDirection != ScrollDirection.DOWN){
            mOldScrollDirection = ScrollDirection.DOWN;
            mOnDetectScrollListener.onScrollDirectionChanged(ScrollDirection.DOWN);
        }
    }

    private void onScrollUp() {
        if(mOldScrollDirection != ScrollDirection.UP) {
            mOldScrollDirection = ScrollDirection.UP;
            mOnDetectScrollListener.onScrollDirectionChanged(ScrollDirection.UP);
        }
    }
}
