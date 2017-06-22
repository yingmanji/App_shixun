package com.example.a0_0.app_shixun.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import com.example.a0_0.app_shixun.R;

/**
 * Created by 樱满集0_0 on 2017/6/23.
 */

public class CDView extends View {
    private static final int MSG_RUN=0X00000100;
    private static final int TIME_UPDATE=50;

    private Bitmap circleBitmap;
    private Bitmap clipBitmap;//cd图片

    private float rotation=0.0f;

    private Matrix matrix;

    private volatile boolean isRunning;

    public CDView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CDView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circleBitmap= BitmapFactory.decodeResource(getResources(),
                R.drawable.cd_center);
        matrix=new Matrix();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning=false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(clipBitmap==null){

        }
    }
}
