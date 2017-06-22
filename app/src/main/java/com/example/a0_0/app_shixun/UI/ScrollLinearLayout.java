package com.example.a0_0.app_shixun.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.a0_0.app_shixun.Base.L;

/**
 * Created by 樱满集0_0 on 2017/6/22.
 */

public class ScrollLinearLayout extends LinearLayout{
    private Scroller scroller;
    private int indicatorHeight;

    public ScrollLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller=new Scroller(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        View indicator=getChildAt(0);
        indicatorHeight=indicator.getMeasuredHeight();

        L.l("indicator height",indicatorHeight);
    }

    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()){
            scrollTo(0,scroller.getCurrY());
            postInvalidate();
        }
    }
    public void hideIndicator(){
        if(!scroller.isFinished()){
            scroller.abortAnimation();
        }
        scroller.startScroll(0,0,0,indicatorHeight,500);
    }

    public void showIndicator(){
        scrollTo(0,0);
        postInvalidate();
    }
}
