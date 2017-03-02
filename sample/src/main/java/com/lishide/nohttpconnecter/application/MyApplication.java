package com.lishide.nohttpconnecter.application;

import android.app.Application;

import com.yanzhenjie.nohttp.BuildConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 NoHttp
        NoHttp.initialize(this, new NoHttp.Config()
                .setConnectTimeout(30 * 1000)  // 设置全局连接超时时间，单位毫秒，默认10s。
                .setReadTimeout(30 * 1000)  // 设置全局服务器响应超时时间，单位毫秒，默认10s。
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .setCacheStore(
                        new DBCacheStore(this).setEnable(true) // 如果不使用缓存，设置setEnable(false)禁用。
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
                .setCookieStore(
                        new DBCookieStore(this).setEnable(true) // 如果不维护cookie，设置false禁用。
                )
                // 配置网络层，默认使用URLConnection，如果想用OkHttp：OkHttpNetworkExecutor。
                .setNetworkExecutor(new OkHttpNetworkExecutor())
        );

        Logger.setDebug(BuildConfig.DEBUG);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttpSample");// 设置NoHttp打印Log的tag。

    }
}
