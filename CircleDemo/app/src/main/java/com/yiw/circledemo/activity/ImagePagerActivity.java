package com.yiw.circledemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bm.library.PhotoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.yiw.circledemo.R;
import com.yiw.circledemo.utils.ImageSize;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Lewis on 2016/7/22.
 */
public class ImagePagerActivity extends Activity{

    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_POSITION = "position";
    public static final String INTENT_IMAGESIZE = "imagesize";
    ViewPager viewPager;
    LinearLayout viewGroup;
    ArrayList<String> imgUrls;
    private List<View> guideViewList = new ArrayList<View>();
     int startPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepager);
        getIntentData();
        viewGroup=(LinearLayout)findViewById(R.id.guideGroup) ;
        viewPager=(ViewPager) findViewById(R.id.pager);
        viewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        addGuideView(viewGroup, startPos, imgUrls);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                if(imgUrls==null) {return 0;}
                else {return imgUrls.size();}
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }
            @Override
            public Object instantiateItem(ViewGroup container, int position){
                PhotoView view = new PhotoView(ImagePagerActivity.this);
                view.enable();
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //view.setImageResource(imgsId[position]);
                DisplayImageOptions imageOptions= new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                        .showImageForEmptyUri(R.drawable.balloon_qupai_image).showImageOnFail(R.drawable.balloon_qupai_image).cacheInMemory(true).cacheOnDisk(true).build();
                ImageLoader.getInstance().displayImage(imgUrls.get(position),view,imageOptions);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goBack();
                    }
                });
                container.addView(view);
                return view;
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.setCurrentItem(startPos);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0; i<guideViewList.size(); i++){
                    guideViewList.get(i).setSelected(i==position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static void startImagePagerActivity(Context context, List<String> imgUrls, int position, ImageSize imageSize){
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<String>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        //intent.putExtra(INTENT_IMAGESIZE, imageSize);
        context.startActivity(intent);
    }

    public void goBack(){
        this.onBackPressed();
    }

    private void getIntentData() {
        startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        imgUrls = getIntent().getStringArrayListExtra(INTENT_IMGURLS);
        //imageSize = (ImageSize) getIntent().getSerializableExtra(INTENT_IMAGESIZE);
    }

    public void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<String> imgUrls){
        if(imgUrls!=null && imgUrls.size()>0){
            guideViewList.clear();
            for (int i=0; i<imgUrls.size(); i++){
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i==startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }
}
