package com.yiw.circledemo.widgets.videolist.visibility.calculator;

import android.util.Log;
import android.view.View;

import com.yiw.circledemo.widgets.videolist.visibility.items.ListItem;
import com.yiw.circledemo.widgets.videolist.visibility.items.ListItemData;
import com.yiw.circledemo.widgets.videolist.visibility.scroll.ItemsPositionGetter;
import com.yiw.circledemo.widgets.videolist.visibility.scroll.ItemsProvider;


/**
 * A utility that tracks current {@link ListItem} visibility.
 * Current ListItem is an item defined by calling {@link #setCurrentItem(ListItemData)}.
 * Or it might be mock current item created in method {@link #getMockCurrentItem}
 *
 * The logic is following: when current view is going out of screen (up or down) by {@link #INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS} or more then neighbour item become "active" by calling {@link Callback#activateNewCurrentItem}
 * "Going out of screen" is calculated when {@link #onStateTouchScroll} is called from super class {@link BaseItemsVisibilityCalculator}
 *
 * Method {@link ListItemsVisibilityCalculator#onScrollStateIdle} should be called only when scroll state become idle. // TODO: test it
 * When it's called we look for new current item that eventually will be set as "active" by calling {@link #setCurrentItem(ListItemData)}
 * Regarding the {@link #mScrollDirection} new current item is calculated from top to bottom (if DOWN) or from bottom to top (if UP).
 * The first(or last) visible item is set to current. It's visibility percentage is calculated. Then we are going though all visible items and find the one that is the most visible.
 *
 * Method {@link #onStateLost} is calling {@link Callback#deactivateCurrentItem}
 *
 * @author Wayne
 */
public class SingleListViewItemActiveCalculator extends BaseItemsVisibilityCalculator {

    private static final String TAG = "ListViewItemActiveCal";
    private static final boolean SHOW_LOGS = true;

    private static final int INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS = 70;

    private final Callback<ListItem> mCallback;
    private final ItemsProvider mItemsProvider;

    /**
     * The data of this member will be changing all the time
     */
    private final ListItemData mCurrentItem = new ListItemData();

    private final ListItemData mPreActiveItem = new ListItemData();

    public SingleListViewItemActiveCalculator(ItemsProvider itemsProvider, ItemsPositionGetter itemsPositionGetter) {
        this(new DefaultSingleItemCalculatorCallback(), itemsProvider, itemsPositionGetter);
    }

    public SingleListViewItemActiveCalculator(Callback<ListItem> callback, ItemsProvider itemsProvider,
                                              ItemsPositionGetter itemsPositionGetter) {
        super(itemsPositionGetter);
        mCallback = callback;
        mItemsProvider = itemsProvider;
    }

    /**
     * Methods of this callback will be called when new active item is found {@link Callback#activateNewCurrentItem(ListItem, View, int)}
     * or when there is no active item {@link Callback#deactivateCurrentItem(ListItem, View, int)} - this might happen when user scrolls really fast
     */
    public interface Callback<T extends ListItem>{
        void activateNewCurrentItem(T newListItem, View currentView, int position);
        void deactivateCurrentItem(T listItemToDeactivate, View view, int position);
    }

    public ListItemData getCurrentItem() {
        return mCurrentItem;
    }

    /**
     * When Scrolling list is in this state we start calculating Active Item.
     * Here we assume that scroll state was idle previously and {@link #mCurrentItem} already contains some data
     *
     * @param itemsPositionGetter
     */
    @Override
    protected void onStateTouchScroll(ItemsPositionGetter itemsPositionGetter) {
        ListItemData listItemData = mCurrentItem;

        calculateActiveItem(itemsPositionGetter, listItemData);

        if(mCurrentItem.isVisibleItemChanged() && !mPreActiveItem.equals(mCurrentItem)) {
            if(SHOW_LOGS) {
                Log.d(TAG, "onStateTouchScroll " + mCurrentItem + " " + VisibilityPercentsCalculator.getVisibilityPercents(mCurrentItem.getView())
                        + " " + mPreActiveItem + " " + VisibilityPercentsCalculator.getVisibilityPercents(mPreActiveItem.getView()));
            }
            mCallback.deactivateCurrentItem(mPreActiveItem.getListItem(),
                    mPreActiveItem.getView(),
                    mPreActiveItem.getIndex());
            activeCurrentItem(mCurrentItem);
        }
    }

    /**
     * This method calculates visibility of next item.
     * There are some cases when next item cannot be filled with data:
     *
     * 1. When current data is last item in the list. In this case there is no next data
     * 2. Index of current view cannot be calculated because view was already recycled
     *
     * @param itemsPositionGetter
     * @param currentIem - the item that is active right now
     * @param outNextItemData - out parameter. It will be filled with next item data if the one exists
     */
    private void findNextItem(ItemsPositionGetter itemsPositionGetter, ListItemData currentIem, ListItemData outNextItemData) {
        int nextItemIndex = currentIem.getIndex() + 1;

        if(nextItemIndex < mItemsProvider.listItemSize()){
            int indexOfCurrentView = itemsPositionGetter.indexOfChild(currentIem.getView());

            if(indexOfCurrentView >= 0){
                View nextView = itemsPositionGetter.getChildAt(indexOfCurrentView + 1);
                if(nextView != null){
                    ListItem next = mItemsProvider.getListItem(nextItemIndex);
                    if (next == null && SHOW_LOGS) {
                        Log.e(TAG, "null list item");
                    }

                    outNextItemData.fillWithData(nextItemIndex, nextView, next);
                }
            }
        }
    }

    /**
     * This method calculates visibility of previous item.
     * There are some cases when previous item cannot be filled with data:
     *
     * 1. When current data is first item in the list. in this case there is no previous data
     * 2. Index of current view cannot be calculated because view was already recycled
     *
     * @param itemsPositionGetter
     * @param currentIem - the item that is active right now
     * @param outPreviousItemData - out parameter. It will be filled with previous item data if the one exists
     */
    private void findPreviousItem(ItemsPositionGetter itemsPositionGetter, ListItemData currentIem, ListItemData outPreviousItemData) {
        int previousItemIndex = currentIem.getIndex() -1;

        if(previousItemIndex >= 0){
            int indexOfCurrentView = itemsPositionGetter.indexOfChild(currentIem.getView());

            if(indexOfCurrentView > 0){
                View previousView = itemsPositionGetter.getChildAt(indexOfCurrentView - 1);
                ListItem previous = mItemsProvider.getListItem(previousItemIndex);
                if (previous == null && SHOW_LOGS) {
                    Log.e(TAG, "null list item");
                }

                outPreviousItemData.fillWithData(previousItemIndex, previousView, previous);
            }
        }
    }

    @Override
    public void onScrollStateIdle() {

        int firstVisiblePosition = mPositionGetter.getFirstVisiblePosition();
        int lastVisiblePosition = mPositionGetter.getLastVisiblePosition();

        calculateMostVisibleItem(mPositionGetter, firstVisiblePosition, lastVisiblePosition);

        if (mCurrentItem.isVisibleItemChanged() && !mPreActiveItem.equals(mCurrentItem)) {
            if(SHOW_LOGS) {
                Log.d(TAG, "onScrollStateIdle " + mCurrentItem + " " + VisibilityPercentsCalculator.getVisibilityPercents(mCurrentItem.getView())
                        + " " + mPreActiveItem + " " + VisibilityPercentsCalculator.getVisibilityPercents(mPreActiveItem.getView()));
            }
            if (mPreActiveItem.isAvailable()) {
                mCallback.deactivateCurrentItem(mPreActiveItem.getListItem(),
                        mPreActiveItem.getView(),
                        mPreActiveItem.getIndex());
            }
            activeCurrentItem(mCurrentItem);
        }
    }

    /**
     * This method calculates most visible item from top to bottom or from bottom to top depends on scroll direction.
     *
     * @param itemsPositionGetter
     * @param firstVisiblePosition
     * @param lastVisiblePosition
     */
    private void calculateMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {

        ListItemData mostVisibleItem = getMockCurrentItem(itemsPositionGetter, firstVisiblePosition, lastVisiblePosition);
        int maxVisibilityPercents = VisibilityPercentsCalculator.getVisibilityPercents(mostVisibleItem.getView());

        switch (mScrollDirection){
            case UP:
                bottomToTopMostVisibleItem(itemsPositionGetter, maxVisibilityPercents, mostVisibleItem);
                break;
            case DOWN:
                topToBottomMostVisibleItem(itemsPositionGetter, maxVisibilityPercents, mostVisibleItem);
                break;
            default:
                throw new RuntimeException("not handled mScrollDirection " + mScrollDirection);
        }

        if(mostVisibleItem.isVisibleItemChanged()){
            setCurrentItem(mostVisibleItem);
        }
    }

    private void topToBottomMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int maxVisibilityPercents, ListItemData outMostVisibleItem) {
        int mostVisibleItemVisibilityPercents = maxVisibilityPercents;

        int currentItemVisibilityPercents;

        for(int indexOfCurrentItem = itemsPositionGetter.getFirstVisiblePosition(), indexOfCurrentView = itemsPositionGetter.indexOfChild(outMostVisibleItem.getView())
                ; indexOfCurrentView < itemsPositionGetter.getChildCount() // iterating via listView Items
                ; indexOfCurrentItem++, indexOfCurrentView++){

            ListItem listItem = mItemsProvider.getListItem(indexOfCurrentItem);
            View currentView = itemsPositionGetter.getChildAt(indexOfCurrentView);
            currentItemVisibilityPercents = VisibilityPercentsCalculator.getVisibilityPercents(currentView);

            if(currentItemVisibilityPercents > mostVisibleItemVisibilityPercents
                    && currentItemVisibilityPercents > INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS){

                mostVisibleItemVisibilityPercents = currentItemVisibilityPercents;
                outMostVisibleItem.fillWithData(indexOfCurrentItem, currentView, listItem);

            }
        }

        boolean itemChanged = !mCurrentItem.equals(outMostVisibleItem);

        outMostVisibleItem.setVisibleItemChanged(itemChanged);
    }

    private void bottomToTopMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int maxVisibilityPercents, ListItemData outMostVisibleItem) {
        int mostVisibleItemVisibilityPercents = maxVisibilityPercents;

        int currentItemVisibilityPercents;
        for(int indexOfCurrentItem = itemsPositionGetter.getLastVisiblePosition(), indexOfCurrentView = itemsPositionGetter.indexOfChild(outMostVisibleItem.getView())
                ; indexOfCurrentView >= 0 // iterating via listView Items
                ; indexOfCurrentItem--, indexOfCurrentView--){
            if(indexOfCurrentItem<0){
                break;
            }
            ListItem listItem = mItemsProvider.getListItem(indexOfCurrentItem);
            View currentView = itemsPositionGetter.getChildAt(indexOfCurrentView);
            currentItemVisibilityPercents = VisibilityPercentsCalculator.getVisibilityPercents(currentView);

            if(currentItemVisibilityPercents > mostVisibleItemVisibilityPercents &&
                    currentItemVisibilityPercents > INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS){
                mostVisibleItemVisibilityPercents = currentItemVisibilityPercents;
                outMostVisibleItem.fillWithData(indexOfCurrentItem, currentView, listItem);
            }
        }
        boolean itemChanged = !mCurrentItem.equals(outMostVisibleItem);
        outMostVisibleItem.setVisibleItemChanged(itemChanged);
    }

    /**
     * ListItemData at lastVisiblePosition if user scrolled UP and ListItemData at firstVisiblePosition if user scrolled DOWN
     */
    private ListItemData getMockCurrentItem(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {
        ListItemData mockCurrentItemData;
        switch (mScrollDirection){
            case UP:
                int lastVisibleItemIndex;
                if(lastVisiblePosition < 0/*-1 may be returned from ListView*/){
                    lastVisibleItemIndex = firstVisiblePosition;
                } else {
                    lastVisibleItemIndex = lastVisiblePosition;
                }

                mockCurrentItemData = new ListItemData().fillWithData(lastVisibleItemIndex,
                        itemsPositionGetter.getChildAt(itemsPositionGetter.getChildCount() - 1),
                        mItemsProvider.getListItem(lastVisiblePosition));
                break;
            case DOWN:
                mockCurrentItemData = new ListItemData().fillWithData(firstVisiblePosition,
                        itemsPositionGetter.getChildAt(itemsPositionGetter.getChildCount()
                                - (lastVisiblePosition - firstVisiblePosition + 1)),
                        mItemsProvider.getListItem(firstVisiblePosition));
                break;
            default:
                throw new RuntimeException("not handled mScrollDirection " + mScrollDirection);
        }
        return mockCurrentItemData;
    }

    /**
     * 1. This method get current item visibility percents.
     *
     * 2. Then get scroll direction and depending on it either call {@link #findNextItem(ItemsPositionGetter, ListItemData, ListItemData)}
     * or {@link #findPreviousItem(ItemsPositionGetter, ListItemData, ListItemData)}
     *
     * 3. Then it checks if current item visibility percents is enough for deactivating it.
     * If it's enough it checks if new active item was found.
     *
     * 4. If all conditions match it calls {@link #setCurrentItem(ListItemData)}
     * @param itemsPositionGetter
     * @param listItemData
     */
    private void calculateActiveItem(ItemsPositionGetter itemsPositionGetter, ListItemData listItemData) {
        /** 1. */
        int currentItemVisibilityPercents = VisibilityPercentsCalculator.getVisibilityPercents(listItemData.getView());

        /** 2. */
        ListItemData neighbourItemData = new ListItemData();
        switch (mScrollDirection){
            case UP:
                findPreviousItem(itemsPositionGetter, listItemData, neighbourItemData);
                break;
            case DOWN:
                findNextItem(itemsPositionGetter, listItemData, neighbourItemData);
                break;
        }

        /** 3. */
        int nextItemVisibilityPercents = VisibilityPercentsCalculator.getVisibilityPercents(neighbourItemData.getView());
        if(enoughPercentsForDeactivation(currentItemVisibilityPercents, nextItemVisibilityPercents)
                && neighbourItemData.isAvailable()){

            // neighbour item become active (current)
            /** 4. */
            setCurrentItem(neighbourItemData);
        }
    }

    private boolean enoughPercentsForDeactivation(int visibilityPercents, int nextVisibilityPercents) {
        return visibilityPercents < INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS &&
                nextVisibilityPercents >= INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS;
    }

    @Override
    public void onStateLost() {
        if(mCurrentItem.isAvailable()) {
            mCallback.deactivateCurrentItem(mCurrentItem.getListItem(), mCurrentItem.getView(), mCurrentItem.getIndex());
        }
        mCurrentItem.fillWithData(0, null, null);
        mPreActiveItem.fillWithData(0, null, null);
    }

    private void setCurrentItem(ListItemData newCurrentItem) {
        int itemPosition = newCurrentItem.getIndex();
        View view = newCurrentItem.getView();
        ListItem item = newCurrentItem.getListItem();

        mCurrentItem.fillWithData(itemPosition, view, item);
        mCurrentItem.setVisibleItemChanged(true);
    }

    private void activeCurrentItem(ListItemData newCurrentItem) {
        int itemPosition = newCurrentItem.getIndex();
        View view = newCurrentItem.getView();
        ListItem item = newCurrentItem.getListItem();

        mPreActiveItem.fillWithData(newCurrentItem.getIndex(), newCurrentItem.getView(), item);

        mCallback.activateNewCurrentItem(item, view, itemPosition);

        newCurrentItem.setVisibleItemChanged(false);
    }
}
