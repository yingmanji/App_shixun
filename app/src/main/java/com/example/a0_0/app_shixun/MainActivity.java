package com.example.a0_0.app_shixun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.a0_0.app_shixun.Base.BaseActivity;
import com.example.a0_0.app_shixun.Base.L;
import com.example.a0_0.app_shixun.Service.PlayService;
import com.example.a0_0.app_shixun.UI.Indicator;
import com.example.a0_0.app_shixun.UI.ScrollLinearLayout;
import com.example.a0_0.app_shixun.fragments.Fragment_bendi;
import com.example.a0_0.app_shixun.music.MusicUtils;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements android.view.View.OnClickListener {
    private static final String TAG=MainActivity.class.getSimpleName();
    private ScrollLinearLayout mainContainer;
    private Indicator indicator;

    private TextView tv_bendi;
    private TextView tv_wangluo;
    private ViewPager viewPager;
    private View popshownView;

    private PopupWindow popupWindow;

    private ArrayList<Fragment> fragments=new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver();
        initFragment();
        setupViews();
    }
    private void registerReceiver(){
        IntentFilter filter=new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addDataScheme("file");
        registerReceiver(scanSDCardReceiver,filter);
    }
    private void setupViews()
    {
        mainContainer=(ScrollLinearLayout) findViewById(R.id.lin_main_container);
        indicator=(Indicator)findViewById(R.id.main_indicator);
        tv_bendi = (TextView) findViewById(R.id.tv_bendi);
        tv_wangluo = (TextView) findViewById(R.id.tv_wangluo);
        viewPager=(ViewPager)findViewById(R.id.vp_main_container);
        popshownView=findViewById(R.id.view_pop_show);


        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        tv_bendi.setOnClickListener(this);
        tv_wangluo.setOnClickListener(this);

        selectTab(0);
    }
    private ViewPager.OnPageChangeListener pageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            indicator.scroll(position,positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            selectTab(position);
            mainContainer.showIndicator();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private FragmentPagerAdapter pagerAdapter=
            new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return fragments.get(position);
                }

                @Override
                public int getCount() {
                    return fragments.size();
                }
            };

    /**
     * 切换导航indicator
     * @param index
     */
    private void selectTab(int index) {
        switch (index) {
            case 0:
                tv_bendi.setTextColor(ContextCompat.getColor(this,R.color.main));
                tv_wangluo.setTextColor(ContextCompat.getColor(this,R.color.main_dark));
                break;
            case 1:
                tv_bendi.setTextColor(ContextCompat.getColor(this,R.color.main_dark));
                tv_wangluo.setTextColor(ContextCompat.getColor(this,R.color.main));
                break;
        }
    }
    private void initFragment() {
        Fragment_bendi fragment_bendi=new Fragment_bendi();
        fragments.add(fragment_bendi);
    }

    /**
     * 获取音乐播放服务
     * @return
     */
    public PlayService getPlayService() {
        return playService;
    }
    public void hideIndicator() {
        mainContainer.hideIndicator();
    }
    public void showIndicator() {
        mainContainer.showIndicator();
    }
    public void onPopupWindowShown(){
        popshownView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.layer_gone_anim));
        popshownView.setVisibility(View.VISIBLE);
    }
    public void onPopupWindowDismiss(){
        popshownView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.layer_gone_anim));
        popshownView.setVisibility(View.GONE);
    }
    public void onPublish(int progress) {
        //如果当前显示的fragment是音乐列表fragment
        //则调用fragment的setProgress设置进度
        if(viewPager.getCurrentItem()==0)
        {
            ((Fragment_bendi)fragments.get(0)).setProgress(progress);
        }
    }
    public void onChange(int position){
        //如果当前显示的fragment是音乐列表fragment
        //则调用fragment的setProgress切换歌曲
        if(viewPager.getCurrentItem()==0)
        {
            ((Fragment_bendi)fragments.get(0)).onPlay(position);
        }
    }
    public void onShowMenu(){
        onPopupWindowShown();
        if(popshownView==null)
        {
            View view=View.inflate(this,R.layout.exit_pop_layout,null);
            View shutdown=view.findViewById(R.id.tv_pop_shutdown);
            View exit=view.findViewById(R.id.tv_pop_exit);
            View cancel=view.findViewById(R.id.tv_pop_cancel);

            //不需要共享变量,所以放这没事
            shutdown.setOnClickListener(this);
            exit.setOnClickListener(this);
            cancel.setOnClickListener(this);

            popupWindow=new PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setAnimationStyle(R.style.popwin_anim);
            popshownView.setFocusable(true);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    onPopupWindowDismiss();
                }
            });
        }
        popupWindow.showAtLocation(getWindow().getDecorView(),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_bendi:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_wangluo:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_pop_exit:
                stopService(new Intent(this,PlayService.class));
//                stopService(new Intent(this,DownloadService.class));
                break;
            case R.id.tv_pop_shutdown:
                finish();
                break;
            case R.id.tv_pop_cancel:
                if(popupWindow!=null&&popupWindow.isShowing())
                    popupWindow.dismiss();
                onPopupWindowDismiss();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_MENU){
            onShowMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(scanSDCardReceiver);
        super.onDestroy();
    }
    private BroadcastReceiver scanSDCardReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.l(TAG,"scanSDCardReceiver-->onReceive()");
            if(intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)){
                MusicUtils.initMusicList();
                ((Fragment_bendi)fragments.get(0)).onMusicListChanged();
            }
        }
    };
}
