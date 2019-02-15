package com.example.ccbim.ccbimlib;


import android.support.multidex.MultiDexApplication;

import cn.pinming.modelsdk.CCBimSdkUtil;

/**
 * Created by lgf on 2019/1/22.
 */

public class SdkApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //CCBimSdkUtil.initSdk(this);
//        WeqiaApplication.getInstance().init();

    }
}
