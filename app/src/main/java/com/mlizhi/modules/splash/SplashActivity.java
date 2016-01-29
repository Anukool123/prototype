package com.mlizhi.modules.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mlizhi.modules.login.LoginActivity;
import com.philips.skincare.skincareprototype.R;

public class SplashActivity extends Activity implements OnPageChangeListener {
    private static final int SPLASH_COUNTS = 4;
    private Context mContext;
    private LayoutInflater mInflater;
    private View[] mSplashViews;
    private int[] splashImages;
    private ImageView[] tips;
    private ViewPager viewPager;

    /* renamed from: com.mlizhi.modules.splash.SplashActivity.1 */
    class C01701 implements OnClickListener {
        C01701() {
        }

        public void onClick(View v) {
            SplashActivity.this.mContext.startActivity(new Intent(SplashActivity.this.mContext, LoginActivity.class));
            SplashActivity.this.finish();
        }
    }

    public class MyAdapter extends PagerAdapter {
        public int getCount() {
            return SplashActivity.this.mSplashViews.length;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(SplashActivity.this.mSplashViews[position % SplashActivity.this.mSplashViews.length]);
        }

        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(SplashActivity.this.mSplashViews[position % SplashActivity.this.mSplashViews.length], 0);
            return SplashActivity.this.mSplashViews[position % SplashActivity.this.mSplashViews.length];
        }
    }

    public SplashActivity() {
        this.splashImages = new int[]{R.drawable.ic_splash_img1, R.drawable.ic_splash_img2, R.drawable.ic_splash_img3, R.drawable.ic_splash_img4};
    }

    @SuppressLint({"InflateParams"})
    protected void onCreate(Bundle savedInstanceState) {
        int i;
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_splash);
        ViewGroup group = (ViewGroup) findViewById(R.id.splash_viewGroup);
        this.viewPager = (ViewPager) findViewById(R.id.splash_viewPager);
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mSplashViews = new View[SPLASH_COUNTS];
        for (i = 0; i < this.mSplashViews.length - 1; i++) {
            this.mSplashViews[i] = this.mInflater.inflate(R.layout.view_splash_item, null);
            ((ImageView) this.mSplashViews[i].findViewById(R.id.pre_setting_image)).setImageResource(this.splashImages[i]);
        }
        this.mSplashViews[this.mSplashViews.length - 1] = this.mInflater.inflate(R.layout.view_splash_item_last, null);
        ((ImageView) this.mSplashViews[this.mSplashViews.length - 1].findViewById(R.id.pre_setting_image_last)).setImageResource(this.splashImages[this.mSplashViews.length - 1]);
        this.mSplashViews[this.mSplashViews.length - 1].findViewById(R.id.pre_setting_btn_last).setOnClickListener(new C01701());
        this.tips = new ImageView[SPLASH_COUNTS];
        for (i = 0; i < this.tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(10, 10));
            this.tips[i] = imageView;
            if (i == 0) {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator_focused);
            } else {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LayoutParams(-2, -2));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            layoutParams.bottomMargin = 30;
            group.addView(imageView, layoutParams);
        }
        this.viewPager.setAdapter(new MyAdapter());
        this.viewPager.setOnPageChangeListener(this);
        this.viewPager.setCurrentItem(0);
    }

    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int arg0) {
        setImageBackground(arg0 % this.mSplashViews.length);
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < this.tips.length; i++) {
            if (i == selectItems) {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator_focused);
            } else {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator);
            }
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}
