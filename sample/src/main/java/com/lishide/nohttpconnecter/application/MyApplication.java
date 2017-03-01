package com.lishide.nohttpconnecter.application;

import android.app.Application;

import com.yanzhenjie.nohttp.BuildConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 NoHttp
        NoHttp.initialize(this, new NoHttp.Config()
                .setConnectTimeout(30 * 1000) // 全局连接超时时间，单位毫秒。
                .setReadTimeout(30 * 1000) // 全局服务器响应超时时间，单位毫秒。
                .setNetworkExecutor(new OkHttpNetworkExecutor())  // 使用OkHttp做网络层。
        );

        Logger.setDebug(BuildConfig.DEBUG);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttpSample");// 设置NoHttp打印Log的tag。

    }
}
