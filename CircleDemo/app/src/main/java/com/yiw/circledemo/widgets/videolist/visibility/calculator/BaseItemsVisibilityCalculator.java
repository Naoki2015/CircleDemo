package com.yiw.circledemo.widgets.videolist.visibility.calculator;

import android.widget.AbsListView;

import com.yiw.circledemo.widgets.videolist.visibility.scroll.ItemsPositionGetter;
import com.yiw.circledemo.widgets.videolist.visibility.scroll.ScrollDirectionDetector;


/**
 * This class encapsulates some basic logic of Visibility calculator.
 * In onScroll event it calculates Scroll direction using {@link com.waynell.videolist.visibility.scroll.ScrollDirectionDetector}
 * and then calls appropriate methods
 *
 * @author Wayne
 */
public abstract class BaseItemsVisibilityCalculator implements ListItemsVisibilityCalculator{

    /** Initial scroll direction should be UP in order to set as active most top item if no active item yet*/
    protected ScrollDirectionDetector.ScrollDirection mScrollDirection = ScrollDirectionDetector.ScrollDirection.UP;

    protected final ItemsPositionGetter mPositionGetter;

    public BaseItemsVisibilityCalculator(ItemsPositionGetter positionGetter) {
        mPositionGetter = positionGetter;
    }

    private final ScrollDirectionDetector mScrollDirectionDetector = new ScrollDirectionDetector(
            new ScrollDirectionDetector.OnDetectScrollListener() {
        @Override
        public void onScrollDirectionChanged(ScrollDirectionDetector.ScrollDirection scrollDirection) {
            mScrollDirection = scrollDirection;
        }
    });

    @Override
    public void onScrolled(int scrollState) {
        int firstVisiblePosition = mPositionGetter.getFirstVisiblePosition();

        mScrollDirectionDetector.onDetectedListScroll(mPositionGetter, firstVisiblePosition);

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                onStateTouchScroll(mPositionGetter);
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                onStateTouchScroll(mPositionGetter);
                break;

            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                onScrollStateIdle();
                break;
        }
    }

    public abstract void onStateLost();

    protected abstract void onStateTouchScroll(ItemsPositionGetter itemsPositionGetter);

}
