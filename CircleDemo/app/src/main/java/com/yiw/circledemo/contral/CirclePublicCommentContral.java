package com.yiw.circledemo.contral;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.yiw.circledemo.MainActivity;
import com.yiw.circledemo.MyApplication;
import com.yiw.circledemo.R;
import com.yiw.circledemo.bean.User;
import com.yiw.circledemo.mvp.presenter.CirclePresenter;
import com.yiw.circledemo.mvp.view.ICircleViewUpdate;
import com.yiw.circledemo.utils.CommonUtils;
import com.yiw.circledemo.widgets.AppNoScrollerListView;
/**
 * 
* @ClassName: CirclePublicCommentContral 
* @Description: 控制EdittextView的显示和隐藏，以及发布动作，根据回复的位置调整listview的位置
* @author yiw
* @date 2015-12-28 下午3:45:21 
*
 */
public class CirclePublicCommentContral {
	private static final String TAG = CirclePublicCommentContral.class.getSimpleName();
	private View mEditTextBody;
	private EditText mEditText;
	private View mSendBt;
	private CirclePresenter mCirclePresenter;
	private int mCirclePosition;
	private int mCommentType;
	private User mReplyUser;
	private int mCommentPosition;
	private ListView mListView;
	private Context mContext;
	/**
	 * 选择动态条目的高
	 */
	private int mSelectCircleItemH;
	/**
	 * 选择的commentItem距选择的CircleItem底部的距离
	 */
	private int mSelectCommentItemBottom;
	
	public ListView getmListView() {
		return mListView;
	}

	public void setmListView(ListView mListView) {
		this.mListView = mListView;
	}

	public CirclePublicCommentContral(Context context, View editTextBody, EditText editText, View sendBt){
		mContext = context;
		mEditTextBody = editTextBody;
		mEditText = editText;
		mSendBt = sendBt;
		mSendBt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mCirclePresenter!=null){
					//发布评论
					mCirclePresenter.addComment(mCirclePosition, mCommentType, mReplyUser);
				}
				editTextBodyVisible(View.GONE);
			}
		});
	}
	/**
	 * 
	* @Title: editTextBodyVisible 
	* @Description: 评论时显示发布布局，评论完隐藏，根据不同位置调节listview的滑动
	* @param  visibility
	* @param  mCirclePresenter
	* @param  mCirclePosition
	* @param  commentType  0:发布评论   1：回复评论
	* @param  replyUser    
	* @return void    返回类型 
	* @throws
	 */
	public void editTextBodyVisible(int visibility, CirclePresenter mCirclePresenter, int mCirclePosition, int commentType, User replyUser, int commentPosition) {
		this.mCirclePosition = mCirclePosition;
		this.mCirclePresenter = mCirclePresenter;
		this.mCommentType = commentType;
		this.mReplyUser = replyUser;
		this.mCommentPosition = commentPosition;
		editTextBodyVisible(visibility);

		measure(mCirclePosition, commentType);
	}

	private void measure(int mCirclePosition, int commentType) {
		if(mListView != null){
			int firstPosition = mListView.getFirstVisiblePosition();
			View selectCircleItem = mListView.getChildAt(mCirclePosition-firstPosition);
			mSelectCircleItemH = selectCircleItem.getHeight();

			if(commentType == ICircleViewUpdate.TYPE_REPLY_COMMENT){//回复评论的情况
				AppNoScrollerListView commentLv = (AppNoScrollerListView) selectCircleItem.findViewById(R.id.commentList);
				if(commentLv!=null){
					int firstCommentPosition = commentLv.getFirstVisiblePosition();
					//找到要回复的评论view,计算出该view距离所属动态底部的距离
					View selectCommentItem = commentLv.getChildAt(mCommentPosition - firstCommentPosition);
					if(selectCommentItem!=null){
						mSelectCommentItemBottom = 0;
						View parentView = selectCommentItem;
						do {
							int subItemBottom = parentView.getBottom();
							parentView = (View) parentView.getParent();
							if(parentView != null){
								mSelectCommentItemBottom += (parentView.getHeight() - subItemBottom);
							}
						} while (parentView != null && parentView != selectCircleItem);
					}
				}
			}
		}
	}

	public void handleListViewScroll() {
		int keyH = MyApplication.mKeyBoardH;//键盘的高度
		int editTextBodyH = ((MainActivity)mContext).getEditTextBodyHeight();//整个EditTextBody的高度
		int screenlH = ((MainActivity)mContext).getScreenHeight();//整个应用屏幕的高度
		int listviewOffset = screenlH - mSelectCircleItemH - keyH - editTextBodyH;
		Log.d(TAG, "offset="+listviewOffset + " &mSelectCircleItemH="+mSelectCircleItemH + " &keyH="+keyH + " &editTextBodyH="+editTextBodyH);
		if(mCommentType == ICircleViewUpdate.TYPE_REPLY_COMMENT){
			listviewOffset = listviewOffset + mSelectCommentItemBottom;
		}
		if(mListView!=null){
			mListView.setSelectionFromTop(mCirclePosition, listviewOffset);
		}

	}
	
	public void editTextBodyVisible(int visibility) {
		if(mEditTextBody!=null){
			mEditTextBody.setVisibility(visibility);
			if(View.VISIBLE==visibility){
				mEditText.requestFocus();
				//弹出键盘
				CommonUtils.showSoftInput(mEditText.getContext(), mEditText);
				
			}else if(View.GONE==visibility){
				//隐藏键盘
				CommonUtils.hideSoftInput(mEditText.getContext(), mEditText);
			}
		}
	}

	public String getEditTextString() {
		String result = "";
		if(mEditText!=null){
			result =  mEditText.getText().toString();
		}
		return result;
	}
	
	public void clearEditText(){
		if(mEditText!=null){
			mEditText.setText("");
		}
	}

}
