package com.yiw.circledemo.mvp.presenter;

import com.yiw.circledemo.bean.User;
import com.yiw.circledemo.mvp.modle.CircleModel;
import com.yiw.circledemo.mvp.modle.IDataRequestListener;
import com.yiw.circledemo.mvp.view.ICircleViewUpdate;
/**
 * 
* @ClassName: CirclePresenter 
* @Description: 通知model请求服务器和通知view更新
* @author yiw
* @date 2015-12-28 下午4:06:03 
*
 */
public class CirclePresenter {
	private CircleModel mCircleModel;
	private ICircleViewUpdate mCircleView;
	
	public CirclePresenter(ICircleViewUpdate view){
		this.mCircleView = view;
		mCircleModel = new CircleModel();
	}
	/**
	 * 
	* @Title: deleteCircle 
	* @Description: 删除动态 
	* @param  circleId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteCircle(final String circleId){
		mCircleModel.deleteCircle(new IDataRequestListener() {
			
			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2DeleteCircle(circleId);
			}
		});
	}
	/**
	 * 
	* @Title: addFavort 
	* @Description: 点赞
	* @param  circlePosition     
	* @return void    返回类型 
	* @throws
	 */
	public void addFavort(final int circlePosition){
		mCircleModel.addFavort(new IDataRequestListener() {
			
			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2AddFavorite(circlePosition);
			}
		});
	}
	/**
	 * 
	* @Title: deleteFavort 
	* @Description: 取消点赞 
	* @param @param circlePosition
	* @param @param favortId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteFavort(final int circlePosition, final String favortId){
		mCircleModel.deleteFavort(new IDataRequestListener() {
				
				@Override
				public void loadSuccess(Object object) {
					mCircleView.update2DeleteFavort(circlePosition, favortId);
				}
			});
	}
	
	/**
	 * 
	* @Title: addComment 
	* @Description: 增加评论
	* @param  circlePosition
	* @param  type  0：发布评论  1：回复评论
	* @param  replyUser  回复评论时对谁的回复   
	* @return void    返回类型 
	* @throws
	 */
	public void addComment(final int circlePosition, final int type, final User replyUser){
		mCircleModel.addComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2AddComment(circlePosition, type, replyUser);
			}
			
		});
	}
	
	/**
	 * 
	* @Title: deleteComment 
	* @Description: 删除评论 
	* @param @param circlePosition
	* @param @param commentId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteComment(final int circlePosition, final String commentId){
		mCircleModel.addComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2DeleteComment(circlePosition, commentId);
			}
			
		});
	}
}
