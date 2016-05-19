package com.yiw.circledemo.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * 类描述：
 * 创建人：Bob
 * 创建时间：2015/9/25 11:36
 */
public class TitleBar extends ViewGroup implements View.OnClickListener {
    private static final int DEFAULT_MAIN_TEXT_SIZE = 18;
    private static final int DEFAULT_SUB_TEXT_SIZE = 12;
    private static final int DEFAULT_ACTION_TEXT_SIZE = 15;
    private static final int DEFAULT_TITLE_BAR_HEIGHT = 48;

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private TextView mLeftText;
    private LinearLayout mLeftLayout;
    private LinearLayout mRightLayout;
    private LinearLayout mCenterLayout;
    private TextView mCenterText;
    private TextView mSubTitleText;
    private View mCustomCenterView;
    private View mDividerView;

    private boolean mImmersive;

    private int mScreenWidth;
    private int mStatusBarHeight;
    private int mActionPadding;
    private int mOutPadding;
    private int mActionTextColor;
    private int mHeight;

    public TitleBar(Context context) {
        super(context);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        }
        mActionPadding = dip2px(5);
        mOutPadding = dip2px(10);
        mHeight = dip2px(DEFAULT_TITLE_BAR_HEIGHT);
        initView(context);
    }

    private void initView(Context context) {
        mLeftLayout = new LinearLayout(context);
        mCenterLayout = new LinearLayout(context);
        mRightLayout = new LinearLayout(context);
        mDividerView = new View(context);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mLeftText = new TextView(context);
        mLeftText.setPadding(0, 0, mActionPadding + dip2px(10), 0);
        mLeftLayout.addView(mLeftText);
        mLeftLayout.setGravity(Gravity.CENTER);
        mLeftText.setTextSize(DEFAULT_ACTION_TEXT_SIZE);
        mLeftText.setSingleLine();
        mLeftText.setGravity(Gravity.CENTER_VERTICAL);
        mLeftLayout.setPadding(mOutPadding, 0, mOutPadding, 0);


        mCenterText = new TextView(context);
        mSubTitleText = new TextView(context);
        mCenterLayout.addView(mCenterText);
        mCenterLayout.addView(mSubTitleText);

        mCenterLayout.setGravity(Gravity.CENTER);
        mCenterText.setTextSize(DEFAULT_MAIN_TEXT_SIZE);
        mCenterText.setSingleLine();
        mCenterText.setGravity(Gravity.CENTER);
        mCenterText.setEllipsize(TextUtils.TruncateAt.END);

        mSubTitleText.setTextSize(DEFAULT_SUB_TEXT_SIZE);
        mSubTitleText.setSingleLine();
        mSubTitleText.setGravity(Gravity.CENTER);
        mSubTitleText.setEllipsize(TextUtils.TruncateAt.END);

        mRightLayout.setPadding(mOutPadding, 0, mOutPadding, 0);

        addView(mLeftLayout, layoutParams);
        addView(mCenterLayout);
        addView(mRightLayout, layoutParams);
        addView(mDividerView, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
    }

    public void setImmersive(boolean immersive) {
        mImmersive = immersive;
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        } else {
            mStatusBarHeight = 0;
        }
    }

    public void setHeight(int height) {
        mHeight = height;
        setMeasuredDimension(getMeasuredWidth(), mHeight);
    }

    public void setLeftImageResource(int resId) {
        mLeftText.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
    }

    public void setLeftClickListener(OnClickListener l) {
        mLeftLayout.setOnClickListener(l);
    }

    public void setLeftText(CharSequence title) {
        mLeftText.setText(title);
    }

    public void setLeftText(int resid) {
        mLeftText.setText(resid);
    }

    public void setLeftTextSize(float size) {
        mLeftText.setTextSize(size);
    }

    public void setLeftTextColor(int color) {
        mLeftText.setTextColor(color);
    }

    public void setLeftVisible(boolean visible) {
        mLeftText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setTitle(CharSequence title) {
        int index = title.toString().indexOf("\n");
        if (index > 0) {
            setTitle(title.subSequence(0, index), title.subSequence(index + 1, title.length()), LinearLayout.VERTICAL);
        } else {
            index = title.toString().indexOf("\t");
            if (index > 0) {
                setTitle(title.subSequence(0, index), "  " + title.subSequence(index + 1, title.length()), LinearLayout.HORIZONTAL);
            } else {
                mCenterText.setText(title);
                mSubTitleText.setVisibility(View.GONE);
            }
        }
    }

    private void setTitle(CharSequence title, CharSequence subTitle, int orientation) {
        mCenterLayout.setOrientation(orientation);
        mCenterText.setText(title);

        mSubTitleText.setText(subTitle);
        mSubTitleText.setVisibility(View.VISIBLE);
    }

    public void setCenterClickListener(OnClickListener l) {
        mCenterLayout.setOnClickListener(l);
    }

    public void setTitle(int resid) {
        setTitle(getResources().getString(resid));
    }

    public void setTitleColor(int resid) {
        mCenterText.setTextColor(resid);
    }

    public void setTitleSize(float size) {
        mCenterText.setTextSize(size);
    }

    public void setTitleBackground(int resid) {
        mCenterText.setBackgroundResource(resid);
    }

    public void setSubTitleColor(int resid) {
        mSubTitleText.setTextColor(resid);
    }

    public void setSubTitleSize(float size) {
        mSubTitleText.setTextSize(size);
    }

    public void setCustomTitle(View titleView) {
        if (titleView == null) {
            mCenterText.setVisibility(View.VISIBLE);
            if (mCustomCenterView != null) {
                mCenterLayout.removeView(mCustomCenterView);
            }

        } else {
            if (mCustomCenterView != null) {
                mCenterLayout.removeView(mCustomCenterView);
            }
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mCustomCenterView = titleView;
            mCenterLayout.addView(titleView, layoutParams);
            mCenterText.setVisibility(View.GONE);
        }
    }

    public void setDivider(Drawable drawable) {
        mDividerView.setBackgroundDrawable(drawable);
    }

    public void setDividerColor(int color) {
        mDividerView.setBackgroundColor(color);
    }

    public void setDividerHeight(int dividerHeight) {
        mDividerView.getLayoutParams().height = dividerHeight;
    }

    public void setActionTextColor(int colorResId) {
        mActionTextColor = colorResId;
    }

    /**
     * Function to set a click listener for Title TextView
     *
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mCenterText.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     * @param action the action to add
     */
    public View addAction(Action action) {
        final int index = mRightLayout.getChildCount();
        return addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * @param action the action to add
     * @param index the position at which to add the action
     */
    public View addAction(Action action, int index) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        View view = inflateAction(action);
        mRightLayout.addView(view, index, params);
        return view;
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mRightLayout.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mRightLayout.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mRightLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mRightLayout.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mRightLayout.removeView(view);
                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     * @return action count
     */
    public int getActionCount() {
        return mRightLayout.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = null;
        if (TextUtils.isEmpty(action.getText())) {
            ImageView img = new ImageView(getContext());
            img.setImageResource(action.getDrawable());
            view = img;
        } else {
            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            text.setText(action.getText());
            text.setTextSize(DEFAULT_ACTION_TEXT_SIZE);
            if (mActionTextColor != 0) {
                text.setTextColor(mActionTextColor);
            }
            view = text;
        }

        view.setPadding(mActionPadding, 0, mActionPadding, 0);
        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    public TextView getLeftText() {
        return mLeftText;
    }

    public TextView getTitleView(){
        return mCenterText;
    }

    public View getViewByAction(Action action) {
        View view = findViewWithTag(action);
        return view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height;
        if (heightMode != MeasureSpec.EXACTLY) {
            height = mHeight + mStatusBarHeight;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec) + mStatusBarHeight;
        }

        measureChild(mLeftLayout, widthMeasureSpec, heightMeasureSpec);
        measureChild(mRightLayout, widthMeasureSpec, heightMeasureSpec);
        if (mLeftLayout.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
            mCenterLayout.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mLeftLayout.getMeasuredWidth(), MeasureSpec.EXACTLY)
                    , heightMeasureSpec);
        } else {
            mCenterLayout.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mRightLayout.getMeasuredWidth(), MeasureSpec.EXACTLY)
                    , heightMeasureSpec);
        }
        measureChild(mDividerView, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mLeftLayout.layout(0, mStatusBarHeight, mLeftLayout.getMeasuredWidth(), mLeftLayout.getMeasuredHeight() + mStatusBarHeight);
        mRightLayout.layout(mScreenWidth - mRightLayout.getMeasuredWidth(), mStatusBarHeight,
                mScreenWidth, mRightLayout.getMeasuredHeight() + mStatusBarHeight);
        if (mLeftLayout.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
            mCenterLayout.layout(mLeftLayout.getMeasuredWidth(), mStatusBarHeight,
                    mScreenWidth - mLeftLayout.getMeasuredWidth(), getMeasuredHeight());
        } else {
            mCenterLayout.layout(mRightLayout.getMeasuredWidth(), mStatusBarHeight,
                    mScreenWidth - mRightLayout.getMeasuredWidth(), getMeasuredHeight());
        }
        mDividerView.layout(0, getMeasuredHeight() - mDividerView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
    }

    public static int dip2px(int dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 计算状态栏高度高度
     * getStatusBarHeight
     * @return
     */
    public static int getStatusBarHeight() {
        return getInternalDimensionSize(Resources.getSystem(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    @SuppressWarnings("serial")
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        String getText();
        int getDrawable();
        void performAction(View view);
    }

    public static abstract class ImageAction implements Action {
        private int mDrawable;

        public ImageAction(int drawable) {
            mDrawable = drawable;
        }

        @Override
        public int getDrawable() {
            return mDrawable;
        }

        @Override
        public String getText() {
            return null;
        }
    }

    public static abstract class TextAction implements Action {
        final private String mText;

        public TextAction(String text) {
            mText = text;
        }

        @Override
        public int getDrawable() {
            return 0;
        }

        @Override
        public String getText() {
            return mText;
        }
    }

}
