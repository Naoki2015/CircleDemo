package com.yiw.circledemo.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yiw.circledemo.ImagePagerActivity;
import com.yiw.circledemo.MyApplication;
import com.yiw.circledemo.R;
import com.yiw.circledemo.bean.ActionItem;
import com.yiw.circledemo.bean.CircleItem;
import com.yiw.circledemo.bean.CommentConfig;
import com.yiw.circledemo.bean.CommentItem;
import com.yiw.circledemo.bean.FavortItem;
import com.yiw.circledemo.mvp.presenter.CirclePresenter;
import com.yiw.circledemo.spannable.ISpanClick;
import com.yiw.circledemo.utils.DatasUtil;
import com.yiw.circledemo.utils.GlideCircleTransform;
import com.yiw.circledemo.utils.UrlUtils;
import com.yiw.circledemo.widgets.CircleVideoView;
import com.yiw.circledemo.widgets.CircularImage;
import com.yiw.circledemo.widgets.CommentListView;
import com.yiw.circledemo.widgets.FavortListView;
import com.yiw.circledemo.widgets.MagicTextView;
import com.yiw.circledemo.widgets.MultiImageView;
import com.yiw.circledemo.widgets.SnsPopupWindow;
import com.yiw.circledemo.widgets.dialog.CommentDialog;
import com.yiw.circledemo.widgets.videolist.model.VideoLoadMvpView;
import com.yiw.circledemo.widgets.videolist.widget.TextureVideoView;

import java.util.List;

/**
 * Created by yiwei on 16/5/17.
 */
public class CircleAdapter extends BaseRecycleViewAdapter {

    public final static int TYPE_HEAD = 0;
    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;
    public static final int HEADVIEW_SIZE = 1;

    int curPlayIndex=-1;

    private CirclePresenter presenter;
    private Context context;
    public void setCirclePresenter(CirclePresenter presenter){
        this.presenter = presenter;
    }

    public CircleAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEAD;
        }

        int itemType = 0;
        CircleItem item = (CircleItem) datas.get(position-1);
        if (CircleItem.TYPE_URL.equals(item.getType())) {
            itemType = TYPE_URL;
        } else if (CircleItem.TYPE_IMG.equals(item.getType())) {
            itemType = TYPE_IMAGE;
        } else if(CircleItem.TYPE_VIDEO.equals(item.getType())){
            itemType = TYPE_VIDEO;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType == TYPE_HEAD){
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.head_circle, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_circle_item, parent, false);
            viewHolder = new CircleViewHolder(view, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if(getItemViewType(position)==TYPE_HEAD){
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        }else{

            final int circlePosition = position - HEADVIEW_SIZE;
            final CircleViewHolder holder = (CircleViewHolder) viewHolder;
            CircleItem circleItem = (CircleItem) datas.get(circlePosition);
            final String circleId = circleItem.getId();
            String name = circleItem.getUser().getName();
            String headImg = circleItem.getUser().getHeadUrl();
            final String content = circleItem.getContent();
            String createTime = circleItem.getCreateTime();
            final List<FavortItem> favortDatas = circleItem.getFavorters();
            final List<CommentItem> commentsDatas = circleItem.getComments();
            boolean hasFavort = circleItem.hasFavort();
            boolean hasComment = circleItem.hasComment();

            Glide.with(context).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.bg_no_photo).transform(new GlideCircleTransform(context)).into(holder.headIv);

            holder.nameTv.setText(name);
            holder.timeTv.setText(createTime);

            if(!TextUtils.isEmpty(content)){
                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

            if(DatasUtil.curUser.getId().equals(circleItem.getUser().getId())){
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }else{
                holder.deleteBtn.setVisibility(View.GONE);
            }
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除
                    if(presenter!=null){
                        presenter.deleteCircle(circleId);
                    }
                }
            });
            if(hasFavort || hasComment){
                if(hasFavort){//处理点赞列表
                    holder.favortListTv.setSpanClickListener(new ISpanClick() {
                        @Override
                        public void onClick(int position) {
                            String userName = favortDatas.get(position).getUser().getName();
                            String userId = favortDatas.get(position).getUser().getId();
                            Toast.makeText(MyApplication.getContext(), userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.favortListAdapter.setDatas(favortDatas);
                    holder.favortListAdapter.notifyDataSetChanged();
                    holder.favortListTv.setVisibility(View.VISIBLE);
                }else{
                    holder.favortListTv.setVisibility(View.GONE);
                }

                if(hasComment){//处理评论列表
                    holder.commentList.setOnItemClick(new CommentListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int commentPosition) {
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            if(DatasUtil.curUser.getId().equals(commentItem.getUser().getId())){//复制或者删除自己的评论

                                CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                                dialog.show();
                            }else{//回复别人的评论
                                if(presenter != null){
                                    CommentConfig config = new CommentConfig();
                                    config.circlePosition = circlePosition;
                                    config.commentPosition = commentPosition;
                                    config.commentType = CommentConfig.Type.REPLY;
                                    config.replyUser = commentItem.getUser();
                                    presenter.showEditTextBody(config);
                                }
                            }
                        }
                    });
                    holder.commentList.setOnItemLongClick(new CommentListView.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int commentPosition) {
                            //长按进行复制或者删除
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                            dialog.show();
                        }
                    });
                    holder.commentAdapter.setDatas(commentsDatas);
                    holder.commentAdapter.notifyDataSetChanged();
                    holder.commentList.setVisibility(View.VISIBLE);

                }else {

                    holder.commentList.setVisibility(View.GONE);
                }
                holder.digCommentBody.setVisibility(View.VISIBLE);
            }else{
                holder.digCommentBody.setVisibility(View.GONE);
            }

            holder.digLine.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

            final SnsPopupWindow snsPopupWindow = holder.snsPopupWindow;
            //判断是否已点赞
            String curUserFavortId = circleItem.getCurUserFavortId(DatasUtil.curUser.getId());
            if(!TextUtils.isEmpty(curUserFavortId)){
                snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
            }else{
                snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
            }
            snsPopupWindow.update();
            snsPopupWindow.setmItemClickListener(new PopupItemClickListener(circlePosition, circleItem, curUserFavortId));
            holder.snsBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //弹出popupwindow
                    snsPopupWindow.showPopupWindow(view);
                }
            });

            holder.urlTipTv.setVisibility(View.GONE);
            switch (holder.viewType) {
                case TYPE_URL:// 处理链接动态的链接内容和和图片
                    String linkImg = circleItem.getLinkImg();
                    String linkTitle = circleItem.getLinkTitle();
                    Glide.with(context).load(linkImg).into(holder.urlImageIv);
                    holder.urlContentTv.setText(linkTitle);
                    holder.urlBody.setVisibility(View.VISIBLE);
                    holder.urlTipTv.setVisibility(View.VISIBLE);
                    break;
                case TYPE_IMAGE:// 处理图片
                    final List<String> photos = circleItem.getPhotos();
                    if (photos != null && photos.size() > 0) {
                        holder.multiImageView.setVisibility(View.VISIBLE);
                        holder.multiImageView.setList(photos);
                        holder.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //imagesize是作为loading时的图片size
                                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                                ImagePagerActivity.startImagePagerActivity(context, photos, position, imageSize);
                            }
                        });
                    } else {
                        holder.multiImageView.setVisibility(View.GONE);
                    }
                    break;
                case TYPE_VIDEO:
                    holder.videoView.setVideoUrl(circleItem.getVideoUrl());
                    holder.videoView.setPostion(position);
                    holder.videoView.setOnPlayClickListener(new CircleVideoView.OnPlayClickListener() {
                        @Override
                        public void onPlayClick(int pos) {
                            curPlayIndex = pos;
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size()+1;//有head需要加1
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class CircleViewHolder extends RecyclerView.ViewHolder implements VideoLoadMvpView {
        public int viewType;

        public ImageView headIv;
        public TextView nameTv;
        public TextView urlTipTv;
        /** 动态的内容 */
        public MagicTextView contentTv;
        public TextView timeTv;
        public TextView deleteBtn;
        public ImageView snsBtn;
        /** 点赞列表*/
        public FavortListView favortListTv;

        public LinearLayout urlBody;
        public LinearLayout digCommentBody;
        public View digLine;

        /** 评论列表 */
        public CommentListView commentList;
        /** 链接的图片 */
        public ImageView urlImageIv;
        /** 链接的标题 */
        public TextView urlContentTv;
        /** 图片*/
        public MultiImageView multiImageView;

        public CircleVideoView videoView;
        // ===========================
        public FavortListAdapter favortListAdapter;
        public CommentAdapter commentAdapter;
        public SnsPopupWindow snsPopupWindow;

        public CircleViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);
            switch (viewType) {
                case TYPE_URL:// 链接view
                    viewStub.setLayoutResource(R.layout.viewstub_urlbody);
                    viewStub.inflate();
                    LinearLayout urlBodyView = (LinearLayout) itemView.findViewById(R.id.urlBody);
                    if(urlBodyView != null){
                        urlBody = urlBodyView;
                        urlImageIv = (ImageView) itemView.findViewById(R.id.urlImageIv);
                        urlContentTv = (TextView) itemView.findViewById(R.id.urlContentTv);
                    }
                    break;
                case TYPE_IMAGE:// 图片view
                    viewStub.setLayoutResource(R.layout.viewstub_imgbody);
                    viewStub.inflate();
                    MultiImageView multiImageView = (MultiImageView) itemView.findViewById(R.id.multiImagView);
                    if(multiImageView != null){
                        this.multiImageView = multiImageView;
                    }
                    break;
                case TYPE_VIDEO:
                    viewStub.setLayoutResource(R.layout.viewstub_videobody);
                    viewStub.inflate();

                    CircleVideoView videoBody = (CircleVideoView) itemView.findViewById(R.id.videoView);
                    if(videoBody!=null){
                        this.videoView = videoBody;
                    }
                    break;
                default:
                    break;
            }
            headIv = (ImageView) itemView.findViewById(R.id.headIv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            digLine = itemView.findViewById(R.id.lin_dig);

            contentTv = (MagicTextView) itemView.findViewById(R.id.contentTv);
            urlTipTv = (TextView) itemView.findViewById(R.id.urlTipTv);
            timeTv = (TextView) itemView.findViewById(R.id.timeTv);
            deleteBtn = (TextView) itemView.findViewById(R.id.deleteBtn);
            snsBtn = (ImageView) itemView.findViewById(R.id.snsBtn);
            favortListTv = (FavortListView) itemView.findViewById(R.id.favortListTv);

            digCommentBody = (LinearLayout) itemView.findViewById(R.id.digCommentBody);

            commentList = (CommentListView)itemView.findViewById(R.id.commentList);
            commentAdapter = new CommentAdapter(itemView.getContext());
            favortListAdapter = new FavortListAdapter();

            favortListTv.setAdapter(favortListAdapter);
            commentList.setAdapter(commentAdapter);

            snsPopupWindow = new SnsPopupWindow(itemView.getContext());

        }

        @Override
        public TextureVideoView getVideoView() {
            return null;
        }

        @Override
        public void videoBeginning() {

        }

        @Override
        public void videoStopped() {

        }

        @Override
        public void videoPrepared(MediaPlayer player) {

        }

        @Override
        public void videoResourceReady(String videoPath) {

        }
    }

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener{
        private String mFavorId;
        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private CircleItem mCircleItem;

        public PopupItemClickListener(int circlePosition, CircleItem circleItem, String favorId){
            this.mFavorId = favorId;
            this.mCirclePosition = circlePosition;
            this.mCircleItem = circleItem;
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if(System.currentTimeMillis()-mLasttime<700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if(presenter != null){
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            presenter.addFavort(mCirclePosition);
                        } else {//取消点赞
                            presenter.deleteFavort(mCirclePosition, mFavorId);
                        }
                    }
                    break;
                case 1://发布评论
                    if(presenter != null){
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = mCirclePosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
