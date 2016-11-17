package com.yiw.circledemo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yiw.circledemo.R;
import com.yiw.circledemo.bean.PhotoInfo;
import com.yiw.circledemo.utils.DensityUtil;

import java.util.List;

/**
 * @ClassName MultiImageView.java
 * @author shoyu
 * @version 
 * @Description: 显示1~N张图片的View
 */

public class MultiImageView extends LinearLayout {
	public static int MAX_WIDTH = 0;

	// 照片的Url列表
	private List<PhotoInfo> imagesList;

	/** 长度 单位为Pixel **/
	private int pxOneMaxWandH;  // 单张图最大允许宽高
	private int pxMoreWandH = 0;// 多张图的宽高
	private int pxImagePadding = DensityUtil.dip2px(getContext(), 3);// 图片间的间距

	private int MAX_PER_ROW_COUNT = 3;// 每行显示最大数

	private LayoutParams onePicPara;
	private LayoutParams morePara, moreParaColumnFirst;
	private LayoutParams rowPara;

	private OnItemClickListener mOnItemClickListener;
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		mOnItemClickListener = onItemClickListener;
	}

	public MultiImageView(Context context) {
		super(context);
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setList(List<PhotoInfo> lists) throws IllegalArgumentException{
		if(lists==null){
			throw new IllegalArgumentException("imageList is null...");
		}
		imagesList = lists;
		
		if(MAX_WIDTH > 0){
			pxMoreWandH = (MAX_WIDTH - pxImagePadding*2 )/3; //解决右侧图片和内容对不齐问题
			pxOneMaxWandH = MAX_WIDTH * 2 / 3;
			initImageLayoutParams();
		}

		initView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(MAX_WIDTH == 0){
			int width = measureWidth(widthMeasureSpec);
			if(width>0){
				MAX_WIDTH = width;
				if(imagesList!=null && imagesList.size()>0){
					setList(imagesList);
				}
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			// result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
			// + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private void initImageLayoutParams() {
		int wrap = LayoutParams.WRAP_CONTENT;
		int match = LayoutParams.MATCH_PARENT;

		onePicPara = new LayoutParams(wrap, wrap);

		moreParaColumnFirst = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara.setMargins(pxImagePadding, 0, 0, 0);

		rowPara = new LayoutParams(match, wrap);
	}

	// 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
	private void initView() {
		this.setOrientation(VERTICAL);
		this.removeAllViews();
		if(MAX_WIDTH == 0){
			//为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
			addView(new View(getContext()));
			return;
		}
		
		if (imagesList == null || imagesList.size() == 0) {
			return;
		}

		if (imagesList.size() == 1) {
			addView(createImageView(0, false));
		} else {
			int allCount = imagesList.size();
			if(allCount == 4){
				MAX_PER_ROW_COUNT = 2;
			}else{
				MAX_PER_ROW_COUNT = 3;
			}
			int rowCount = allCount / MAX_PER_ROW_COUNT
					+ (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// 行数
			for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
				LinearLayout rowLayout = new LinearLayout(getContext());
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);

				rowLayout.setLayoutParams(rowPara);
				if (rowCursor != 0) {
					rowLayout.setPadding(0, pxImagePadding, 0, 0);
				}

				int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
						: allCount % MAX_PER_ROW_COUNT;//每行的列数
				if (rowCursor != rowCount - 1) {
					columnCount = MAX_PER_ROW_COUNT;
				}
				addView(rowLayout);

				int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
				for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
					int position = columnCursor + rowOffset;
					rowLayout.addView(createImageView(position, true));
				}
			}
		}
	}

	private ImageView createImageView(int position, final boolean isMultiImage) {
		PhotoInfo photoInfo = imagesList.get(position);
		ImageView imageView = new ColorFilterImageView(getContext());
		if(isMultiImage){
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ?moreParaColumnFirst : morePara);
		}else {
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			//imageView.setMaxHeight(pxOneMaxWandH);

            int expectW = photoInfo.w;
            int expectH = photoInfo.h;

            if(expectW == 0 || expectH == 0){
                imageView.setLayoutParams(onePicPara);
            }else{
                int actualW = 0;
                int actualH = 0;
                float scale = ((float) expectH)/((float) expectW);
                if(expectW > pxOneMaxWandH){
                    actualW = pxOneMaxWandH;
                    actualH = (int)(actualW * scale);
                } else if(expectW < pxMoreWandH){
                    actualW = pxMoreWandH;
                    actualH = (int)(actualW * scale);
                }else{
                    actualW = expectW;
                    actualH = expectH;
                }
                imageView.setLayoutParams(new LayoutParams(actualW, actualH));
            }
		}

		imageView.setId(photoInfo.url.hashCode());
		imageView.setOnClickListener(new ImageOnClickListener(position));
		imageView.setBackgroundColor(getResources().getColor(R.color.im_font_color_text_hint));
		Glide.with(getContext()).load(photoInfo.url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

		return imageView;
	}

	private class ImageOnClickListener implements View.OnClickListener{

		private int position;
		public ImageOnClickListener(int position){
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			if(mOnItemClickListener != null){
				mOnItemClickListener.onItemClick(view, position);
			}
		}
	}

	public interface OnItemClickListener{
		public void onItemClick(View view, int position);
	}
}