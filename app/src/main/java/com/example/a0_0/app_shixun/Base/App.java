package com.example.a0_0.app_shixun.Base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class App extends Application {
    public static Context context;
    public static int screenWidth;
    public static int screenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

        /*startService(new Intent(this,playService.class));
        startService(new Intent(this,DownloadService.class));*/

        WindowManager wm=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;
        screenHeight=dm.heightPixels;
    }
}
