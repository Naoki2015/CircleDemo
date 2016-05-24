package com.yiw.circledemo.widgets.videolist.visibility.items;

import android.view.View;

/**
 * @author Wayne
 */
public class ListItemData {

    private Integer mIndexInAdapter;
    private View mView;
    private ListItem mListItem;

    private boolean mIsVisibleItemChanged;

    public int getIndex() {
        return mIndexInAdapter;
    }

    public View getView() {
        return mView;
    }

    public ListItem getListItem() {
        return mListItem;
    }

    public ListItemData fillWithData(int indexInAdapter, View view, ListItem item) {
        mIndexInAdapter = indexInAdapter;
        mView = view;
        mListItem = item;
        return this;
    }

    public boolean isAvailable() {
        return mIndexInAdapter != null && mView != null && mListItem != null;
    }

    public void setVisibleItemChanged(boolean isDataChanged) {
        mIsVisibleItemChanged = isDataChanged;
    }

    public boolean isVisibleItemChanged() {
        return mIsVisibleItemChanged;
    }

    @Override
    public String toString() {
        return "ListItemData{" +
                "mIndexInAdapter=" + mIndexInAdapter +
                ", mView=" + mView +
                ", mListItem=" + mListItem +
                ", mIsVisibleItemChanged=" + mIsVisibleItemChanged +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListItemData that = (ListItemData) o;

        return (mIndexInAdapter != null ? mIndexInAdapter.equals(that.mIndexInAdapter) : that.mIndexInAdapter == null)
                && (mView != null ? mView.equals(that.mView) : that.mView == null);

    }

    @Override
    public int hashCode() {
        int result = mIndexInAdapter != null ? mIndexInAdapter.hashCode() : 0;
        result = 31 * result + (mView != null ? mView.hashCode() : 0);
        return result;
    }
}
