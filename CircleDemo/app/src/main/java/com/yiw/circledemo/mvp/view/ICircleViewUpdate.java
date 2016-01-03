package com.yiw.circledemo.mvp.view;

import com.yiw.circledemo.bean.User;
/**
 * 
* @ClassName: ICircleViewUpdateListener 
* @Description: view,服务器响应后更新界面 
* @author yiw
* @date 2015-12-28 下午4:13:04 
*
 */
public interface ICircleViewUpdate {
	/**
	 * 发布评论
	 */
	public static final int TYPE_PUBLIC_COMMENT = 0;
	/**
	 * 回复评论
	 */
	public static final int TYPE_REPLY_COMMENT = 1;
	
	public void update2DeleteCircle(String circleId);
	public void update2AddFavorite(int circlePosition);
	public void update2DeleteFavort(int circlePosition, String favortId);
	public void update2AddComment(int circlePosition, int type, User replyUser);//type: 0 发布评论  1 回复评论
	public void update2DeleteComment(int circlePosition, String commentId);
}
