package com.example.a0_0.app_shixun.Service;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class Download implements Serializable {
    private static final long seriaVersionUID=0X00001000L;
    private static final int START=1;//开始下载
    private static final int PUBLISH=2;//更新进度
    private static final int PAUSE=3;//暂停下载
    private static final int CANCEL=4;//取消下载
    private static final int ERROR=5;//下载错误
    private static final int SUCCESS=6;//下载成功
    private static final int GOON=7;//继续下载

    private static final String  UA="Mozilla/5.0(Window NT6.1;WOW64)"+
            "AppleWebKit/537.36(KHTML,like Gecko)"+
            "Chrome/37.0.2041.4 safari/537.36";
    private static ExecutorService ThreadPool;//线程池
    static {
        ThreadPool= Executors.newFixedThreadPool(5);//默认5个
    }

}
