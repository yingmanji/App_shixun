package com.example.a0_0.app_shixun.Base;

import java.util.Objects;

/**
 * Created by 樱满集0_0 on 2017/6/21.
 */

public class L {
    private static final boolean debug=true;

    public static void l(String tag, Object msg) {
        l(tag+"-->"+msg);
    }
    public static void l(Object msg) {
        if(!debug) return;
        System.out.print(msg);
    }
}
