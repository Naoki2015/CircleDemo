package com.yiw.circledemo.widgets;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yiw.circledemo.utils.DensityUtil;

/**
 * @ClassName MultiImageView.java
 * @author shoyu
 * @version 
 * @Description: 显示1~N张图片的View
 */

public class MultiImageView extends LinearLayout {
	public static int MAX_WIDTH = 0;

	// 照片的Url列表
	private List<String> imagesList;

	/** 长度 单位为Pixel **/
	private int pxOneWidth = DensityUtil.dip2px(getContext(), 115);// 单张图时候的宽
	private int pxOneHeight = DensityUtil.dip2px(getContext(), 150);// 单张图时候的高
	private int pxMoreWandH = 0;// 多张图的宽高
	private int pxImagePadding = DensityUtil.dip2px(getContext(), 3);// 图片间的间距

	private int MAX_PER_ROW_COUNT = 3;// 每行显示最大数

	private LayoutParams onePicPara;
	private LayoutParams morePara;
	private LayoutParams rowPara;

	public MultiImageView(Context context) {
		super(context);
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setList(List<String> lists, int width) throws IllegalArgumentException{
		if(lists==null){
			throw new IllegalArgumentException("imageList is null...");
		}
		imagesList = lists;
		
		if(width>0){
			pxMoreWandH = width/3 - pxImagePadding;
			pxOneWidth = width/2;
			pxOneHeight = width*2/3;
			initVariable();
		}
		initView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(MAX_WIDTH==0){
			int width = measureWidth(widthMeasureSpec);
			if(width>0){
				MAX_WIDTH = width;
				setList(imagesList,width);
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

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	/*private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			// result = (int) (-mAscent + mTextPaint.descent()) +
			// getPaddingTop()
			// + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}*/

	private void initVariable() {

		onePicPara = new LayoutParams(pxOneWidth, pxOneHeight);

		morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara.setMargins(0, 0, pxImagePadding, 0);

		int wrap = LayoutParams.WRAP_CONTENT;
		int match = LayoutParams.MATCH_PARENT;
		rowPara = new LayoutParams(match, wrap);
		rowPara.setMargins(0, 0, 0, pxImagePadding);
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
			for (String url : imagesList) {
				ImageView imageView = new ImageView(getContext());
				imageView.setId(url.hashCode());// 指定id
				imageView.setLayoutParams(onePicPara);
				imageView.setMinimumWidth(pxMoreWandH);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				ImageLoader.getInstance().displayImage(url, imageView);
				//Drawable drawable = imageView.getDrawable();
				//获取图片的真实宽高，如果宽>高,则宽取最大宽度的2/3；如果宽<高,则宽取最大宽度的一半；高怎按比例计算出来。
				//drawable.getIntrinsicWidth();

				int position = 0;
				imageView.setTag(position);
				imageView.setOnClickListener(ImageViewOnClickListener);
				addView(imageView);
			}

		} else {
			int allCount = imagesList.size();
			if(allCount==4){
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
				if (rowCursor == 0) {
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
					String thumbUrl = imagesList.get(position);

					ImageView imageView = new ImageView(getContext());
					imageView.setId(thumbUrl.hashCode());// 指定id
					imageView.setLayoutParams(morePara);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					ImageLoader.getInstance().displayImage(thumbUrl, imageView);
					
					imageView.setTag(position);
					imageView.setOnClickListener(ImageViewOnClickListener);

					rowLayout.addView(imageView);
				}
			}
		}
	}

	// 图片点击事件
	private View.OnClickListener ImageViewOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
//			int position = (Integer) view.getTag();
//			Intent photoViewIntent = new Intent(getContext(), PhotoViewActivity.class);
//			ArrayList<String> picUrls = new ArrayList<String>();
//			picUrls.addAll(imagesList);
//			
//			photoViewIntent.putStringArrayListExtra("picUrls", picUrls);
//			photoViewIntent.putExtra("initIndex", position);
//			
//			photoViewIntent.putExtra(PhotoViewActivity.EXTRA_OPEN_LONG_CLICK_ACTION, true);
//			photoViewIntent.putExtra(PhotoViewActivity.EXTRA_LONG_CLICK_IMAGE_SAVE_FILE_NAME, Constants.ROOT);
//			getContext().startActivity(photoViewIntent);
		}
	};

}