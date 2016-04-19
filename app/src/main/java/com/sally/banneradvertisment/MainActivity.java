package com.sally.banneradvertisment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现自动轮播的方法有：
 * 1. 定时器 Timer
 * 2. while(true) 循环
 * 3. ClockManager
 * 4. Handler
 *
 * 这里为了实现循环轮播效果，做了一个假象：假想有n多个位置，但是图片资源有限个。、
 * 在PagerAdapter类的getCount()方法中，使用了定义的整数，这里假设有COUNT_IAMGE个。
 * 据说使用Interge.MAX_VALUE 会出现啥问题，不太安全
 */
public class MainActivity extends AppCompatActivity {
    public static final int COUNT_IMAGE = 100;
    public static final int RUNNING = 0x110;
    public static final String TAG = "MainActivity";

    private ViewPager mViewPager;
    private MyPagerAdapter mAdapter;
    private TextView mTextView;
    private LinearLayout mPointGroup;

    private String[] mTitles = new String[]{"动漫1:东京食尸鬼", "动漫2:寄生兽", "动漫3:火影忍者", "动漫4:亚人", "动漫5:进击的巨人"};
    private int[] mPics = new int[]{R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e};

    private List<ImageView> mImageViews;
    private int lastPosition;

    private boolean isRunning = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            if(isRunning) {
                mHandler.sendEmptyMessageDelayed(RUNNING, 2000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();

        isRunning = true;
        mHandler.sendEmptyMessageDelayed(RUNNING, 2000);
    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mTextView = (TextView) findViewById(R.id.id_textview);
        mPointGroup = (LinearLayout) findViewById(R.id.id_linearlayout);

        mTextView.setText(mTitles[0]);

        mImageViews = new ArrayList<ImageView>();
        for(int i=0; i<mTitles.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(mPics[i]);
            mImageViews.add(iv);

            ImageView point = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = 20;
            point.setLayoutParams(lp);
            point.setBackgroundResource(R.drawable.point_bg);
            mPointGroup.addView(point);

            if(i == 1) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
            }
        }
    }

    private void initEvents() {
        mAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                position = position%mImageViews.size();
                mTextView.setText(mTitles[position]);
                mPointGroup.getChildAt(position).setEnabled(true);
                mPointGroup.getChildAt(lastPosition).setEnabled(false);
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean isDestroyed() {
        isRunning = false;
        return super.isDestroyed();
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return COUNT_IMAGE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 获得相应位置上的view
         * @param container
         * @param position 0-100
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            Log.e(TAG, "current point position =====" + position);
            container.addView(mImageViews.get(position%mImageViews.size()));
            return mImageViews.get(position%mImageViews.size());
        }

        /**
         * 销毁对应位置上的view
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            object = null;
        }
    }
}
